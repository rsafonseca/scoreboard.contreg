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
package eionet.cr.api.feeds.xmlconv;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import eionet.cr.api.feeds.SubjectsRDFWriter;
import eionet.cr.api.feeds.amp.AmpFeedServlet;
import eionet.cr.common.Namespace;
import eionet.cr.common.Predicates;
import eionet.cr.common.Subjects;
import eionet.cr.dao.SearchDAO;
import eionet.cr.dao.mysql.MySQLDAOFactory;
import eionet.cr.dto.SubjectDTO;
import eionet.cr.util.Pair;
import eionet.cr.util.pagination.PagingRequest;

/**
 * 
 * @author <a href="mailto:jaanus.heinlaid@tietoenator.com">Jaanus Heinlaid</a>
 *
 */
public class XmlConvFeedServlet extends HttpServlet{
	
	/** */
	private static final String SCHEMA_PARAM = "schema";
	
	/** */
	private static final Logger logger = Logger.getLogger(XmlConvFeedServlet.class);

	/*
	 * (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		
		String methodName = XmlConvFeedServlet.class.getSimpleName() + ".doGet()";
		logger.debug("Entered " + methodName);
		
		response.setContentType("text/xml");

		try {
			List<SubjectDTO> subjects = null;
			String xmlSchema = request.getParameter(SCHEMA_PARAM);
			if (!StringUtils.isBlank(xmlSchema)){
				
				SearchDAO searchDao = MySQLDAOFactory.get().getDao(SearchDAO.class);
				Map<String, String> criteria = new HashMap<String, String>();
				criteria.put(Predicates.CR_SCHEMA, xmlSchema);

				Pair<Integer, List<SubjectDTO>> results = searchDao.searchByFilters(
						criteria,
						null, 
						null,
						null);

				int subjectCount = results==null ? 0 : (results.getRight()==null ? 0 : results.getRight().size());  
				logger.debug(methodName + ", " + subjectCount + " subjects found in total");
				subjects = results.getRight();
			}

			SubjectsRDFWriter rdfWriter = new SubjectsRDFWriter(request.getParameter(AmpFeedServlet.INCLUDE_DERIVED_VALUES)!=null);
			rdfWriter.addNamespace(Namespace.CR);
			rdfWriter.addNamespace(Namespace.DC);
			rdfWriter.addNamespace(Namespace.OWL);
			rdfWriter.addNamespace(Namespace.ROD);
			rdfWriter.addNamespace(Namespace.EPER);

			rdfWriter.write(subjects, response.getOutputStream());
		}
		catch (Exception e) {
			logger.error("Error in " + methodName, e);
			if (!response.isCommitted()) {
				response.sendError(500);
			}
		}

		response.getOutputStream().flush();

	}
}
