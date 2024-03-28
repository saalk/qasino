#!/bin/bash

if [ $# -ne 2 ]; then
    echo
    echo "Script to decrypt a single password via Jasypt.'
    Usage:
            $(tput setaf 2)${0} [tst|acc|prd] [password]$(tput sgr0)
	" >&2
    exit 1
fi

ENV="${1}"
INPUT="${2}"

function jasypt_encrypt() {

	PW_FILE="${1}"
	
	if [ ! -s ${PW_FILE} ] ; then
		echo "The script expects a encryption password in ${PW_FILE}"
		exit 1
	fi

	ENCRYPTION_JAR=lib/jasypt-encryption.jar
	ENCRYPTION_CLASS=org.jasypt.intf.cli.JasyptPBEStringEncryptionCLI
	ENCRYPTION_ALGORITHM=PBEWithHMACSHA512AndAES_256
	IV_GENERATOR_CLASS=org.jasypt.iv.RandomIvGenerator
	PASSWORD=$(cat ${PW_FILE})

	ENCRYPTED_STRING=$(java -cp $ENCRYPTION_JAR $ENCRYPTION_CLASS \
								input=$INPUT \
								password=$PASSWORD \
								algorithm=$ENCRYPTION_ALGORITHM \
								ivGeneratorClassName=$IV_GENERATOR_CLASS \
								verbose=false)
								
	echo "ENC($ENCRYPTED_STRING)"
}

# Execute call.
case "${ENV}" in
    [tst]*) echo $(jasypt_encrypt "jasypt_pwd_tst.txt") ;;
    [acc]*) echo $(jasypt_encrypt "jasypt_pwd_acc.txt") ;;
    [prd]*) echo $(jasypt_encrypt "jasypt_pwd_prd.txt") ;;
    *) echo "Invalid environment provided $ENV. Should be [tst|acc|prd]" ;;
esac
