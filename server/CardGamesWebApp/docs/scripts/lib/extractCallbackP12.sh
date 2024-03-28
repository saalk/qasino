#!/bin/bash

# Check input
if [ $# -ne 1 ]; then
    echo "This script can be used to extract a key and a cer from a keystore. Please use it as described below.
Usage:
          ${0} {keyStore}
keystore is sampleclient-test.p12 or sampleclient-acc.p12
    "
    exit 1
fi

#Start process
keyStoreFile="${1%.${1#*.}}" # removed file extension
echo "plainFile: ${keyStoreFile}"
keyAlias="sampleclient"
echo "keyAlias: ${keyAlias}"

#read -r -s -p "Entry Keystore Password for ${keyStoreFile}: " keyStorePass
#echo
keyStorePass=mypass
#read -r -s -p "Entry privateKey Password for Alias: ${keyAlias}: " privateKeyPass
#echo
privateKeyPass=mypass


# extracting key from p12.
echo "Extracting key from ${keyStoreFile}.p12"
openssl pkcs12 -in "${keyStoreFile}.p12" -nodes -nocerts -out "${keyStoreFile}.key" -password pass:"${keyStorePass}"
if [ ! -f "${keyStoreFile}.key" ]; then
    echo "!ERROR! key could not be extracted: ${keyStoreFile}.key. Exiting script."
    exit 0
fi

echo "getting public key for ${keyStoreFile}.key"
keytool -export -alias "${keyAlias}" -file "${keyAlias}.der"  -keystore "${keyStoreFile}.p12" -storepass "${keyStorePass}"
openssl x509 -inform der -in "${keyAlias}.der" -out "${keyStoreFile}.cer"
if [ ! -f "${keyStoreFile}.cer" ]; then
    echo "!ERROR! public key could not be extracted: ${keyAlias}.cer. Exiting script."
    exit 0
fi


echo "cleaning up files: ${keyAlias}.der"
rm "${keyAlias}.der"
