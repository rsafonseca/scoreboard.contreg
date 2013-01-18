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

package eionet.cr.web.action.admin;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.FileBean;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.validation.ValidationMethod;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import eionet.cr.dao.DAOException;
import eionet.cr.dao.DAOFactory;
import eionet.cr.dao.StagingDatabaseDAO;
import eionet.cr.dto.StagingDatabaseDTO;
import eionet.cr.staging.StagingDatabaseCreator;
import eionet.cr.web.action.AbstractActionBean;

/**
 * Action bean for operations with a single staging database.
 *
 * @author jaanus
 */
@UrlBinding("/admin/stagingDb.action")
public class StagingDatabaseActionBean extends AbstractActionBean {

    /** */
    private static final String STAGING_DATABASE_JSP = "/pages/admin/stagingDb/stagingDatabase.jsp";

    /** */
    private static final String ADD_STAGING_DATABASE_JSP = "/pages/admin/stagingDb/addStagingDatabase.jsp";

    /** Static logger. */
    private static final Logger LOGGER = Logger.getLogger(StagingDatabaseActionBean.class);

    /** */
    private StagingDatabaseDTO database;

    /** */
    private String dbName;
    private String dbDescription;

    /** */
    private FileBean dbFile;

    /**
     * The bean's default event handler method.
     *
     * @return Resolution to go to.
     */
    @DefaultHandler
    public Resolution defaultHandler() {
        return new ForwardResolution(STAGING_DATABASE_JSP);
    }

    /**
     *
     * @return
     * @throws DAOException
     */
    public Resolution add() throws DAOException {

        if (getContext().getRequest().getMethod().equalsIgnoreCase("GET")){
            return new ForwardResolution(ADD_STAGING_DATABASE_JSP);
        }

        LOGGER.debug("dbName: " + dbName);
        LOGGER.debug("dbDescription: " + dbDescription);
        LOGGER.debug("dbFile: " + (dbFile == null ? null : dbFile.getFileName()));

        // Create the database record.
        StagingDatabaseDTO dto = new StagingDatabaseDTO();
        dto.setName(dbName);
        dto.setDescription(dbDescription);
        int id = DAOFactory.get().getDao(StagingDatabaseDAO.class).createRecord(dto, getUserName());
        LOGGER.debug("New staging database record created, id = " + id);

        // Create the database in DBMS and populate it from the uploaded file.
        StagingDatabaseCreator creator = new StagingDatabaseCreator(dto, dbFile);
        creator.start();

        addSystemMessage("Database created and import started in the background! Go to monitoring from operations menu.");
        return new RedirectResolution(StagingDatabasesActionBean.class);
    }

    /**
     *
     * @return
     */
    public Resolution backToDbList() {
        return new RedirectResolution(StagingDatabasesActionBean.class);
    }

    /**
     * @return the database
     */
    public StagingDatabaseDTO getDatabase() {
        return database;
    }

    /**
     * @return the dbName
     */
    public String getDbName() {
        return dbName;
    }

    /**
     * @param dbName the dbName to set
     */
    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    /**
     * @return the dbDescription
     */
    public String getDbDescription() {
        return dbDescription;
    }

    /**
     * @param dbDescription the dbDescription to set
     */
    public void setDbDescription(String dbDescription) {
        this.dbDescription = dbDescription;
    }

    /**
     * @return the dbFile
     */
    public FileBean getDbFile() {
        return dbFile;
    }

    /**
     * @param dbFile the dbFile to set
     */
    public void setDbFile(FileBean dbFile) {
        this.dbFile = dbFile;
    }

    /**
     * Validate POST request on "add" event.
     */
    @ValidationMethod(on = {"add"})
    public void validateAdd() {

        // If no POST request, nothing to validate.
        if (getContext().getRequest().getMethod().equalsIgnoreCase("GET")) {
            return;
        }

        if (dbFile == null) {
            addGlobalValidationError("No file supplied!");
        }

        if (StringUtils.isBlank(dbName)) {
            addGlobalValidationError("Name must not be blank!");
        }

        getContext().setSourcePageResolution(new ForwardResolution(ADD_STAGING_DATABASE_JSP));
    }

    /**
     * Validates the the user is authorised for any operations on this action bean.
     * If user not authorised, redirects to the {@link AdminWelcomeActionBean} which displays a proper error message.
     * Will be run on any events, since no specific events specified in the {@link ValidationMethod} annotation.
     */
    @ValidationMethod(priority = 1)
    public void validateUserAuthorised() {

        if (getUser() == null || !getUser().isAdministrator()) {
            addGlobalValidationError("You are not authorized for this operation!");
            getContext().setSourcePageResolution(new RedirectResolution(AdminWelcomeActionBean.class));
        }
    }
}
