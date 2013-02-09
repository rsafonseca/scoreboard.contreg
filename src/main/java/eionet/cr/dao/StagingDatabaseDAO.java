/*
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 * The Original Code is Content Registry 3
 *
 * The Initial Owner of the Original Code is European Environment
 * Agency. Portions created by TripleDev or Zero Technologies are Copyright
 * (C) European Environment Agency.  All Rights Reserved.
 *
 * Contributor(s):
 *        jaanus
 */

package eionet.cr.dao;

import java.util.List;

import eionet.cr.dto.StagingDatabaseDTO;
import eionet.cr.staging.exp.ExportDTO;
import eionet.cr.staging.exp.ExportRunner;
import eionet.cr.staging.exp.ExportStatus;
import eionet.cr.staging.exp.QueryConfiguration;
import eionet.cr.staging.imp.ImportStatus;

/**
 * DAO interface for operations with staging databases.
 *
 * @author jaanus
 */
public interface StagingDatabaseDAO extends DAO {

    /**
     * Creates the staging database in the underlying RDBMS. Simply creates the database, does nothing further. i.e. the result of
     * this method will be a new empty staging database.
     *
     * @param databaseName
     * @throws DAOException
     */
    void createDatabase(String databaseName) throws DAOException;

    /**
     * Creates a record in the staging databases table for the given database DTO. Returns the newly created record's surrogate id.
     *
     * @param databaseDTO
     * @param userName
     * @return
     * @throws DAOException
     */
    int createRecord(StagingDatabaseDTO databaseDTO, String userName) throws DAOException;

    /**
     * Gets a staging database by the given id.
     *
     * @param id
     * @return
     * @throws DAOException
     */
    StagingDatabaseDTO getDatabaseById(int id) throws DAOException;

    /**
     * Gets a staging database by the given name.
     *
     * @param name
     * @return
     * @throws DAOException
     */
    StagingDatabaseDTO getDatabaseByName(String name) throws DAOException;

    /**
     *
     * @param databaseId
     * @param importStatus
     * @throws DAOException
     */
    void updateImportStatus(int databaseId, ImportStatus importStatus) throws DAOException;

    /**
     *
     * @param databaseId
     * @param message
     * @throws DAOException
     */
    void appendToImportLog(int databaseId, String message) throws DAOException;

    /**
     *
     * @param exportId
     * @param message
     * @throws DAOException
     */
    void appendToExportLog(int exportId, String message) throws DAOException;

    /**
     * Lists all staging databases in the database, ordered by name ascending.
     *
     * @return
     * @throws DAOException
     */
    public List<StagingDatabaseDTO> listAll() throws DAOException;

    /**
     * Deletes the given staging databases.
     *
     * @param dbNames
     * @throws DAOException
     */
    public void delete(List<String> dbNames) throws DAOException;

    /**
     * Returns true if there is at least one table under the given Virtuoso database (i.e. qualifier). Otherwise returns false. The
     * database is looked only within the current SQL connection's user space.
     *
     * @param dbName
     * @return
     * @throws DAOException
     */
    public boolean exists(String dbName) throws DAOException;

    /**
     * Returns the import log of the staging database by the given integer id.
     *
     * @param databaseId
     * @return
     * @throws DAOException
     */
    public String getImportLog(int databaseId) throws DAOException;

    /**
     * Returns the log of the RDF export by the given id.
     *
     * @param exportId The export's ID.
     * @return The export's log.
     * @throws DAOException In case any sort of database access error happens.
     */
    public String getExportLog(int exportId) throws DAOException;

    /**
     * Prepares the given SQL statement for the given database. The method returns the list of column names selected by the given
     * SQL statement. If the latter is not a query (i.e. it's an update statement), the returned list is empty.
     *
     * @param sql The given SQL statement.
     * @param dbName The database name on which the query should be executed.
     * @return List of selected columns. It is empty if the statement is not a select query.
     * @throws DAOException In case any sort of database access error happens.
     */
    public List<String> prepareStatement(String sql, String dbName) throws DAOException;

    /**
     * Creates a new staging database RDF export record in the database, using the given inputs. The newly created export's status
     * will be that of "started".
     *
     * @param databaseId the database id
     * @param exportName the export's descriptive name
     * @param userName the user name
     * @param queryConf the query configuration
     * @return the newly created export's auto-generated id
     * @throws DAOException If a database access error happens.
     */
    int startRDEExport(int databaseId, String exportName, String userName, QueryConfiguration queryConf) throws DAOException;

    /**
     * Updates the status of the staging database RDF export by the given export id.
     *
     * @param exportId the export id
     * @param status the status
     * @throws DAOException If a database access error happens.
     */
    void updateExportStatus(int exportId, ExportStatus status) throws DAOException;

    /**
     * Sets the given RDF export's "finished" date to the current date and its status to the given status. Also sets the export's
     * following fields based on corresponding getters in the given {@link ExportRunner}.
     *
     * @param exportId The export's ID.
     * @param exportRunner The export's runner.
     * @param status The export's status.
     * @throws DAOException If a database access error happens.
     */
    void finishRDFExport(int exportId, ExportRunner exportRunner, ExportStatus status) throws DAOException;

    /**
     * Returns a list of RDF exports for the given staging database id. If the latter is <= 0, then RDF exports for all staging
     * databases are returned.
     *
     * @param databaseId The given staging database id.
     * @return The list.
     * @throws DAOException If a database access error happens.
     */
    List<ExportDTO> listRDFExports(int databaseId) throws DAOException;

    /**
     * Retrieves an RDF export DTO by the given id.
     *
     * @param exportId The given id.
     * @return The matching DTO object.
     * @throws DAOException If a database access error happens.
     */
    ExportDTO getRDFExport(int exportId) throws DAOException;

    /**
     * Returns true if an RDF export by the given database id and export name exists. Otherwise returns true.
     *
     * @param databaseId the database id
     * @param exportName the export name
     * @return As described above.
     * @throws DAOException If a database access error happens.
     */
    boolean existsRDFExport(int databaseId, String exportName) throws DAOException;
}
