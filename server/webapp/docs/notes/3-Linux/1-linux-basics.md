| What        | Url                                                                                                                            |
|-------------|--------------------------------------------------------------------------------------------------------------------------------|
| ...         | ...                                                                                                                            | 

## Background
OS = An operating system is the software that sits between applications and hardware and makes the connections between all of your software and the physical resources that do the work.
Linux = 1992 open source OS similar to UNIX and based on a Unix clone Minix
Unix = 1969 OS for mainframe by AT&T, servers eg Solaris (oracle), AIX (ibm) and OSX (apple)
FreeBSD/OpenBSD = Berkeley Software Distribution rebuild of Unix but still licenced to AT&T
BASH = Linux default shell, UNIX has Bourne Shell
Gnome = GUI for Linux and Unix
DOS = 1981 MS DOS an OS that runs from a disk - its single task single user cli os
Windows = 1985 based on dos but with multitasking, multiuser and a gui os - 1992 windows 3.1 had minecraft and patience
C64 OS = 1982 written in Basic and was a ROM OS for C64 so a machine with OS

## Linux distributions
RHEL is based on a free, open source model like all Linux distributions. Red Hat 
- In the past, RHEL was available at no cost, and users only had to pay for support.
-  However, Red Hat has since created two versions of RHEL

## Folder structure
| folder                  | what                                                |
|:------------------------|:----------------------------------------------------|
| /bin or /usr/bin        | compiled source code eg. cat, ls, cd, grep          |
| /sbin                   | compiled source code for root eg. route, mkfs       |                  
| /lib                    | shared libraries for (s)bin eg. firmware            | 
| /etc                    | linux network and group/passwd config files         |
| /home/bob               | personal user folder of bob also called $HOME and ~ |
| /usr                    | system users source code and docs for all to access |
| /opt(/pivotal/tcserver) | optional third-party pre-bundled source code        |
| /root                   | home dir of superuser (not /usr/root)               |
| /srv                    | websites                                            |
| /var/[log, mail, spool] | writable counterpart for running software           |

## File commands
`grep -i --color=auto error file // finds ERROR in file also error` 

`diff file1 file2`

`chmod u=rw,og=r new_file.txt // u(ser),g(roup),o(ther), using the “=” wipes out any existing permissions, - and + will remove and grant`

`less +G file // =-info | n/N-next/previous | g/G-begin/end`

`vi // gg/G - begin/end | i/esc - edit/command mode | :wq! or ZZ does write and quit`

## Process commands
`top -H`

## Working instructions
