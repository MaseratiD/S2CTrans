import org.junit.Test;
import translator.TripleParser;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class testURI {
    @Test
    public void testSubjectURI() throws IOException {
        String fileName = Thread.currentThread().getStackTrace()[1].getMethodName() + ".txt";
        String queryString = testFileInput.inputQuery(fileName);
        TripleParser tp = new TripleParser();
        tp.addRelationshipIntoGraph("http://neo4j.org/vocab/rel#created");
        assertEquals("MATCH (product)-[created:`http://neo4j.org/vocab/rel#created`]->(prodFeature), " +
                        "(Product8 {uri: 'http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/instances/dataFromProducer1/Product8'})-" +
                        "[created:`http://neo4j.org/vocab/rel#created`]->(prodFeature) " +
                        "WHERE (product.`http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/vocabulary/productPropertyNumeric1` < 120 " +
                        "AND product.`http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/vocabulary/productPropertyNumeric1` > 20) " +
                        "RETURN DISTINCT product.`http://www.w3.org/2000/01/rdf-schema#label` " +
                        "ORDER BY product.`http://www.w3.org/2000/01/rdf-schema#label` ASC " +
                        "LIMIT 5",
                tp.parseSparqlQuery(queryString));
    }
}
