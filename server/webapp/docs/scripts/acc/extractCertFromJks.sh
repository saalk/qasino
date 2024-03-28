#!/bin/bash

# Check input
if [ $# -ne 2 ]; then
    echo "This script can be used to extract a key and a cer from a keystore. Please use it as described below.
Usage:
          ${0} {keyStore} {keyAlias}
To obtain the keyAlias:
          keytool -list -keystore identity.jks
    "
    exit 1
fi

#Start process
keyStoreFile="${1%.${1#*.}}" # removed file extension
echo "plainFile: ${keyStoreFile}"
keyAlias="${2}"
echo "keyAlias: ${keyAlias}"

read -r -s -p "Entry Keystore Password for ${keyStoreFile}: " keyStorePass
echo
# echo ${keyStorePass}
read -r -s -p "Entry privateKey Password for Alias: ${keyAlias}: " privateKeyPass
echo
# echo ${privateKeyPass}



echo "Creating ${keyStoreFile}.p12 from ${1} with Alias ${keyAlias}"
keytool -importkeystore \
    -srckeystore "${keyStoreFile}.jks" \
    -destkeystore "${keyStoreFile}.p12" \
    -deststoretype PKCS12 \
    -srcalias "${keyAlias}" \
    -deststorepass "${keyStorePass}" \
    -destkeypass "${keyStorePass}" \
    -srcstorepass "${keyStorePass}"  \
    -srckeypass "${privateKeyPass}"
if [ ! -f "${keyStoreFile}.p12" ]; then
	echo "!ERROR! keystore file not created: ${keyStoreFile}.p12. Exiting script."
	exit 0
fi
echo "Created ${keyStoreFile}.p12"



# extracting key from p12.
echo "Extracting key from ${keyStoreFile}.p12"
openssl pkcs12 -in "${keyStoreFile}.p12" -nodes -nocerts -out "${keyAlias}.key" -password pass:"${keyStorePass}"
if [ ! -f "${keyAlias}.key" ]; then
    echo "!ERROR! key could not be extracted: ${keyStoreFile}.key. Exiting script."
    exit 0
fi




echo "getting public key for ${keyAlias}.key"
keytool -export -alias "${keyAlias}" -file "${keyAlias}.der"  -keystore "${keyStoreFile}.jks" -storepass "${keyStorePass}"
openssl x509 -inform der -in "${keyAlias}.der" -out "${keyAlias}.cer"
if [ ! -f "${keyAlias}.cer" ]; then
    echo "!ERROR! public key could not be extracted: ${keyAlias}.cer. Exiting script."
    exit 0
fi





echo "cleaning up files: ${keyStoreFile}.p12"
rm "${keyStoreFile}.p12"
echo "cleaning up files: ${keyAlias}.der"
rm "${keyAlias}.der"
