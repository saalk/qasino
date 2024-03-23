#!/bin/bash

PORT="15126"
HOST=localhost:"${PORT}"
# XLCAS host names on TEST OLD not used any more
TPA_URL_ON_TEST_DCR="clrv0000242864.ic.ing.net:8091"
TPA_URL_ON_TEST_WPR="clrv0000242865.ic.ing.net:8091"

# XLCAS host names after migratino to tomccat 9
TPA_NEW_URL_ON_TEST_DCR="clrv0000279240.ic.ing.net:8091"
#TPA_NEW_URL_ON_TEST_DCR="test.xclas.ing:8091" # just a try if this works
TPA_NEW_URL_ON_TEST_WPR="clrv0000279241.ic.ing.net:8091"


# XLCAS host names on ACCEPTANCE OLD before 2023-10-23
TPA_URL_ON_ACC_DCR="clrv0000243884.ic.ing.net:8091"
TPA_URL_ON_ACC_WPR="clrv0000243886.ic.ing.net:8091"

# XLCAS host names after migratino to tomccat 9
TPA_NEW_URL_ON_ACC_DCR1="clrv0000243884.ic.ing.net:8091"
TPA_NEW_URL_ON_ACC_WPR1="clrv0000243885.ic.ing.net:8091"
TPA_NEW_URL_ON_ACC_DCR2="clrv0000243886.ic.ing.net:8091"
TPA_NEW_URL_ON_ACC_WPR2="clrv0000243887.ic.ing.net:8091"

# we are deployed on TST clrv0000237020     MERAK = clrv0000195182
# we are deployed on ACC WPR clrv0000237049 MERAK WPR RED = clrv0000209340

if [ $# -ne 2 ]; then
    LOGIN_USER=$(echo "${USER}" | tr '[:upper:]' '[:lower:]')
    echo
    echo "Script that allows a call to XCLAS Financial Acceptance TPA on our riaf
    Usage:
            $(tput setaf 2)${0} [tst|acc|prd] [RGB/dummy]$(tput sgr0)

    $(tput setaf 9)!! Tunnel required !!!$(tput sgr0). Currently set on: ${HOST}.
        $(tput setaf 3) Test: $(tput sgr0)
            $(tput setaf 5)ssh -oProxyCommand=\"ssh -4 -W %h:%p ${LOGIN_USER}@jumphostdbnl.tunnel.ing.net -L ${PORT}:localhost:${PORT}\" -4 ${LOGIN_USER}@clrv0000276685.ic.ing.net -L ${PORT}:${TPA_NEW_URL_ON_TEST_DCR}$(tput sgr0)
        $(tput setaf 3) Acc wpr red: $(tput sgr0)
            $(tput setaf 5)ssh -oProxyCommand=\"ssh -4 -W %h:%p ${LOGIN_USER}@jumphostdbnl.tunnel.ing.net -L ${PORT}:localhost:${PORT}\" -4 sba_credit05@clrv0000252858.ic.ing.net -L ${PORT}:${TPA_NEW_URL_ON_ACC_DCR1}$(tput sgr0)

    Hosts (if incorrect) can be found here: https://api.test.touchpoint.ing.net/services?serviceNames=xclas-orchestration-api

    curl -v -k https://clrv0000279240.ic.ing.net:8091/prweb/PRRestService/dbzconsumerloans/loanapplicationtpa/FinancialAcceptance/1079/124
    nc -zv clrv0000242864.ic.ing.net 8091
    curl -v --tlsv1.2 --tls-max 1.2 GET https://172-35-81-36.cla-npd-tst.pod.dcr.non-prod.ichp.ing.net:8443/isalive.aspx" >&2

    echo

    exit 1

fi

ENV="${1}"
TEST_REQUEST_GOOD='{"version": "1",
            "correlationId": "456f359a-a602-45df-bbc1-046a5393a3km",
            "applicationName": "pCreditcardsApplyCardAPI",
            "operationCode": "007",
            "operationMode": "quick",
            "applicants": [
              {
                "role": "11",
                "dateOfBirth": "1972-01-28",
                "lastName": "P. tester",
                "houseNumber": "14",
                "postalCode": "1234PD",
                "Id": "79670123",
                "Type": "RGB"
              }
            ],
            "accountDetails": {
              "accountNumber": "NL21INGB0007590123",
              "accountType": "IBAN"
            }
}'

## https://test.mijn.ing.nl/login/?profileId=63786f06-600f-480d-851d-42622459ab36&personId=a1839d67-f29a-4c7a-aaf3-cbad4b72ccd1&loa=5&redirectUrl=/banking
## rgb 166729817 iban NL28INGB0757463495
REQUEST_BKR_AND_SCORING='{
"version": "1",
"correlationId": "123f359a-a602-45df-bbc1-046a5393a3km",
"applicationName": "pCreditcardsApplyCardAPI",
"operationCode": "203",
"operationMode": "thorough",
"applicants": [
 {
   "role": "11",
   "dateOfBirth": "1972-01-28",
   "lastName": "P. tester",
   "houseNumber": "14",
   "postalCode": "1234PD",
   "Id": "f6d463fe-3a0a-4491-9b28-6b0fca673249",
   "Type": "UUID"
 }
],
"accountDetails": {
 "accountNumber": "NL21INGB0007590123",
 "accountType": "IBAN"
}
}'
REQUEST_BKR_AND_SCORING_WITH_RGB='{
"version": "1",
"correlationId": "123f359a-a602-45df-bbc1-046a5393a3km",
"applicationName": "pCreditcardsApplyCardAPI",
"operationCode": "202",
"operationMode": "quick",
"applicants": [
 {
   "role": "11",
   "dateOfBirth": "1972-01-28",
   "lastName": "P. tester",
   "houseNumber": "14",
   "postalCode": "1234PD",
   "Id": "146551184",
   "Type": "RGB"
 }
],
"accountDetails": {
 "accountNumber": "NL71INGB0756414539",
 "accountType": "IBAN"
}
}'
TEST=TEST_REQUEST_GOOD
#TEST=REQUEST_BKR_AND_SCORING
#TEST=REQUEST_BKR_AND_SCORING_WITH_RGB

BASE_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" >/dev/null 2>&1 && pwd)"
LIB_DIR="${BASE_DIR}/../lib"
TST_CERTS_PATH="${BASE_DIR}/../tst"
ACC_CERTS_PATH="${BASE_DIR}/../acc"

function callXCLAS-FA-tpa() {
    CERTS_BASE_DIR="${1}"

    LOCATION_CERT_TLS_KEY="${CERTS_BASE_DIR}/$(ls "${CERTS_BASE_DIR}" | grep .key)"
    LOCATION_CERT_TLS_PUBLIC="${CERTS_BASE_DIR}/$(ls "${CERTS_BASE_DIR}" | grep .cer)"
    LOCATION_CERT_TRUST=${BASE_DIR}/../trust/intg3_b64.cer

    if [ ! -f "${LOCATION_CERT_TLS_KEY}" ] ; then
        echo "Missing certificates for performing the call. Looking for files LOCATION_CERT_TLS_KEY:
            - ${LOCATION_CERT_TLS_KEY}
        $(tput setaf 9)Exiting Now!$(tput sgr0)" >&2
        exit 1
    fi

    if [ ! -f "${LOCATION_CERT_TLS_PUBLIC}" ]; then
        echo "Missing certificates for performing the call. Looking for files LOCATION_CERT_TLS_PUBLIC:
            - ${LOCATION_CERT_TLS_PUBLIC}
        $(tput setaf 9)Exiting Now!$(tput sgr0)" >&2
        exit 1
    fi

    if [ ! -f "${LOCATION_CERT_TRUST}" ]; then
        echo "Missing certificates for performing the call. Looking for files LOCATION_CERT_TRUST:
            - ${LOCATION_CERT_TRUST}
        $(tput setaf 9)Exiting Now!$(tput sgr0)" >&2
        exit 1
    fi

    PEER_TOKEN="$("${LIB_DIR}"/getPeerToken.sh "${ENV}")"

    ## OLD https://clrv0000242864.ic.ing.net:8091/prweb/PRRestService/dbzconsumerloans/loanapplication/FinancialAcceptance/1079/124
    ## NEW https://clrv0000242864.ic.ing.net:8091/prweb/PRRestService/dbzconsumerloanstpa/loanapplication/FinancialAcceptance/1079/124

    echo "Sending  request : ${TEST_REQUEST_GOOD} " >&2

    PTH="/prweb/PRRestService/dbzconsumerloanstpa/loanapplication/FinancialAcceptance/1079/124"
    curl -X POST -k -v --cert "${LOCATION_CERT_TLS_PUBLIC}" --key "${LOCATION_CERT_TLS_KEY}" --cacert "${LOCATION_CERT_TRUST}" \
        "https://${HOST}${PTH}" \
            --header "Content-Type: application/json" \
            --header "X-ING-PeerToken: ${PEER_TOKEN}" \
            --header "Host: api.qasino.cloud" \
            -d "${TEST}"
    echo
}

# Execute call.
case "${ENV}" in
    [tst]*) callXCLAS-FA-tpa "${TST_CERTS_PATH}" ;;
    [acc]*) callXCLAS-FA-tpa "${ACC_CERTS_PATH}" ;;
    [prd]*) echo "only test and acc for now" ;;
    *) echo "Invalid environment provided. Should be [tst|acc|prd]" ;;
esac
echo
