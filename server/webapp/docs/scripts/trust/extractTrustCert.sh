#!/bin/bash

# Check input
if [ $# -ne 2 ]; then
    echo "This script can be used to extract a trust cer from a keystore. Please use it as described below.
Usage:
          ${0} {keyStore} {keyAlias}
To obtain the keyAlias:
          keytool -list -keystore trust.jks
    "
    exit 1
fi

keyStoreFile="${1%.${1#*.}}" # removed file extension
echo "plainFile: ${keyStoreFile}"
keyAlias="${2}"
echo "keyAlias: ${keyAlias}"

keytool -export -keystore "${keyStoreFile}.jks" -alias "${keyAlias}" -file "${keyAlias}.cer"
