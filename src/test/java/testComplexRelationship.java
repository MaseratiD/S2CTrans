import org.junit.Test;
import translator.TripleParser;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class testComplexRelationship {
    @Test
    public void testRelationshipChainQuery() throws IOException {
        String fileName = Thread.currentThread().getStackTrace()[1].getMethodName() + ".txt";
        String queryString = testFileInput.inputQuery(fileName);
        TripleParser tp = new TripleParser();
        tp.addRelationshipIntoGraph("http://neo4j.org/vocab/rel#created");
        assertEquals("MATCH (person {`http://neo4j.org/vocab/v#name`: 'daniel'})-" +
                        "[created:`http://neo4j.org/vocab/rel#created`]->(software)-" +
                        "[created:`http://neo4j.org/vocab/rel#created`]->(location) " +
                        "RETURN person.`http://neo4j.org/vocab/v#age`, " +
                        "location.`http://neo4j.org/vocab/v#value`, " +
                        "location.`http://neo4j.org/vocab/v#startTime`",
                tp.parseSparqlQuery(queryString));
    }

    @Test
    public void testStarRelationshipQuery() throws IOException {
        String fileName = Thread.currentThread().getStackTrace()[1].getMethodName() + ".txt";
        String queryString = testFileInput.inputQuery(fileName);
        TripleParser tp = new TripleParser();
        tp.addRelationshipIntoGraph("http://neo4j.org/vocab/rel#created");
        assertEquals("MATCH (person {`http://neo4j.org/vocab/v#name`: 'daniel'})-" +
                        "[created:`http://neo4j.org/vocab/rel#created`]->(software)-" +
                        "[created:`http://neo4j.org/vocab/rel#created`]->(location), " +
                        "(person)-[created:`http://neo4j.org/vocab/rel#created`]->(location) " +
                        "RETURN person.`http://neo4j.org/vocab/v#age`, " +
                        "location.`http://neo4j.org/vocab/v#value`, " +
                        "location.`http://neo4j.org/vocab/v#startTime`",
                tp.parseSparqlQuery(queryString));
    }
}
