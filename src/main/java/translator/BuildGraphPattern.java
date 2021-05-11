package translator;

import org.neo4j.cypherdsl.core.Node;
import org.neo4j.cypherdsl.core.Relationship;

import java.util.HashMap;

public class BuildGraphPattern {
    protected static PathType getPathType(String path) {
        if (VariableMap.getRegexString(path, ".+[<].*[>]").equals(""))
            return PathType.Normal;
        else {
            path = path.replaceAll("\\(|\\)", "");
            String[] s = path.split("[>][/][<]");
            if (s.length > 1) {
                TripleParser.SpecificLength = s.length;
                return PathType.Specific;
            }
            else {
                path = path.replaceAll("[<].*[>]", "");
                switch (path) {
                    case "*":
                        return PathType.ZeroOrMore;
                    case "+":
                        return PathType.OneOrMore;
                    case "^":
                        return PathType.Inverse;
                    default:
                        throw new IllegalStateException(String.format("Not support %s. ", path));
                }
            }
        }
    }

    protected static Relationship buildRelationship(Node subject,
                                                    String rName,
                                                    String rType,
                                                    Node object,
                                                    PathType pathType,
                                                    HashMap<String, Object> relProperties)  {
        if (rName.equals(""))
            rName = BuildUtils.getURINodeName(rType);
        Relationship relationship = null;
        switch (pathType) {
            case Normal:
                relationship = subject.relationshipTo(object, rType).named(rName);
                break;
            case ZeroOrMore:
                relationship = subject.relationshipTo(object, rType).named(rName).min(0);
                break;
            case OneOrMore:
                relationship = subject.relationshipTo(object, rType).named(rName).min(1);
                break;
            case Inverse:
                relationship = subject.relationshipFrom(object, rType).named(rName);
                break;
            default:
                throw new IllegalStateException(String.format("Not support pathType %s. ",pathType));
        }
        if (!relProperties.isEmpty())
            relationship = relationship.withProperties(relProperties);
        return relationship;
    }
}
