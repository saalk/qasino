#!/bin/bash
ALL_ARGS="$@"

function outputHelp {
	echo "$(tput setaf 2)Help$(tput sgr0):
		usage: $0 [-e|--env (tst|acc)] [-c|--cardid (4 digit)] [-r|--reason (DAMAGED_PLASTIC|EARLY_REISSUE)] [-p|--person (UUID)] [-a|--accessprofile (UUID)] [-em|--employee (CORPKEY)] [-i|--issuer (ming|iris)]
		example:
			${0} tst --p 7a7f8db0-03d6-49b7-ab9e-8e6f5a39ad23 --a 48510cf5-8ff1-4ae1-97b8-ad28f846d898
			${0} acc --p 0eb504b9-fd0d-4a6d-ad44-5ee56946e8d2 --a c1a14ac5-e12b-4216-8200-384215e99e35 -i iris -em NA51RB

		optional values:
	    			$(tput setaf 2) -e|--env $(tput sgr0): the environment to call [tst|acc|prd].
	    			$(tput setaf 2) -d|--reason $(tput sgr0): the reason from for the card replacement [DAMAGED_PLASTIC | EARLY_REISSUE].
	    			$(tput setaf 2) -c|--cardid $(tput sgr0): the last 4 digits of the credit card number.
	    			$(tput setaf 2) -p|--person $(tput sgr0): the id of the person to put in the token.
	    			$(tput setaf 2) -a|--accessprofile $(tput sgr0): the id of the profile to put in the token.
	    			$(tput setaf 2) -em|--employee $(tput sgr0): corp key of an employee to put in the access token. Will be put in executor field.
	    			$(tput setaf 2) -i|--issuer $(tput sgr0): the issuer to provide [ming|iris].
	    			$(tput setaf 2) -h|--help $(tput sgr0): print this help" >&2
}

# Filter through provided options.
while [[ $# -gt 0 ]]; do
  case "$1" in
        -e|--env) ENV="${2}"; shift; shift; ;;
        -i|--issuer) ISSUER="${2}"; shift; shift; ;;
        -h|--help) outputHelp; exit 1; ;;
        -*|--*)
        echo "$(tput setaf 1)Invalid options$(tput sgr0), your input: ${0} ${ALL_ARGS}" >&2
      outputHelp; exit 1; ;;
      *) POSITIONAL+=("$1"); shift; ;;
  esac
done

set -- "${POSITIONAL[@]}" # restore positional parameters

function getIrisHost {
    ENV="${1}"
    case "${ENV}" in
        [tst]*) echo "test.iris.ing.net" ;;
        [acc]*) echo "accp.iris.ing.net" ;;
        [prd]*) echo "only test and acc for now" ;;
        *) echo "Invalid environment provided. Should be [tst|acc|prd]" ;;
    esac
}

function getMingHost () {
    ENV="${1}"
    case "${ENV}" in
        [tst]*) echo "test.mijn.ing.nl" ;;
        [acc]*) echo "accp.mijn.ing.nl" ;;
        [prd]*) echo "only test and acc for now" ;;
        *) echo "Invalid environment provided. Should be [tst|acc|prd]" ;;
    esac
}

function getHost() {
    if [[ ${ISSUER} = "iris" ]]; then
        echo "$(getIrisHost "${ENV}")"
    else
        echo "$(getMingHost "${ENV}")"
    fi
}

getHost






