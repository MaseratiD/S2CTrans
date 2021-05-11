public class testExists {
    //    @Test
//    public void testExists() {
//        String queryString = "prefix v: <http://neo4j.org/vocab/v#>\n" +
//                "prefix rel: <http://neo4j.org/vocab/rel#>\n" +
//                "prefix label: <http://neo4j.org/vocab/label#>\n" +
//                "prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
//                "SELECT ?name\n" +
//                "WHERE {\n" +
//                "  ?person rdf:type label:person .\n" +
//                "  ?person v:name ?name .\n" +
//                "    FILTER EXISTS { ?person rel:created ?project }\n" +
//                "}";
//        TripleParser tp = new TripleParser();
//        System.out.println(tp.parseSparqlQuery(queryString));
//    }
//
//    @Test
//    public void testNotExists() {
//        String queryString = "prefix v: <http://neo4j.org/vocab/v#>\n" +
//                "prefix rel: <http://neo4j.org/vocab/rel#>\n" +
//                "prefix label: <http://neo4j.org/vocab/label#>\n" +
//                "prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
//                "SELECT ?name\n" +
//                "WHERE {\n" +
//                "  ?person rdf:type label:person .\n" +
//                "  ?person v:name ?name .\n" +
//                "    FILTER NOT EXISTS { ?person rel:created ?project }\n" +
//                "}";
//        TripleParser tp = new TripleParser();
//        System.out.println(tp.parseSparqlQuery(queryString));
//    }
}
