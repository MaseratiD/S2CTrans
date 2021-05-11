import com.opencsv.CSVWriter;
import org.apache.jena.query.Query;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.neo4j.driver.*;
import translator.TripleParser;
import translator.VariableMap;

import java.io.*;
import java.util.ArrayList;


public class Experiments {
    private static String filename;
    private static String queryFile;
    private static String compareResult;

    public static String inputQuery() throws IOException {
        File file = new File(queryFile);
        InputStreamReader read = new InputStreamReader(
                new FileInputStream(file));
        BufferedReader bufferedReader = new BufferedReader(read);
        String query = "";
        String lineTxt = null;
        while((lineTxt = bufferedReader.readLine()) != null)
            query += lineTxt;
        read.close();
        return query;
    }

    public static void main(String[] args) throws IOException {
        filename = args[0];
        queryFile = args[1];
        compareResult = args[2];
        System.out.println("Dataset:" + filename);
        System.out.println("QueryFile:" + queryFile);
        System.out.println("Result:" + compareResult);
        int index = queryFile.lastIndexOf("\\");
        String feature = queryFile.substring(index+1).replaceAll(".txt","");
        String queryString = inputQuery();
        Model model = ModelFactory.createMemModelMaker().createDefaultModel();
        model.read(filename);
        Driver driver = GraphDatabase.driver("bolt://localhost:7687", AuthTokens.basic("neo4j","123456"));
        File output = new File(compareResult);
        CSVWriter csvWriter = new CSVWriter(new OutputStreamWriter(new FileOutputStream(output, true), "UTF-8"));
        try(Session session = driver.session()){
            Result RelList = session.run("call db.relationshipTypes" );
            int i = 0;
            while (i < 5) {
                System.out.println("It's "+ i +" epoch.");
                ArrayList<String> result = new ArrayList<>();
                result.add(feature);
                TripleParser tp = new TripleParser();
                while (RelList.hasNext()) {
                    String l = RelList.next().toString();
                    l = VariableMap.getRegexString(l, "\\\".*\\\"");
                    l = l.replaceAll("\"", "");
                    tp.addRelationshipIntoGraph(l);
                }
                result.add(queryString);
                long startTime = System.currentTimeMillis();   //
                Query query = QueryFactory.create(queryString);
                QueryExecution qe = QueryExecutionFactory.create(query, model);
                ResultSet results = qe.execSelect();
                result.add(ResultSetFormatter.asText(results));
                qe.close();
                long endTime = System.currentTimeMillis(); //
                result.add(String.valueOf(endTime - startTime));
//                System.out.println("Jena run time "+(endTime-startTime)+"ms");

                startTime = System.currentTimeMillis();   //
                String cypherquery = tp.parseSparqlQuery(queryString);
                long parseEndTime = System.currentTimeMillis();   //
                result.add(String.valueOf(parseEndTime - startTime));
//                System.out.println("Convert Query time "+(parseEndTime - startTime)+"ms");

//                System.out.println(queryString);
//                    System.out.println("------Converted Cypher query statement------");
                result.add(cypherquery);
//                System.out.println(cypherquery);
                Result list = session.run(cypherquery);
//                System.out.println("------Cypher query results------");
                String records = "";
                while (list.hasNext()) {
                    Record record = list.next();
                    records = records + record.fields() + "\n";
//                    System.out.println(record.fields());
                }
                result.add(records);
                endTime = System.currentTimeMillis(); //
//                System.out.println("Cypher run time "+(endTime-startTime)+"ms");
                result.add(String.valueOf(endTime - startTime));
                csvWriter.writeNext(result.toArray(new String[0]));
                csvWriter.flush();
                i++;
            }
        }
        csvWriter.close();
        driver.close();
    }
//    }
}
