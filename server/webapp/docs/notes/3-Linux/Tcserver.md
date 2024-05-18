| What        | Url                                                                                                                            |
|-------------|--------------------------------------------------------------------------------------------------------------------------------|
| Amyna logon | [https://employee-authentication.europe.intranet/amyna/pwvault](https://employee-authentication.europe.intranet/amyna/pwvault) |
| ...         | ...                                                                                                                            |

## Change log


## Background
TcServer = a Lightweight Java web application server that extends Apache Tomcat
Pivotal  = formerly known as VMware vFabric tc Server, Pivotal owns tc Server.
RHEL = a commercial Linux (derived from Unix) distribution

## Folder structure
/opt/pivotal = main tc Server installation directory
/opt/pivotal/tcserver = tcserver installation directory

## Actions
Extract tcserver:
- groupadd pivotal
- useradd tcserver -g pivotal
- mkdir -p /opt/pivotal/tcserver
- cd /opt/pivotal/tcserver
- copy the pivotal-tc-server-standard-4.1.12.RELEASE.tar.gz to /opt/pivotal/tcserver
- tar -zxvf pivotal-tc-server-standard-4.1.12.RELEASE.tar.gz

## Rules for using NPAs

## Working instructions
