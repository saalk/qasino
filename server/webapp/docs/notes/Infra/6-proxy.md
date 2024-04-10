## Change log

## Background
GIBA = Global Internet Browse Access = proxy environment of ING
- PRD = giba-proxy.wps.ing.net:8080 
- ACC = giba-accp-proxy.wps.ing.net:8080

Only generic users (Personal Accounts) are allowed to authenticate on the proxy and connect via port 8080. 
Non-Personal Accounts (NPAs) are not allowed.

## Firewall
- Raise the firewall request via the IPC VRA Portal - "Create Firewall Rule (3.1.0)"
- The workflow Create Firewall Rule triggers the approval workflows
- Once approved, the request is registered in GSO - GSO start deploying to the Palo Alto firewall devices


## Working instructions
Our RIAF api's do not have a way to work on a secure https tls connection. 
Therefore we have apache proxy to relay https tls to http.


8081 -> 8011-99
