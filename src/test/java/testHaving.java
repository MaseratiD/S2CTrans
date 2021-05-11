import org.junit.Test;
import translator.TripleParser;

public class testHaving {
    @Test
    public void testHavingQuery() {
        String queryString = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
                "PREFIX foaf: <http://xmlns.com/foaf/0.1/>\n" +
                "PREFIX myNs2: <http://dbtune.org/bbc/peel/signal/1032/>\n" +
                "prefix rel: <http://neo4j.org/vocab/rel#> \n" +
                "prefix v: <http://neo4j.org/vocab/v#> \n" +
                "SELECT (sum(?instrument) as ?total) WHERE {\n" +
                "  ?sub rdf:type myNs2:Performance . \n" +
                "  ?sub v:place \"Maida Vale 4\".\n" +
                "  ?sub rel:created ?obj.\n" +
                "  ?obj rdf:type myNs2:Performance . \n" +
                "  ?obj v:instrument ?instrument .\n" +
                " optional { ?sub myNs2:created ?prop . " +
                " FILTER(?prop > 1000) }" +
                "} ";
        TripleParser tp = new TripleParser();
        tp.addRelationshipIntoGraph("http://neo4j.org/vocab/rel#created");
        System.out.println(tp.parseSparqlQuery(queryString));
    }
}
