# Certificates
Place the Certificates for connecting to an test environment here. The script to extract cersts from a jks can be used
to obtain the required certificates.

```bash
    # Place an identity.jks file from which to extract the certificates next to this file. Then execute:
    ./extractCertFromJks.sh identity.jks clrv0000195182.ic.ing.net
```

To obtain the alias from a keystore (e.g. clrv0000195182.ic.ing.net):
```bash
    keytool -list -keystore identity.jks
```

The resulting certificates need to be placed in the corresponding environment folder.
- `tst` for test certificates
- `acc` for acceptance certificates
