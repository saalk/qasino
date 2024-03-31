curl -i --cert ./client_0.crt:apisdksecret --key ./client_0.key --cacert ca.crt \
     -H "Host: api.qasino.cloud" https://localhost:8086/api/merak/base/hello/world
