package translator;

import java.util.HashMap;

public class RelationshipUtils {
    private String subject;
    private String predicateName;
    private String predicateType = "";
    private String object;
    private HashMap<String, Object> properties = new HashMap<>();

    public RelationshipUtils(String sub, String preName, String preType, String obj){
        this.subject = sub;
        this.predicateName = preName;
        this.predicateType = preType;
        this.object = obj;
    }

    public RelationshipUtils(String sub, String preType, String obj){
        this.subject = sub;
        this.predicateType = preType;
        this.object = obj;
    }

    public RelationshipUtils(String sub, String obj){
        this.subject = sub;
        this.object = obj;
    }

    public String getStartNode() {
        return this.subject;
    }

    public String getRelationshipType() {
        return this.predicateType;
    }

    public String getRelationshipName() {
        return this.predicateName;
    }

    public String getEndNode() {
        return this.object;
    }

    public void setRelationshipName(String name) {
        this.predicateName = name;
    }

    public void setRelationshipType(String type) {
        this.predicateType = type;
    }

    public void addProperty(String key, Object value) {
        this.properties.put(key, value);
    }

    public HashMap<String, Object> getProperties() {
        return this.properties;
    }
}
