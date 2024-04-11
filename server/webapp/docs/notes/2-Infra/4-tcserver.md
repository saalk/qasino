Back to [Index](0-index.md)

| What        | Url                                                                                                                            |
|-------------|--------------------------------------------------------------------------------------------------------------------------------|
| ...         | ...                                                                                                                            | 

## Background
TcServer = a Lightweight Java web application server that extends Apache Tomcat
Pivotal  = formerly known as VMware vFabric tc Server, Pivotal owns tc Server.

## Tomcat logs
Console log - not rotated by default
- catalina.out (system.out and system.err)

Java logs (tomcat uses java.util.logging)
- catalina.[date].log (catalina engine logs)
- localhost.[date].log (application related)

Access log
- localhost_access_log.[date].log

## Folder structure TC server
| folder                                    | what                                               |
|:------------------------------------------|:---------------------------------------------------|
| /opt/pivotal/*                            | main tc Server installation directory              |
| /opt/pivotal/tcserver/standard            | ${TCSERVER_HOME} = tcserver installation directory |
| ${TCSERVER_HOME}/tcserverXX               | tcserver runtime(s)                                |
| ${TCSERVER_HOME}/conf/tcserver.properties |                                                    |

## Folder structure TC server logs
/var/opt/pivotal/tcserver/instances/tcserver** = tcserver log files directory

## 'ln -s' symlink
A symlink is a symbolic Linux/ UNIX link that points to another file or folder on your computer.
> ln -s /<path to be linked> <path of the link to be created>
> symlink in ing for logs

## Install Actions
Extract tcserver:
- groupadd pivotal
- useradd tcserver -g pivotal
- mkdir -p /opt/pivotal/tcserver
- cd /opt/pivotal/tcserver
- copy the pivotal-tc-server-standard-4.1.12.RELEASE.tar.gz to /opt/pivotal/tcserver
- tar -zxvf pivotal-tc-server-standard-4.1.12.RELEASE.tar.gz


