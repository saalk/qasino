#!/bin/bash

if [ $# -ne 3 ]; then
    LOGIN_USER=$(echo "${USER}" | tr '[:upper:]' '[:lower:]')
    echo
    echo "Script that allows a call to the qualification checks
    Usage:
            $(tput setaf 2)${0} [tst|acc|prd] [personId (UUID)] [IBAN]$(tput sgr0)

       $(tput setaf 9)!! Tunnel not required !!!$(tput sgr0).
       Host are automatically being selected based on environment

       They found here for test: https://api.test.touchpoint.ing.net/services?serviceNames=CreditScoringService
       curl -v -k https://172-34-74-139.creditscr-tst.pod.dcr.non-prod.ichp.ing.net:8443/credit-score/scores
       nc -zv 172-34-74-139.creditscr-tst.pod.dcr.non-prod.ichp.ing.net 8443
       curl -v --tlsv1.2 --tls-max 1.2 GET https://172-34-74-139.creditscr-tst.pod.dcr.non-prod.ichp.ing.net:8443/isalive.aspx
       " >&2
    exit 1
fi

ENV="${1}"
REQUEST='{
 "transactionalScorecardRequest": {
 "productID": "124",
 "hasCoApplicant": "false",
 "loanApplicants": [
 {
 "customerUUID": "28b7e212-5675-4caf-b1e1-4bacd8da872b"
 }
 ]
 },
 "legolendRequest": {
 "loanProduct": "Creditcard",
 "mortgageIndicator": "false",
 "referenceID": "LA-123",
 "accountInformation": [
 {
 "accountNumber": "NL88INGB0003014263",
 "customerIdentifier": "222000003",
 "openingDate": "20160302",
 "closingDate": "20180302",
 "productType": "20",
 "accountType": "NL_BTLRKG"
 }
 ],
 "applicants": [
 {
 "customerIdentifier": "222000003",
 "dateOfBirth": "19850513",
 "lastName": "Binsbergen",
 "postalAddress": {
  "houseNumber": "10",
  "postalCode": "1829HV"
 }
 }
 ],
 "requestor": {
 "applicationID": "AP-05654",
 "initiatedChannel": "DIRECT_WEB",
 "participantNumber": "7001009",
 "systemName": "XCLAS",
 "userID": "TAMNUSER"
 }
 }
}'
#PERSON_ID="${2}"
#PROFILE_ID="${3}"
BASE_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" >/dev/null 2>&1 && pwd)"
LIB_DIR="${BASE_DIR}/../lib"
TST_CERTS_PATH="${BASE_DIR}/../tst"
ACC_CERTS_PATH="${BASE_DIR}/../acc"

function callCreditCardScoring() {

    CERTS_BASE_DIR="${1}"
    HOST="$("${LIB_DIR}"/getHosts.sh "${ENV}" CreditScoringService | jq -r '.[0]')"
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
     "https://${HOST}/credit-score/scores" \
     --header "Content-Type: application/json" \
     --header "X-ING-PeerToken: ${PEER_TOKEN}" \
     --header "Host: api.qasino.cloud" \
     -d "${REQUEST}"
}

# Execute call.
echo "REQUEST=
${REQUEST}" >&2
echo "RESPONSE = " >&2

case "${ENV}" in
    [tst]*) callCreditCardScoring "${TST_CERTS_PATH}" ;;
    [acc]*) callCreditCardScoring "${ACC_CERTS_PATH}" ;;
    [prd]*) echo "only test and acc for now" ;;
    *) echo "Invalid environment provided. Should be [tst|acc|prd]" ;;
esac
