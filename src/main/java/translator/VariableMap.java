package translator;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VariableMap {
    private HashMap<String, String> varMap = new HashMap<>();
    private HashMap<String, Object> varMapDSL = new HashMap<>();

    public void addVariety(String key, String value) {
        String varRegex = "\\?[a-zA-Z][a-zA-Z0-9_]*";
        Pattern p = Pattern.compile(varRegex);
        Matcher m = p.matcher(value);
        while (m.find())
            value = value.replace(m.group(0), this.varMap.get(m.group()));
        this.varMap.put(key, value);
    }

    public HashMap<String, String> getVarMap() {
        return this.varMap;
    }

    public HashMap<String, Object> getVarMapDSL() {
        return this.varMapDSL;
    }

    public static String getRegexString(String str, String regex) {
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(str);
        if (m.find())
            return m.group(0);
        return "";
    }
    public static boolean isVariety(String str) {
        if (getRegexString(str, "^[\\?][a-zA-Z][a-zA-Z0-9_]*").equals(""))
            return false;
        return true;
    }

}
