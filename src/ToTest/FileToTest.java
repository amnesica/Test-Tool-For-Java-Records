package ToTest;

import java.nio.file.Path;
import java.util.ArrayList;

/**
 * Klasse, die die Eingabedatei des Testprogramms darstellt. Hier wird der Pfad zur Datei, der Name der Datei,
 * der Inhalt sowie eine Liste mit den gefundenen Records in der Datei gespeichert
 */
public class FileToTest {

    //Pfad zur Datei
    private Path path;

    //Name der Datei
    private String filename;

    //Inhalt der Datei als String
    private String fileContent;

    //Liste mit allen gefundenen Records
    private ArrayList<RecordToTest> listRecords;

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

    /**
     * Fuegt einen RecordToTest der Liste an zu testenden Records hinzu.
     * Initialisiert die Liste beim ersten Record
     *
     * @param recordToTest RecordToTest
     */
    public void fuegeRecordZuListeDerZuTestendenRecordsHinzu(RecordToTest recordToTest) {
        if (listRecords == null) {
            listRecords = new ArrayList<>();
        }
        listRecords.add(recordToTest);
    }

    public ArrayList<RecordToTest> getListRecords() {
        return listRecords;
    }
}
