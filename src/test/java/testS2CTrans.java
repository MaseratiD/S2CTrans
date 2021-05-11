import org.junit.Test;
import translator.TripleParser;

import java.io.*;

import static org.junit.Assert.assertEquals;

public class testS2CTrans {
    @Test
    public void testEasyQuery() throws IOException {
        String fileName = Thread.currentThread().getStackTrace()[1].getMethodName() + ".txt";
        String queryString = testFileInput.inputQuery(fileName);
        TripleParser tp = new TripleParser();
        assertEquals("MATCH (sub {`http://www.w3.org/2004/02/skos/core#notation`: '7'}) " +
                        "RETURN sub",
                tp.parseSparqlQuery(queryString));
    }

    @Test
    public void testMultipleFiltersQuery() throws IOException {
        String fileName = Thread.currentThread().getStackTrace()[1].getMethodName() + ".txt";
        String queryString = testFileInput.inputQuery(fileName);
        TripleParser tp = new TripleParser();
        tp.addRelationshipIntoGraph("http://neo4j.org/vocab/rel#created");
        assertEquals("MATCH (person:`http://neo4j.org/vocab/label#person` {`http://neo4j.org/vocab/v#star`: 30})-" +
                        "[created:`http://neo4j.org/vocab/rel#created`]->(project) " +
                        "WHERE ((person.`http://neo4j.org/vocab/v#age` > 30 AND person.`http://neo4j.org/vocab/v#age` < 50) " +
                        "OR project.`http://neo4j.org/vocab/v#lang` = 'java') " +
                        "RETURN DISTINCT project.`http://neo4j.org/vocab/v#name`, " +
                        "person.`http://neo4j.org/vocab/v#age`, " +
                        "project.`http://neo4j.org/vocab/v#lang` " +
                        "LIMIT 2",
                tp.parseSparqlQuery(queryString));

    }

    @Test
    public void testSelectAll() throws IOException {
        String fileName = Thread.currentThread().getStackTrace()[1].getMethodName() + ".txt";
        String queryString = testFileInput.inputQuery(fileName);
        TripleParser tp = new TripleParser();
        assertEquals("MATCH (n) RETURN n",
                tp.parseSparqlQuery(queryString));
    }

    @Test
    public void testSelectLabel() throws IOException {
        String fileName = Thread.currentThread().getStackTrace()[1].getMethodName() + ".txt";
        String queryString = testFileInput.inputQuery(fileName);
        TripleParser tp = new TripleParser();
        assertEquals("MATCH (person:`http://neo4j.org/vocab/label#person`) " +
                        "RETURN person",
                tp.parseSparqlQuery(queryString));
    }

    @Test
    public void testSelectSpecificElements() throws IOException {
        String fileName = Thread.currentThread().getStackTrace()[1].getMethodName() + ".txt";
        String queryString = testFileInput.inputQuery(fileName);
        TripleParser tp = new TripleParser();
        assertEquals("MATCH (person:`http://neo4j.org/vocab/label#person`) " +
                        "RETURN person.`http://neo4j.org/vocab/v#name`, " +
                        "person.`http://neo4j.org/vocab/v#age`",
                tp.parseSparqlQuery(queryString));
    }

    @Test
    public void testPatternMatching() throws IOException {
        String fileName = Thread.currentThread().getStackTrace()[1].getMethodName() + ".txt";
        String queryString = testFileInput.inputQuery(fileName);
        TripleParser tp = new TripleParser();
        tp.addRelationshipIntoGraph("http://neo4j.org/vocab/rel#created");
        assertEquals("MATCH (person:`http://neo4j.org/vocab/label#person`)-" +
                        "[created:`http://neo4j.org/vocab/rel#created`]->(project) " +
                        "RETURN person.`http://neo4j.org/vocab/v#name`, " +
                        "person.`http://neo4j.org/vocab/v#age`",
                tp.parseSparqlQuery(queryString));
    }

    @Test
    public void testFiltering() throws IOException {
        String fileName = Thread.currentThread().getStackTrace()[1].getMethodName() + ".txt";
        String queryString = testFileInput.inputQuery(fileName);
        TripleParser tp = new TripleParser();
        tp.addRelationshipIntoGraph("http://neo4j.org/vocab/rel#created");
        assertEquals("MATCH (person:`http://neo4j.org/vocab/label#person`)-" +
                        "[created:`http://neo4j.org/vocab/rel#created`]->(project) " +
                        "WHERE person.`http://neo4j.org/vocab/v#age` > 30 " +
                        "RETURN person.`http://neo4j.org/vocab/v#name`, " +
                        "person.`http://neo4j.org/vocab/v#age`",
                tp.parseSparqlQuery(queryString));
    }

    @Test
    public void testDeduplication() throws IOException {
        String fileName = Thread.currentThread().getStackTrace()[1].getMethodName() + ".txt";
        String queryString = testFileInput.inputQuery(fileName);
        TripleParser tp = new TripleParser();
        tp.addRelationshipIntoGraph("http://neo4j.org/vocab/rel#created");
        assertEquals("MATCH (person:`http://neo4j.org/vocab/label#person`)-" +
                        "[created:`http://neo4j.org/vocab/rel#created`]->(project) " +
                        "WHERE person.`http://neo4j.org/vocab/v#age` > 30 " +
                        "RETURN DISTINCT project.`http://neo4j.org/vocab/v#name`",
                tp.parseSparqlQuery(queryString));
    }

    @Test
    public void testMultipleFilters() throws IOException {
        String fileName = Thread.currentThread().getStackTrace()[1].getMethodName() + ".txt";
        String queryString = testFileInput.inputQuery(fileName);
        TripleParser tp = new TripleParser();
        tp.addRelationshipIntoGraph("http://neo4j.org/vocab/rel#created");
        assertEquals("MATCH (person:`http://neo4j.org/vocab/label#person`)-" +
                        "[created:`http://neo4j.org/vocab/rel#created`]->(project) " +
                        "WHERE (person.`http://neo4j.org/vocab/v#age` > 30 AND " +
                        "project.`http://neo4j.org/vocab/v#lang` = 'java') " +
                        "RETURN DISTINCT project.`http://neo4j.org/vocab/v#name`",
                tp.parseSparqlQuery(queryString));
    }

    @Test
    public void testMetaPropertyAccess() throws IOException {
        String fileName = Thread.currentThread().getStackTrace()[1].getMethodName() + ".txt";
        String queryString = testFileInput.inputQuery(fileName);
        TripleParser tp = new TripleParser();
        tp.addRelationshipIntoGraph("http://neo4j.org/vocab/rel#created");
        assertEquals("MATCH (person {`http://neo4j.org/vocab/v#name`: 'daniel'})-" +
                        "[created:`http://neo4j.org/vocab/rel#created`]->(location) " +
                        "RETURN location.`http://neo4j.org/vocab/v#value`, " +
                        "location.`http://neo4j.org/vocab/v#startTime`",
                tp.parseSparqlQuery(queryString));
    }

    @Test
    public void testOrderBy() throws IOException {
        String fileName = Thread.currentThread().getStackTrace()[1].getMethodName() + ".txt";
        String queryString = testFileInput.inputQuery(fileName);
        TripleParser tp = new TripleParser();
        assertEquals("MATCH (person:`http://neo4j.org/vocab/label#person`) " +
                        "RETURN person " +
                        "ORDER BY person.`http://neo4j.org/vocab/v#name` ASC, " +
                        "person.`http://neo4j.org/vocab/v#age` DESC, " +
                        "person.`http://neo4j.org/vocab/v#star` ASC",
                tp.parseSparqlQuery(queryString));
    }

    @Test
    public void testGroupBy() throws IOException {
        String fileName = Thread.currentThread().getStackTrace()[1].getMethodName() + ".txt";
        String queryString = testFileInput.inputQuery(fileName);
        TripleParser tp = new TripleParser();
        assertEquals("MATCH (person:`http://neo4j.org/vocab/label#person`) " +
                        "RETURN person.`http://neo4j.org/vocab/v#age`",
                tp.parseSparqlQuery(queryString));
    }

    @Test
    public void testAggregators() throws IOException {
        String fileName = Thread.currentThread().getStackTrace()[1].getMethodName() + ".txt";
        String queryString = testFileInput.inputQuery(fileName);
        TripleParser tp = new TripleParser();
        tp.addRelationshipIntoGraph("http://neo4j.org/vocab/rel#created");
        assertEquals("MATCH (person:`http://neo4j.org/vocab/label#person`)-" +
                        "[created:`http://neo4j.org/vocab/rel#created`]->(software) " +
                        "RETURN max(person.`http://neo4j.org/vocab/v#name`) AS maxName",
                tp.parseSparqlQuery(queryString));
    }

    @Test
    public void testRelationshipProp() throws IOException {
        String fileName = Thread.currentThread().getStackTrace()[1].getMethodName() + ".txt";
        String queryString = testFileInput.inputQuery(fileName);
        TripleParser tp = new TripleParser();
        assertEquals("MATCH (sub:`http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/vocabulary/offer`)-" +
                        "[product:`http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/vocabulary/product` " +
                        "{`http://www.w3.org/2000/01/rdf-schema#label`: 'label'}]->" +
                        "(obj:`http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/vocabulary/vendor`) " +
                        "RETURN sub, obj",
                tp.parseSparqlQuery(queryString));
    }
}
