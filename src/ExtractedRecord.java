public class ExtractedRecord {
    private String filename;
    private final boolean overriddenMethod = false;
    private String name;
    private char[] recordAsString;

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public boolean isOverriddenMethod() {
        return overriddenMethod;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public char[] getRecordAsString() {
        return recordAsString;
    }

    public void setRecordAsString(char[] recordAsString) {
        this.recordAsString = recordAsString;
    }
}
