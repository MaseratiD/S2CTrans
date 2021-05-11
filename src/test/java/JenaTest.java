import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.junit.Test;

public class JenaTest {
    @Test
    public void testReturnRelType() {
        Model model = ModelFactory.createMemModelMaker().createDefaultModel();
        model.read("D:\\Desktop\\S2CTrans\\S2CTrans\\dataset\\BSBM.ttl");
        String queryString = "PREFIX bsbm-inst: <http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/instances/>\n" +
                "PREFIX bsbm: <http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/vocabulary/>\n" +
                "\n" +
                "SELECT distinct ?pPN1 (max(?price) as ?maxPrice) WHERE{ \n" +
                "\t?offer a bsbm:Offer .\n" +
                "\t?offer bsbm:price ?price .\n" +
                "\t?offer bsbm:product ?product1 .\n" +
                "    ?product1 a bsbm-inst:ProductType1. \n" +
                "\t?product1 bsbm:productPropertyNumeric1 ?pPN1 .\n" +
                "\tFILTER(?pPN1 > 600 && ?pPN1 < 800)\n" +
                "\tOPTIONAL {\n" +
                "\t\t?product1 bsbm:productPropertyNumeric2 1000 .\n" +
                "\t}\n" +
                "}\n" +
                "GROUP BY(?pPN1)\n" +
                "ORDER BY(?maxPrice)\n" +
                "LIMIT 10\n";
        Query query = QueryFactory.create(queryString);
        QueryExecution qe = QueryExecutionFactory.create(query, model);
        ResultSet results = qe.execSelect();
        ResultSetFormatter.out(results, query);
        qe.close();
    }
}
