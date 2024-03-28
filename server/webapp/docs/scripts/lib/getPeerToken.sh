#!/bin/bash

if [ $# -ne 1 ]; then
  echo "usage: ${0} [Env (tst|acc|prd)]
        e.g.: ${0} tst" >&2
  exit 1
fi

ENV="${1}"
BASE_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" >/dev/null 2>&1 && pwd)"

function getPeerToken() {
    PEER_TOKEN_FILE=
    case "${ENV}" in
        [tst]*) PEER_TOKEN_FILE="${BASE_DIR}/../../../src/main/environments/tst/TST/properties/peer-token.jwt" ;;
        [acc]*) PEER_TOKEN_FILE="${BASE_DIR}/../../../src/main/environments/acc/shared/properties/peer-token.jwt" ;;
        [prd]*) PEER_TOKEN_FILE="${BASE_DIR}/../../../src/main/environments/prd/shared/properties/peer-token.jwt" ;;
        *) echo "Invalid environment provided. Should be [tst|acc|prd]" ;;
    esac
    
    if [ ! -f "${PEER_TOKEN_FILE}" ]; then
        echo "$(tput setaf 9)Could not find peerToken to use.$(tput sgr0)
            These should point to the location for the respective environment.
            Looking for file: ${PEER_TOKEN_FILE}" >&2
        exit 1
    fi
    cat "${PEER_TOKEN_FILE}"
}

getPeerToken
