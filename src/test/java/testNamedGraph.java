import org.junit.Test;
import translator.TripleParser;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class testNamedGraph {
    @Test
    public void testNamedGraphProperty1() throws IOException {
        String fileName = Thread.currentThread().getStackTrace()[1].getMethodName() + ".txt";
        String queryString = testFileInput.inputQuery(fileName);
        TripleParser tp = new TripleParser();
        tp.addRelationshipIntoGraph("http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/vocabulary/product");
        assertEquals("MATCH (sub:`http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/vocabulary/offer`)" +
                        "-[product:`http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/vocabulary/product`]->" +
                        "(obj:`http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/vocabulary/vendor`) " +
                        "RETURN product.`http://www.w3.org/2000/01/rdf-schema#label`",
                tp.parseSparqlQuery(queryString));
    }

    @Test
    public void testNamedGraphProperty2() throws IOException {
        String fileName = Thread.currentThread().getStackTrace()[1].getMethodName() + ".txt";
        String queryString = testFileInput.inputQuery(fileName);
        TripleParser tp = new TripleParser();
        tp.addRelationshipIntoGraph("http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/vocabulary/product");
        assertEquals("MATCH (sub:`http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/vocabulary/offer`)-" +
                        "[pre {`http://www.w3.org/2000/01/rdf-schema#label`: 'label'}]->" +
                        "(obj:`http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/vocabulary/vendor`) " +
                        "RETURN type(pre)",
                tp.parseSparqlQuery(queryString));
    }

//    @Test
//    public void testNamedGraphType1() throws IOException {
//        String fileName = Thread.currentThread().getStackTrace()[1].getMethodName() + ".txt";
//        String queryString = inputQuery(fileName);
//        TripleParser tp = new TripleParser();
//        tp.addRelationshipIntoGraph("http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/vocabulary/product");
//        assertEquals("MATCH (sub:`http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/vocabulary/offer`)" +
//                        "-[pre:`http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/vocabulary/product`]->" +
//                        "(obj:`http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/vocabulary/vendor`) " +
//                        "RETURN type(pre)",
//                tp.parseSparqlQuery(queryString));
//    }
//
//    @Test
//    public void testNamedGraphType2() throws IOException {
//        String fileName = Thread.currentThread().getStackTrace()[1].getMethodName() + ".txt";
//        String queryString = inputQuery(fileName);
//        TripleParser tp = new TripleParser();
//        tp.addRelationshipIntoGraph("http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/vocabulary/product");
//        assertEquals("MATCH (sub:`http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/vocabulary/offer`)" +
//                        "-[product:`http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/vocabulary/product`]->" +
//                        "(obj:`http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/vocabulary/vendor`) " +
//                        "RETURN type(product)",
//                tp.parseSparqlQuery(queryString));
//    }
}
