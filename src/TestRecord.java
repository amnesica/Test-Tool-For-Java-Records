/**
 * Klasse, welche einen TestRecord in der generierten Testdatei fuer den Test auf Funktionalitaet zum Einsatz kommt
 */
public class TestRecord {

    //Name des TestRecord
    private String name;

    //Initialisierter Record als String
    private String initializedRecord;

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
}
