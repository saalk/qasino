#!/bin/bash

if [ $# -ne 2 ]; then
    echo
    echo "Script to decrypt a single password via Jasypt. Accepts parameters in formats such as: 'ENC(LNTorM68FXamzI)' or 'LNTorM68FXamzI'
    Usage:
            $(tput setaf 2)${0} [tst|acc|prd] [password]$(tput sgr0)
	" >&2
    exit 1
fi

ENV="${1}"
INPUT="${2}"

function jasypt_decrypt() {

	PW_FILE="${1}"
	
	if [ ! -s ${PW_FILE} ] ; then
		echo "The script expects a decryption password in ${PW_FILE}"
		exit 1
	fi
	
	if [[ $INPUT == ENC* ]] ; then
		INPUT=${INPUT#*(}
		INPUT=${INPUT%?}
	fi

	ENCRYPTION_JAR=lib/jasypt-encryption.jar
	DECRYPTION_CLASS=org.jasypt.intf.cli.JasyptPBEStringDecryptionCLI
	ENCRYPTION_ALGORITHM=PBEWithHMACSHA512AndAES_256
	IV_GENERATOR_CLASS=org.jasypt.iv.RandomIvGenerator
	PASSWORD=$(cat ${PW_FILE})

	java -cp $ENCRYPTION_JAR $DECRYPTION_CLASS \
			  input=$INPUT \
			  password=$PASSWORD \
			  algorithm=$ENCRYPTION_ALGORITHM \
			  ivGeneratorClassName=$IV_GENERATOR_CLASS \
			  verbose=false 
}


# Execute call.
case "${ENV}" in
    [tst]*) echo $(jasypt_decrypt "jasypt_pwd_tst.txt") ;;
    [acc]*) echo $(jasypt_decrypt "jasypt_pwd_acc.txt") ;;
    [prd]*) echo $(jasypt_decrypt "jasypt_pwd_prd.txt") ;;
    *) echo "Invalid environment provided. Should be [tst|acc|prd]" ;;
esac
