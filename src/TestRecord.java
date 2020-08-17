public class TestRecord {
    private String name;
    private String initializedRecord;

    private boolean isForNegativTest;
    private String recordNegativTestFull;
    private String accessorToOverride;
    private String accessorOverridden;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInitializedRecord() {
        return initializedRecord;
    }

    public void setInitializedRecord(String initializedRecord) {
        this.initializedRecord = initializedRecord;
    }

    public boolean isForNegativTest() {
        return isForNegativTest;
    }

    public void setForNegativTest(boolean forNegativTest) {
        isForNegativTest = forNegativTest;
    }

    public String getRecordNegativTestFull() {
        return recordNegativTestFull;
    }

    public void setRecordNegativTestFull(String recordNegativTestFull) {
        this.recordNegativTestFull = recordNegativTestFull;
    }

    public String getAccessorOverridden() {
        return accessorOverridden;
    }

    public void setAccessorOverridden(String accessorOverridden) {
        this.accessorOverridden = accessorOverridden;
    }

    public String getAccessorToOverride() {
        return accessorToOverride;
    }

    public void setAccessorToOverride(String accessorToOverride) {
        this.accessorToOverride = accessorToOverride;
    }
}
