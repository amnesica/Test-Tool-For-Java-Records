import java.nio.file.Path;
import java.util.ArrayList;

public class FileToTest {

    //Pfad zur Datei
    private Path path;

    //Name der Datei
    private String filename;

    //Inhalt der Datei als String
    private String fileContent;

    //Liste mit allen gefundenen Records
    private ArrayList<RecordToTest> listRecords;

    //Liste mit gefundenen Records, für die Testfälle erstellt werden sollen
    private ArrayList<RecordToTest> listRecordsToTest;

    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getFileContent() {
        return fileContent;
    }

    public void setFileContent(String fileContent) {
        this.fileContent = fileContent;
    }

    public void fuegeRecordToTestZuListeHinzu(RecordToTest recordToTest) {
        if (listRecordsToTest == null) {
            listRecordsToTest = new ArrayList<>();
        }
        listRecordsToTest.add(recordToTest);
    }

    public ArrayList<RecordToTest> getListRecordsToTest() {
        return listRecordsToTest;
    }

    public ArrayList<RecordToTest> getListRecords() {
        return listRecords;
    }

    public void setListRecords(ArrayList<RecordToTest> listRecords) {
        this.listRecords = listRecords;
    }
}
