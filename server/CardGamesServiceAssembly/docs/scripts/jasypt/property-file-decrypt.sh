#!/bin/bash

if [ $# -ne 2 ]; then
    echo
    echo "Script that decrypts Jasypt encrypted passwords within file and inserts full local path to api-trust.jks if present.
    Usage:
            $(tput setaf 2)${0} [tst|acc] [relative path to my-properties.enc]$(tput sgr0)
	" >&2
    exit 1
fi

DECRYPTED_FILE=$(echo $2)
DECRYPTED_FILE=${DECRYPTED_FILE/.enc/}
cp $2 $DECRYPTED_FILE

IFS=$'\n'

INPUT=$(sed -n -e '/ENC(/p' $DECRYPTED_FILE)
INPUT=($INPUT)

for (( i=0; i<${#INPUT[@]}; i++ ))
do
  LINE=${INPUT[$i]}
  ENCRYPTED_PWD=${LINE#*=}
  sed -i "s|"$ENCRYPTED_PWD"|$(./decrypt.sh $1 "$ENCRYPTED_PWD")|g" $DECRYPTED_FILE
done

sed -i "s|api-trust.jks|$(realpath ../"$1"/api-trust.jks)|g" $DECRYPTED_FILE