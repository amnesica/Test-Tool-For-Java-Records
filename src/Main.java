import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Einstiegspunkt des Test-Tools
 */
public class Main {

    //Pfad zur Test mit den zu testenden Records
    private static Path path;

    //Gesammelte Informationen über den zu testenden Record
    private static RecordInfo recordInfo;

    //Index direkt vor dem Finden eines Records
    private static int indexEntryPointRecordBeforeMatch = 0;

    //Index direkt nach dem Finden eines Records
    private static int indexEntryPointRecordAfterMatch = 0;

    //Index des Endes der Komponenten-Liste
    private static int indexEndOfComponentList = 0;


    /**
     * Einstiegspunkt des Programms
     *
     * @param args Voller Dateiname der Java-Datei, welche Records zum Testen enthält
     */
    public static void main(String[] args) {
        System.out.println("-----------------Test-Tool Java-Records-----------------");
        //get Dateiname von *.java-Datei, die Records enthält
        if (args != null && args.length > 0 && !args[0].isEmpty() && args[0].contains(".java")) {
            String filename = args[0];
            System.out.println("Dateiname der Test-Datei: " + filename);

            //starte Test-Tool
            starteTestTool(filename);
        } else {
            System.err.println("Error: Bitte geben sie eine gültige Java-Datei als Argument an\nTest-Tool wird beendet.");
        }
    }

    /**
     * Startet das Test-Tool mit einem Dateinamen filename
     *
     * @param filename Dateiname
     */
    private static void starteTestTool(String filename) {
        //Pfad zur Datei mit den zu testenden Records generieren
        path = Paths.get(System.getProperty("user.dir") + "/src/" + filename);

        try {
            //extrahiere Inhalt der Datei in String
            String fileContent = new String(Files.readAllBytes(path));

            //prüfe, ob Test durchgeführt werden soll und starte Generierung der Testfälle
            if (pruefeObTestfaelleGeneriertWerdenSollen(fileContent)) {
                //TODO generiere Testfaelle
                generiereTestfaelle();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Generiert die Testfaelle für den vorliegenden Record in recordInfo
     */
    private static void generiereTestfaelle() {
        Path pathForNewTestDirectory = Paths.get(System.getProperty("user.dir") + "/src/" + "/generated/");
        Path pathForNewTestFile = Paths.get(System.getProperty("user.dir") + "/src/" + "/generated/" + recordInfo.getName() + "Testfaelle.java");
        String nameTestKlasse = recordInfo.getName() + "Testfaelle";

        try {
            erstelleNeueTestJavaDatei(pathForNewTestDirectory, pathForNewTestFile);

            erstelleGrundlegendeTestStrukturInDatei(pathForNewTestFile, nameTestKlasse);

            fuegeZuTestendenRecordEin(pathForNewTestFile);

            erstelleTestfaelleFunktionalitaet(pathForNewTestFile);

            //TODO nicht-funktionale Testfälle

            System.out.println("Testdatei wurde erfolgreich erstellt und kann ausgeführt werden.");
            System.out.println("Pfad der generierten Datei: " + pathForNewTestFile);
        } catch (IOException e) {
            System.err.println("Error: Beim Erstellen der Testdatei gab es einen Fehler.\nTest-Tool wird beendet.");
            e.printStackTrace();
        }
    }

    /**
     * Erstellt die Testrecords mit den Testdaten und fügt diese in der Testdatei ein.
     *
     */
    private static void erstelleTestRecords() {
        //Liste mit den erstellten Testrecords
        ArrayList<String> testRecords = new ArrayList<>();

        //Liste mit Namen der erstellten Testrecords
        ArrayList<String> testRecordNames = new ArrayList<>();

        //generiere Liste mit Testwerten für Testrecords
        ArrayList<Integer> listTestWerte = new ArrayList<>();
        Collections.addAll(listTestWerte, 1, Integer.MAX_VALUE, Integer.MIN_VALUE, -1, 0, Integer.MAX_VALUE - 1, Integer.MIN_VALUE + 1);
        //TODO Möglichkeit einbauen, benutzerdefinierte Grenzwerte einzufügen (und diese der Liste listTestWerte hinzufügen)

        //bestimme Anzahl zu erstellender Testrecords
        int anzahlZuErstellenderTestrecords = listTestWerte.size() / recordInfo.getAmountComponents();
        int fehlendeKomponenten = listTestWerte.size() % recordInfo.getAmountComponents();
        if (fehlendeKomponenten != 0) {
            do {
                anzahlZuErstellenderTestrecords += 1;
                fehlendeKomponenten -= 1;
            } while (fehlendeKomponenten != 0);
        }

        //Erstelle Testrecords und verteile zu testende Werte
        int indexListTestWerte = 0;
        for (int i = 0; i < anzahlZuErstellenderTestrecords; i++) {
            String testRecordName = recordInfo.getName() + i;
            testRecordNames.add(testRecordName);

            String testRecord = recordInfo.getName() + " " + testRecordName + " = new " + recordInfo.getName() + "(";

            for (int j = 0; j < recordInfo.getAmountComponents(); j++) {
                if (indexListTestWerte >= listTestWerte.size()) {
                    indexListTestWerte = 0;
                }
                testRecord = testRecord.concat(String.valueOf(listTestWerte.get(indexListTestWerte)));
                indexListTestWerte += 1;

                //hänge ein "," oder ein ");" an, um Record abzuschließen
                if (j == recordInfo.getAmountComponents() - 1) {
                    testRecord = testRecord.concat(");");
                } else {
                    testRecord = testRecord.concat(",");
                }
            }
            testRecords.add(testRecord);
        }
        recordInfo.setTestRecordsPositiv(testRecords);
        recordInfo.setTestRecordNames(testRecordNames);
    }

    /**
     * Fügt den zu testenden Record in die Testdatei ein.
     *
     * @param pathForNewTestFile Pfad zur generierten Testdatei
     * @throws IOException IOException
     */
    private static void fuegeZuTestendenRecordEin(Path pathForNewTestFile) throws IOException {
        FileWriter fw = new FileWriter(pathForNewTestFile.toString(), true);
        BufferedWriter bw = new BufferedWriter(fw);
        bw.newLine();
        bw.newLine();
        bw.write("//zu testender Record");
        bw.newLine();
        bw.write(recordInfo.getRecordFull());
        bw.newLine();
        bw.close();
    }

    /**
     * Erstellt die Testfaelle für das Qualitätskriterium "Funktionelle Eignung".
     * Es wird ein Positiv- und ein Negativtest erstellt. Zuvor werden die Testrecords erstellt
     */
    private static void erstelleTestfaelleFunktionalitaet(Path pathForNewTestFile) throws IOException {
        //Erstelle TestRecords und speichere diese in recordInfo ab
        erstelleTestRecords();

        //füge manipulierten zu testenden Record für Negativtests ein
        fuegeNegativtestRecordEin(pathForNewTestFile);

        //Equals
        erstelleTestfallEqualsPositivtest(pathForNewTestFile);
        erstelleTestfallEqualsNegativtest(pathForNewTestFile);

        //hashCode
        erstelleTestfallHashCodePositivtest(pathForNewTestFile);

        //toString
        erstelleTestfallToStringPositivtest(pathForNewTestFile);
    }

    private static void erstelleTestfallToStringPositivtest(Path pathForNewTestFile) throws IOException {
        StringBuilder sb = new StringBuilder();

        String headerMethod = "@Test" + "\n" +
                "public void testeFunktionalitaetToStringPositivtest(){";

        fuegeTestRecordsMitEqualsAnHeader(sb, headerMethod);

        //erstelle Assertions mit toString
        for(int i = 0; i < recordInfo.getTestRecordNames().size(); i++){
            sb.append("assertEquals(").append(recordInfo.getTestRecordNames().get(i))
                    .append(".toString(),").append(recordInfo.getTestRecordNames().get(i)).append("copy.toString());\n");
        }

        //schließe Methode ab
        sb.append("}\n");

        //schließe Klass ab
        sb.append("}\n");

        FileWriter fw = new FileWriter(pathForNewTestFile.toString(), true);
        BufferedWriter bw = new BufferedWriter(fw);
        bw.newLine();
        bw.newLine();
        bw.write("//Testfall ToString - Positivtest");
        bw.newLine();
        bw.write(sb.toString());
        bw.newLine();
        bw.close();
    }

    private static void erstelleTestfallHashCodePositivtest(Path pathForNewTestFile) throws IOException {
        StringBuilder sb = new StringBuilder();

        String headerMethod = "@Test" + "\n" +
                "public void testeFunktionalitaetHashCodePositivtest(){";

        fuegeTestRecordsMitEqualsAnHeader(sb, headerMethod);

        //erstelle Assertions mit hashCode
        for(int i = 0; i < recordInfo.getTestRecordNames().size(); i++){
            sb.append("assertEquals(").append(recordInfo.getTestRecordNames().get(i))
                    .append(".hashCode(),").append(recordInfo.getTestRecordNames().get(i)).append("copy.hashCode());\n");
        }

        //schließe Methode ab
        sb.append("}\n");

       FileWriter fw = new FileWriter(pathForNewTestFile.toString(), true);
        BufferedWriter bw = new BufferedWriter(fw);
        bw.newLine();
        bw.newLine();
        bw.write("//Testfall HashCode - Positivtest");
        bw.newLine();
        bw.write(sb.toString());
        bw.newLine();
        bw.close();
    }

    private static void fuegeTestRecordsMitEqualsAnHeader(StringBuilder sb, String headerMethod) {
        sb.append(headerMethod);

        //füge TestRecords mit Testdaten sowie Kopien der Testrecords ein
        for(int i = 0; i < recordInfo.getTestRecordsPositiv().size(); i++){
            sb.append(recordInfo.getTestRecordsPositiv().get(i)).append("\n");
            sb.append(recordInfo.getTestRecordCopies().get(i)).append("\n").append("\n");
        }

        //erstelle Assertions mit equals
        for(int i = 0; i < recordInfo.getTestRecordNames().size(); i++){
            sb.append("assertTrue(").append(recordInfo.getTestRecordNames().get(i))
                    .append(".equals(").append(recordInfo.getTestRecordNames().get(i)).append("copy));\n");
        }

        sb.append("\n");
    }

    private static void erstelleTestfallEqualsNegativtest(Path pathForNewTestFile) throws IOException {
        ArrayList<String> testRecordsNegativ = new ArrayList<>();
        ArrayList<String> testRecordsNegativCopies = new ArrayList<>();
        StringBuilder sb = new StringBuilder();

        //baue bereits erstellte records für Negativtest um
        for(String testRecordPositiv : recordInfo.getTestRecordsPositiv()) {
            String testRecordNegativ = testRecordPositiv.replace(recordInfo.getName(), recordInfo.getRecordNegativTestName());
            testRecordsNegativ.add(testRecordNegativ);
        }

        //baue bereits erstellte copy records für Negativtest um
        for(String testRecordPositivCopy : recordInfo.getTestRecordCopies()) {
            String testRecordNegativCopy = testRecordPositivCopy.replace(recordInfo.getName(), recordInfo.getRecordNegativTestName());
            testRecordsNegativCopies.add(testRecordNegativCopy);
        }

        String headerMethod = "@Test" + "\n" +
                "public void testeFunktionalitaetEqualsNegativtest(){";
        sb.append(headerMethod);

        //füge TestRecords mit Testdaten sowie Kopien der Testrecords ein
        for(int i = 0; i < testRecordsNegativ.size(); i++){
            sb.append(testRecordsNegativ.get(i)).append("\n");
            sb.append(testRecordsNegativCopies.get(i)).append("\n").append("\n");
        }

        //erstelle Assertions
        for(int i = 0; i < recordInfo.getTestRecordNames().size(); i++){
            sb.append("assertFalse(").append(recordInfo.getRecordNegativTestName()).append(i)
                    .append(".equals(").append(recordInfo.getRecordNegativTestName()).append(i).append("copy));\n");
        }

        //schließe Methode ab
        sb.append("}\n");

        FileWriter fw = new FileWriter(pathForNewTestFile.toString(), true);
        BufferedWriter bw = new BufferedWriter(fw);
        bw.newLine();
        bw.newLine();
        bw.write("//Testfall Equals - Negativtest");
        bw.newLine();
        bw.write(sb.toString());
        bw.newLine();
        bw.close();
    }

    /**
     * Erstellt einen Record für den Negativtest bei der Funktionalität. Hierbei wird bewusst ein nicht
     * überschriebener Akzessor des originalen Records überschrieben. Wenn bereits alle Akzessoren
     * überschrieben wurden, wird bei dem Akzessor der ersten Komponente ein "+ 1" beim "return" rangehängt,
     * um den gewünschten Effekt zu erzielen.
     *
     * @param pathForNewTestFile Pfad zur generierten Testdatei
     * @throws IOException IOException
     */
    private static void fuegeNegativtestRecordEin(Path pathForNewTestFile) throws IOException {
        String methodToOverrideBadly = bestimmeZuUeberschreibendeMethodeFuerNegativTest();

        //Setze Negativtest-Record erst einmal auf zu testenden Record
        recordInfo.setRecordNegativTest(recordInfo.getRecordFull());

        if (recordInfo.getListOverriddenMethods().contains(methodToOverrideBadly)) {
            //Akzessor wurde bereits überschrieben -> verändere Rückgabewert
            manipuliereVorhandenenAkzessor(methodToOverrideBadly);
        } else {
            //füge Akzessor am Ende des Records an
            //Initialisierung des RecordNegativTest
            recordInfo.setRecordNegativTest(recordInfo.getRecordFull());

            erstelleNeuenManipuliertenAkzessor(methodToOverrideBadly);
        }
        erstelleNegativTestRecord();
        schreibeNegativtestRecordInDatei(pathForNewTestFile);
    }

    /**
     * Erstellt einen NegativTestRecord für den Negativtest
     */
    private static void erstelleNegativTestRecord() {
        String overriddenMethodOld = recordInfo.getRecordNegativTestOverriddenMethodOld();
        String overriddenMethodNew = recordInfo.getRecordNegativTestOverriddenMethod();

        //Ersetze vorhandene Methode
        if(overriddenMethodOld != null && overriddenMethodNew != null){
            recordInfo.setRecordNegativTest(recordInfo.getRecordNegativTest().replace(overriddenMethodOld, overriddenMethodNew));
        }else{ //füge Methode am Ende des Records an
            if(recordInfo.getRecordNegativTest().endsWith("}")){
                String oldNegativTestRecord = recordInfo.getRecordNegativTest().substring(0,recordInfo.getRecordNegativTest().length() - 1);
                //Ersetze alten RecordNegativTest mit "}" am Ende, um Record abzuschließen
                recordInfo.setRecordNegativTest(oldNegativTestRecord.concat(overriddenMethodNew + "\n}"));
            }
        }

        //Record für NegativTest soll <name>NegativTest heißen
        ueberschreibeAlleBezeichnerFuerNegativTest();
    }

    /**
     * Erstellt einen manipulierten Akzessor, welcher noch nicht im TestRecord überschrieben wurde.
     * Dient der Erstellung eines NegativTestRecords.
     * @param methodToOverrideBadly Name der Methode, welche manipuliert werden soll
     */
    private static void erstelleNeuenManipuliertenAkzessor(String methodToOverrideBadly) {
        String newAccessor = "public int " + methodToOverrideBadly + "(){\nreturn " + methodToOverrideBadly + "+ 5;\n}";
        recordInfo.setRecordNegativTestOverriddenMethod(newAccessor);
    }

    /**
     * Schreibt den neuen NegativtestRecord in die Datei pathForNewTestFile
     * @param pathForNewTestFile Pfad zur generierten Testdatei
     * @throws IOException IOException
     */
    private static void schreibeNegativtestRecordInDatei(Path pathForNewTestFile) throws IOException {
        FileWriter fw = new FileWriter(pathForNewTestFile.toString(), true);
        BufferedWriter bw = new BufferedWriter(fw);
        bw.newLine();
        bw.newLine();
        bw.write("//zu testender Record für Negativtest");
        bw.newLine();
        bw.write(recordInfo.getRecordNegativTest());
        bw.newLine();
        bw.close();
    }

    /**
     * Manipuliert einen vorhandenen Akzessor und speichert die manipulierte Methode
     * im recordInfo ab
     * @param methodToOverrideBadly Name der Methode, welche manipuliert werden soll
     */
    private static void manipuliereVorhandenenAkzessor(String methodToOverrideBadly) {
        String beginOfAccessorRegex = "(public int " + methodToOverrideBadly + "(\\s)*\\()";
        Pattern r = Pattern.compile(beginOfAccessorRegex);
        Matcher m = r.matcher(recordInfo.getRecordNegativTest());

        //Suche nach Akzessor im Body des Records
        if (m.find()) {
            int startIndex = m.start(); //m.end() + 1;
            int indexEndMethod;

            //Set um die Klammern zu zählen, wenn keine Klammer mehr drinn ist, ist Record zuende
            ArrayList<Character> brackets = new ArrayList<>();

            for (int i = startIndex; i < recordInfo.getRecordNegativTest().length(); i++) {
                if (recordInfo.getRecordNegativTest().charAt(i) == '{') {
                    //speichere Klammer in Liste
                    brackets.add(recordInfo.getRecordNegativTest().charAt(i));
                }
                if (recordInfo.getRecordNegativTest().charAt(i) == '}') {
                    if (!brackets.isEmpty()) {
                        //wenn noch mehrere Klammern vorhanden sind -> entferne diese aus Liste
                        if (brackets.size() != 1) {
                            brackets.remove(0);
                        } else {
                            //letzte '}'-Klammer wurde gelesen (i+1 da sonst letzte '}'-Klammer fehlt)
                            indexEndMethod = i + 1;

                            //extrahiere Methode des Records (inklusive Header)
                            String methodBody = recordInfo.getRecordNegativTest().substring(startIndex, indexEndMethod);

                            //Setze zu manipulierenden Akzessor
                            recordInfo.setRecordNegativTestOverriddenMethod(methodBody);

                            veraendereRueckgabeWertDerMethode();

                            return;
                        }
                    }
                }
            }
        }
    }

    /**
     * Überschreibt alle alten Bezeichner damit Negativtest-Record <name>NegativTest heißt.
     */
    private static void ueberschreibeAlleBezeichnerFuerNegativTest() {
        String nameNegativTestRecord = recordInfo.getName() + "NegativTest";

        recordInfo.setRecordNegativTestName(nameNegativTestRecord);
        recordInfo.setRecordNegativTest(recordInfo.getRecordNegativTest().replace(
                recordInfo.getName(), nameNegativTestRecord));
    }

    /**
     * Verändert den Rückgabewert der ausgewählten Akzessormethode zum Erstellen des
     * Negativtest-Records. Der Rückgabewert wird um 5 erhöht. (TODO willkürlich)
     */
    private static void veraendereRueckgabeWertDerMethode() {
        //Backup: (return(\s)*([\d\w\s,\.\[\]\{\}\(\)\<\>\*\+\-\=\!\?\^\$\|\%])*;)
        String regexReturn = "(return(\\s)*([\\d\\w\\s,.\\[\\]{}()<>*+\\-=!?^$|%])*;)";
        Pattern r = Pattern.compile(regexReturn);
        Matcher m = r.matcher(recordInfo.getRecordNegativTestOverriddenMethod());

        String overriddenMethodOld = recordInfo.getRecordNegativTestOverriddenMethod();
        recordInfo.setRecordNegativTestOverriddenMethodOld(overriddenMethodOld);
        String overriddenMethodNew;

        //manipuliere Akzessor für jedes Match (da mehrere "returns" möglich)
        while (m.find()) {
            int indexEndOfReturn = m.end() - 1;

            //fügt "+ 5" an letzter Stelle vor dem ";" ein
            overriddenMethodNew = overriddenMethodOld.substring(0, indexEndOfReturn)
                    + "+ 5"
                    + overriddenMethodOld.substring(indexEndOfReturn);

            //Ersetze extrahierten Akzessor mit manipulierter Methode
            recordInfo.setRecordNegativTestOverriddenMethod(overriddenMethodNew);
        }
    }

    /**
     * Bestimmt einen nicht überschriebene Akzessor des zu testenden Records.
     * Wenn alle Akzessoren überschrieben wurden, wird die erste Komponente
     * in der HashMap ausgewählt.
     *
     * @return Name der zu überschreibenden Methode
     */
    private static String bestimmeZuUeberschreibendeMethodeFuerNegativTest() {
        for (Object key : recordInfo.getComponentMap().keySet()) {
            if (!recordInfo.getListOverriddenMethods().contains(key.toString())) {
                return key.toString();
            }
        }

        //Wenn alle Akzessoren überschrieben wurden, wähle einen aus
        Map.Entry<Object, Object> entry = recordInfo.getComponentMap().entrySet().iterator().next();
        return (String) entry.getKey();
    }

    /**
     * Erstellt den Positivtest für "equals"
     * @param pathForNewTestFile Pfad zur generierten Testdatei
     */
    private static void erstelleTestfallEqualsPositivtest(Path pathForNewTestFile) throws IOException {
        StringBuilder sb = new StringBuilder();

        //erstelle TestRecord Copies
        erstelleTestRecordCopies();

        String headerMethod = "@Test" + "\n" +
                    "public void testeFunktionalitaetEqualsPositivtest(){";

        fuegeTestRecordsMitEqualsAnHeader(sb, headerMethod);

        //schließe Methode ab
        sb.append("}\n");

        FileWriter fw = new FileWriter(pathForNewTestFile.toString(), true);
        BufferedWriter bw = new BufferedWriter(fw);
        bw.newLine();
        bw.newLine();
        bw.write("//Testfall Equals - Positivtest");
        bw.newLine();
        bw.write(sb.toString());
        bw.newLine();
        bw.close();
    }

    private static void erstelleTestRecordCopies() {
        ArrayList<String> listTestRecordsCopy = new ArrayList<>();

            for(int i = 0; i < recordInfo.getTestRecordsPositiv().size(); i++){
                StringBuilder sb = new StringBuilder();
                String testRecordCopyTemp = recordInfo.getName() + " " + recordInfo.getTestRecordNames().get(i) + "copy" + " = new " + recordInfo.getName() + "(";

                sb.append(testRecordCopyTemp);

                for (Object key : recordInfo.getComponentMap().keySet()) {
                    sb.append(recordInfo.getName()).append(i).append(".").append(key.toString()).append("()").append(",");
                    testRecordCopyTemp = sb.toString();
                }
                String testRecordCopyTemp2 = testRecordCopyTemp.substring(0, testRecordCopyTemp.length() - 1);
                String testRecordCopyFinal = testRecordCopyTemp2.concat(");");

                listTestRecordsCopy.add(testRecordCopyFinal);
            }
            recordInfo.setTestRecordCopies(listTestRecordsCopy);

    }

    /**
     * Erstellt die grundlegende Struktur der Testdatei mit imports für JUnit.
     *
     * @param pathForNewTestFile Pfad zur generierten Testdatei
     * @param nameTestKlasse     Name der generierten Testdatei
     */
    private static void erstelleGrundlegendeTestStrukturInDatei(Path pathForNewTestFile, String nameTestKlasse) throws IOException {
        //TODO package generated; wieder herausnehmen, wenn endgültiger Export-Path feststeht
        String headerTestClass = "package generated;\n" +
                "import static org.junit.jupiter.api.Assertions.*;\n" +
                "\n" +
                "import org.junit.jupiter.api.Test;\n" +
                "\n" +
                "/**\n" +
                " * Automatisch generierte Testklasse mit JUnit-Tests für den Record \"" + recordInfo.getName() + "\"\n" +
                " */" + "\n" +
                "public class " + nameTestKlasse + "{";

        //schreibe in Datei, damit sie auch erstellt wird
        FileWriter myWriter;
        myWriter = new FileWriter(pathForNewTestFile.toString());
        myWriter.write(headerTestClass);
        myWriter.close();
    }

    /**
     * Erstellt eine neue Datei für die generierten Testfaelle im "Projektverzeichnis/generated"
     * und eine initiale Testdatei mit .java-Endung in dem neuen Verzeichnis "/generated"
     */
    private static void erstelleNeueTestJavaDatei(Path pathForNewTestDirectory, Path pathForNewTestFile) throws IOException {
        //erstelle neue Datei
        new File(pathForNewTestFile.toString());

        //erstelle Ordner, wenn dieser nicht existiert
        Files.createDirectories(pathForNewTestDirectory);

        //schreibe in Datei, damit sie auch erstellt wird
        FileWriter myWriter;
        myWriter = new FileWriter(pathForNewTestFile.toString());
        myWriter.write("");
        myWriter.close();
    }

    /**
     * Überprüft, ob Testfälle erstellt werden sollen
     *
     * @param fileContent Dateiinhalt als String
     * @return true, wenn Testfälle generiert werden sollen
     */
    private static boolean pruefeObTestfaelleGeneriertWerdenSollen(String fileContent) {
        if (!fileContent.isEmpty() && fileContent.contains("public class") && fileContent.contains("record")) {

            //Regulärer Ausdruck um Beginn des Records zu ermitteln (record\s\w+\()
            String beginOfRecordRegex = "(record\\s\\w+\\()";
            Pattern r = Pattern.compile(beginOfRecordRegex);
            Matcher m = r.matcher(fileContent);

            //Suche nach Record in Datei
            if (m.find()) {
                recordInfo = new RecordInfo();
                recordInfo.setFilename(path.toString());

                indexEntryPointRecordBeforeMatch = m.start();
                indexEntryPointRecordAfterMatch = m.end();

                //speichere Komponenten in RecordInfo
                speichereKomponenten(fileContent);

                if (recordInfo.getComponentMap() != null) {
                    if (recordEnthaeltNurInteger(recordInfo.getComponentMap())) {

                        //extrahiere kompletten Record (mit Body)
                        speichereBody(fileContent);
                        speichereKomplettenRecord();

                        //prüfe, ob automatisch generierte Methoden oder Konstruktor im Record überschrieben wurden
                        if (recordUeberschreibtMethodenOderKonstruktor()) {
                            return true;
                        } else {
                            System.err.println("Der angegebene Record überschreibt keine automatisch generierten Methoden und muss daher nicht getestet werden." + "\nTest-Tool wird beendet.");
                            return false;
                        }
                    }else{
                        return false;
                    }
                }
            } else {
                System.err.println("Error: Es wurde kein Record in der angegebenen Datei gefunden." + "\nTest-Tool wird beendet.");
                return false;
            }
        } else {
            System.err.println("Error: Es wurde kein Record in der angegebenen Datei gefunden." + "\nTest-Tool wird beendet.");
            return false;
        }
        return true;
    }

    /**
     * Überprüft, ob der Record eine automatisch generierte Methode oder den kanonischen Konstruktor überschreibt
     *
     * @return true, wenn Konstruktor/Methode überschrieben wurde
     */
    private static boolean recordUeberschreibtMethodenOderKonstruktor() {
        //Liste mit regulären Ausdrücken zum Durchsuchen des Records
        List<Pattern> methodenPatterns = new ArrayList<>();

        //Reguläre Ausdrücke zum Durchsuchen des Records
        String constructorRegex = "(public " + recordInfo.getName() + "(\\s)*\\{)";
        String equalsRegex = "(public boolean equals(\\s)*\\()";
        String toStringRegex = "(public String toString(\\s)*\\()";
        String hashCodeRegex = "(public int hashCode(\\s)*\\()";

        //Füge alle regulären Ausdrücke zur Liste hinzu
        Collections.addAll(methodenPatterns, Pattern.compile(constructorRegex), Pattern.compile(equalsRegex),
                Pattern.compile(toStringRegex), Pattern.compile(hashCodeRegex));

        //Füge alle Akzessoren zur Liste hinzu
        for (Object key : recordInfo.getComponentMap().keySet()) {
            String accessorRegex = "(public int " + key.toString() + "(\\s)*\\()";
            methodenPatterns.add(Pattern.compile(accessorRegex));
        }

        //Liste für gefundene Matches
        ArrayList<String> results = new ArrayList<>();

        //Durchsuche kompletten Record nach Patterns
        for (Pattern p : methodenPatterns) {
            Matcher m = p.matcher(recordInfo.getRecordFull());
            //Bei erstem Match gebe true zurück
            if (m.find()) {
                String output = m.group(0);
                //Formatiere output for dem Speichern
                if (m.group(0).contains("{")) {
                    results.add(output.replace("{", ""));
                } else if (m.group(0).contains("(")) {
                    results.add(output.replace("(", ""));
                }
            }
        }

        //Printe gefundene Ergebnisse oder Error wenn nicht gefunden wurde
        if (!results.isEmpty()) {
            //Speichere überschriebene Methoden und Konstruktoren in recordInfo ab
            speichereUeberschriebeneMethoden(results);

            System.out.println("Gefundene überschriebene Methoden und Konstruktoren:");
            for (String result : results) {
                if (result.contains(recordInfo.getName())) {
                    //wenn Name des Records enthalten ist -> Konstruktor wurde überschrieben
                    System.out.println("Konstruktor: " + result);
                } else {
                    System.out.println("Methode: " + result);
                }
            }
            System.out.println("--------------------------------------------------------");
            return true;
        } else {
            return false;
        }
    }

    /**
     * Speichert die Bezeichner der überschriebenen Methoden und Konstruktoren in RecordInfo ab.
     * Die Liste an gefundenen Methoden und Konstruktoren muss vorher konvertiert werden, damit
     * auch nur immer das letzte Wort (d.h. Bezeichner) gespeichert wird
     *
     * @param results Liste von gefundenen Methoden und Konstruktoren
     */
    private static void speichereUeberschriebeneMethoden(ArrayList<String> results) {
        ArrayList<String> overriddenMethods = new ArrayList<>();
        for (String result : results) {
            String lastWord = result.substring(result.lastIndexOf(" ") + 1);
            overriddenMethods.add(lastWord);
        }
        recordInfo.setListOverriddenMethods(overriddenMethods);
    }

    /**
     * Speichert den kompletten Record mit Header und Body als "RecordFull" in recordInfo ab
     */
    private static void speichereKomplettenRecord() {
        if (recordInfo.getRecordHeader() != null && recordInfo.getRecordBody() != null) {
            recordInfo.setRecordFull(recordInfo.getRecordHeader().concat(recordInfo.getRecordBody()));
        }
    }

    /**
     * Speichert den Body des Records als "RecordBody" in recordInfo ab
     *
     * @param fileContent Dateiinhalt als String
     */
    private static void speichereBody(String fileContent) {
        // + 1, da sonst die letzte ')'-Klammer des Headers mitgenommen werden würde
        int indexStartBody = indexEndOfComponentList + 1;
        int indexEndBody;

        //Set um die Klammern zu zählen, wenn keine Klammer mehr drinn ist, ist Record zuende
        ArrayList<Character> brackets = new ArrayList<>();

        if (fileContent.charAt(indexStartBody) == '{') {
            for (int i = indexStartBody; i < fileContent.length(); i++) {
                if (fileContent.charAt(i) == '{') {
                    //speichere Klammer in Liste
                    brackets.add(fileContent.charAt(i));
                }
                if (fileContent.charAt(i) == '}') {
                    if (!brackets.isEmpty()) {
                        //wenn noch mehrere Klammern vorhanden sind -> entferne diese aus Liste
                        if (brackets.size() != 1) {
                            brackets.remove(0);
                        } else {
                            //letzte '}'-Klammer wurde gelesen (i+1 da sonst letzte '}'-Klammer fehlt)
                            indexEndBody = i + 1;

                            //extrahiere Body des Records
                            String body = fileContent.substring(indexStartBody, indexEndBody);
                            recordInfo.setRecordBody(body);
                            return;
                        }
                    }
                }
            }
        }
    }

    /**
     * Speichert die Komponenten des Records in einer Hashmap ab, um zu erkennen, ob es sich nur um
     * Integer-Werte handelt. Speichert Anzahl der Komponenten ab.
     *
     * @param fileContent Dateiinhalt als String
     */
    private static void speichereKomponenten(String fileContent) {
        for (int i = indexEntryPointRecordAfterMatch; i < fileContent.length(); i++) {
            if (fileContent.charAt(i) == ')') {
                //Ende der Parameterliste erreicht
                indexEndOfComponentList = i;

                //extrahiere Record Header mit Name und Komponentenliste (+ 1, damit letzte ')'-Klammer noch mitkommt)
                recordInfo.setRecordHeader(fileContent.substring(indexEntryPointRecordBeforeMatch, indexEndOfComponentList + 1));
                recordInfo.setName(extrahiereRecordName(fileContent));
                //Ausgabe des Namens auf Konsole
                System.out.println("Name des Records: " + recordInfo.getName());
                System.out.println("--------------------------------------------------------");

                //speichere String-Komponenten-Liste in Arraylist
                String componentsAsString = fileContent.substring(indexEntryPointRecordAfterMatch, indexEndOfComponentList);
                ArrayList<String> listWords = extrahiereKomponentenInArrayList(componentsAsString);

                //speichere einzelne Komponenten in Hashmap
                LinkedHashMap<Object, Object> componentMap = new LinkedHashMap<>();
                if (!listWords.isEmpty()) {
                    for (int j = 0; j < listWords.size(); j++) {
                        if (j % 2 == 0) {
                            //key=name, value=type
                            //hier andersherum, da sonst der vorherige Eintrag immer ersetzt wird, da Key gleich wäre (int)
                            componentMap.put(listWords.get(j + 1), listWords.get(j));
                        }
                    }
                    recordInfo.setComponentMap(componentMap);
                    recordInfo.setAmountComponents(componentMap.keySet().size());

                    //beende Schleife nach Extrahieren
                    break;
                }
            }
        }
    }

    /**
     * Extrahiert den Namen eines Records aus dem Dateiinhalt der Datei, welche als
     * Parameter dem Programm mitgegeben wurde
     *
     * @param fileContent Dateiinhalt als String
     * @return Name des Records als String
     */
    private static String extrahiereRecordName(String fileContent) {
        ArrayList<String> listWords = new ArrayList<>();
        Scanner s = new Scanner(fileContent.substring(indexEntryPointRecordBeforeMatch, indexEntryPointRecordAfterMatch));
        while (s.hasNext()) {
            String word = s.next();
            listWords.add(word.replace("(", ""));
        }
        //last word is name of record
        return listWords.get(listWords.size() - 1);
    }

    /**
     * Überprüft, ob der Record nur Integer als Komponenten enthält. Wenn eine Komponente ein Object ist,
     * wird eine Warnung herausgegeben, dass der Wert der Variable geändert werden könnte
     *
     * @param componentMap HashMap mit Komponenten, wobei key=Bezeichner, value=type
     * @return true, wenn Record nur Integer als Komponenten enthält
     */
    private static boolean recordEnthaeltNurInteger(HashMap<Object, Object> componentMap) {
        //prüfe, ob Record nur Integer-Werte enthält
        ArrayList<String> dataTypes = new ArrayList<>();
        Collections.addAll(dataTypes, "boolean", "char", "byte", "short", "long", "float", "double", "String");

        ArrayList<String> listFoundKeysNotInt = new ArrayList<>();
        ArrayList<String> listFoundObjects = new ArrayList<>();
        ArrayList<String> listFoundDataTypes = new ArrayList<>();

        for (Object key : componentMap.values()) {
            for (String type : dataTypes) {
                //prüfe auf Datentypen in Java. Mit "!key.equals("int")" werden auch benutzerdefinierte Objekte geprüft
                if (key.equals(type)) {
                    listFoundKeysNotInt.add(key.toString());
                    break;
                } else if (!key.equals(type) && !key.equals("int")) {
                    listFoundKeysNotInt.add(key.toString());
                    break;
                }
            }
        }

        //Sortiere gefundene Nicht-Integer-Komponenten auf zwei Listen: listFoundDataTypes und listFoundObjects
        if (!listFoundKeysNotInt.isEmpty()) {
            for (String keyNotInt : listFoundKeysNotInt) {
                if (!dataTypes.contains(keyNotInt)) {
                    listFoundObjects.add(keyNotInt);
                } else {
                    listFoundDataTypes.add(keyNotInt);
                }
            }
        }

        //Gebe Error oder Warnung heraus
        if (!listFoundDataTypes.isEmpty() && listFoundObjects.isEmpty()) {
            if (listFoundDataTypes.size() == 1) {
                System.err.println("Error: Komponente mit Nicht-Integer-Wert gefunden:\n" + listFoundDataTypes.get(0) + "\nTest-Tool wird beendet.");
            } else {
                System.err.println("Error: Komponenten mit Nicht-Integer-Wert gefunden: ");
                for (String foundTypes : listFoundDataTypes) {
                    System.err.println(foundTypes);
                }
                System.err.println("Test-Tool wird beendet.");
            }
            return false;
        } else if (!listFoundObjects.isEmpty() && listFoundDataTypes.isEmpty()) {
            if (listFoundObjects.size() == 1) {
                System.err.println("Warnung: Komponente mit Objekt gefunden:\n" + listFoundObjects.get(0) + "\nHinweis: Objekte können weiterhin verändert werden!\nTest-Tool wird beendet.");
            } else {
                System.err.println("Warnung: Komponente mit Objekt gefunden: ");
                for (String foundObjects : listFoundObjects) {
                    System.err.println(foundObjects);
                }
                System.err.println("Hinweis: Objekte können weiterhin verändert werden!\nTest-Tool wird beendet.");
            }
            return false;
        } else if (!listFoundDataTypes.isEmpty()) { //old: && !listFoundObjects.isEmpty()
            //Warnung, wenn Komponente ein Objekt ist -> Objekte können weiterhin verändert werden
            System.err.println("Warnung: Komponente(n) mit Nicht-Integer-Wert(en) gefunden: ");
            for (String foundTypes : listFoundDataTypes) {
                System.err.println(foundTypes);
            }
            for (String foundObjects : listFoundObjects) {
                System.err.println(foundObjects);
            }
            System.err.println("Hinweis: Objekte können weiterhin verändert werden!\nTest-Tool wird beendet.");
            return false;
        }

        //Wenn nur Integer-Komponenten gefunden worden -> gebe true aus
        return true;
    }

    /**
     * Extrahiert die Komponenten des Records in eine ArrayListe, wobei jedes Wort ein einzelnes Element der Liste ist
     *
     * @param componentsAsString Komponenten des Records als String
     * @return ArrayListe<String>, wobei jedes Wort ein einzelnes Element der Liste ist
     */
    private static ArrayList<String> extrahiereKomponentenInArrayList(String componentsAsString) {
        ArrayList<String> listWords = new ArrayList<>();
        Scanner s = new Scanner(componentsAsString);
        while (s.hasNext()) {
            String word = s.next();

            if (word.contains(",")) {
                listWords.add(word.replace(",", ""));
            } else {
                if (word.contains(" ")) {
                    listWords.add(word.replace(" ", ""));
                } else {
                    listWords.add(word);
                }
            }
        }
        return listWords;
    }
}
