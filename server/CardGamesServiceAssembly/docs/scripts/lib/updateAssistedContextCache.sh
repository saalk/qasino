#!/bin/bash
ALL_ARGS="$@"

function outputHelp {
	echo "$(tput setaf 2)Help$(tput sgr0):
		usage: $0 [-e|--env (tst|acc)] [-i|--issuer (ming|iris)] [-a|--accesstoken] [-r|--rgb]

		optional values:
	    			$(tput setaf 2) -e|--env $(tput sgr0): the environment to call [tst|acc|prd].
	    			$(tput setaf 2) -i|--issuer $(tput sgr0): the issuer to provide [ming|iris].
	    			$(tput setaf 2) -t|--accesstoken $(tput sgr0): the token which contains the session.
	    			$(tput setaf 2) -r|--rgb $(tput sgr0): the customer identifier in the legacy format.
	    			$(tput setaf 2) -h|--help $(tput sgr0): print this help" >&2
}

# Filter through provided options.
while [[ $# -gt 0 ]]; do
  case "$1" in
        -e|--env) ENV="${2}"; shift; shift; ;;
        -i|--issuer) ISSUER="${2}"; shift; shift; ;;
        -t|--accesstoken) ACCESS_TOKEN="${2}"; shift; shift; ;;
        -r|--rgb) RGB="${2}"; shift; shift; ;;
        -h|--help) outputHelp; exit 1; ;;
        -*|--*)
        echo "$(tput setaf 1)Invalid options$(tput sgr0), your input: ${0} ${ALL_ARGS}" >&2
      outputHelp; exit 1; ;;
      *) POSITIONAL+=("$1"); shift; ;;
  esac
done

set -- "${POSITIONAL[@]}" # restore positional parameters

function updateAssistedContext {
    BASE_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"
    COOKIE_FILE="${BASE_DIR}/cookies.txt"

    ##### update information in cache of iris (needed for some legacy applications)
    REQUEST='{
               "customers": [
                 {
                   "data": {
                     "qualification": "individual",
                     "segmentCode": null,
                     "selected": true
                   },
                   "key": "'${RGB}'"
                 }
               ],
               "data": {
                 "channelSubType": "Oosterhout NB",
                 "channelSubTypeId": 80152
               },
               "employee": {
                 "levelOfAssurance": 5
               }
             }'
    if [[ ${ENV} = "tst" ]]; then
        HOST="api.test.iris.ing.net"
    else
        HOST="api.accp.iris.ing.net"
    fi

    echo "calling: https://${HOST}/assisted-context/context with rgb ${RGB}" >&2
    ANSWER=$(curl -X PUT -k https://${HOST}/assisted-context/context \
        --cookie-jar "${COOKIE_FILE}" --cookie "${COOKIE_FILE}" \
        --header "Content-Type: application/json" \
        --header "Authorization: Bearer ${ACCESS_TOKEN}" \
        --header "X-XSRF-TOKEN: $("${BASE_DIR}"/getXsrfToken.sh "${COOKIE_FILE}")" \
        -d "${REQUEST}")
    echo assisted-context:
    echo "${ANSWER}" | sed -e "s/^)]}',//" |jq
}

updateAssistedContext



