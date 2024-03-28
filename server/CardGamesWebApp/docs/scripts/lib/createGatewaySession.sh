#!/bin/bash
ALL_ARGS="$@"

function outputHelp {
	echo "$(tput setaf 2)Help$(tput sgr0):
		usage: $0 [-e|--env (tst|acc)] [-p|--person (UUID)] [-a|--accessprofile (UUID)] [-em|--employee (CORPKEY)] [-i|--issuer (ming|iris)]
		example:
			${0} tst --p 7a7f8db0-03d6-49b7-ab9e-8e6f5a39ad23 --a 48510cf5-8ff1-4ae1-97b8-ad28f846d898
			${0} acc --p 0eb504b9-fd0d-4a6d-ad44-5ee56946e8d2 --a c1a14ac5-e12b-4216-8200-384215e99e35 -i iris -em NA51RB

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
        -p|--person) CUSTOMER="${2}"; shift; shift; ;;
        -a|--accessprofile) PROFILE="${2}"; shift; shift; ;;
        -i|--issuer) ISSUER="${2}"; shift; shift; ;;
        -h|--help) outputHelp; exit 1; ;;
        -*|--*)
        echo "$(tput setaf 1)Invalid options$(tput sgr0), your input: ${0} ${ALL_ARGS}" >&2
      outputHelp; exit 1; ;;
      *) POSITIONAL+=("$1"); shift; ;;
  esac
done

set -- "${POSITIONAL[@]}" # restore positional parameters

function createGatewaySession {

    if [ -z ${ENV} ]; then
        echo "No env specified" >&2
        outputHelp
        exit 0
    fi

    BASE_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"
    COOKIE_FILE="${BASE_DIR}/cookies.txt"
    echo > ${COOKIE_FILE} #clear file.

    HOST_TO_CALL="$("${BASE_DIR}"/getGatewayHost.sh -e "${ENV}" -i "${ISSUER}")"


    ###### sets initial list of cookies including XSRF token
    if [[ ${ISSUER} = "iris" ]]; then
        INIT=$(curl -s --cookie-jar "${COOKIE_FILE}" --cookie "${COOKIE_FILE}" "https://${HOST_TO_CALL}")
        echo ".iris.ing.net	TRUE	/	TRUE	0	XSRF-TOKEN	xku-gt-kGIrhZ1AtP3MkU9g37Eu5oPGlzaMPIkl-nfPE-jSqn9QRk-iyE-K9OZNu" >> ${COOKIE_FILE}
    else
        INIT=$(curl -s --cookie-jar "${COOKIE_FILE}" --cookie "${COOKIE_FILE}" \
             "https://${HOST_TO_CALL}/login?profileId=${PROFILE}&personId=${CUSTOMER}&loa=5")
    fi


    ###### adds spin cookie to the cookie file. This should be added through request, but was missing.
    # (ensure this is separated by Tabs, and not spaces)
    if [[ ${ISSUER} = "iris" ]]; then
        echo ".iris.ing.net	TRUE	/	TRUE	0	spin	secure_random_value" >> ${COOKIE_FILE}
    else
        echo ".mijn.ing.nl	TRUE	/	TRUE	0	spin	secure_random_value" >> ${COOKIE_FILE}
    fi
}

createGatewaySession



