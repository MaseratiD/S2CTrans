import org.junit.Test;
import translator.TripleParser;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class testComplex {
    @Test
    public void testComplexQuery() throws IOException {
        String fileName = Thread.currentThread().getStackTrace()[1].getMethodName() + ".txt";
        String queryString = testFileInput.inputQuery(fileName);
        TripleParser tp = new TripleParser();
        tp.addRelationshipIntoGraph("http://neo4j.org/vocab/rel#created");
        assertEquals("MATCH (person:`http://neo4j.org/vocab/label#person`:`http://neo4j.org/vocab/label#student` " +
                        "{`http://neo4j.org/vocab/v#name`: '11', `http://neo4j.org/vocab/v#star`: 11})-" +
                        "[created:`http://neo4j.org/vocab/rel#created`]->" +
                        "(project:`http://neo4j.org/vocab/label#project`)-" +
                        "[created:`http://neo4j.org/vocab/rel#created`]->(subproject) " +
                        "WHERE person.`http://neo4j.org/vocab/v#age` < 30 " +
                        "RETURN count(project) AS count",
                tp.parseSparqlQuery(queryString));
    }

    @Test
    public void testComplexQuery2() throws IOException {
        String fileName = Thread.currentThread().getStackTrace()[1].getMethodName() + ".txt";
        String queryString = testFileInput.inputQuery(fileName);
        TripleParser tp = new TripleParser();
        tp.addRelationshipIntoGraph("http://neo4j.org/vocab/rel#created");
        assertEquals("MATCH (person:`http://neo4j.org/vocab/label#person`)-" +
                        "[created:`http://neo4j.org/vocab/rel#created`]->(project) " +
                        "WHERE person.`http://neo4j.org/vocab/v#age` < 30 " +
                        "RETURN count(project) AS count " +
                        "LIMIT 2",
                tp.parseSparqlQuery(queryString));
    }

    @Test
    public void testSuperComplexQuery() throws IOException {
        String fileName = Thread.currentThread().getStackTrace()[1].getMethodName() + ".txt";
        String queryString = testFileInput.inputQuery(fileName);
        TripleParser tp = new TripleParser();
        tp.addRelationshipIntoGraph("http://neo4j.org/vocab/rel#created");
        assertEquals("MATCH (person:`http://neo4j.org/vocab/label#person`" +
                        ":`http://neo4j.org/vocab/label#student` " +
                        "{`http://neo4j.org/vocab/v#name`: '11', `http://neo4j.org/vocab/v#star`: 11})-" +
                        "[created:`http://neo4j.org/vocab/rel#created`]->" +
                        "(project:`http://neo4j.org/vocab/label#project`)-" +
                        "[created:`http://neo4j.org/vocab/rel#created`]->(subproject) " +
                        "WHERE ((person.`http://neo4j.org/vocab/v#age` > 30 AND " +
                        "person.`http://neo4j.org/vocab/v#age` < 50) OR " +
                        "project.`http://neo4j.org/vocab/v#lang` = 'java') " +
                        "OPTIONAL MATCH (project {`http://neo4j.org/vocab/v#lang`: 'python'}) " +
                        "RETURN DISTINCT project.`http://neo4j.org/vocab/v#lang` " +
                        "ORDER BY person.`http://neo4j.org/vocab/v#age` ASC " +
                        "LIMIT 3",
                tp.parseSparqlQuery(queryString));
    }

    @Test
    public void testAST() throws IOException {
        String fileName = Thread.currentThread().getStackTrace()[1].getMethodName() + ".txt";
        String queryString = testFileInput.inputQuery(fileName);
        TripleParser tp = new TripleParser();
        tp.addRelationshipIntoGraph("http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/vocabulary/product");
        assertEquals("MATCH (offer:`http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/vocabulary/Offer`)-" +
                        "[product:`http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/vocabulary/product`]->" +
                        "(product1:`http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/instances/ProductType1`) " +
                        "WHERE (product1.`http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/vocabulary/productPropertyNumeric1` > 600 " +
                        "AND product1.`http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/vocabulary/productPropertyNumeric1` < 800) " +
                        "OPTIONAL MATCH (" +
                        "product1 {`http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/vocabulary/productPropertyNumeric2`: 1000}) " +
                        "RETURN DISTINCT " +
                        "product1.`http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/vocabulary/productPropertyNumeric1`, " +
                        "max(offer.`http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/vocabulary/price`) AS maxPrice " +
                        "ORDER BY maxPrice ASC " +
                        "LIMIT 10",
                tp.parseSparqlQuery(queryString));
    }
}
