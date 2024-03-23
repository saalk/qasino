#!/bin/bash

if [ $# -ne 2 ]; then
  echo "usage: ${0} [Env (tst|acc|prd)] [serviceName]
        e.g.: ${0} CreditCardsTestCleanerService" >&2
  exit 1
fi

ENV="${1}"
SERVICE_NAME="${2}"

function callServiceDiscovery() {
    echo $(curl -s "https://api${1}touchpoint.ing.net/services?serviceNames=${2}" | jq '[.services[].versionInstances[].instances[].host + ":" + (.services[].versionInstances[].instances[].port | tostring)]')
}


case "${ENV}" in
  [tst]*) callServiceDiscovery .test. ${SERVICE_NAME} ;;
  [acc]*) callServiceDiscovery .accp. ${SERVICE_NAME} ;;
  [prd]*) callServiceDiscovery . ${SERVICE_NAME} ;;
  *) echo "Invalid environment provided. Should be [tst|acc|prd]" ;;
esac
