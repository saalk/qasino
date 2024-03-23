#!/bin/bash
# Script to switch the existing mode of the application. The available modes should be:
#       - live                  -> Api should return KEEPALIVE_OK from endpoint
#       - confidence_checking   -> Api should return UNAVAILABLE from endpoint
#       - maintenance           -> Api should return UNAVAILABLE from endpoint


BASE_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"
MODE_FILE="${BASE_DIR}/mode"
if [ ! -f "${MODE_FILE}" ]; then
    echo "Could not find mode file, Exiting script"
    exit 1
fi
MODE="$(cat "${MODE_FILE}")"
echo "Current mode is set to: ${MODE}"

# Available options
AVAILABLE_MODES=("live" "confidence_checking" "maintenance")
echo "Select a new mode:"
for (( i = 1; i -1 < ${#AVAILABLE_MODES[@]}; i++ )); do
    echo "	$(tput setaf 2)${i}$(tput sgr0) = ${AVAILABLE_MODES[(($i - 1))]}" >&2
done
read -r -p "Select a variable [number]: " SELECTED_OUTPUT


# input Validations
NUMBER_REGEX='^[0-9]+$'
if ! [[ ${SELECTED_OUTPUT} =~ ${NUMBER_REGEX} ]] ; then
    echo "error: Not a number" >&2;
    exit 1
fi
if [[ ${SELECTED_OUTPUT} > ${#AVAILABLE_MODES[@]} ]]; then
    echo "Input cannot be bigger then the available size ${#AVAILABLE_MODES[@]}"
    exit 1
fi
RESULT="${AVAILABLE_MODES[((${SELECTED_OUTPUT} - 1))]}"
if [[ -z ${RESULT} ]]; then
    echo "Result is empty, Selected value was probably invalid: ${SELECTED_OUTPUT}"
    exit 1
fi


# set Result
echo "SELECTED_OUTPUT: ${RESULT}" >&2
echo "${RESULT}" > "${MODE_FILE}"

echo "Done"