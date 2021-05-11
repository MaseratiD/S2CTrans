package translator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NodeUtils {
    private List<String> labels = new ArrayList<>();
    private HashMap<String, Object> properties = new HashMap<>();

    public void addLabel(String label){
        this.labels.add(label);
    }

    public void addProperties(String key, Object value) {
        this.properties.put(key, value);
    }

    public List<String> getLabels() {
        return this.labels;
    }

    public HashMap<String, Object> getProperties() {
        return this.properties;
    }
}
