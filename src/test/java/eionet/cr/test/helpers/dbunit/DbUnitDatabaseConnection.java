/*
* The contents of this file are subject to the Mozilla Public
* 
* License Version 1.1 (the "License"); you may not use this file
* except in compliance with the License. You may obtain a copy of
* the License at http://www.mozilla.org/MPL/
* 
* Software distributed under the License is distributed on an "AS
* IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
* implied. See the License for the specific language governing
* rights and limitations under the License.
* 
* The Original Code is Content Registry 2.0.
* 
* The Initial Owner of the Original Code is European Environment
* Agency. Portions created by Tieto Eesti are Copyright
* (C) European Environment Agency. All Rights Reserved.
* 
* Contributor(s):
* Jaanus Heinlaid, Tieto Eesti*/
package eionet.cr.test.helpers.dbunit;

import java.util.Properties;

import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;

import eionet.cr.config.GeneralConfig;
import eionet.cr.util.sql.DbConnectionProvider;

/**
 * 
 * @author <a href="mailto:jaanus.heinlaid@tietoenator.com">Jaanus Heinlaid</a>
 *
 */
public abstract class DbUnitDatabaseConnection {

	/**
	 * 
	 * @return
	 * @throws Exception
	 */
	public static IDatabaseConnection get() throws Exception{
		
		DbConnectionProvider.setConnectionType(DbConnectionProvider.ConnectionType.SIMPLE);
		DatabaseConnection dbConn = new DatabaseConnection(DbConnectionProvider.getUnitTestConnection());
		dbConn.getConfig().setPropertiesByString(getConfigProperties());
		return dbConn;
	}
	
	/**
	 * 
	 * @return
	 */
	private static Properties getConfigProperties(){
		
		String dbUrl = GeneralConfig.getRequiredProperty(GeneralConfig.DB_URL);
		if (dbUrl.startsWith("jdbc:postgresql:"))
			return getPostgreConfigProperties();
		else
			return new Properties();

	}

	/**
	 * 
	 * @return
	 */
	private static Properties getPostgreConfigProperties(){
		
		Properties properties = new Properties();
		properties.setProperty("http://www.dbunit.org/properties/datatypeFactory",
				"eionet.cr.test.helpers.dbunit.DbUnitPostgreDataTypeFactory");
		return properties;
	}
}
