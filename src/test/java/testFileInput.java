import java.io.*;
public class testFileInput {
    public static String path = "src/test/resources/Query/";

    public static String inputQuery(String queryFile) throws IOException {
        File file = new File(path + queryFile);
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
}
