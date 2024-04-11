## Change log

## Background
link = Oracle Database as a Service on confluence
Due to the nature of DBaaS, it is not possible to:
- Connect to a SID, DBaaS uses service names.
- Connect to a hostname, DBaaS uses a scan listener per cluster per data center
- Connect to DBaaS from your laptop/desktop
- Fail if there is any of NON_OMF (Oracle Managed Files)

## Connection string for thin database
(DESCRIPTION=
 (CONNECT_TIMEOUT=30)(RETRY_COUNT=20)(RETRY_DELAY=3)(FAILOVER=ON)(LOAD_BALANCE=OFF)
 (ADDRESS_LIST=(LOAD_BALANCE=ON)(ADDRESS=(PROTOCOL=TCP)(HOST=xbi407-scan.dbaas.ing.net)(PORT=1521)))
 (ADDRESS_LIST=(LOAD_BALANCE=ON)(ADDRESS=(PROTOCOL=TCP)(HOST=xbi307-scan.dbaas.ing.net)(PORT=1521)))
(CONNECT_DATA=(SERVICE_NAME=XXXXXX_1_ac_dbaas)))

## PEDR
Production exercise disaster recovery = PEDR
- makes your database available at the other data center
- if your database is down PEDR will not work
- choose not to wait for the 'Create Level0 backup before opening the database'

## Second day operations
- Stop/Start database
- Resize
- Pathing - If you want to upgrade to v19.12 use: Change Patch Ring
- Change Patch Ring - Moves your database to a ring number if it was not part of a ring yet
  when your database belongs to a ring number it will be automatically patched initiated by the provider DBaaS.

## PDBADMIN
NPA administrator role for DBaaS is managed by Tech PL Privilege Management Squad. 
For that role a new assignment group was created in SNOW: Tech/Infra/ITSecurity/IAM/DBaaS_PWV.



## Working instructions

