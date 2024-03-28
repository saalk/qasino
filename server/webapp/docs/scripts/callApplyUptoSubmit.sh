#!/bin/bash

PORT="15125"
HOST=localhost:"${PORT}"

if [ $# -ne 5 ]; then
    LOGIN_USER=$(echo "${USER}" | tr '[:upper:]' '[:lower:]')
    echo
    echo "Script that allows a call to the CreditCardsApplyCardService endpoint for \"consumer-credit-cards/requests/apply-card/initiate\"
    Usage:
            $(tput setaf 2)${0} [tst|acc|prd] [personId (UUID)] [profileId (UUID)] [ibanId (UUID)] [cardType (Creditcard/Platinumcard/Studentencard)] [limit] $(tput sgr0)

    $(tput setaf 9)!! Tunnel required !!!$(tput sgr0). Currently set on: ${HOST}.
        $(tput setaf 3) Test: $(tput sgr0)
            $(tput setaf 5)ssh -oProxyCommand=\"ssh -4 -W %h:%p ${LOGIN_USER}@jumphostdbnl.tunnel.ing.net -L ${PORT}:localhost:${PORT}\" -4 ${LOGIN_USER}@clrv0000276685.ic.ing.net -L ${PORT}:localhost:8093$(tput sgr0)
        $(tput setaf 3) Acc wpr red: $(tput sgr0)
            $(tput setaf 5)ssh -oProxyCommand=\"ssh -4 -W %h:%p ${LOGIN_USER}@jumphostdbnl.tunnel.ing.net -L ${PORT}:localhost:${PORT}\" -4 sba_credit03@clrv0000281666.ic.ing.net -L ${PORT}:localhost:8093$(tput sgr0)
    " >&2
    exit 1
fi

ENV="${1}"
PERSON_ID="${2}"
PROFILE_ID="${3}"

BASE_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" >/dev/null 2>&1 && pwd)"
LIB_DIR="${BASE_DIR}/lib"
TST_CERTS_PATH="${BASE_DIR}/tst/"
ACC_CERTS_PATH="${BASE_DIR}/acc/"

function callApplyUptoSubmit() {
  CERTS_BASE_DIR="${1}"

  LOCATION_CERT_TLS_KEY="${CERTS_BASE_DIR}/$(ls "${CERTS_BASE_DIR}" | grep .key)"
  LOCATION_CERT_TLS_PUBLIC="${CERTS_BASE_DIR}/$(ls "${CERTS_BASE_DIR}" | grep .cer)"
  LOCATION_CERT_TRUST=${BASE_DIR}/trust/intg3_b64.cer

  if [ ! -f "${LOCATION_CERT_TLS_KEY}" ] || [ ! -f "${LOCATION_CERT_TLS_PUBLIC}" ] || [ ! -f "${LOCATION_CERT_TRUST}" ]; then
      echo "Missing certificates for performing the call. Looking for files:
          - ${LOCATION_CERT_TLS_KEY}
          - ${LOCATION_CERT_TLS_PUBLIC}
          - ${LOCATION_CERT_TRUST}
      $(tput setaf 9)Exiting Now!$(tput sgr0)" >&2
      exit 1
  fi
  PEER_TOKEN="$("${LIB_DIR}"/getPeerToken.sh "${ENV}")"
  ACCESS_TOKEN="$("${LIB_DIR}"/getAccessToken.sh -e "${ENV}" -p "${PERSON_ID}" -a "${PROFILE_ID}" | jq -r '.[] | .unencrypted_access_token')"

  REQUEST='{"ibanUuid":"'"${4}"'"}'
  echo "calling: https://${HOST}/consumer-credit-cards/requests/apply-card/initiate" >&2
  ANSWER="$(curl -k -X POST --cert "${LOCATION_CERT_TLS_PUBLIC}" --key "${LOCATION_CERT_TLS_KEY}" --cacert "${LOCATION_CERT_TRUST}" \
    "https://${HOST}/consumer-credit-cards/requests/apply-card/initiate" \
        --header "Content-Type: application/json" \
        --header "X-ING-PeerToken: ${PEER_TOKEN}" \
        --header "X-ING-AccessToken: ${ACCESS_TOKEN}" \
        --header "Host: api.qasino.cloud" \
        -d "${REQUEST}")"
  echo
  echo "$(tput setaf 2) response of initiate: ${ANSWER} $(tput sgr0)"

  # null if not present
  REQUEST_ID=$(echo "${ANSWER}" | jq -r '.requestId')
  ERROR_CODE=$(echo "${ANSWER}" | jq -r '.errorCode')
  if [ "${REQUEST_ID}" == null ] || [ "${ERROR_CODE}" != null ]; then
    echo "Not continuing next call due to missing requestId or present errorCode
    $(tput setaf 9)Exiting Now!$(tput sgr0)" >&2
      exit 1
  fi

  REQUEST='{"cardType":"'"${5}"'"}'
  echo "calling: https://${HOST}/consumer-credit-cards/requests/apply-card/select-card-type" >&2
  ANSWER="$(curl -k -X PUT --cert "${LOCATION_CERT_TLS_PUBLIC}" --key "${LOCATION_CERT_TLS_KEY}" --cacert "${LOCATION_CERT_TRUST}" \
    "https://${HOST}/consumer-credit-cards/requests/apply-card/initiate" \
        --header "Content-Type: application/json" \
        --header "X-ING-PeerToken: ${PEER_TOKEN}" \
        --header "X-ING-AccessToken: ${ACCESS_TOKEN}" \
        --header "Host: api.qasino.cloud" \
        -d "${REQUEST}")"
  echo
  echo "$(tput setaf 2) response of select card type: ${ANSWER} $(tput sgr0)"

  # null if not present
  REQUEST_ID=$(echo "${ANSWER}" | jq -r '.requestId')
  ERROR_CODE=$(echo "${ANSWER}" | jq -r '.errorCode')
  if [ "${REQUEST_ID}" == null ] || [ "${ERROR_CODE}" != null ]; then
    echo "Not continuing next call due to missing requestId or present errorCode
    $(tput setaf 9)Exiting Now!$(tput sgr0)" >&2
      exit 1
  fi

  echo "calling: https://${HOST}/consumer-credit-cards/requests/apply-card/credit-score" >&2
  ANSWER="$(curl -k -X PUT --cert "${LOCATION_CERT_TLS_PUBLIC}" --key "${LOCATION_CERT_TLS_KEY}" --cacert "${LOCATION_CERT_TRUST}" \
    "https://${HOST}/consumer-credit-cards/requests/apply-card/credit-score" \
        --header "Content-Type: application/json" \
        --header "X-ING-PeerToken: ${PEER_TOKEN}" \
        --header "X-ING-AccessToken: ${ACCESS_TOKEN}" \
        --header "Host: api.qasino.cloud")"
  echo
  echo "$(tput setaf 2) response of credit score: ${ANSWER} $(tput sgr0)"

  # null if not present
  REQUEST_ID=$(echo "${ANSWER}" | jq -r '.requestId')
  ERROR_CODE=$(echo "${ANSWER}" | jq -r '.errorCode')
  if [ "${REQUEST_ID}" == null ] || [ "${ERROR_CODE}" != null ]; then
    echo "Not continuing next call due to missing requestId or present errorCode
    $(tput setaf 9)Exiting Now!$(tput sgr0)" >&2
      exit 1
  fi

    REQUEST='{"requestedCreditLimit":"'"${6}"'"}'
    echo "calling: https://${HOST}/consumer-credit-cards/requests/apply-card/credit-limit" >&2
    ANSWER="$(curl -k -X PUT --cert "${LOCATION_CERT_TLS_PUBLIC}" --key "${LOCATION_CERT_TLS_KEY}" --cacert "${LOCATION_CERT_TRUST}" \
      "https://${HOST}/consumer-credit-cards/requests/apply-card/credit-limit" \
          --header "Content-Type: application/json" \
          --header "X-ING-PeerToken: ${PEER_TOKEN}" \
          --header "X-ING-AccessToken: ${ACCESS_TOKEN}" \
          --header "Host: api.qasino.cloud" \
          -d "${REQUEST}")"
    echo
    echo "$(tput setaf 2) response of credit limit: ${ANSWER} $(tput sgr0)"

    # null if not present
    REQUEST_ID=$(echo "${ANSWER}" | jq -r '.requestId')
    ERROR_CODE=$(echo "${ANSWER}" | jq -r '.errorCode')
    if [ "${REQUEST_ID}" == null ] || [ "${ERROR_CODE}" != null ]; then
      echo "Not continuing next call due to missing requestId or present errorCode
      $(tput setaf 9)Exiting Now!$(tput sgr0)" >&2
        exit 1
    fi

  echo "calling: https://${HOST}/consumer-credit-cards/requests/apply-card/${REQUEST_ID}/submit" >&2
  ANSWER="$(curl -k -X PUT --cert "${LOCATION_CERT_TLS_PUBLIC}" --key "${LOCATION_CERT_TLS_KEY}" --cacert "${LOCATION_CERT_TRUST}" \
    "https://${HOST}/consumer-credit-cards/requests/apply-card/${REQUEST_ID}/submit" \
        --header "Content-Type: application/json" \
        --header "X-ING-PeerToken: ${PEER_TOKEN}" \
        --header "X-ING-AccessToken: ${ACCESS_TOKEN}" \
        --header "Host: api.qasino.cloud")"
    echo
    echo "$(tput setaf 2) response of submit: ${ANSWER} $(tput sgr0)"

}



# Execute call.
case "${ENV}" in
    [tst]*) callApplyUptoSubmit "${TST_CERTS_PATH}" ;;
    [acc]*) callApplyUptoSubmit "${ACC_CERTS_PATH}" ;;
    [prd]*) echo "only test and acc for now" ;;
    *) echo "Invalid environment provided. Should be [tst|acc|prd]" ;;
esac
