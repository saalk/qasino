#!/bin/bash
#ALL_ARGS="$@"

function outputHelp {
	echo "$(tput setaf 2)Help$(tput sgr0):
		usage: $0 [-e|--env (tst|acc)] [-b|--ibanId (UUID)] [-c|--cardType (Creditcard/Platinumcard/Studentencard)] [-l|--limit] [-p|--person (UUID)] [-a|--accessprofile (UUID)] [-i|--issuer (ming|iris)] [-em|--employee (CORPKEY)] [-r|--rgb]
		example:
			${0} -e tst -b c1a14ac5-e12b-4216-8200-384215e99e35 -c Creditcard -l 1000 -p 7a7f8db0-03d6-49b7-ab9e-8e6f5a39ad23 -a 48510cf5-8ff1-4ae1-97b8-ad28f846d898
			${0} -e acc -b c1a14ac5-e12b-4216-8200-384215e99e35 -c Creditcard -l 1000 -p 7a7f8db0-03d6-49b7-ab9e-8e6f5a39ad23 -a c1a14ac5-e12b-4216-8200-384215e99e35 -i iris -em NA51RB -r 122777824

		optional values:
	    			$(tput setaf 2) -e|--env $(tput sgr0): the environment to call [tst|acc|prd].
	    			$(tput setaf 2) -b|--ibanId $(tput sgr0): UUID of the iban to create the card on.
	    			$(tput setaf 2) -c|--cardType $(tput sgr0): type of requested card on [Creditcard|Platinumcard|Studentencard].
	    			$(tput setaf 2) -l|--limit $(tput sgr0): the limit of the requested.
	    			$(tput setaf 2) -p|--person $(tput sgr0): the id of the person to put in the token.
	    			$(tput setaf 2) -a|--accessprofile $(tput sgr0): the id of the profile to put in the token.
	    			$(tput setaf 2) -i|--issuer $(tput sgr0): the issuer to provide [ming|iris].
	    			$(tput setaf 2) -em|--employee $(tput sgr0): corp key of an employee to put in the access token. Will be put in executor field. (only required for iris).
	    			$(tput setaf 2) -r|--rgb $(tput sgr0): the id of the person in legacy format (only required for iris).
	    			$(tput setaf 2) -h|--help $(tput sgr0): print this help" >&2
}

# Filter through provided options.
while [[ $# -gt 0 ]]; do
	case "$1" in
        -e|--env) ENV="${2}"; shift; shift; ;;
        -b|--ibanId) IBAN_ID="${2}"; shift; shift; ;;
        -c|--cardType) CARD_TYPE="${2}"; shift; shift; ;;
        -l|--limit) LIMIT="${2}"; shift; shift; ;;
        -p|--person) CUSTOMER="${2}"; shift; shift; ;;
        -a|--accessprofile) PROFILE="${2}"; shift; shift; ;;
        -i|--issuer) ISSUER="${2}"; shift; shift; ;;
        -em|--employee) EMPLOYEE="${2}"; shift; shift; ;;
        -r|--rgb) RGB="${2}"; shift; shift; ;;
        -h|--help) outputHelp; exit 1; ;;
        -*|--*)
		    echo "$(tput setaf 1)Invalid options$(tput sgr0), your input: ${0} ${ALL_ARGS}" >&2
			outputHelp; exit 1; ;;
	    *) POSITIONAL+=("$1"); shift; ;;
	esac
done

set -- "${POSITIONAL[@]}" # restore positional parameters

if [ -z ${ENV} ]; then
    echo "No env specified" >&2
    outputHelp
    exit 0
fi

BASE_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"
LIB_DIR="${BASE_DIR}/lib"

###### Setup session to call though gateway
SESSION="$("${LIB_DIR}"/createGatewaySession.sh -e "${ENV}" -p "${CUSTOMER}" -a "${PROFILE}" -i "${ISSUER}")"

API_HOST_TO_CALL="api."$("${LIB_DIR}"/getGatewayHost.sh -e "${ENV}" -i "${ISSUER}")""
COOKIE_FILE="${LIB_DIR}/cookies.txt"
ACCESS_TOKEN="$("${LIB_DIR}/getAccessToken.sh" \
            -e "${ENV}" \
            -em "${EMPLOYEE}" \
            -p "${CUSTOMER}" \
            -a "${PROFILE}" \
            -i "${ISSUER}"  | jq -r '.[] | .access_token')"

##### Update iris cache due to current account inquire dependency using legacy
if [[ ${ISSUER} = "iris" ]]; then
    if [ -z "${RGB}" ] || [ -z "${EMPLOYEE}" ]; then
      echo "Not continuing next call due to missing input for iris
      $(tput setaf 9)Exiting Now!$(tput sgr0)" >&2
        exit 1
    fi
    SESSION="$("${LIB_DIR}/updateAssistedContextCache.sh" \
            -e "${ENV}" \
            -r "${RGB}" \
            -t "${ACCESS_TOKEN}" \
            -i "${ISSUER}")"
#    echo ${SESSION}
fi


###### Call application
URL="https://${API_HOST_TO_CALL}/consumer-credit-cards/requests/apply-card/initiate"
REQUEST='{"ibanUuid":"'"${IBAN_ID}"'"}'
echo "calling: ${URL} with ${REQUEST}"
ANSWER=$(curl -X POST -k ${URL} \
		    --cookie-jar "${COOKIE_FILE}" --cookie "${COOKIE_FILE}" \
        --header "Content-Type: application/json" \
        --header "Authorization: Bearer ${ACCESS_TOKEN}" \
      	--header "X-XSRF-TOKEN: $("${LIB_DIR}"/getXsrfToken.sh "${COOKIE_FILE}")" \
      	-d "${REQUEST}")

echo ANSWER:
echo "${ANSWER}" | sed -e "s/^)]}',//" |jq


REQUEST_ID=$(echo "${ANSWER}" | jq -r '.requestId')
ERROR_CODE=$(echo "${ANSWER}" | jq -r '.errorCode')
if [ "${REQUEST_ID}" == null ] || [ "${ERROR_CODE}" != null ]; then
  echo "Not continuing next call due to missing requestId or present errorCode
  $(tput setaf 9)Exiting Now!$(tput sgr0)" >&2
    exit 1
fi

URL="https://${API_HOST_TO_CALL}/consumer-credit-cards/requests/apply-card/${REQUEST_ID}/select-card-type"
REQUEST='{"cardType":"'"${CARD_TYPE}"'"}'
echo "calling: ${URL} with ${REQUEST}"
ANSWER=$(curl -X PUT -k ${URL} \
		    --cookie-jar "${COOKIE_FILE}" --cookie "${COOKIE_FILE}" \
        --header "Content-Type: application/json" \
        --header "Authorization: Bearer ${ACCESS_TOKEN}" \
      	--header "X-XSRF-TOKEN: $("${LIB_DIR}"/getXsrfToken.sh "${COOKIE_FILE}")" \
      	-d "${REQUEST}")

echo ANSWER:
echo "${ANSWER}" | sed -e "s/^)]}',//" |jq


REQUEST_ID=$(echo "${ANSWER}" | jq -r '.requestId')
ERROR_CODE=$(echo "${ANSWER}" | jq -r '.errorCode')
if [ "${REQUEST_ID}" == null ] || [ "${ERROR_CODE}" != null ]; then
  echo "Not continuing next call due to missing requestId or present errorCode
  $(tput setaf 9)Exiting Now!$(tput sgr0)" >&2
    exit 1
fi

URL="https://${API_HOST_TO_CALL}/consumer-credit-cards/requests/apply-card/${REQUEST_ID}/credit-score"
echo "calling: ${URL} without request body"
ANSWER=$(curl -X PUT -k ${URL} \
		    --cookie-jar "${COOKIE_FILE}" --cookie "${COOKIE_FILE}" \
        --header "Content-Type: application/json" \
        --header "Authorization: Bearer ${ACCESS_TOKEN}" \
      	--header "X-XSRF-TOKEN: $("${LIB_DIR}"/getXsrfToken.sh "${COOKIE_FILE}")")

echo ANSWER:
echo "${ANSWER}" | sed -e "s/^)]}',//" |jq

REQUEST_ID=$(echo "${ANSWER}" | jq -r '.requestId')
ERROR_CODE=$(echo "${ANSWER}" | jq -r '.errorCode')
if [ "${REQUEST_ID}" == null ] || [ "${ERROR_CODE}" != null ]; then
  echo "Not continuing next call due empty response or empty referenceId
  $(tput setaf 9)Exiting Now!$(tput sgr0)" >&2
    exit 1
fi


URL="https://${API_HOST_TO_CALL}/consumer-credit-cards/requests/apply-card/${REQUEST_ID}/credit-limit"
REQUEST='{"requestedCreditLimit":"'"${LIMIT}"'"}'
echo "calling: ${URL} with ${REQUEST}"
ANSWER=$(curl -X PUT -k ${URL} \
		    --cookie-jar "${COOKIE_FILE}" --cookie "${COOKIE_FILE}" \
        --header "Content-Type: application/json" \
        --header "Authorization: Bearer ${ACCESS_TOKEN}" \
      	--header "X-XSRF-TOKEN: $("${LIB_DIR}"/getXsrfToken.sh "${COOKIE_FILE}")" \
      	-d "${REQUEST}")

echo ANSWER:
echo "${ANSWER}" | sed -e "s/^)]}',//" |jq


REQUEST_ID=$(echo "${ANSWER}" | jq -r '.requestId')
ERROR_CODE=$(echo "${ANSWER}" | jq -r '.errorCode')
if [ "${REQUEST_ID}" == null ] || [ "${ERROR_CODE}" != null ]; then
  echo "Not continuing next call due to missing requestId or present errorCode
  $(tput setaf 9)Exiting Now!$(tput sgr0)" >&2
    exit 1
fi

URL="https://${API_HOST_TO_CALL}/consumer-credit-cards/requests/apply-card/${REQUEST_ID}/submit"
echo "calling: ${URL} without request body"
ANSWER=$(curl -X PUT -k ${URL} \
		    --cookie-jar "${COOKIE_FILE}" --cookie "${COOKIE_FILE}" \
        --header "Content-Type: application/json" \
        --header "Authorization: Bearer ${ACCESS_TOKEN}" \
      	--header "X-XSRF-TOKEN: $("${LIB_DIR}"/getXsrfToken.sh "${COOKIE_FILE}")")

echo ANSWER:
echo "${ANSWER}" | sed -e "s/^)]}',//" |jq


