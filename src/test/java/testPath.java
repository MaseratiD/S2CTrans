import org.junit.Test;
import translator.TripleParser;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class testPath {
    @Test
    public void testInverseRelationship() throws IOException {
        String fileName = Thread.currentThread().getStackTrace()[1].getMethodName() + ".txt";
        String queryString = testFileInput.inputQuery(fileName);
        TripleParser tp = new TripleParser();
        tp.addRelationshipIntoGraph("http://neo4j.org/vocab/rel#created");
        assertEquals("MATCH (person)<-[created:`http://neo4j.org/vocab/rel#created`]-(software) " +
                        "RETURN software",
                tp.parseSparqlQuery(queryString));
    }

    @Test
    public void testZeroOrMoreRelationship() throws IOException {
        String fileName = Thread.currentThread().getStackTrace()[1].getMethodName() + ".txt";
        String queryString = testFileInput.inputQuery(fileName);
        TripleParser tp = new TripleParser();
        tp.addRelationshipIntoGraph("http://neo4j.org/vocab/rel#created");
        assertEquals("MATCH (person {`http://neo4j.org/vocab/v#age`: 11})-" +
                        "[created:`http://neo4j.org/vocab/rel#created`*0..]->(software) " +
                        "RETURN software",
                tp.parseSparqlQuery(queryString));
    }

    @Test
    public void testOneOrMoreRelationship() throws IOException {
        String fileName = Thread.currentThread().getStackTrace()[1].getMethodName() + ".txt";
        String queryString = testFileInput.inputQuery(fileName);
        TripleParser tp = new TripleParser();
        tp.addRelationshipIntoGraph("http://neo4j.org/vocab/rel#created");
        assertEquals("MATCH (person)-[created:`http://neo4j.org/vocab/rel#created`*1..]->(software) " +
                        "RETURN software",
                tp.parseSparqlQuery(queryString));
    }

    @Test
    public void testTwoHopsRelationship() throws IOException {
        String fileName = Thread.currentThread().getStackTrace()[1].getMethodName() + ".txt";
        String queryString = testFileInput.inputQuery(fileName);
        TripleParser tp = new TripleParser();
        tp.addRelationshipIntoGraph("http://neo4j.org/vocab/rel#created");
        tp.addRelationshipIntoGraph("http://neo4j.org/vocab/rel#notCreated");
        assertEquals("MATCH (person:`http://www.w3.org/1999/02/22-rdf-syntax-ns#Person`)-" +
                        "[:`http://neo4j.org/vocab/rel#created`]->()-" +
                        "[:`http://neo4j.org/vocab/rel#created`]->" +
                        "(software:`http://www.w3.org/1999/02/22-rdf-syntax-ns#Software`) " +
                        "RETURN software",
                tp.parseSparqlQuery(queryString));
    }

    @Test
    public void testThreeHopsRelationship() throws IOException {
        String fileName = Thread.currentThread().getStackTrace()[1].getMethodName() + ".txt";
        String queryString = testFileInput.inputQuery(fileName);
        TripleParser tp = new TripleParser();
        tp.addRelationshipIntoGraph("http://neo4j.org/vocab/rel#created");
        assertEquals("MATCH (person)-[:`http://neo4j.org/vocab/rel#created`]->()" +
                        "-[:`http://neo4j.org/vocab/rel#created`]->()" +
                        "-[:`http://neo4j.org/vocab/rel#created`]->(software) " +
                        "RETURN software",
                tp.parseSparqlQuery(queryString));
    }

    @Test
    public void testInverseRelationship2() throws IOException {
        String fileName = Thread.currentThread().getStackTrace()[1].getMethodName() + ".txt";
        String queryString = testFileInput.inputQuery(fileName);
        TripleParser tp = new TripleParser();
        tp.addRelationshipIntoGraph("http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/vocabulary/reviewFor");
        assertEquals("MATCH (product:`http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/instances/ProductType1`)" +
                        "<-[reviewFor:`http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/vocabulary/reviewFor`]-" +
                        "(review:`http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/vocabulary/Review`) " +
                        "WHERE product.`http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/vocabulary/productPropertyNumeric1` > 1000 " +
                        "RETURN DISTINCT product.`http://www.w3.org/2000/01/rdf-schema#label` " +
                        "ORDER BY product.`http://www.w3.org/2000/01/rdf-schema#label` DESC " +
                        "LIMIT 10",
                tp.parseSparqlQuery(queryString));
    }

    @Test
    public void testZeroOrMoreRelationship2() throws IOException {
        String fileName = Thread.currentThread().getStackTrace()[1].getMethodName() + ".txt";
        String queryString = testFileInput.inputQuery(fileName);
        TripleParser tp = new TripleParser();
        tp.addRelationshipIntoGraph("http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/vocabulary/vendor");
        assertEquals("MATCH (offer:`http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/vocabulary/Offer`)" +
                        "-[vendor:`http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/vocabulary/vendor`*0..]->" +
                        "(vendor1:`http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/vocabulary/Vendor`) " +
                        "WHERE offer.`http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/vocabulary/deliveryDays` > 4 " +
                        "RETURN DISTINCT vendor1.`http://www.w3.org/2000/01/rdf-schema#label` " +
                        "ORDER BY vendor1.`http://www.w3.org/2000/01/rdf-schema#label` ASC " +
                        "LIMIT 10",
                tp.parseSparqlQuery(queryString));
    }

    @Test
    public void testOneOrMoreRelationship2() throws IOException {
        String fileName = Thread.currentThread().getStackTrace()[1].getMethodName() + ".txt";
        String queryString = testFileInput.inputQuery(fileName);
        TripleParser tp = new TripleParser();
        tp.addRelationshipIntoGraph("http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/vocabulary/reviewFor");
        assertEquals("MATCH (review:`http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/vocabulary/Review`)" +
                        "-[reviewFor:`http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/vocabulary/reviewFor`*1..]->" +
                        "(product1:`http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/instances/ProductType1`) " +
                        "WHERE product1.`http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/vocabulary/productPropertyNumeric1` > 1000 " +
                        "RETURN DISTINCT review.`http://purl.org/dc/elements/1.1/title` " +
                        "ORDER BY review.`http://purl.org/dc/elements/1.1/title` DESC " +
                        "LIMIT 10",
                tp.parseSparqlQuery(queryString));
    }

    @Test
    public void testTwoHopsSame() throws IOException {
        String fileName = Thread.currentThread().getStackTrace()[1].getMethodName() + ".txt";
        String queryString = testFileInput.inputQuery(fileName);
        TripleParser tp = new TripleParser();
        tp.addRelationshipIntoGraph("http://purl.org/dc/elements/1.1/publisher");
        assertEquals("MATCH (product:`http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/instances/ProductType1`)-" +
                        "[:`http://purl.org/dc/elements/1.1/publisher`]->()-" +
                        "[:`http://purl.org/dc/elements/1.1/publisher`]->" +
                        "(producer:`http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/vocabulary/Producer`) " +
                        "WHERE product.`http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/vocabulary/productPropertyNumeric1` > 1000 " +
                        "RETURN DISTINCT product.`http://www.w3.org/2000/01/rdf-schema#label` " +
                        "ORDER BY product.`http://www.w3.org/2000/01/rdf-schema#label` ASC " +
                        "LIMIT 10",
                tp.parseSparqlQuery(queryString));
    }

    @Test
    public void testTwoHopsDiff() throws IOException {
        String fileName = Thread.currentThread().getStackTrace()[1].getMethodName() + ".txt";
        String queryString = testFileInput.inputQuery(fileName);
        TripleParser tp = new TripleParser();
        tp.addRelationshipIntoGraph("http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/vocabulary/reviewFor");
        tp.addRelationshipIntoGraph("http://purl.org/dc/elements/1.1/publisher");
        assertEquals("MATCH (review:`http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/vocabulary/Review`)" +
                        "-[:`http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/vocabulary/reviewFor`]->()" +
                        "-[:`http://purl.org/dc/elements/1.1/publisher`]->" +
                        "(producer:`http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/vocabulary/Producer`) " +
                        "RETURN DISTINCT review.`http://purl.org/dc/elements/1.1/title`, " +
                        "producer.`http://www.w3.org/2000/01/rdf-schema#label` " +
                        "ORDER BY producer.`http://www.w3.org/2000/01/rdf-schema#label` ASC " +
                        "LIMIT 10",
                tp.parseSparqlQuery(queryString));
    }

    @Test
    public void testThreeHopsDiff() throws IOException {
        String fileName = Thread.currentThread().getStackTrace()[1].getMethodName() + ".txt";
        String queryString = testFileInput.inputQuery(fileName);
        TripleParser tp = new TripleParser();
        tp.addRelationshipIntoGraph("http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/vocabulary/reviewFor");
        tp.addRelationshipIntoGraph("http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/vocabulary/productFeature");
        tp.addRelationshipIntoGraph("http://purl.org/dc/elements/1.1/publisher");
        assertEquals("MATCH (review:`http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/vocabulary/Review` " +
                        "{`http://purl.org/dc/elements/1.1/title`: 'detents anarchism overinflated raises scientists'})" +
                        "-[:`http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/vocabulary/reviewFor`]->()" +
                        "-[:`http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/vocabulary/productFeature`]->()" +
                        "-[:`http://purl.org/dc/elements/1.1/publisher`]->(resource) " +
                        "RETURN DISTINCT resource " +
                        "LIMIT 3",
                tp.parseSparqlQuery(queryString));
    }
}
