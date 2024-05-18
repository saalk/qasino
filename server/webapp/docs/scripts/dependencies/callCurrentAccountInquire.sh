#!/bin/bash

if [ $# -ne 3 ]; then
    LOGIN_USER=$(echo "${USER}" | tr '[:upper:]' '[:lower:]')
    echo
    echo "Script that allows a call to the current account inquire
    Usage:
            $(tput setaf 2)${0} [tst|acc|prd] [personId (UUID)] [profileId (UUID)]$(tput sgr0)

    $(tput setaf 9)!! Tunnel not required !!!$(tput sgr0).
    Host are automatically being selected based on environment

    They found here for test: https://api.test.touchpoint.ing.net/services?serviceNames=CurrAcctsInqAPI" >&2
    exit 1
fi

ENV="${1}"
PERSON_ID="${2}"
PROFILE_ID="${3}"
BASE_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" >/dev/null 2>&1 && pwd)"
LIB_DIR="${BASE_DIR}/../lib"
TST_CERTS_PATH="${BASE_DIR}/../certs/tst"
ACC_CERTS_PATH="${BASE_DIR}/../certs/acc"

function callCurrentAccountInquire() {
  CERTS_BASE_DIR="${1}"
  HOST="$("${LIB_DIR}"/getHosts.sh "${ENV}" CurrAcctsInqAPI | jq -r '.[0]')"
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

  ACCESS_TOKEN="$("${LIB_DIR}"/getAccessToken.sh "${ENV}" "${PERSON_ID}" "${PROFILE_ID}" | jq -r '.[] | .unencrypted_access_token')"
  PEER_TOKEN="$("${LIB_DIR}"/getPeerToken.sh "${ENV}")"
  echo "calling: https://${HOST}/deposit-accounts/current-accounts/me" >&2
  curl -k -X GET --cert "${LOCATION_CERT_TLS_PUBLIC}" --key "${LOCATION_CERT_TLS_KEY}" --cacert "${LOCATION_CERT_TRUST}" \
    "https://${HOST}/deposit-accounts/current-accounts/me" \
        --header "Content-Type: application/json" \
        --header "X-ING-PeerToken: ${PEER_TOKEN}" \
        --header "X-ING-AccessToken: ${ACCESS_TOKEN}" \
        --header "Host: api.qasino.cloud"
}


# Execute call.
case "${ENV}" in
    [tst]*) callCurrentAccountInquire "${TST_CERTS_PATH}" ;;
    [acc]*) callCurrentAccountInquire "${ACC_CERTS_PATH}" ;;
    [prd]*) echo "only test and acc for now" ;;
    *) echo "Invalid environment provided. Should be [tst|acc|prd]" ;;
esac
