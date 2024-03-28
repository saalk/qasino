
#!/bin/bash
COOKIE_FILE="${1}"

function getXsrfToken {
  XSRF_TOKEN_VALUE=$(cat "${COOKIE_FILE}" | grep XSRF-TOKEN)
#	echo XSRF_TOKEN_VALUE: ${XSRF_TOKEN_VALUE} >&2
  for i in ${XSRF_TOKEN_VALUE}; do
    RESULT_TOKEN=${i}
  done
  echo ${RESULT_TOKEN}
}

getXsrfToken





