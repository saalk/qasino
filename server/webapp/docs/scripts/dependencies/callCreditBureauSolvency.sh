#!/bin/bash

PORT="15118"
HOST=localhost:"${PORT}"

OUR_HOST_ON_TST_RHEL8="clrv0000276685.ic.ing.net"
OUR_HOST_ON_TST_RHEL7="clrv0000195182.ic.ing.net"

BKR_HOST_ON_TST_WPR="clrv0000185686.ic.ing.net:8084"
BKR_URL_ON_ACC_WPR="clrv0000185756.ic.ing.net:8084"
BKR_URL_ON_ACC_DCR="clrv0000187474.ic.ing.net:8084"

if [ $# -ne 2 ]; then
    LOGIN_USER=$(echo "${USER}" | tr '[:upper:]' '[:lower:]')
    echo
    echo "Script that allows a call to the CreditBureauSolvency (BKR) endpoint for call to /credit-bureau/nl/solvency
    Usage:
            $(tput setaf 2)${0} [tst|acc|prd] [personId (UUID)]$(tput sgr0)

    $(tput setaf 9)!! Tunnel required !!!$(tput sgr0). Currently set on: ${HOST}.
        $(tput setaf 3) Test: $(tput sgr0)
            $(tput setaf 5)ssh -oProxyCommand=\"ssh -4 -W %h:%p ${LOGIN_USER}@jumphostdbnl.tunnel.ing.net -L ${PORT}:localhost:${PORT}\" -4 ${LOGIN_USER}@${OUR_HOST_ON_TST_RHEL8} -L ${PORT}:${BKR_HOST_ON_TST_WPR}$(tput sgr0)
        $(tput setaf 3) Acc wpr red: $(tput sgr0)
            $(tput setaf 5)ssh -oProxyCommand=\"ssh -4 -W %h:%p ${LOGIN_USER}@jumphostdbnl.tunnel.ing.net -L ${PORT}:localhost:${PORT}\" -4 sba_credit06@clrv0000252858.ic.ing.net -L ${PORT}:${BKR_HOST_ON_ACC_WPR}$(tput sgr0)

    Hosts (if incorrect) can be found here: https://api.test.touchpoint.ing.net/services?serviceNames=CreditBureauService" >&2
    exit 1
fi

## https://test.mijn.ing.nl/login/?profileId=63786f06-600f-480d-851d-42622459ab36&personId=a1839d67-f29a-4c7a-aaf3-cbad4b72ccd1&loa=5&redirectUrl=/banking
## rgb 166729817 iban NL28INGB0757463495
ENV="${1}"
TST_REQUEST='{ "mainApplicant": { "internalIdentifier": { "type": "UUID", "value":
"f6d463fe-3a0a-4491-9b28-6b0fca673249" } } }'
#ACC_REQUEST='{ "mainApplicant": { "internalIdentifier": { "type": "UUID", "value": "'"${2}"'" } } }'
ACC_REQUEST='{
               "mainApplicant" : {
                 "lastName": "ORWELL",
                 "dateOfBirth": "1984-01-01",
                 "houseNumber": "225",
                 "postalCode": "1111AA"
               },
               "partner": {
                 "lastName": "ORWELL",
                 "dateOfBirth": "1984-01-01",
                 "houseNumber": "225",
                 "postalCode": "1111AA"
               }
             }'



BASE_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" >/dev/null 2>&1 && pwd)"
LIB_DIR="${BASE_DIR}/../lib"
TST_CERTS_PATH="${BASE_DIR}/../tst"
ACC_CERTS_PATH="${BASE_DIR}/../acc"


function callCreditBureauSolvency() {
    CERTS_BASE_DIR="${1}"

    LOCATION_CERT_TLS_KEY="${CERTS_BASE_DIR}/$(ls "${CERTS_BASE_DIR}" | grep .key)"
    LOCATION_CERT_TLS_PUBLIC="${CERTS_BASE_DIR}/$(ls "${CERTS_BASE_DIR}" | grep .cer)"
    LOCATION_CERT_TRUST=${BASE_DIR}/../trust/intg3_b64.cer

    if [ ! -f "${LOCATION_CERT_TLS_KEY}" ] || [ ! -f "${LOCATION_CERT_TLS_PUBLIC}" ] || [ ! -f "${LOCATION_CERT_TRUST}" ]; then
        echo "Missing certificates for performing the call. Looking for files:
            - ${LOCATION_CERT_TLS_KEY}
            - ${LOCATION_CERT_TLS_PUBLIC}
            - ${LOCATION_CERT_TRUST}
        $(tput setaf 9)Exiting Now!$(tput sgr0)" >&2
        exit 1
    fi

    PEER_TOKEN="$("${LIB_DIR}"/getPeerToken.sh "${ENV}")"
    curl -X POST -k -vvv --cert "${LOCATION_CERT_TLS_PUBLIC}" --key "${LOCATION_CERT_TLS_KEY}" --cacert "${LOCATION_CERT_TRUST}" \
        "https://${HOST}/credit-bureau/nl/solvency?option=agreements&option=noevidence" \
            --header "Content-Type: application/json" \
            --header "X-ING-PeerToken: ${PEER_TOKEN}" \
            --header "Host: api.qasino.cloud" \
            -d "${ACC_REQUEST}"

}


# Execute call. You can use old api name still  "X-CB-ApplicationId: pCreditcardsApplyCardAPI"
case "${ENV}" in
    [tst]*) callCreditBureauSolvency "${TST_CERTS_PATH}" ;;
    [acc]*) callCreditBureauSolvency "${ACC_CERTS_PATH}" ;;
    [prd]*) echo "only test and acc for now" ;;
    *) echo "Invalid environment provided. Should be [tst|acc|prd]" ;;
esac
echo