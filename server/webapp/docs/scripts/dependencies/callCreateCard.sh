#!/bin/bash

PORT="15113"
HOST=localhost:"${PORT}"

if [ $# -ne 1 ]; then
    LOGIN_USER=$(echo "${USER}" | tr '[:upper:]' '[:lower:]')
    echo
    echo "Script that allows a call to the CreateCard endpoint for \"/consumer-credit-cards/requests/create-card\"
    Usage:
            $(tput setaf 2)${0} [tst|acc|prd] $(tput sgr0)

    $(tput setaf 9)!! Tunnel required !!!$(tput sgr0). Currently set on: ${HOST}. e.g for tst:
            $(tput setaf 5)ssh -oProxyCommand=\"ssh -4 -W %h:%p ${LOGIN_USER}@jumphostdbnl.tunnel.ing.net -L ${PORT}:localhost:${PORT}\" -4 ${LOGIN_USER}@clrv0000276685.ic.ing.net -L ${PORT}:clrv0000237021.ic.ing.net:8088$(tput sgr0)
    Hosts (if incorrect) can be found here: https://api.test.touchpoint.ing.net/services?serviceNames=CreditCardsCreateCardAPI" >&2
    echo "in  line 16"
    exit 1
fi

ENV="${1}"

echo $ENV

REQUEST='{
           "correlationId": "7e3b3235-6194-4fee-a073-63d910de9351",
           "applicationName": "APPLY_CARD_SERVICE",
           "legalEntity": "1079",
           "cardType": "CREDITCARD",
           "customerId": "2719d7bb-ae5c-4247-bcd7-ffd9a209a6bb",
           "beneficiaryId": null,
           "currentAccount": { "accountType": "iban", "accountNumber": "NL53INGB0686583981" },
           "portfolioCode": "CREDITCARD_CHARGE",
           "isStudentCard": false,
           "createCardOperation": "REGULAR",
           "packageType": "ORANJEPAKKET",
           "limit": "1000",
           "creditcardArrangement":"",
            "creditcardNumber":""
}
'


echo $REQUEST
echo

BASE_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" >/dev/null 2>&1 && pwd)"
LIB_DIR="${BASE_DIR}/../lib"
TST_CERTS_PATH="${BASE_DIR}/../tst/"
ACC_CERTS_PATH="${BASE_DIR}/../acc/"

function callCreateCard() {
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

    echo "line 68 :  before request"
    curl -X POST -k --cert "${LOCATION_CERT_TLS_PUBLIC}" --key "${LOCATION_CERT_TLS_KEY}" --cacert "${LOCATION_CERT_TRUST}" \
     "https://${HOST}/consumer-credit-cards/requests/create-card" \
     --header "Content-Type: application/json" \
     --header "X-ING-PeerToken: ${PEER_TOKEN}" \
     --header "Host: api.qasino.cloud" \
     -d "${REQUEST}"

}

# Execute call.
case "${ENV}" in
    [tst]*) callCreateCard "${TST_CERTS_PATH}" ;;
    [acc]*) callCreateCard "${ACC_CERTS_PATH}" ;;
    [prd]*) echo "only test and acc for now" ;;
    *) echo "Invalid environment provided. Should be [tst|acc|prd]" ;;
esac
echo