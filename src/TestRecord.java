/**
 * Klasse, welche einen TestRecord in der generierten Testdatei fuer den Test auf Funktionalitaet zum Einsatz kommt
 */
public class TestRecord {

    //Name des TestRecord
    private String name;

    //Initialisierter Record als String
    private String initializedRecord;

    //Boolean, ob TestRecord fuer den Negativtest vorgesehen ist
    private boolean isForNegativTest;

    //Kompletter Record fuer den NegativTest
    private String recordNegativTestFull;

    //Komplette Akzessor-Methode zum ueberschreiben
    private String accessorToOverride;

    //Komplette Akzessor-Methode, der ueberschrieben wurde
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
