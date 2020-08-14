import java.util.ArrayList;
import java.util.LinkedHashMap;

public class RecordInfo {
    private String filename;
    private String name;

    private String recordHeader;
    private String recordBody;
    private String recordFull;

    private LinkedHashMap<Object, Object> componentMap;
    private ArrayList<String> listOverriddenMethods;
    private int amountComponents;

    private ArrayList<String> testRecordsPositiv;
    private ArrayList<String> testRecordNames;
    private ArrayList<String> testRecordCopies;

    private String recordNegativTest;
    private String recordNegativTestName;
    private String recordNegativTestOverriddenMethod;
    private String recordNegativTestOverriddenMethodOld;

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

    public LinkedHashMap<Object, Object> getComponentMap() {
        return componentMap;
    }

    public void setComponentMap(LinkedHashMap<Object, Object> componentMap) {
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

    public ArrayList<String> getListOverriddenMethods() {
        return listOverriddenMethods;
    }

    public void setListOverriddenMethods(ArrayList<String> listOverriddenMethods) {
        this.listOverriddenMethods = listOverriddenMethods;
    }

    public int getAmountComponents() {
        return amountComponents;
    }

    public void setAmountComponents(int amountComponents) {
        this.amountComponents = amountComponents;
    }

    public String getRecordNegativTest() {
        return recordNegativTest;
    }

    public void setRecordNegativTest(String recordNegativTest) {
        this.recordNegativTest = recordNegativTest;
    }

    public String getRecordNegativTestOverriddenMethod() {
        return recordNegativTestOverriddenMethod;
    }

    public void setRecordNegativTestOverriddenMethod(String recordNegativTestOverriddenMethod) {
        this.recordNegativTestOverriddenMethod = recordNegativTestOverriddenMethod;
    }

    public String getRecordNegativTestName() {
        return recordNegativTestName;
    }

    public void setRecordNegativTestName(String recordNegativTestName) {
        this.recordNegativTestName = recordNegativTestName;
    }

    public String getRecordNegativTestOverriddenMethodOld() {
        return recordNegativTestOverriddenMethodOld;
    }

    public void setRecordNegativTestOverriddenMethodOld(String recordNegativTestOverriddenMethodOld) {
        this.recordNegativTestOverriddenMethodOld = recordNegativTestOverriddenMethodOld;
    }

    public ArrayList<String> getTestRecordsPositiv() {
        return testRecordsPositiv;
    }

    public void setTestRecordsPositiv(ArrayList<String> testRecordsPositiv) {
        this.testRecordsPositiv = testRecordsPositiv;
    }

    public ArrayList<String> getTestRecordNames() {
        return testRecordNames;
    }

    public void setTestRecordNames(ArrayList<String> testRecordNames) {
        this.testRecordNames = testRecordNames;
    }

    public ArrayList<String> getTestRecordCopies() {
        return testRecordCopies;
    }

    public void setTestRecordCopies(ArrayList<String> testRecordCopies) {
        this.testRecordCopies = testRecordCopies;
    }
}
