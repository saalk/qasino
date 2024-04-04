Certificates for local testing
==============================

This directory contains client and ca certificates that you can add to your browser for local development.
Merak applications require tls (https) and a client certificate.


You can then invoke the API using curl:
```
curl --cacert ca.crt --key client_0.key --cert client_0.crt:apisdksecret -H 'Host: api.qasino.cloud' https://localhost:8086/api/merak/reference/hello 
```
