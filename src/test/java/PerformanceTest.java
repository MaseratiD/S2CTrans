import org.apache.jena.query.Query;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.neo4j.driver.*;
import translator.TripleParser;
import translator.VariableMap;

import java.io.*;
import java.util.ArrayList;


public class PerformanceTest {
    private static String filename = "D://Dataset/wn31.nt";
    private static String queryFile = "D://Dataset/PerformanceTest";
    private static File compareResult = new File("D://MH1_7.xlsx");

    public static String inputQuery(int i) throws IOException {
        File file = new File(queryFile + "/MH" + i + ".txt");
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

    public static void writeToExcel(ArrayList<String> result) {
        XSSFWorkbook wb =null;
        XSSFSheet sheet = null;
        InputStream input = null;
        FileOutputStream output=null;
        try {
            input = new FileInputStream(compareResult);
            wb = (XSSFWorkbook) WorkbookFactory.create(input);
            sheet = wb.getSheet("wn31WitoutIndex"); //
            XSSFRow row = sheet.getRow(0);
            int hang = 0;
            if("".equals(row)||row==null){
                hang=0;
            }else{
                hang=sheet.getLastRowNum();
                hang=hang+1;
            }
            //
            FileOutputStream out=new FileOutputStream(compareResult); //
            row=sheet.createRow((short)(hang)); //
            for (int i = 0; i < result.size(); i++) {
                row.createCell(i).setCellValue(result.get(i));
            }
            out.flush();
            wb.write(out);
            out.close();
        } catch (IOException | InvalidFormatException e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) throws IOException {
        String queryString = "";
        for (int j = 1; j <= 7; j++) {
        Model model = ModelFactory.createMemModelMaker().createDefaultModel();
        model.read(filename);

        Driver driver = GraphDatabase.driver("bolt://localhost:7687", AuthTokens.basic("neo4j","123456"));
        try(Session session = driver.session()){
            Result RelList = session.run("call db.relationshipTypes" );
            int i = 0;

                queryString = inputQuery(j);
                while (i < 5) {
                    ArrayList<String> result = new ArrayList<>();
                    result.add("MH"+j);
                    result.add("Relationship length: "+j);
                    result.add("Relationship length: "+j);
                    TripleParser tp = new TripleParser();
                    while (RelList.hasNext()) {
                        String l = RelList.next().toString();
                        l = VariableMap.getRegexString(l, "\\\".*\\\"");
                        l = l.replaceAll("\"", "");
                        tp.GraphRelationshipSet.add(l);
                    }
//                    System.out.println("------SPARQL query statement entered------");
                    result.add(queryString);
//                    System.out.println(queryString);

                    long startTime = System.currentTimeMillis();   //
                    Query query = QueryFactory.create(queryString);
                    QueryExecution qe = QueryExecutionFactory.create(query, model);
                    ResultSet results = qe.execSelect();
//                System.out.println("------Sparql query results------");
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

                    Result list = session.run(cypherquery);
                    while (list.hasNext()) {
                        Record record = list.next();
//                System.out.println(record.fields());
                    }
//                System.out.println(queryString);
//                    System.out.println("------Converted Cypher query statement------");
                    result.add(cypherquery);
//                System.out.println(cypherquery);
                    list = session.run(cypherquery);
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
                    writeToExcel(result);
                    i++;
                }
            }
            driver.close();
        }
    }
}
