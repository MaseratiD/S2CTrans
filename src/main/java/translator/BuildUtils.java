package translator;

import org.neo4j.cypherdsl.core.Cypher;
import org.neo4j.cypherdsl.core.Node;

import java.util.ArrayList;
import java.util.Iterator;

public class BuildUtils {
    public static boolean isURI(String node) {
        if (VariableMap.getRegexString(node, "http://").equals(""))
            return false;
        return true;
    }
    public static String getNodeName(Node node)  {
        return node.getSymbolicName().get().getValue();
    }

    public static String getRelationshipFromPath(String path)  {
        path = path.replaceAll("\\(|\\)", "");
        if (path.split("[/][<]").length > 1)
            return path.split("[/][<]")[0].replaceAll("\\<|\\>", "");
        else
            return VariableMap.getRegexString(path, "[<].*[>]").replaceAll("\\<|\\>", "");
    }

    public static String getURINodeName(String uri) {
        if (uri.contains("#"))
            return uri.substring(uri.lastIndexOf("#") + 1);
        return uri.substring(uri.lastIndexOf("/") + 1);
    }

    protected static String getPredicateType(org.apache.jena.graph.Node node) {
        if (!node.isConcrete() || TripleParser.GraphRelationshipSet.contains(node.getURI()))
            return "Relationship";
        else if (Prefixes.getURIValue(node.getURI()).equals("type"))
            return "Type";
        else
            return "Property";
    }

    protected static Node getNode(ArrayList<Node> nodes, String name) {
        Iterator<Node> it = nodes.iterator();
        while (it.hasNext()){
            Node node = it.next();
            if (getNodeName(node).equals(name))
                return node;
        }
        return Cypher.anyNode(name);
    }

    protected static void removeBuildedNode(ArrayList<Node> nodes, String name) {
        for (int i = 0; i < nodes.size(); i++) {
            if (nodes.get(i).getSymbolicName().get().getValue().equals(name)) {
                nodes.remove(i);
                i--;
            }
        }
    }


}
