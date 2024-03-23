#!/bin/bash

PORT="15112"
HOST=localhost:"${PORT}"

if [ $# -ne 2 ]; then
    LOGIN_USER=$(echo "${USER}" | tr '[:upper:]' '[:lower:]')
    echo
    echo "Script that allows a call to the ConnectDOTAPI endpoint for \"/message/send/with-response\"
    Usage:
            $(tput setaf 2)${0} [tst|acc|prd] [accountNumber (2100xxx)]$(tput sgr0)

    $(tput setaf 9)!! Tunnel required !!!$(tput sgr0). Currently set on: ${HOST}. e.g for tst:
            $(tput setaf 5)ssh -oProxyCommand=\"ssh -4 -W %h:%p ${LOGIN_USER}@jumphostdbnl.tunnel.ing.net -L ${PORT}:localhost:${PORT}\" -4 ${LOGIN_USER}@clrv0000195182.ic.ing.net -L ${PORT}:clrv0000172468.ic.ing.net:8081$(tput sgr0)
    Hosts (if incorrect) can be found here: https://api.test.touchpoint.ing.net/services?serviceNames=ConnectDOTAPI" >&2
    exit 1
fi

ENV="${1}"
REQUEST='{
  "identifiers": {
    "requestId": "'$(uuidgen)'",
  },
  "metaData": {
    "feedback": {
      "type": "email",
      "trigger": "on-error"
    },
    "entityCode": "1079",
    "costCenter": "B3735500",
    "logicalDataDomain": "nl",
    "initiatingCI": "CreditCardsApplyCardService"
  },
  "subject": {
    "type": "agreement",
    "identifierType": "63",
    "identifierValue": "M'${2}'"
  },
  "recipient": {
    "type": "agreement-relationship",
    "roleTypes": [
      "11"
    ]
  },
  "channels": [
    {
      "destinations": [
        {
          "type": "inbox",
          "templateName": "A-CCRCR03",
          "output": "A-CCR_OutputProfile"
        },
        {
          "type": "paper",
          "templateName": "A-CCRCR03",
          "output": "A-CCR_OutputProfile"
        }
      ]
    }
  ],
  "payload": {
    "DocumentData": {
      "CodeDeliveryPeriod": 10,
      "CreditCardNumber": "1234.****.****.1111",
      "ReferencedAccountNumber": "NL89INGB0757396534"
    }
  }
}
'

echo $REQUEST
echo


BASE_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" >/dev/null 2>&1 && pwd)"
LIB_DIR="${BASE_DIR}/../lib"
TST_CERTS_PATH="${BASE_DIR}/../tst/"
ACC_CERTS_PATH="${BASE_DIR}/../acc/"


function callConnectDot() {
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
    curl -X POST -k --cert "${LOCATION_CERT_TLS_PUBLIC}" --key "${LOCATION_CERT_TLS_KEY}" --cacert "${LOCATION_CERT_TRUST}" \
     "https://${HOST}/message/send/with-response" \
     --header "Content-Type: application/json" \
     --header "X-ING-PeerToken: ${PEER_TOKEN}" \
     --header "Host: api.qasino.cloud" \
     -d "${REQUEST}"
}


# Execute call.
case "${ENV}" in
    [tst]*) callConnectDot "${TST_CERTS_PATH}" ;;
    [acc]*) callConnectDot "${ACC_CERTS_PATH}" ;;
    [prd]*) echo "only test and acc for now" ;;
    *) echo "Invalid environment provided. Should be [tst|acc|prd]" ;;
esac
echo