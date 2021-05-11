import org.junit.Test;
import translator.TripleParser;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class testReturnLabelType {
    @Test
    public void testReturnLabel() throws IOException {
        String fileName = Thread.currentThread().getStackTrace()[1].getMethodName() + ".txt";
        String queryString = testFileInput.inputQuery(fileName);
        TripleParser tp = new TripleParser();
        tp.addRelationshipIntoGraph("http://neo4j.org/vocab/rel#created");
        assertEquals("MATCH (sub {`http://neo4j.org/vocab/v#place`: 'Maida Vale 4'})-" +
                        "[created:`http://neo4j.org/vocab/rel#created`]->" +
                        "(obj:`http://dbtune.org/bbc/peel/signal/1032/Performance`) " +
                        "RETURN labels(sub)",
                tp.parseSparqlQuery(queryString));
    }

    @Test
    public void testReturnType() throws IOException {
        String fileName = Thread.currentThread().getStackTrace()[1].getMethodName() + ".txt";
        String queryString = testFileInput.inputQuery(fileName);
        TripleParser tp = new TripleParser();
        tp.addRelationshipIntoGraph("http://neo4j.org/vocab/rel#created");
        assertEquals("MATCH (sub:`http://dbtune.org/bbc/peel/signal/1032/Person` " +
                        "{`http://neo4j.org/vocab/v#place`: 'Maida Vale 4'})-[rel]->" +
                        "(obj:`http://dbtune.org/bbc/peel/signal/1032/Performance`) " +
                        "RETURN type(rel)",
                tp.parseSparqlQuery(queryString));
    }
}
