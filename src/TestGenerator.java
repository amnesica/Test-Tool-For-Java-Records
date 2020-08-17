import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestGenerator {

    //Der zu testende Record
    private RecordToTest recordToTest;

    //Liste mit den erstellten Testrecords für Positivtest
    private ArrayList<TestRecord> listTestRecordsPositiv;

    //Liste mit Namen der erstellten Testrecords als String
    private ArrayList<String> listTestRecordNames;

    //Liste mit TestRecord Kopien als TestRecord
    private ArrayList<TestRecord> listTestRecordsPositivCopies;

    //Name des TestRecords für den NegativTest
    private String nameNegativTestRecord;

    /**
     * Generiert die Testfaelle für den vorliegenden Record in recordInfo
     */
    void generiereTestfaelle(RecordToTest recordToTest) {

        //Speichere recordToTest als Feld ab
        this.recordToTest = recordToTest;

        //Spezifiziere Pfade für JUnit-Testklasse und Name der Datei
        Path pathForNewTestDirectory = Paths.get(System.getProperty("user.dir") + "/generated/");
        String nameTestKlasse = recordToTest.getName() + "Test";
        Path pathForNewTestFile = Paths.get(System.getProperty("user.dir") + "/generated/" + nameTestKlasse + ".java");

        try {
            erstelleNeueTestJavaDatei(pathForNewTestDirectory, pathForNewTestFile);

            erstelleGrundlegendeTestStrukturInDatei(pathForNewTestFile, nameTestKlasse);

            fuegeZuTestendenRecordEin(pathForNewTestFile, recordToTest);

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
     */
    private void erstelleTestRecords() {
        //Initialisiere Listen
        listTestRecordNames = new ArrayList<>();
        listTestRecordsPositiv = new ArrayList<>();

        //generiere Liste mit Testwerten für Testrecords
        ArrayList<Integer> listTestWerte = new ArrayList<>();
        Collections.addAll(listTestWerte, 1, Integer.MAX_VALUE, Integer.MIN_VALUE, -1, 0, Integer.MAX_VALUE - 1, Integer.MIN_VALUE + 1);
        //TODO Möglichkeit einbauen, benutzerdefinierte Grenzwerte einzufügen (und diese der Liste listTestWerte hinzufügen)

        //bestimme Anzahl zu erstellender Testrecords
        int anzahlZuErstellenderTestrecords = listTestWerte.size() / recordToTest.getAmountComponents();
        int fehlendeKomponenten = listTestWerte.size() % recordToTest.getAmountComponents();
        if (fehlendeKomponenten != 0) {
            do {
                anzahlZuErstellenderTestrecords += 1;
                fehlendeKomponenten -= 1;
            } while (fehlendeKomponenten != 0);
        }

        //Erstelle Testrecords und verteile zu testende Werte
        int indexListTestWerte = 0;
        for (int i = 0; i < anzahlZuErstellenderTestrecords; i++) {
            TestRecord testRecord = new TestRecord();

            String testRecordName = recordToTest.getName() + i;
            testRecord.setName(testRecordName);
            listTestRecordNames.add(testRecordName);

            String initializedTestRecord = recordToTest.getName() + " " + testRecordName + " = new " + recordToTest.getName() + "(";

            for (int j = 0; j < recordToTest.getAmountComponents(); j++) {
                if (indexListTestWerte >= listTestWerte.size()) {
                    indexListTestWerte = 0;
                }
                initializedTestRecord = initializedTestRecord.concat(String.valueOf(listTestWerte.get(indexListTestWerte)));
                indexListTestWerte += 1;

                //hänge ein "," oder ein ");" an, um Record abzuschließen
                if (j == recordToTest.getAmountComponents() - 1) {
                    initializedTestRecord = initializedTestRecord.concat(");");
                } else {
                    initializedTestRecord = initializedTestRecord.concat(",");
                }
            }
            testRecord.setInitializedRecord(initializedTestRecord);

            listTestRecordsPositiv.add(testRecord);
        }
    }

    /**
     * Fügt den zu testenden Record in die Testdatei ein.
     *
     * @param pathForNewTestFile Pfad zur generierten Testdatei
     * @throws IOException IOException
     */
    private void fuegeZuTestendenRecordEin(Path pathForNewTestFile, RecordToTest recordToTest) throws IOException {
        FileWriter fw = new FileWriter(pathForNewTestFile.toString(), true);
        BufferedWriter bw = new BufferedWriter(fw);
        bw.newLine();
        bw.newLine();
        bw.write("//zu testender Record");
        bw.newLine();
        bw.write(recordToTest.getRecordFull());
        bw.newLine();
        bw.close();
    }

    /**
     * Erstellt die Testfaelle für das Qualitätskriterium "Funktionelle Eignung".
     * Es wird ein Positiv- und ein Negativtest erstellt. Zuvor werden die Testrecords erstellt
     */
    private void erstelleTestfaelleFunktionalitaet(Path pathForNewTestFile) throws IOException {
        //Erstelle TestRecords und speichere diese in Liste testRecordsPositiv
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

    private void erstelleTestfallToStringPositivtest(Path pathForNewTestFile) throws IOException {
        StringBuilder sb = new StringBuilder();

        String headerMethod = "@Test" + "\n" +
                "public void testeFunktionalitaetToStringPositivtest(){";

        fuegeTestRecordsMitEqualsAnHeader(sb, headerMethod);

        //erstelle Assertions mit toString
        for (String listTestRecordName : listTestRecordNames) {
            sb.append("assertEquals(").append(listTestRecordName)
                    .append(".toString(),").append(listTestRecordName).append("copy.toString());\n");
        }

        //schließe Methode ab
        sb.append("}\n");

        //schließe Klass ab
        sb.append("}\n");

        schreibeInDatei(pathForNewTestFile, sb.toString(), "//Testfall ToString - Positivtest");
    }

    private void erstelleTestfallHashCodePositivtest(Path pathForNewTestFile) throws IOException {
        StringBuilder sb = new StringBuilder();

        String headerMethod = "@Test" + "\n" +
                "public void testeFunktionalitaetHashCodePositivtest(){";

        fuegeTestRecordsMitEqualsAnHeader(sb, headerMethod);

        //erstelle Assertions mit hashCode
        for (String listTestRecordName : listTestRecordNames) {
            sb.append("assertEquals(").append(listTestRecordName)
                    .append(".hashCode(),").append(listTestRecordName).append("copy.hashCode());\n");
        }

        //schließe Methode ab
        sb.append("}\n");

        schreibeInDatei(pathForNewTestFile, sb.toString(), "//Testfall HashCode - Positivtest");
    }

    private void schreibeInDatei(Path pathForNewTestFile, String content, String comment) throws IOException {
        FileWriter fw = new FileWriter(pathForNewTestFile.toString(), true);
        BufferedWriter bw = new BufferedWriter(fw);
        bw.newLine();
        bw.newLine();
        bw.write(comment);
        bw.newLine();
        bw.write(content);
        bw.newLine();
        bw.close();
    }

    private void fuegeTestRecordsMitEqualsAnHeader(StringBuilder sb, String headerMethod) {
        sb.append(headerMethod);

        //füge TestRecords mit Testdaten sowie Kopien der Testrecords ein
        for (int i = 0; i < listTestRecordsPositiv.size(); i++) {
            sb.append(listTestRecordsPositiv.get(i).getInitializedRecord()).append("\n");
            sb.append(listTestRecordsPositivCopies.get(i).getInitializedRecord()).append("\n").append("\n");
        }

        //erstelle Assertions mit equals
        for (String listTestRecordName : listTestRecordNames) {
            sb.append("assertTrue(").append(listTestRecordName)
                    .append(".equals(").append(listTestRecordName).append("copy));\n");
        }

        sb.append("\n");
    }

    private void erstelleTestfallEqualsNegativtest(Path pathForNewTestFile) throws IOException {
        ArrayList<String> testRecordsNegativ = new ArrayList<>();
        ArrayList<String> testRecordsNegativCopies = new ArrayList<>();
        StringBuilder sb = new StringBuilder();

        //baue bereits erstellte Records für Negativtest um
        for (TestRecord testRecord : listTestRecordsPositiv) {
            String testRecordNegativ = testRecord.getInitializedRecord()
                    .replace(recordToTest.getName(), nameNegativTestRecord);
            testRecordsNegativ.add(testRecordNegativ);
        }

        //baue bereits erstellte Kopier-Records für Negativtest um
        for (TestRecord testRecordCopy : listTestRecordsPositivCopies) {
            String testRecordNegativCopy = testRecordCopy.getInitializedRecord()
                    .replace(recordToTest.getName(), nameNegativTestRecord);
            testRecordsNegativCopies.add(testRecordNegativCopy);
        }

        String headerMethod = "@Test" + "\n" +
                "public void testeFunktionalitaetEqualsNegativtest(){";
        sb.append(headerMethod);

        //füge TestRecords mit Testdaten sowie Kopien der Testrecords ein
        for (int i = 0; i < testRecordsNegativ.size(); i++) {
            sb.append(testRecordsNegativ.get(i)).append("\n");
            sb.append(testRecordsNegativCopies.get(i)).append("\n").append("\n");
        }

        //erstelle Assertions
        for (int i = 0; i < listTestRecordNames.size(); i++) {
            sb.append("assertFalse(").append(nameNegativTestRecord).append(i)
                    .append(".equals(").append(nameNegativTestRecord).append(i).append("copy));\n");
        }

        //schließe Methode ab
        sb.append("}\n");

        schreibeInDatei(pathForNewTestFile, sb.toString(), "//Testfall Equals - Negativtest");
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
    private void fuegeNegativtestRecordEin(Path pathForNewTestFile) throws IOException {
        String methodToOverrideBadly = bestimmeZuUeberschreibendeMethodeFuerNegativTest();

        TestRecord testRecordNegativ = new TestRecord();
        testRecordNegativ.setForNegativTest(true);

        //Setze Negativtest-Record erst einmal auf zu testenden Record
        testRecordNegativ.setRecordNegativTestFull(recordToTest.getRecordFull());

        if (recordToTest.getListOverriddenMethods().contains(methodToOverrideBadly)) {
            //Akzessor wurde bereits überschrieben -> verändere Rückgabewert
            manipuliereVorhandenenAkzessor(methodToOverrideBadly, testRecordNegativ);
        } else {
            //füge Akzessor am Ende des Records an
            erstelleNeuenManipuliertenAkzessor(methodToOverrideBadly, testRecordNegativ);
        }
        erstelleNegativTestRecord(testRecordNegativ);

        schreibeInDatei(pathForNewTestFile, testRecordNegativ.getRecordNegativTestFull(), "//zu testender Record für Negativtest");
    }

    /**
     * Erstellt einen NegativTestRecord für den Negativtest
     */
    private void erstelleNegativTestRecord(TestRecord testRecordNegativ) {
        String accessorToOverride = testRecordNegativ.getAccessorToOverride();
        String accessorOverridden = testRecordNegativ.getAccessorOverridden();

        //Ersetze vorhandene Methode in RecordNegativTestFull
        if (accessorToOverride != null && accessorOverridden != null) {
            testRecordNegativ.setRecordNegativTestFull(testRecordNegativ.getRecordNegativTestFull()
                    .replace(accessorToOverride, accessorOverridden));

        } else { //füge Methode am Ende des Records an
            if (testRecordNegativ.getRecordNegativTestFull().endsWith("}")) {
                String oldNegativTestRecord = testRecordNegativ.getRecordNegativTestFull()
                        .substring(0, testRecordNegativ.getRecordNegativTestFull().length() - 1);

                //Ersetze alten RecordNegativTest mit "}" am Ende, um Record abzuschließen
                testRecordNegativ.setRecordNegativTestFull(oldNegativTestRecord.concat(accessorOverridden + "\n}"));
            }
        }

        //Record für NegativTest soll <name>NegativTest heißen
        ueberschreibeAlleBezeichnerFuerNegativTest(testRecordNegativ);
    }

    /**
     * Erstellt einen manipulierten Akzessor, welcher noch nicht im TestRecord überschrieben wurde.
     * Dient der Erstellung eines NegativTestRecords.
     *
     * @param methodToOverrideBadly Name der Methode, welche manipuliert werden soll
     */
    private void erstelleNeuenManipuliertenAkzessor(String methodToOverrideBadly, TestRecord testRecordNegativ) {
        String newAccessor = "public int " + methodToOverrideBadly + "(){\nreturn " + methodToOverrideBadly + "+ 5;\n}";
        testRecordNegativ.setAccessorOverridden(newAccessor);
    }

    /**
     * Manipuliert einen vorhandenen Akzessor und speichert die manipulierte Methode
     * im recordInfo ab
     *
     * @param methodToOverrideBadly Name der Methode, welche manipuliert werden soll
     */
    private void manipuliereVorhandenenAkzessor(String methodToOverrideBadly, TestRecord testRecordNegativ) {
        String beginOfAccessorRegex = "(public int " + methodToOverrideBadly + "(\\s)*\\()";
        Pattern r = Pattern.compile(beginOfAccessorRegex);
        Matcher m = r.matcher(testRecordNegativ.getRecordNegativTestFull());

        //Suche nach Akzessor im Body des Records
        if (m.find()) {
            int startIndex = m.start();
            int indexEndMethod;

            //Set um die Klammern zu zählen, wenn keine Klammer mehr drinn ist, ist Record zuende
            ArrayList<Character> brackets = new ArrayList<>();

            for (int i = startIndex; i < testRecordNegativ.getRecordNegativTestFull().length(); i++) {
                if (testRecordNegativ.getRecordNegativTestFull().charAt(i) == '{') {
                    //speichere Klammer in Liste
                    brackets.add(testRecordNegativ.getRecordNegativTestFull().charAt(i));
                }
                if (testRecordNegativ.getRecordNegativTestFull().charAt(i) == '}') {
                    if (!brackets.isEmpty()) {
                        //wenn noch mehrere Klammern vorhanden sind -> entferne diese aus Liste
                        if (brackets.size() != 1) {
                            brackets.remove(0);
                        } else {
                            //letzte '}'-Klammer wurde gelesen (i+1 da sonst letzte '}'-Klammer fehlt)
                            indexEndMethod = i + 1;

                            //extrahiere Methode des Records (inklusive Header)
                            String method = testRecordNegativ.getRecordNegativTestFull().substring(startIndex, indexEndMethod);

                            //Setze zu manipulierenden Akzessor
                            testRecordNegativ.setAccessorToOverride(method);

                            veraendereRueckgabeWertDerMethode(testRecordNegativ);

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
    private void ueberschreibeAlleBezeichnerFuerNegativTest(TestRecord testRecordNegativ) {
        String nameNegativTestRecord = recordToTest.getName() + "NegativTest";
        this.nameNegativTestRecord = nameNegativTestRecord;

        testRecordNegativ.setRecordNegativTestFull(testRecordNegativ.getRecordNegativTestFull()
                .replace(recordToTest.getName(), nameNegativTestRecord));
    }

    /**
     * Verändert den Rückgabewert der ausgewählten Akzessormethode zum Erstellen des
     * Negativtest-Records. Der Rückgabewert wird um 5 erhöht. (TODO willkürlich)
     */
    private void veraendereRueckgabeWertDerMethode(TestRecord testRecordNegativ) {
        String regexReturn = "(return(\\s)*([\\d\\w\\s,.\\[\\]{}()<>*+\\-=!?^$|%])*;)";
        Pattern r = Pattern.compile(regexReturn);
        Matcher m = r.matcher(testRecordNegativ.getAccessorToOverride());

        String manipulatedAccessor = testRecordNegativ.getAccessorToOverride();
        String overriddenManipulatedAccessor;

        //manipuliere Akzessor für jedes Match (da mehrere "returns" möglich)
        while (m.find()) {
            int indexEndOfReturn = m.end() - 1;

            //fügt "+ 5" an letzter Stelle vor dem ";" ein
            overriddenManipulatedAccessor = manipulatedAccessor.substring(0, indexEndOfReturn)
                    + "+ 5"
                    + manipulatedAccessor.substring(indexEndOfReturn);

            //Ersetze extrahierten Akzessor mit manipulierter Methode
            testRecordNegativ.setAccessorOverridden(overriddenManipulatedAccessor);
        }
    }

    /**
     * Bestimmt einen nicht überschriebene Akzessor des zu testenden Records.
     * Wenn alle Akzessoren überschrieben wurden, wird die erste Komponente
     * in der HashMap ausgewählt.
     *
     * @return Name der zu überschreibenden Methode
     */
    private String bestimmeZuUeberschreibendeMethodeFuerNegativTest() {
        for (Object key : recordToTest.getComponentMap().keySet()) {
            if (!recordToTest.getListOverriddenMethods().contains(key.toString())) {
                return key.toString();
            }
        }

        //Wenn alle Akzessoren überschrieben wurden, wähle einen aus
        Map.Entry<Object, Object> entry = recordToTest.getComponentMap().entrySet().iterator().next();
        return (String) entry.getKey();
    }

    /**
     * Erstellt den Positivtest für "equals"
     *
     * @param pathForNewTestFile Pfad zur generierten Testdatei
     */
    private void erstelleTestfallEqualsPositivtest(Path pathForNewTestFile) throws IOException {
        StringBuilder sb = new StringBuilder();

        //erstelle TestRecord Copies
        erstelleTestRecordCopies();

        String headerMethod = "@Test" + "\n" +
                "public void testeFunktionalitaetEqualsPositivtest(){";

        fuegeTestRecordsMitEqualsAnHeader(sb, headerMethod);

        //schließe Methode ab
        sb.append("}\n");

        schreibeInDatei(pathForNewTestFile, sb.toString(), "//Testfall Equals - Positivtest");
    }

    private void erstelleTestRecordCopies() {
        //Initialisiere Liste
        listTestRecordsPositivCopies = new ArrayList<>();

        for (int i = 0; i < listTestRecordsPositiv.size(); i++) {
            StringBuilder sb = new StringBuilder();
            String testRecordCopyTemp = recordToTest.getName() + " " +
                    listTestRecordNames.get(i) + "copy" + " = new " + recordToTest.getName() + "(";

            sb.append(testRecordCopyTemp);

            for (Object key : recordToTest.getComponentMap().keySet()) {
                sb.append(recordToTest.getName()).append(i).append(".").append(key.toString()).append("()").append(",");
                testRecordCopyTemp = sb.toString();
            }
            String testRecordCopyTemp2 = testRecordCopyTemp.substring(0, testRecordCopyTemp.length() - 1);
            String testRecordCopyFinal = testRecordCopyTemp2.concat(");");

            TestRecord testRecordCopy = new TestRecord();
            testRecordCopy.setInitializedRecord(testRecordCopyFinal);

            listTestRecordsPositivCopies.add(testRecordCopy);
        }
    }

    /**
     * Erstellt die grundlegende Struktur der Testdatei mit imports für JUnit.
     *
     * @param pathForNewTestFile Pfad zur generierten Testdatei
     * @param nameTestKlasse     Name der generierten Testdatei
     */
    private void erstelleGrundlegendeTestStrukturInDatei(Path pathForNewTestFile, String nameTestKlasse) throws IOException {
        //TODO package generated; wieder herausnehmen, wenn endgültiger Export-Path feststeht
        String headerTestClass = "package generated;\n" +
                "import static org.junit.jupiter.api.Assertions.*;\n" +
                "\n" +
                "import org.junit.jupiter.api.Test;\n" +
                "\n" +
                "/**\n" +
                " * Automatisch generierte Testklasse mit JUnit-Tests für den Record \"" + recordToTest.getName() + "\"\n" +
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
    private void erstelleNeueTestJavaDatei(Path pathForNewTestDirectory, Path pathForNewTestFile) throws IOException {
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
}
