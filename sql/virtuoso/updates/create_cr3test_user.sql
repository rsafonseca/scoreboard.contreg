USER_CREATE('cr3test', 'zzz', vector ('SQL_ENABLE',1,'DAV_ENABLE',1));
USER_GRANT_ROLE('cr3test','SPARQL_SELECT',0);
USER_GRANT_ROLE('cr3test','SPARQL_UPDATE',0);