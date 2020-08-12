import java.util.HashMap;

public class RecordInfo {
    private String filename;
    private String name;
    private final boolean overriddenMethod = false;

    private HashMap<Object, Object> componentMap;
    private String recordHeader;
    private String recordBody;
    private String recordFull;

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isOverriddenMethod() {
        return overriddenMethod;
    }

    public HashMap<Object, Object> getComponentMap() {
        return componentMap;
    }

    public void setComponentMap(HashMap<Object, Object> componentMap) {
        this.componentMap = componentMap;
    }

    public String getRecordHeader() {
        return recordHeader;
    }

    public void setRecordHeader(String recordHeader) {
        this.recordHeader = recordHeader;
    }

    public String getRecordBody() {
        return recordBody;
    }

    public void setRecordBody(String recordBody) {
        this.recordBody = recordBody;
    }

    public String getRecordFull() {
        return recordFull;
    }

    public void setRecordFull(String recordFull) {
        this.recordFull = recordFull;
    }
}
