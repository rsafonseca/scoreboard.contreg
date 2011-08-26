package eionet.cr.dao.virtuoso;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import eionet.cr.common.Predicates;
import eionet.cr.dao.DAOException;
import eionet.cr.dao.MockVirtuosoBaseDAOTest;
import eionet.cr.dao.readers.SubjectDataReader;
import eionet.cr.dto.SubjectDTO;

public class VirtuosoBaseDAOTest extends MockVirtuosoBaseDAOTest {

    public VirtuosoBaseDAOTest() {
        super("test-subjectsdata.nt");
    }

    @Test
    public void testSubjectsDataQuery() {
        String[] uris =
        {"http://rod.eionet.europa.eu/obligations/392", "http://rod.eionet.europa.eu/instruments/618",
                "http://rod.eionet.europa.eu/issues/15", "http://planner.eionet.europa.eu/WorkPlan_2010/PRJ1752885689",
                "http://planner.eionet.europa.eu/WorkPlan_2010/PRJ1607205326",
                "http://planner.eionet.europa.eu/WorkPlan_2010/PRJ9599558008",
                "http://planner.eionet.europa.eu/WorkPlan_2010/PRJ9193135010",
                "http://www.eea.europa.eu/data-and-maps/figures/potential-climatic-tipping-elements",
                "http://rod.eionet.europa.eu/obligations/171", "http://rod.eionet.europa.eu/obligations/661",
                "http://rod.eionet.europa.eu/obligations/606", "http://rod.eionet.europa.eu/obligations/136",
                "http://rod.eionet.europa.eu/obligations/520", "http://rod.eionet.europa.eu/obligations/522",
        "http://rod.eionet.europa.eu/obligations/521"};

        // String[] uris = {http://rod.eionet.europa.eu/obligations/392, http://rod.eionet.europa.eu/instruments/618,
        // http://rod.eionet.europa.eu/issues/15, http://planner.eionet.europa.eu/WorkPlan_2010/PRJ1752885689,
        // http://planner.eionet.europa.eu/WorkPlan_2010/PRJ1607205326, http://planner.eionet.europa.eu/WorkPlan_2010/PRJ9599558008,
        // http://planner.eionet.europa.eu/WorkPlan_2010/PRJ9193135010,
        // http://www.eea.europa.eu/data-and-maps/figures/potential-climatic-tipping-elements,
        // http://rod.eionet.europa.eu/obligations/171, http://rod.eionet.europa.eu/obligations/661,
        // "http://rod.eionet.europa.eu/obligations/606", "http://rod.eionet.europa.eu/obligations/136",
        // "http://rod.eionet.europa.eu/obligations/520", "http://rod.eionet.europa.eu/obligations/522",
        // "http://rod.eionet.europa.eu/obligations/521"};
        List<String> subjectUris = Arrays.asList(uris);
        // String[] predicateUris = {"http://www.w3.org/1999/02/22-rdf-syntax-ns#type",
        // "http://www.w3.org/2000/01/rdf-schema#label"};

        String[] gUris =
        {"http://rod.eionet.europa.eu/obligations", "http://rod.eionet.europa.eu/obligations.rdf",
                "http://www.eea.europa.eu/data-and-maps/figures/potential-climatic-tipping-elements/@@rdf",
                "http://planner.eionet.europa.eu/WorkPlan_2010/projects_rdf", "http://rod.eionet.europa.eu/issues",
        "http://rod.eionet.europa.eu/instruments.rdf"};
        List<String> graphUris = Arrays.asList(gUris);
        SubjectDataReader dataReader = new SubjectDataReader(subjectUris);
        dataReader.setBlankNodeUriPrefix(VirtuosoBaseDAO.BNODE_URI_PREFIX);

        // only these predicates will be queried for
        String[] neededPredicates = {Predicates.RDF_TYPE, Predicates.RDFS_LABEL};

        // logger.trace("Free-text search, getting the data of the found subjects");

        // get the subjects data
        List<SubjectDTO> resultList = null;
        try {
            resultList = getSubjectsData(subjectUris, neededPredicates, dataReader, graphUris);
        } catch (DAOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        SubjectDTO subjectDTO = resultList.get(0);

        assertTrue(subjectDTO.getPredicateCount() == 1);

        // String query = fakeDao.getSubjectsDataQ
        assertNotNull(resultList);
        // assertTrue(resultList.contains(arg0))
        assertEquals(dataReader.getSubjectsMap().size(), 15);

    }

    @Test
    public void testGetSubjectsData() {
        // subjecturis
        String[] s1 =
        {"http://rod.eionet.europa.eu/obligations/130", "http://rod.eionet.europa.eu/obligations/143",
                "http://rod.eionet.europa.eu/instruments/381", "http://rod.eionet.europa.eu/instruments/273",
                "http://rod.eionet.europa.eu/obligations/523", "http://rdfdata.eionet.europa.eu/eper/facilities/01035",
                "http://rdfdata.eionet.europa.eu/eper/facilities/01039",
                "http://rdfdata.eionet.europa.eu/eper/facilities/01046",
                "http://rdfdata.eionet.europa.eu/eper/facilities/01047",
                "http://rdfdata.eionet.europa.eu/eper/facilities/01052",
                "http://rdfdata.eionet.europa.eu/eper/facilities/01053",
                "http://rdfdata.eionet.europa.eu/eper/facilities/01054",
                "http://rdfdata.eionet.europa.eu/eper/facilities/01055",
                "http://rdfdata.eionet.europa.eu/eper/facilities/01068",
        "http://rdfdata.eionet.europa.eu/eper/facilities/01074"};

        List<String> subjectUris = Arrays.asList(s1);

        // predicateuris
        String[] predicateUris = {"http://www.w3.org/1999/02/22-rdf-syntax-ns#type", "http://www.w3.org/2000/01/rdf-schema#label"};

        // graphuris
        String s2[] =
        {"http://rdfdata.eionet.europa.eu/eper/send_all", "http://rod.eionet.europa.eu/obligations",
                "http://rod.eionet.europa.eu/obligations.rdf", "http://rod.eionet.europa.eu/instruments.rdf"};

        List<String> graphUris = Arrays.asList(s2);

        SubjectDataReader dataReader = new SubjectDataReader(subjectUris);
        dataReader.setBlankNodeUriPrefix(VirtuosoBaseDAO.BNODE_URI_PREFIX);
        try {
            getSubjectsData(subjectUris, predicateUris, dataReader, graphUris);
        } catch (DAOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        assertEquals(
                "select * where {graph ?g {?s ?p ?o. filter (?s IN (?subjectValue1,?subjectValue2,?subjectValue3,?subjectValue4,?subjectValue5,?subjectValue6,?subjectValue7,?subjectValue8,?subjectValue9,?subjectValue10,?subjectValue11,?subjectValue12,?subjectValue13,?subjectValue14,?subjectValue15)) "
                + "filter (?p IN(?predicateValue1,?predicateValue2)) filter (?g IN(?graphValue1,?graphValue2,?graphValue3,?graphValue4)) OPTIONAL { ?g ?crLastModified ?t } }} ORDER BY ?s ?p",
                // "select * where {graph ?g {?s ?p ?o. filter (?s IN (<http://rod.eionet.europa.eu/obligations/130>, <http://rod.eionet.europa.eu/obligations/143>, <http://rod.eionet.europa.eu/instruments/381>, <http://rod.eionet.europa.eu/instruments/273>, <http://rod.eionet.europa.eu/obligations/523>, <http://rdfdata.eionet.europa.eu/eper/facilities/01035>, <http://rdfdata.eionet.europa.eu/eper/facilities/01039>, <http://rdfdata.eionet.europa.eu/eper/facilities/01046>, <http://rdfdata.eionet.europa.eu/eper/facilities/01047>, <http://rdfdata.eionet.europa.eu/eper/facilities/01052>, <http://rdfdata.eionet.europa.eu/eper/facilities/01053>, <http://rdfdata.eionet.europa.eu/eper/facilities/01054>, <http://rdfdata.eionet.europa.eu/eper/facilities/01055>, <http://rdfdata.eionet.europa.eu/eper/facilities/01068>, <http://rdfdata.eionet.europa.eu/eper/facilities/01074>)) filter (?p = <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> || ?p = <http://www.w3.org/2000/01/rdf-schema#label>) filter (?g = <http://rdfdata.eionet.europa.eu/eper/send_all> || ?g = <http://rod.eionet.europa.eu/obligations> || ?g = <http://rod.eionet.europa.eu/obligations.rdf> || ?g = <http://rod.eionet.europa.eu/instruments.rdf>) OPTIONAL { ?g <http://cr.eionet.europa.eu/ontologies/contreg.rdf#contentLastModified> ?t } }} ORDER BY ?s ?p",
                getSPARQL());

    }

}
