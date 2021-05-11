import org.junit.Test;
import translator.TripleParser;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class testOptional {
    @Test
    public void testOptional() throws IOException {
        String fileName = Thread.currentThread().getStackTrace()[1].getMethodName() + ".txt";
        String queryString = testFileInput.inputQuery(fileName);
        TripleParser tp = new TripleParser();
        tp.addRelationshipIntoGraph("http://neo4j.org/vocab/rel#created");
        assertEquals("MATCH (person:`http://neo4j.org/vocab/label#person`)-" +
                        "[created:`http://neo4j.org/vocab/rel#created`]->" +
                        "(software {`http://neo4j.org/vocab/v#lang`: 'java'}) " +
                        "OPTIONAL MATCH (software {`http://neo4j.org/vocab/v#lang`: 'python'}) " +
                        "RETURN person",
                tp.parseSparqlQuery(queryString));
    }

    @Test
    public void testOptionalRel() throws IOException {
        String fileName = Thread.currentThread().getStackTrace()[1].getMethodName() + ".txt";
        String queryString = testFileInput.inputQuery(fileName);
        TripleParser tp = new TripleParser();
        tp.addRelationshipIntoGraph("http://neo4j.org/vocab/rel#created");
        assertEquals("MATCH (person:`http://neo4j.org/vocab/label#person`)-" +
                        "[created:`http://neo4j.org/vocab/rel#created`]->" +
                        "(software {`http://neo4j.org/vocab/v#lang`: 'java'}) " +
                        "OPTIONAL MATCH (software {`http://neo4j.org/vocab/v#lang`: 'python'})-" +
                        "[created:`http://neo4j.org/vocab/rel#created`]->(software2) " +
                        "RETURN person",
                tp.parseSparqlQuery(queryString));
    }
}
