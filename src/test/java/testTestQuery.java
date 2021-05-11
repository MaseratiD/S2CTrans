import org.junit.Test;
import translator.TripleParser;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class testTestQuery {
    @Test
    public void testInverseRelationship2() throws IOException {
        String fileName = Thread.currentThread().getStackTrace()[1].getMethodName() + ".txt";
        String queryString = testFileInput.inputQuery(fileName);
        TripleParser tp = new TripleParser();
        tp.addRelationshipIntoGraph("http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/vocabulary/reviewFor");
        System.out.println(tp.parseSparqlQuery(queryString));
    }

    @Test
    public void testZeroOrMoreRelationship2() throws IOException {
        String fileName = Thread.currentThread().getStackTrace()[1].getMethodName() + ".txt";
        String queryString = testFileInput.inputQuery(fileName);
        TripleParser tp = new TripleParser();
        tp.addRelationshipIntoGraph("http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/vocabulary/vendor");
        System.out.println(tp.parseSparqlQuery(queryString));
    }

    @Test
    public void testOneOrMoreRelationship2() throws IOException {
        String fileName = Thread.currentThread().getStackTrace()[1].getMethodName() + ".txt";
        String queryString = testFileInput.inputQuery(fileName);
        TripleParser tp = new TripleParser();
        tp.addRelationshipIntoGraph("http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/vocabulary/reviewFor");
        System.out.println(tp.parseSparqlQuery(queryString));
    }

    @Test
    public void testTwoHopsSame() throws IOException {
        String fileName = Thread.currentThread().getStackTrace()[1].getMethodName() + ".txt";
        String queryString = testFileInput.inputQuery(fileName);
        TripleParser tp = new TripleParser();
        tp.addRelationshipIntoGraph("http://purl.org/dc/elements/1.1/publisher");
        System.out.println(tp.parseSparqlQuery(queryString));
    }

    @Test
    public void testTwoHopsDiff() throws IOException {
        String queryString = "PREFIX bsbm-inst: <http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/instances/>PREFIX bsbm: <http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/vocabulary/>PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>PREFIX ele: <http://purl.org/dc/elements/1.1/>SELECT distinct ?label WHERE{ \t?review rdf:type bsbm:Review .\t?review bsbm:reviewFor ?product .\t?product rdf:type bsbm-inst:ProductType1. \t?product bsbm:productPropertyNumeric1 ?pPN1 .\t?product rdfs:label ?label. \tOPTIONAL {\t\t?product ele:publisher ?producer .\t\t?producer a bsbm:Producer .\t}\tFILTER(?pPN1 > 1000)}ORDER BY(?label) LIMIT 10";
        TripleParser tp = new TripleParser();
        tp.addRelationshipIntoGraph("http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/vocabulary/reviewFor");
        tp.addRelationshipIntoGraph("http://purl.org/dc/elements/1.1/publisher");
        System.out.println(tp.parseSparqlQuery(queryString));
    }

    @Test
    public void testThreeHopsDiff() throws IOException {
        String fileName = Thread.currentThread().getStackTrace()[1].getMethodName() + ".txt";
        String queryString = testFileInput.inputQuery(fileName);
        TripleParser tp = new TripleParser();
        tp.addRelationshipIntoGraph("http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/vocabulary/reviewFor");
        tp.addRelationshipIntoGraph("http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/vocabulary/productFeature");
        tp.addRelationshipIntoGraph("http://purl.org/dc/elements/1.1/publisher");
        System.out.println(tp.parseSparqlQuery(queryString));
    }
}
