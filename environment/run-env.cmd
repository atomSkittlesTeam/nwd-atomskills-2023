@echo off
set NWC_SRV_PORT=1040
set NWC_SRV_MILLING_PORTS={'mm-1':'1041','mm-2':'1042','mm-3':'1043','mm-4':'1045','mm-5':'1047'}
set NWC_SRV_LATHE_PORTS={'lm-1':'1051','lm-2':'1052','lm-3':'1054','lm-4':'1056'}
set NWC_DB_HOST=localhost
set NWC_DB_PORT=5432
set CRM_MAX_EXECUTING_REQUESTS=7
set NWC_MNF_MIN_REPAIRING_DURATION=20
set NWC_MNF_MAX_REPAIRING_DURATION=240
set NWC_MNF_MIN_BROKEN_CHANCE=1
set NWC_MNF_MAX_BROKEN_CHANCE=10

.\jre-17.0.6\bin\java.exe -jar as-environment-service-1.0.0.jar