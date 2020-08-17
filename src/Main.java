import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Einstiegspunkt des Test-Tools
 * Aufruf: java Main <PfadZurDatei> --enable-preview -source 14
 *
 * TODO
 * Kompilieren: javac --enable-preview -source 14 *.java
 * Aufruf: java Main /home/amnesica/Dokumente/Universität_Hamburg/Bachelorarbeit/Test-Tool/DebugRecords.java --enable-preview -source 14
 *         java --enable-preview Main DebugRecords.java
 */
public class Main {

    /**
     * Einstiegspunkt des Programms
     *
     * @param args Voller Dateiname der Java-Datei, welche Records zum Testen enthält
     */
    public static void main(String[] args) {
        System.out.println("-----------------Test-Tool Java-Records-----------------");
        //get Dateiname von *.java-Datei, die Records enthält
        if (args != null && args.length > 0 && !args[0].isEmpty() && args[0].contains(".java")) {

            //starte Test-Tool
            starteTestTool(args[0]);

        } else {
            System.err.println("Error: Bitte geben sie eine gültige Java-Datei als Argument an\nTest-Tool wird beendet.");
        }
    }

    /**
     * Startet das Test-Tool mit einem Dateinamen filename
     *
     * @param path Pfad der Datei mit den zu testenden Records
     */
    private static void starteTestTool(String path) {

        //Setze Infos zu Datei mit Records, die als Argument mitgegeben wurde
        FileToTest fileToTest = new FileToTest();
        fileToTest.setPath(Paths.get(path));
        fileToTest.setFilename(Paths.get(path).getFileName().toString());

        System.out.println("Dateiname der Test-Datei: " + fileToTest.getFilename());
        System.out.println("--------------------------------------------------------");

        try {
            //extrahiere Inhalt der Datei in String
            String fileContent = new String(Files.readAllBytes(fileToTest.getPath()));
            fileToTest.setFileContent(fileContent);

            //prüfe, ob Test durchgeführt werden soll und starte Generierung der Testfälle
            if (new RecordExtractor().pruefeObTestfaelleGeneriertWerdenSollen(fileToTest)) {
                //TODO generiere Testfaelle für mehrere Records
                for (RecordToTest recordToTest : fileToTest.getListRecords()) {
                    if (recordToTest.isRecordShouldBeTested()) {
                        TestGenerator testGenerator = new TestGenerator();

                        //Generiere funktionale Testfaelle
                        testGenerator.generierefunktionaleTestfaelle(recordToTest);

                        //TODO Führe nicht-funktionale Testfälle durch
                        testGenerator.fuehreLeistungseffizienztestDurch(recordToTest);
                    }
                }
            }
        } catch (IOException | InvocationTargetException | NoSuchMethodException | IllegalAccessException |
                InstantiationException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
