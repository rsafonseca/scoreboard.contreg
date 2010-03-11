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
package eionet.cr.dao.postgre;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import eionet.cr.dao.DAOException;
import eionet.cr.dao.HarvestSourceDAO;
import eionet.cr.dao.readers.HarvestSourceDTOReader;
import eionet.cr.dto.HarvestSourceDTO;
import eionet.cr.util.Hashes;
import eionet.cr.util.Pair;
import eionet.cr.util.SortingRequest;
import eionet.cr.util.Util;
import eionet.cr.util.YesNoBoolean;
import eionet.cr.util.pagination.PagingRequest;
import eionet.cr.util.sql.ConnectionUtil;
import eionet.cr.util.sql.SQLUtil;
import eionet.cr.util.sql.SingleObjectReader;

/**
 * 
 * @author <a href="mailto:jaanus.heinlaid@tietoenator.com">Jaanus Heinlaid</a>
 *
 */
public class PostgreSQLHarvestSourceDAO extends PostgreSQLBaseDAO implements HarvestSourceDAO{

	/** */
	private static final String getSourcesSQL = 
		"SELECT * FROM HARVEST_SOURCE WHERE TRACKED_FILE = 'N' AND COUNT_UNAVAIL = 0 AND URL NOT IN (SELECT URL FROM REMOVE_SOURCE_QUEUE) ";
	private static final String searchSourcesSQL = 
		"SELECT * FROM HARVEST_SOURCE WHERE TRACKED_FILE = 'N' AND COUNT_UNAVAIL = 0 AND URL like (?) AND URL NOT IN (SELECT URL FROM REMOVE_SOURCE_QUEUE) ";
	private static final String getHarvestSourcesFailedSQL = 
		"SELECT * FROM HARVEST_SOURCE WHERE LAST_HARVEST_FAILED = 'Y' AND URL NOT IN (SELECT URL FROM REMOVE_SOURCE_QUEUE)";
	private static final String searchHarvestSourcesFailedSQL = 
		"SELECT * FROM HARVEST_SOURCE WHERE LAST_HARVEST_FAILED = 'Y' AND URL LIKE(?) AND URL NOT IN (SELECT URL FROM REMOVE_SOURCE_QUEUE)";
	private static final String getHarvestSourcesUnavailableSQL =
		"SELECT * FROM HARVEST_SOURCE WHERE COUNT_UNAVAIL > " + HarvestSourceDTO.COUNT_UNAVAIL_THRESHOLD + " AND URL NOT IN (SELECT URL FROM REMOVE_SOURCE_QUEUE)";
	private static final String searchHarvestSourcesUnavailableSQL =
		"SELECT * FROM HARVEST_SOURCE WHERE URL LIKE (?) AND COUNT_UNAVAIL > " + HarvestSourceDTO.COUNT_UNAVAIL_THRESHOLD + " AND URL NOT IN (SELECT URL FROM REMOVE_SOURCE_QUEUE)";
	private static final String getHarvestTrackedFiles = 
		"SELECT * FROM HARVEST_SOURCE WHERE TRACKED_FILE = 'Y' AND URL NOT IN (SELECT URL FROM REMOVE_SOURCE_QUEUE)  ";
	private static final String searchHarvestTrackedFiles = 
		"SELECT * FROM HARVEST_SOURCE WHERE TRACKED_FILE = 'Y' and URL like(?) AND URL NOT IN (SELECT URL FROM REMOVE_SOURCE_QUEUE) ";

	/*
	 * (non-Javadoc)
	 * @see eionet.cr.dao.HarvestSourceDAO#getHarvestSources(java.lang.String, eionet.cr.util.PagingRequest, eionet.cr.util.SortingRequest)
	 */
    public Pair<Integer,List<HarvestSourceDTO>> getHarvestSources(String searchString, PagingRequest pagingRequest, SortingRequest sortingRequest)
    		throws DAOException {
    	
    	return getSources(
    			StringUtils.isBlank(searchString)
    					? getSourcesSQL
    					: searchSourcesSQL,
				searchString,
				pagingRequest,
				sortingRequest);	
    }

	/*
	 * (non-Javadoc)
	 * @see eionet.cr.dao.HarvestSourceDAO#getHarvestSourcesFailed(java.lang.String, eionet.cr.util.PagingRequest, eionet.cr.util.SortingRequest)
	 */
	public Pair<Integer, List<HarvestSourceDTO>> getHarvestSourcesFailed(String searchString,
			PagingRequest pagingRequest, SortingRequest sortingRequest)
			throws DAOException {
		
		return getSources(
    			StringUtils.isBlank(searchString)
						? getHarvestSourcesFailedSQL
						: searchHarvestSourcesFailedSQL,
				searchString,
				pagingRequest,
				sortingRequest);
	}

	/*
	 * (non-Javadoc)
	 * @see eionet.cr.dao.HarvestSourceDAO#getHarvestSourcesUnavailable(java.lang.String, eionet.cr.util.PagingRequest, eionet.cr.util.SortingRequest)
	 */
	public Pair<Integer, List<HarvestSourceDTO>> getHarvestSourcesUnavailable(
			String searchString, PagingRequest pagingRequest,
			SortingRequest sortingRequest) throws DAOException {
		
    	return getSources(
    			StringUtils.isBlank(searchString)
						? getHarvestSourcesUnavailableSQL
						: searchHarvestSourcesUnavailableSQL,
				searchString,
				pagingRequest,
				sortingRequest);	
	}

	/*
	 * (non-Javadoc)
	 * @see eionet.cr.dao.HarvestSourceDAO#getHarvestTrackedFiles(java.lang.String, eionet.cr.util.PagingRequest, eionet.cr.util.SortingRequest)
	 */
	public Pair<Integer, List<HarvestSourceDTO>> getHarvestTrackedFiles(String searchString,
			PagingRequest pagingRequest, SortingRequest sortingRequest)
			throws DAOException {
		
    	return getSources(
    			StringUtils.isBlank(searchString)
		    			? getHarvestTrackedFiles
						: searchHarvestTrackedFiles,
				searchString,
				pagingRequest,
				sortingRequest);	
	}

    /**
     * 
     * @param sql
     * @param searchString
     * @param pagingRequest
     * @param sortingRequest
     * @return
     * @throws DAOException
     */
    private Pair<Integer,List<HarvestSourceDTO>> getSources(String sql, String searchString,
    		PagingRequest pagingRequest, SortingRequest sortingRequest) throws DAOException {
        	
    	List<Object> inParams = new LinkedList<Object>();
    	if (!StringUtils.isBlank(searchString)) {
    		inParams.add(searchString);
    	}
    	String queryWithoutOrderAndLimit = new String(sql);
    	List<Object> inParamsWithoutOrderAndLimit = new LinkedList<Object>(inParams);
    	
    	if (sortingRequest!=null && sortingRequest.getSortingColumnName()!=null) {
    		sql += " ORDER BY " +
    		   sortingRequest.getSortingColumnName() + " " + sortingRequest.getSortOrder().toSQL();
    	}
    	else {
    		sql += " ORDER BY URL "; 
    	}
    	
    	if (pagingRequest!=null){
    		sql += " LIMIT ? OFFSET ? ";
    		inParams.add(pagingRequest.getItemsPerPage());
    		inParams.add(pagingRequest.getOffset());
    	}
    	
    	int rowCount = 0;
    	List<HarvestSourceDTO> list = executeQuery(sql, inParams, new HarvestSourceDTOReader());
    	if (list!=null && !list.isEmpty()){
    		
    		StringBuffer buf = new StringBuffer("select count(*) from (").
			append(queryWithoutOrderAndLimit).append(") as foo");
    		
    		rowCount = Integer.parseInt(executeQueryUniqueResult(buf.toString(),
    				inParamsWithoutOrderAndLimit, new SingleObjectReader<Long>()).toString());
    	}
    	
    	return new Pair<Integer,List<HarvestSourceDTO>>(rowCount,list);
    }

    /** */
	private static final String addSourceSQL = "insert into HARVEST_SOURCE" +
			" (URL,URL_HASH,EMAILS,TIME_CREATED,INTERVAL_MINUTES,TRACKED_FILE)" +
			" VALUES (?,?,?,NOW(),?,cast(? as ynboolean))";
    /*
     * (non-Javadoc)
     * @see eionet.cr.dao.HarvestSourceDAO#addSource(eionet.cr.dto.HarvestSourceDTO, java.lang.String)
     */
	public Integer addSource(HarvestSourceDTO source, String user) throws DAOException {
		
		Integer harvestSourceID = null;

		long urlHash = 0;
		String url = source.getUrl();
		if (url!=null){
			url = StringUtils.substringBefore(url, "#"); // harvest sources where URL has fragment part, are not allowed
			urlHash = Hashes.spoHash(url);
		}
		
    	List<Object> values = new ArrayList<Object>();
		values.add(url);
		values.add(Long.valueOf(urlHash));
		values.add(source.getEmails());
		values.add(source.getIntervalMinutes());
		values.add(YesNoBoolean.format(source.isTrackedFile()));
		
		Connection conn = null;
		try{
			// execute the insert statement
			conn = getConnection();
			SQLUtil.executeUpdate(addSourceSQL, values, conn);
			
			// Get the freshly inserted record's ID.
			// We are not using SQLUtil.executeUpdateReturnAutoID(), because in PostgreSQL one
			// cannot use INSERT RETURNING on a table which has a conditional
			// ON INSERT DO INSTEAD rule. And on HARVEST_SOURCE we have exactly such a rule.
			// There should be no performance problems, as this method is expected to be called
			// by human action only (i.e. adding a new source via web interface).
			Object o = SQLUtil.executeSingleReturnValueQuery(
					"select HARVEST_SOURCE_ID from HARVEST_SOURCE where URL_HASH="+urlHash, conn);
			return o==null ? null : Integer.valueOf(o.toString());
		}
		catch (Exception e){
			throw new DAOException(e.getMessage(), e);
		}
		finally{
			ConnectionUtil.closeConnection(conn);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see eionet.cr.dao.HarvestSourceDAO#addSourceIgnoreDuplicate(eionet.cr.dto.HarvestSourceDTO, java.lang.String)
	 */
	public void addSourceIgnoreDuplicate(HarvestSourceDTO source, String user) throws DAOException{
		
		// JH160210 - in PostgreSQL schema we assume there is a rule created that does nothing if
		// duplicate source added. We lose the ability to notify user if she's trying to add
		// a source that already exists, but for the time being we can live with that.
		addSource(source, user);
	}

	/*
	 * (non-Javadoc)
	 * @see eionet.cr.dao.HarvestSourceDAO#deleteHarvestHistory(int)
	 */
	public void deleteHarvestHistory(int neededToRemain) throws DAOException {
		
		Connection conn = null;
		try{
			conn = getConnection();
			conn.setAutoCommit(false);
			
			Object o = SQLUtil.executeSingleReturnValueQuery(
					"select max(HARVEST_ID) from HARVEST", conn);			
			Long maxId = o==null || StringUtils.isBlank(o.toString()) ?
					0L : Long.valueOf(o.toString());

			if (maxId > neededToRemain){
				SQLUtil.executeUpdate(
						"delete from HARVEST where HARVEST_ID<=" + (maxId-neededToRemain), conn);
			}

			SQLUtil.executeUpdate("delete from HARVEST_MESSAGE" +
					" where HARVEST_ID not in (select HARVEST_ID from HARVEST)", conn);
			
			conn.commit();
		}
		catch (SQLException e){
			SQLUtil.rollback(conn);
			throw new DAOException(e.toString(), e);
		}
		finally{
			SQLUtil.close(conn);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see eionet.cr.dao.HarvestSourceDAO#deleteTriplesOfMissingSources()
	 */
	public void deleteTriplesOfMissingSources() throws DAOException {
		
		Connection conn = null;
		try{
			conn = getConnection();
			conn.setAutoCommit(false);
			
			String sql = "delete from SPO where SOURCE not in (select URL_HASH from HARVEST_SOURCE)";
			SQLUtil.executeUpdate(sql, conn);
			sql = "delete from SPO where OBJ_DERIV_SOURCE not in (select URL_HASH from HARVEST_SOURCE)";
			SQLUtil.executeUpdate(sql, conn);
			
			conn.commit();
		}
		catch (SQLException e){
			SQLUtil.rollback(conn);
			throw new DAOException(e.toString(),e);
		}
		finally{
			SQLUtil.close(conn);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see eionet.cr.dao.HarvestSourceDAO#deleteSourceByUrl(java.lang.String)
	 */
	public void deleteSourceByUrl(String url) throws DAOException {
		
    	// we'll need those wrappers later.
    	List<Object> hashList = new LinkedList<Object>();
    	hashList.add(Hashes.spoHash(url));
    	List<Object> urlList = new LinkedList<Object>();
    	urlList.add(url);
    	
		Connection conn = null;
		try{
			// start transaction
			conn = getConnection();
			conn.setAutoCommit(false);
			
			// get ID of the harvest source identified by the given URL
			List<Long> sourceIds = executeQuery(
					"select HARVEST_SOURCE_ID from HARVEST_SOURCE where URL_HASH = ?",
					hashList,
					new SingleObjectReader<Long>());
			String harvestSourceIdsCSV = Util.toCSV(sourceIds);
			
			// if harvest source ID found, delete harvests and harvest messages by it 
			if (!StringUtils.isBlank(harvestSourceIdsCSV)){
				
				List<Long> harvestIds = executeQuery(
					"select HARVEST_ID from HARVEST where HARVEST_SOURCE_ID in ("
						+ harvestSourceIdsCSV + ")",
					null,
					new SingleObjectReader<Long>());

				String harvestIdsCSV = Util.toCSV(harvestIds);
				if (!StringUtils.isBlank(harvestIdsCSV)){
					SQLUtil.executeUpdate("delete from HARVEST_MESSAGE where HARVEST_ID in ("
							+ harvestIdsCSV + ")", conn);
				}
				SQLUtil.executeUpdate("delete from HARVEST where HARVEST_SOURCE_ID in (" +
						harvestSourceIdsCSV + ")", conn);
			}
			
			// delete dependencies of this harvest source in other tables
			SQLUtil.executeUpdate("delete from HARVEST_SOURCE where URL_HASH=?", hashList, conn);
			SQLUtil.executeUpdate("delete from HARVEST_SOURCE where SOURCE=?", hashList, conn);
			SQLUtil.executeUpdate("delete from SPO where SOURCE=?", hashList, conn);
			SQLUtil.executeUpdate("delete from SPO where OBJ_DERIV_SOURCE=?", hashList, conn);
			SQLUtil.executeUpdate("delete from UNFINISHED_HARVEST where SOURCE=?", hashList, conn);
			SQLUtil.executeUpdate("delete from URGENT_HARVEST_QUEUE where URL=?" , urlList, conn);
			SQLUtil.executeUpdate("delete from REMOVE_SOURCE_QUEUE where URL=?", urlList, conn);
			
			// end transaction
			conn.commit();
		}
		catch (Exception e){
			SQLUtil.rollback(conn);
			throw new DAOException(e.getMessage(), e);
		}
		finally{
			SQLUtil.close(conn);
		}
	}

	/** */
	private static final String editSourceSQL = "update HARVEST_SOURCE set URL=?," +
			" URL_HASH=?, EMAILS=?,INTERVAL_MINUTES=? where HARVEST_SOURCE_ID=?";
	/*
	 * (non-Javadoc)
	 * @see eionet.cr.dao.HarvestSourceDAO#editSource(eionet.cr.dto.HarvestSourceDTO)
	 */
	public void editSource(HarvestSourceDTO source) throws DAOException {
		
		List<Object> values = new ArrayList<Object>();
		values.add(source.getUrl());
		values.add(Long.valueOf(source.getUrl()==null ? 0 : Hashes.spoHash(source.getUrl())));
		values.add(source.getEmails());
		values.add(source.getIntervalMinutes());
		values.add(source.getSourceId());
		
		Connection conn = null;
		try{
			conn = getConnection();
			SQLUtil.executeUpdate(editSourceSQL, values, conn);
		}
		catch (Exception e){
			throw new DAOException(e.getMessage(), e);
		}
		finally{
			SQLUtil.close(conn);
		}
	}

	/** */
	private static final String getSourcesByIdSQL =
		"select * from HARVEST_SOURCE where HARVEST_SOURCE_ID=?";
	/*
	 * (non-Javadoc)
	 * @see eionet.cr.dao.HarvestSourceDAO#getHarvestSourceById(java.lang.Integer)
	 */
	public HarvestSourceDTO getHarvestSourceById(Integer harvestSourceID) throws DAOException {
		
    	List<Object> values = new ArrayList<Object>();
    	values.add(harvestSourceID);
    	List<HarvestSourceDTO> list = executeQuery(
    			getSourcesByIdSQL, values, new HarvestSourceDTOReader());
    	return (list!=null && !list.isEmpty()) ? list.get(0) : null;
	}

	/** */
	private static final String getSourcesByUrlSQL = "select * from HARVEST_SOURCE where URL=?";
	/*
	 * (non-Javadoc)
	 * @see eionet.cr.dao.HarvestSourceDAO#getHarvestSourceByUrl(java.lang.String)
	 */
	public HarvestSourceDTO getHarvestSourceByUrl(String url) throws DAOException {
		
		List<Object> values = new ArrayList<Object>();
		values.add(url);
		List<HarvestSourceDTO> list = executeQuery(
				getSourcesByUrlSQL, values, new HarvestSourceDTOReader());
		return (list!=null && !list.isEmpty()) ? list.get(0) : null;
	}

	/** */
	private static final String getNextScheduledSourcesSQL =
		
		"select * from HARVEST_SOURCE where INTERVAL_MINUTES>0 and" +
		" cast(extract(epoch from now()-(coalesce(LAST_HARVEST,(time_created - interval_minutes * interval '1 minute'))))/60 as integer) >= INTERVAL_MINUTES" +
		" order by" +
		" cast(extract(epoch from now()-(coalesce(LAST_HARVEST,(time_created - interval_minutes * interval '1 minute'))))/60 as integer)/INTERVAL_MINUTES" +
		" desc limit ?";
	
	/*
	 * (non-Javadoc)
	 * @see eionet.cr.dao.HarvestSourceDAO#getNextScheduledSources(int)
	 */
	public List<HarvestSourceDTO> getNextScheduledSources(int numOfSegments) throws DAOException {
		
		Long numberOfSources = executeQueryUniqueResult(
				"select count(*) from HARVEST_SOURCE",
				null,
				new SingleObjectReader<Long>());
		if (numberOfSources== null){
			numberOfSources = Long.valueOf(0);
		}
		
		int limit = Math.round((float)numberOfSources/(float)numOfSegments);
		List<Object> values = new ArrayList<Object>();
    	values.add(new Integer(limit));
    	
		return executeQuery(getNextScheduledSourcesSQL, values, new HarvestSourceDTOReader());
	}

	/*
	 * (non-Javadoc)
	 * @see eionet.cr.dao.HarvestSourceDAO#getScheduledForDeletion()
	 */
	public List<String> getScheduledForDeletion() throws DAOException {
		
		return executeQuery("select URL from REMOVE_SOURCE_QUEUE",
				null, new SingleObjectReader<String>());
	}

	/*
	 * (non-Javadoc)
	 * @see eionet.cr.dao.HarvestSourceDAO#queueSourcesForDeletion(java.util.List)
	 */
	public void queueSourcesForDeletion(List<String> urls) throws DAOException {
		
    	if (urls == null || urls.isEmpty()) {
    		return;
    	}
    	StringBuffer sql = new StringBuffer("INSERT INTO REMOVE_SOURCE_QUEUE (URL) VALUES ");
    	List<Object> params = new LinkedList<Object>();
    	int i = 0;
    	for (String url : urls) {
    		sql.append("(?)");
    		if (++i < urls.size()) {
    			sql.append(',');
    		}
    		params.add(url);
    	}
    	execute(sql.toString(), params);
	}

	/** */
    private static final String updateHarvestFinishedSQL =
    	"update HARVEST_SOURCE set STATEMENTS=?, RESOURCES=?," +
    	" LAST_HARVEST_FAILED=cast(? as ynboolean) where HARVEST_SOURCE_ID=?";
    private static final String updateHarvestFinishedSQL_avail =
    	"update HARVEST_SOURCE set STATEMENTS=?, RESOURCES=?," +
    	" COUNT_UNAVAIL=(case when ?=1 then 0 else (COUNT_UNAVAIL+1) end)," +
    	" LAST_HARVEST_FAILED=cast(? as ynboolean) where HARVEST_SOURCE_ID=?";
	/*
	 * (non-Javadoc)
	 * @see eionet.cr.dao.HarvestSourceDAO#updateHarvestFinished(int, java.lang.Integer, java.lang.Integer, java.lang.Boolean, boolean)
	 */
	public void updateHarvestFinished(int sourceId, Integer numStatements,
			Integer numResources, Boolean sourceAvailable, boolean failed)
			throws DAOException {
		
		List<Object> values = new ArrayList<Object>();
		values.add(numStatements);
		values.add(numResources);
		if (sourceAvailable!=null)
			values.add(sourceAvailable.booleanValue()==true ? new Integer(1) : new Integer(0));
		values.add(YesNoBoolean.format(failed));
		values.add(new Integer(sourceId));		
		
		Connection conn = null;
		try{
			conn = getConnection();
			SQLUtil.executeUpdate(sourceAvailable!=null ? updateHarvestFinishedSQL_avail : updateHarvestFinishedSQL, values, conn);
		}
		catch (Exception e){
			throw new DAOException(e.getMessage(), e);
		}
		finally{
			ConnectionUtil.closeConnection(conn);
		}
	}

	/** */
	private static final String updateHarvestStartedSQL =
		"update HARVEST_SOURCE set LAST_HARVEST=NOW() where HARVEST_SOURCE_ID=?";
	/*
	 * (non-Javadoc)
	 * @see eionet.cr.dao.HarvestSourceDAO#updateHarvestStarted(int)
	 */
	public void updateHarvestStarted(int sourceId) throws DAOException {
		
		List<Object> values = new ArrayList<Object>();
		values.add(new Integer(sourceId));		
		
		Connection conn = null;
		try{
			conn = getConnection();
			SQLUtil.executeUpdate(updateHarvestStartedSQL, values, conn);
		}
		catch (Exception e){
			throw new DAOException(e.getMessage(), e);
		}
		finally{
			SQLUtil.close(conn);
		}
	}
}
