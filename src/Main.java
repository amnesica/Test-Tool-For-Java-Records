import ToTest.FileToTest;
import ToTest.RecordToTest;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Einstiegspunkt des Test-Tools für Records in Java
 * <p>
 * Kompilieren:     javac --enable-preview -source 14 *.java
 * Aufruf:          java --enable-preview Main <PfadZurDatei>
 * <p>
 * Hinweis: Aufruf in IDE mit <PfadZurDatei> als Program Arguments
 */
public class Main {

    /**
     * Einstiegspunkt des Programms
     *
     * @param args Voller Dateiname der Java-Datei, welche Records zum Testen enthält
     */
    public static void main(String[] args) {
        System.out.println("-----------------Test-Tool Java-Records-----------------");
        //prüfe Datei mit *.java-Endung, die als Argument mitgegeben wurde
        if (args != null && args.length > 0 && !args[0].isEmpty() && args[0].contains(".java")) {

            //starte Test-Tool mit Pfad der Datei
            starteTestTool(args[0]);

        } else {
            System.out.println("Error: Bitte geben sie eine gültige Java-Datei als Argument an" +
                    "\nTest-Tool wird beendet.");
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

            //prüfe, ob Tests durchgeführt werden soll und starte Generierung und Durchführung der Tests
            if (new RecordExtractor().pruefeObTestfaelleGeneriertWerdenSollen(fileToTest)) {

                //generiere Testfaelle und führe Tests auf Leistungseffizienz und Wartbarkeit
                // für jeden gefundenen Record durch, wenn dieser getestet werden soll
                for (RecordToTest recordToTest : fileToTest.getListRecords()) {

                    //funktionale Testfälle
                    if (recordToTest.isGenerateTestcasesForRecord()) {
                        //Instantiiere TestGenerator für funktionale Testfälle
                        TestGenerator testGenerator = new TestGenerator();

                        //Generiere funktionale Testfaelle
                        testGenerator.generierefunktionaleTestfaelle(recordToTest);
                    }

                    //nicht funktionale Testfälle
                    if (recordToTest.isExecuteTestcasesForRecord()) {
                        //Instantiiere TestExecutor für nicht-funktionale Testfälle
                        TestExecutor testExecutor = new TestExecutor();

                        //Führe Test auf Leistungseffizienz durch
                        testExecutor.fuehreLeistungseffizienztestDurch(recordToTest);

                        //Führe Test auf Wartbarkeit durch
                        testExecutor.fuehreTestAufWartbarkeitDurch(recordToTest);
                    }
                }
            }
        } catch (IOException | InvocationTargetException | NoSuchMethodException | IllegalAccessException |
                InstantiationException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
