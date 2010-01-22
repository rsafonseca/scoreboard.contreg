

package eionet.cr.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import eionet.cr.dto.RawTripleDTO;
import eionet.cr.dto.SubjectDTO;
import eionet.cr.search.SearchException;
import eionet.cr.search.util.PredicateLabels;
import eionet.cr.search.util.SearchExpression;
import eionet.cr.search.util.SubProperties;
import eionet.cr.search.util.SubjectDataReader;
import eionet.cr.util.Pair;
import eionet.cr.util.SortingRequest;

/**
 * Helper dao to use in different searches.
 * 
 * @author Aleksandr Ivanov
 * <a href="mailto:aleksandr.ivanov@tietoenator.com">contact</a>
 */
public interface HelperDAO extends DAO {

	
	/**
	 * fetches recently discovered files.
	 * @param limit how many files to fetch
	 * @return
	 * @throws DAOException
	 */
	List<Pair<String, String>> getRecentlyDiscoveredFiles(int limit) throws DAOException;
	
	/**
	 * @param predicateUri
	 * @return
	 * @throws SearchException
	 */
	Collection<String> getPicklistForPredicate(String predicateUri) throws SearchException;

	/**
	 * 
	 * @param subjectDTO
	 * @throws DAOException 
	 */
	void addTriples(SubjectDTO subjectDTO) throws DAOException;
	
	/**
	 * 
	 * @param uri
	 * @param firstSeenSourceUri
	 * @throws DAOException
	 */
	void addResource(String uri, String firstSeenSourceUri) throws DAOException;
	
	/**
	 * 
	 * @param subjectTypes
	 * @return
	 * @throws DAOException
	 */
	HashMap<String,String> getAddibleProperties(Collection<String> subjectTypes) throws DAOException;
	
	/**
	 * 
	 * @param subject
	 * @throws DAOException
	 */
	void deleteTriples(SubjectDTO subject) throws DAOException;

	/**
	 * 
	 * @param subjectUri
	 * @return
	 * @throws DAOException
	 */
	String getSubjectSchemaUri(String subjectUri) throws DAOException;
	
	/**
	 * fetch sample triplets for given source.
	 * 
	 * @param url - source url
	 * @param limit - how many to fetch
	 * @return
	 * @throws DAOException
	 */
	Pair<Integer, List<RawTripleDTO>> getSampleTriples(String url, int limit) throws DAOException;
	
	/**
	 * 
	 * @param predicateUri
	 * @return
	 * @throws SearchException 
	 */
	boolean isAllowLiteralSearch(String predicateUri) throws SearchException;
	
	/**
	 * 
	 * @param typeUri
	 * @return
	 * @throws DAOException
	 */
	List<SubjectDTO> getPredicatesUsedForType(String typeUri) throws DAOException;
	
	/**
	 * Gets sources that have some spatial content.
	 * 
	 * @return
	 * @throws DAOException
	 */
	List<String> getSpatialSources() throws DAOException;

	/**
	 * 
	 * @param subjectHash
	 * @return
	 * @throws DAOException
	 */
	SubjectDTO getSubject(Long subjectHash) throws DAOException;
	
	/**
	 * 
	 * @param subjectHashes
	 * @return
	 * @throws DAOException
	 */
	PredicateLabels getPredicateLabels(Set<Long> subjectHashes) throws DAOException;
	
	/**
	 * 
	 * @param subjectHashes
	 * @return
	 * @throws DAOException
	 */
	SubProperties getSubProperties(Set<Long> subjectHashes) throws DAOException;
}