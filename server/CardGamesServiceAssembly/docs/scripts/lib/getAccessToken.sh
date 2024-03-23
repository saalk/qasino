#!/bin/bash
ALL_ARGS="$@"

function outputHelp {
	echo "$(tput setaf 2)Help$(tput sgr0):
		usage: ${0} [-e|--env (tst|acc)] [-p|--person (UUID)] [-a|--accessprofile (UUID)] [-em|--employee (CORPKEY)] [-i|--issuer (ming|iris)]
		example:
			${0} -e tst -p 'c24abae2-8a24-45da-b697-6b890b016b62' -a '86daa7a6-6510-4eda-9d56-8b87a7164578'
			${0} -e tst -em 'NA51RB'
			${0} -e tst -p 'c24abae2-8a24-45da-b697-6b890b016b62' -a '86daa7a6-6510-4eda-9d56-8b87a7164578' -em 'NA51RB'

		optional values:
	    			$(tput setaf 2) -e|--env $(tput sgr0): the environment to call [tst|acc|prd].
	    			$(tput setaf 2) -p|--person $(tput sgr0): the id of the person to put in the token.
	    			$(tput setaf 2) -a|--accessprofile $(tput sgr0): the id of the profile to put in the token.
	    			$(tput setaf 2) -em|--employee $(tput sgr0): corp key of an employee to put in the access token. Will be put in executor field.
	    			$(tput setaf 2) -i|--issuer $(tput sgr0): the issuer to provide [ming|iris].
	    			$(tput setaf 2) -h|--help $(tput sgr0): print this help" >&2
}

# Filter through provided options.
while [[ $# -gt 0 ]]; do
	case "$1" in
        -e|--env) ENV="${2}"; shift; shift; ;;
        -p|--person) PERSON_ID="${2}"; shift; shift; ;;
        -a|--accessprofile) PROFILE_ID="${2}"; shift; shift; ;;
        -em|--employee) EMPLOYEE="${2}"; shift; shift; ;;
        -i|--issuer) ISSUER="${2}"; shift; shift; ;;
        -h|--help) outputHelp; exit 1; ;;
        -*|--*)
		    echo "$(tput setaf 1)Invalid options$(tput sgr0), your input: ${0} ${ALL_ARGS}" >&2
			outputHelp; exit 1; ;;
	    *) POSITIONAL+=("$1"); shift; ;;
	esac
done

set -- "${POSITIONAL[@]}" # restore positional parameters



NOT_BEFORE=$(date +%s)

# (ensure this is separated by Tabs, and not spaces)
if [[ ${ISSUER} = "iris" ]]; then
    LOA=3
else
    LOA=5
fi

function getIssuer {
    case "${ISSUER:-"ming"}" in
        ming)
            [[ ${ENV} = "tst" ]] && echo "api.test.mijn.ing.nl" && exit 0
            [[ ${ENV} = "acc" ]] && echo "api.accp.mijn.ing.nl" && exit 0
            ;;
        iris)
            [[ ${ENV} = "tst" ]] && echo "api.test.iris.ing.net" && exit 0
            [[ ${ENV} = "acc" ]] && echo "api.accp.iris.ing.net" && exit 0
            ;;
        *) echo "Invalid issuer" >&2 && outputHelp >&2 && exit 1 ;;
    esac
}

function getExecutor {
    EXECUTOR_PERSON="${PERSON_ID}"
    EXECUTOR_PROFILE="${PROFILE_ID}"
    EXECUTOR_TYPE="customer"
    if [ ! -z ${EMPLOYEE} ]; then
        EXECUTOR_PERSON="${EMPLOYEE}"
        EXECUTOR_PROFILE="${EMPLOYEE}"
        EXECUTOR_TYPE="employee"
    fi

    echo '"executor": {
              "level_of_assurance": 5,
              "means": [
                  {
                      "id": "meansId",
                      "type": "meansType"
                  }
              ],
              "person": "'"${EXECUTOR_PERSON}"'",
              "profile": "'"${EXECUTOR_PROFILE}"'",
              "type": "'"${EXECUTOR_TYPE}"'"
          },'
}

function getRequester {
    if [ ! -z ${PERSON_ID} ] && [ ! -z ${PROFILE_ID} ]; then
        echo '"requester": {
                  "level_of_assurance": '${LOA}',
                  "means": [
                      {
                          "id": "meansId",
                          "type": "meansType"
                      }
                  ],
                  "person": "'"${PERSON_ID}"'",
                  "profile": "'"${PROFILE_ID}"'",
                  "type": "customer"
              },'
    fi
}

function getTestEnvTokens {
    echo "calling [${ENV}]: https://api.test.myaccount.ing.net/security-tokens/access-tokens" >&2
    curl -s -k -X POST 'https://api.test.myaccount.ing.net/security-tokens/access-tokens' \
        --header 'Content-Type: application/json' \
        --header 'Accept: application/json' \
        -d '[
                {
                    "access_token": {
                        "issuer": "'"$(getIssuer)"'",
                        "client_id": "d620cdbb-f296-4d37-972c-71c9b4755019",
                        '"$( getExecutor )"'
                        '"$( getRequester )"'
                        "not_before": "'"${NOT_BEFORE}"'",
                        "time_to_live": 60000,
                        "pinning_type": "cookie",
                        "pinning_value": "secure_random_value",
                        "scopes": [
                            "personal_data"
                        ],
                        "trace_enabled": true,
                        "customer_service_organisation": "CSO_NL"
                    },
                    "refresh_token": {
                        "time_to_live": 600000
                    },
                    "token_identifier": "string",
                    "unencrypted": true
                }
            ]'
}



function getAccpEnvTokens {
    echo "calling: https://api.accp.myaccount.qasino.cloud/security-tokens/access-tokens" >&2
    curl --proxy giba-proxy.wps.ing.net:8080 -s -k -X POST 'https://api.accp.myaccount.qasino.cloud/security-tokens/access-tokens' \
        --header 'Content-Type: application/json' \
        --header 'Accept: application/json' \
        -d '[
                {
                    "access_token": {
                        "issuer": "'"$(getIssuer)"'",
                        "client_id": "d620cdbb-f296-4d37-972c-71c9b4755019",
                        '"$( getExecutor )"'
                        '"$( getRequester )"'
                        "not_before": "'"${NOT_BEFORE}"'",
                        "time_to_live": 60000,
                        "pinning_type": "cookie",
                        "pinning_value": "secure_random_value",
                        "scopes": [
                            "personal_data"
                        ],
                        "trace_enabled": true,
                        "customer_service_organisation": "CSO_NL"
                    },
                    "refresh_token": {
                        "time_to_live": 600000
                    },
                    "token_identifier": "string",
                    "unencrypted": true
                }
            ]'
}


case "${ENV}" in
    tst) getTestEnvTokens ;;
    acc) getAccpEnvTokens ;;
    prd) echo "No no no kind sir/madam. Dont try production !!!!" >&2 ;;
    *) echo "Invalid environment provided. Should be [tst|acc]" && outputHelp >&2 ;;
esac