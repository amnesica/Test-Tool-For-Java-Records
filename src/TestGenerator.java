import ToTest.MethodToTest;
import ToTest.RecordToTest;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Klasse, die die Testfälle für Funktionalität generiert und die Tests auf Leistungseffizienz und Wartbarkeit
 * durchführt
 */
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

    //Classloader für Reflection mit dynamisch erstellten Klassen
    private URLClassLoader urlClassLoader;

    /**
     * Generiert die Testfaelle für Funktionalität für den vorliegenden Record in RecordToTest
     *
     * @param recordToTest RecordToTest
     */
    void generierefunktionaleTestfaelle(RecordToTest recordToTest) {

        //Speichere recordToTest als Feld ab
        this.recordToTest = recordToTest;

        //Spezifiziere Pfade für JUnit-Testklasse und Name der Datei
        Path pathForNewTestDirectory = Paths.get(System.getProperty("user.dir") + "/generated/");
        String nameTestKlasse = recordToTest.getName() + "Test";
        Path pathForNewTestFile = Paths.get(System.getProperty("user.dir") + "/generated/" +
                nameTestKlasse + ".java");

        try {
            erstelleNeueTestDatei(pathForNewTestDirectory, pathForNewTestFile);

            erstelleGrundlegendeTestStrukturJUnitInDatei(pathForNewTestFile, nameTestKlasse);

            fuegeZuTestendenRecordEin(pathForNewTestFile, recordToTest);

            erstelleTestfaelleFunktionalitaet(pathForNewTestFile);

            System.out.println("--------------------------------------------------------");
            System.out.println("Test auf Funktionalität (" + recordToTest.getName() + "):");
            System.out.println("Testdatei für " + recordToTest.getName() +
                    " wurde erfolgreich erstellt und kann ausgeführt werden.");
            System.out.println("Pfad der generierten Datei: " + pathForNewTestFile);
            System.out.println("--------------------------------------------------------");
        } catch (IOException e) {
            System.out.println("Error: Beim Erstellen der Testdatei gab es einen Fehler.");
            e.printStackTrace();
        }
    }

    /**
     * Erstellt die Testrecords mit den festgelegten Testdaten (Integer-Werte) sowie möglichen benutzerdefinierten
     * Grenzwerten, welche per Kommandozeile angefragt werden
     */
    private void erstelleTestRecords() {
        //Initialisiere Listen
        listTestRecordNames = new ArrayList<>();
        listTestRecordsPositiv = new ArrayList<>();

        //generiere Liste mit Testwerten für Testrecords
        ArrayList<Integer> listTestWerte = new ArrayList<>();
        Collections.addAll(listTestWerte, 1, Integer.MAX_VALUE, Integer.MIN_VALUE, -1, 0,
                Integer.MAX_VALUE - 1, Integer.MIN_VALUE + 1);

        //Möglichkeit, benutzerdefinierte Grenzwerte einzufügen (und diese der Liste listTestWerte hinzufügen)
        ArrayList<Integer> listCustomValues = getBenutzerdefiniertenGrenzwerte(recordToTest);
        if (!listCustomValues.isEmpty()) {
            listTestWerte.addAll(listCustomValues);
        }

        //bestimme Anzahl zu erstellender Testrecords
        int anzahlKomponenten = recordToTest.getAmountComponents();
        int anzahlZuTestendeWerte = listTestWerte.size();

        int anzahlAbgedeckterTestWerte = 0;

        //mehr Komponenten als zu testende Werte -> Werte wiederholen lassen! -> ein TestRecord
        int i = 0;
        if (anzahlKomponenten >= anzahlZuTestendeWerte) {
            TestRecord testRecord = new TestRecord();

            String testRecordName = recordToTest.getName() + i;
            testRecord.setName(testRecordName);
            listTestRecordNames.add(testRecordName);

            String initializedTestRecord = recordToTest.getName() + " " + testRecordName + " = new " +
                    recordToTest.getName() + "(";

            while (anzahlAbgedeckterTestWerte != anzahlKomponenten) {
                //lasse i von vorne anfangen
                if (i >= listTestWerte.size()) {
                    i = 0;
                }
                initializedTestRecord = initializedTestRecord.concat(String.valueOf(listTestWerte.get(i)));

                //hänge ein "," oder ein ");" an, um Record abzuschließen
                if (anzahlAbgedeckterTestWerte + 1 == anzahlKomponenten) {
                    initializedTestRecord = initializedTestRecord.concat(");");
                } else {
                    initializedTestRecord = initializedTestRecord.concat(",");
                }

                //inkrementiere Werte
                anzahlAbgedeckterTestWerte += 1;
                i += 1;
            }

            testRecord.setInitializedRecord(initializedTestRecord);
            listTestRecordsPositiv.add(testRecord);

        } else {
            //mehr zu testende Werte als Komponenten -> mehrere TestRecords erstellen
            // (anzahlZuTestendeWerte > anzahlKomponenten)
            int anzahlErstellteTestRecords = 0;
            int anzahlGenutzterTestWerte = 0;

            while (anzahlGenutzterTestWerte < anzahlZuTestendeWerte) {
                TestRecord testRecord = new TestRecord();

                String testRecordName = recordToTest.getName() + anzahlErstellteTestRecords;
                testRecord.setName(testRecordName);
                listTestRecordNames.add(testRecordName);

                String initializedTestRecord = recordToTest.getName() + " " + testRecordName + " = new " +
                        recordToTest.getName() + "(";

                while (anzahlAbgedeckterTestWerte != anzahlKomponenten) {
                    //lasse i von vorne anfangen
                    if (i >= listTestWerte.size()) {
                        i = 0;
                    }
                    initializedTestRecord = initializedTestRecord.concat(String.valueOf(listTestWerte.get(i)));

                    //hänge ein "," oder ein ");" an, um Record abzuschließen
                    if (anzahlAbgedeckterTestWerte + 1 == anzahlKomponenten) {
                        initializedTestRecord = initializedTestRecord.concat(");");
                    } else {
                        initializedTestRecord = initializedTestRecord.concat(",");
                    }

                    //inkrementiere Werte
                    anzahlAbgedeckterTestWerte += 1;
                    i += 1;
                    anzahlGenutzterTestWerte += 1;
                }

                //reset anzahlAbgedeckterTestWerte für den nächsten TestRecord
                anzahlAbgedeckterTestWerte = 0;
                testRecord.setInitializedRecord(initializedTestRecord);
                listTestRecordsPositiv.add(testRecord);
                anzahlErstellteTestRecords += 1;
            }
        }
    }


    /**
     * Fragt auf der Kommandozeile nach benutzerdefinierten Werten und fügt diese der Liste mit den zu testenden
     * Werten hinzu
     *
     * @param recordToTest RecordToTest
     * @return ArrayList<Integer> mit den hinzugefügten Werten
     */
    private ArrayList<Integer> getBenutzerdefiniertenGrenzwerte(RecordToTest recordToTest) {
        ArrayList<Integer> listCustomValues = new ArrayList<>();
        Scanner scanner = new Scanner(System.in);

        String decision = null;
        boolean repeat = true;
        while (repeat) {
            System.out.println("Wollen Sie für den Record " + recordToTest.getName() +
                    " benutzerdefinierte Grenzwerte hinzufügen? (j/n)");
            decision = scanner.nextLine();

            switch (decision) {
                case "j", "n" -> repeat = false;
                default -> System.out.println("Bitte geben Sie nur 'j' oder 'n' an.");
            }
        }

        if (decision.equals("j")) {
            System.out.println("Geben Sie benutzerdefinierte Grenzwerte ein mit einem \"Enter\" getrennt ein. " +
                    "Wenn sie fertig sind drücken Sie nochmals \"Enter\"!");

            boolean repeat2 = true;
            while (repeat2) {
                String stringCustomValue = scanner.nextLine();
                if (!stringCustomValue.equals("")) {
                    try {
                        //füge Wert der Liste hinzu
                        int intCustomValue = Integer.parseInt(stringCustomValue);
                        listCustomValues.add(intCustomValue);
                    } catch (NumberFormatException e) {
                        System.out.println("Bitte geben Sie nur Integer-Werte ein! Versuchen Sie es nochmal:");
                    }
                } else {
                    repeat2 = false;
                }
            }
        }

        return listCustomValues;
    }

    /**
     * Fügt den zu testenden Record in die generierte Testdatei ein
     *
     * @param pathForNewTestFile Pfad zur generierten Testdatei
     * @param recordToTest       RecordToTest
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
     *
     * @param pathForNewTestFile Pfad zur generierten Testdatei
     * @throws IOException IOException
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

    /**
     * Erstellt den Positivtest für "toString"
     *
     * @param pathForNewTestFile Pfad zur generierten Testdatei
     * @throws IOException IOException
     */
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

    /**
     * Erstellt den Positivtest für "hashCode"
     *
     * @param pathForNewTestFile Pfad zur generierten Testdatei
     * @throws IOException IOException
     */
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

    /**
     * Schreibt einen String content in eine Datei mit dem Pfad pathForNewTestFile und fügt darüber einen
     * Kommentar comment ein
     *
     * @param pathForNewTestFile Pfad zur generierten Testdatei
     * @param content            Einzufügender Inhalt als String
     * @param comment            Kommentar, welcher den einzufügenden Inhalt kommentiert
     * @throws IOException IOException
     */
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

    /**
     * Fügt die TestRecords in listTestRecordsPositiv sowie listTestRecordsPositivCopies an den Header headerMethod
     * an und erstellt danach die Assertions mit Equals und jeweiligen TestRecord und seiner Kopie
     *
     * @param sb           StringBuilder
     * @param headerMethod headerMethod als String
     */
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

    /**
     * Erstellt den Negativtest für "equals"
     *
     * @param pathForNewTestFile Pfad zur generierten Testdatei
     * @throws IOException IOException
     */
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
     * um den gewünschten Effekt zu erzielen
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

        schreibeInDatei(pathForNewTestFile, testRecordNegativ.getRecordNegativTestFull(),
                "//zu testender Record für Negativtest");
    }

    /**
     * Erstellt einen NegativTestRecord für den Negativtest bei der Funktionalität
     *
     * @param testRecordNegativ TestRecord
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
     * Dient der Erstellung eines NegativTestRecords
     *
     * @param methodToOverrideBadly Name der Methode, welche manipuliert erstellt werden soll
     * @param testRecordNegativ     TestRecord
     */
    private void erstelleNeuenManipuliertenAkzessor(String methodToOverrideBadly, TestRecord testRecordNegativ) {
        String newAccessor = "public int " + methodToOverrideBadly + "(){\nreturn " + methodToOverrideBadly + "+ 5;\n}";
        testRecordNegativ.setAccessorOverridden(newAccessor);
    }

    /**
     * Manipuliert einen vorhandenen Akzessor im TestRecord und speichert die manipulierte Methode
     * im TestRecord ab
     *
     * @param methodToOverrideBadly Name der Methode, welche manipuliert werden soll
     * @param testRecordNegativ     TestRecord
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
                            String method = testRecordNegativ.getRecordNegativTestFull()
                                    .substring(startIndex, indexEndMethod);

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
     * Überschreibt alle alten Bezeichner damit Negativtest-Record <name>NegativTest heißt
     *
     * @param testRecordNegativ TestRecord
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
     *
     * @param testRecordNegativ TestRecord
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
     * in der HashMap ausgewählt
     *
     * @return Name der zu überschreibenden Methode
     */
    private String bestimmeZuUeberschreibendeMethodeFuerNegativTest() {
        for (Object key : recordToTest.getComponentMap().keySet()) {
            if (!recordToTest.getListOverriddenMethods().contains(key.toString())) {
                return key.toString();
            }
        }

        //Wenn alle Akzessoren überschrieben wurden, wähle erste Komponente des Records aus
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

    /**
     * Erstellt eine Liste an Kopien der TestRecords in den einzelnen Testfaellen
     */
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
     * Erstellt die grundlegende Struktur der Testdatei mit Importen für JUnit und dem Package,
     * in das die Datei erstellt wird
     *
     * @param pathForNewTestFile Pfad zur generierten Testdatei
     * @param nameTestKlasse     Name der generierten Testdatei
     */
    private void erstelleGrundlegendeTestStrukturJUnitInDatei(Path pathForNewTestFile, String nameTestKlasse)
            throws IOException {
        String headerTestClass = "package generated;\n" +
                "import static org.junit.jupiter.api.Assertions.*;\n" +
                "\n" +
                "import org.junit.jupiter.api.Test;\n" +
                "\n" +
                "/**\n" +
                " * Automatisch generierte Testklasse mit JUnit-Tests für den Record \"" + recordToTest.getName() +
                "\"\n" +
                " */" + "\n" +
                "public class " + nameTestKlasse + "{";

        //schreibe in Datei, damit sie auch erstellt wird
        FileWriter myWriter;
        myWriter = new FileWriter(pathForNewTestFile.toString());
        myWriter.write(headerTestClass);
        myWriter.close();
    }

    /**
     * Erstellt eine neue Datei für die generierten Testfaelle und eine initiale Testdatei
     * mit .java-Endung in dem neuen Verzeichnis "/generated"
     */
    private void erstelleNeueTestDatei(Path pathForNewTestDirectory, Path pathForNewTestFile) throws IOException {
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
     * Führt den Leistungseffizienztest mit dem angegebenen recordToTest durch. Test wird nicht durchgeführt,
     * wenn Record mehr als 10 Komponenten enthält
     *
     * @param recordToTest recordToTest
     */
    void fuehreLeistungseffizienztestDurch(RecordToTest recordToTest) throws IOException, ClassNotFoundException,
            IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        System.out.println("Test auf Leistungseffizienz (" + recordToTest.getName() + "):");

        //Bereite Leistungseffizienztest und Wartbarkeitstest vor, indem Record-Klasse dynamisch erstellt werden
        String className = recordToTest.getName() + "PerformanceTest";
        erstelleDynamischKlasseMitTestRecord(className);

        //Führe Leistungseffizienztest durch, wenn es nicht mehr als 10 Komponenten im Record gibt
        if (recordToTest.getAmountComponents() <= 10) {

            //Initialisiere Map mit Grenzwerten
            HashMap<Integer, Integer> mapGrenzwerte = new HashMap<>();
            initialisiereMapGrenzwerte(mapGrenzwerte);

            //Initialisiere Zeitmessung
            long startTime = 0;
            long endTime = 0;

            //Erstelle neue Instanz des zu testenden Records und messe die Zeit
            switch (recordToTest.getAmountComponents()) {
                case 1 -> {
                    startTime = System.nanoTime();
                    urlClassLoader.loadClass(className + "$" + recordToTest.getName()).getConstructor(int.class)
                            .newInstance(10);
                    endTime = System.nanoTime();
                }
                case 2 -> {
                    startTime = System.nanoTime();
                    urlClassLoader.loadClass(className + "$" + recordToTest.getName())
                            .getConstructor(int.class, int.class).newInstance(10, 9);
                    endTime = System.nanoTime();
                }
                case 3 -> {
                    startTime = System.nanoTime();
                    urlClassLoader.loadClass(className + "$" + recordToTest.getName())
                            .getConstructor(int.class, int.class, int.class).newInstance(10, 9, 8);
                    endTime = System.nanoTime();
                }
                case 4 -> {
                    startTime = System.nanoTime();
                    urlClassLoader.loadClass(className + "$" + recordToTest.getName())
                            .getConstructor(int.class, int.class, int.class, int.class)
                            .newInstance(10, 9, 8, 7);
                    endTime = System.nanoTime();
                }
                case 5 -> {
                    startTime = System.nanoTime();
                    urlClassLoader.loadClass(className + "$" + recordToTest.getName())
                            .getConstructor(int.class, int.class, int.class, int.class, int.class)
                            .newInstance(10, 9, 8, 7, 6);
                    endTime = System.nanoTime();
                }
                case 6 -> {
                    startTime = System.nanoTime();
                    urlClassLoader.loadClass(className + "$" + recordToTest.getName())
                            .getConstructor(int.class, int.class, int.class, int.class, int.class, int.class)
                            .newInstance(10, 9, 8, 7, 6, 5);
                    endTime = System.nanoTime();
                }
                case 7 -> {
                    startTime = System.nanoTime();
                    urlClassLoader.loadClass(className + "$" + recordToTest.getName())
                            .getConstructor(int.class, int.class, int.class, int.class, int.class, int.class, int.class)
                            .newInstance(10, 9, 8, 7, 6, 5, 4);
                    endTime = System.nanoTime();
                }
                case 8 -> {
                    startTime = System.nanoTime();
                    urlClassLoader.loadClass(className + "$" + recordToTest.getName())
                            .getConstructor(int.class, int.class, int.class, int.class, int.class, int.class, int.class,
                                    int.class).newInstance(10, 9, 8, 7, 6, 5, 4, 3);
                    endTime = System.nanoTime();
                }
                case 9 -> {
                    startTime = System.nanoTime();
                    urlClassLoader.loadClass(className + "$" + recordToTest.getName())
                            .getConstructor(int.class, int.class, int.class, int.class, int.class, int.class, int.class,
                                    int.class, int.class).newInstance(10, 9, 8, 7, 6, 5, 4, 3, 2);
                    endTime = System.nanoTime();
                }
                case 10 -> {
                    startTime = System.nanoTime();
                    urlClassLoader.loadClass(className + "$" + recordToTest.getName())
                            .getConstructor(int.class, int.class, int.class, int.class, int.class, int.class, int.class,
                                    int.class, int.class, int.class)
                            .newInstance(10, 9, 8, 7, 6, 5, 4, 3, 2, 1);
                    endTime = System.nanoTime();
                }
                default -> System.out.println("Leistungseffizienztest: Hier ist etwas schiefgegangen!");
            }

            //berechne Zeit
            long duration = (endTime - startTime);  //divide by 1000000 to get milliseconds.
            long durationInMilliseconds = duration / 1000000;
            System.out.println("Eine Instantiierung des Records " + recordToTest.getName() + " hat " + duration +
                    " Nanosekunden gedauert (" + durationInMilliseconds + " Millisekunden)");

            if (duration > mapGrenzwerte.get(recordToTest.getAmountComponents())) {
                System.out.println("Ergebnis: Der Record " + recordToTest.getName() +
                        " ist über dem festgelegten Grenzwert von "
                        + mapGrenzwerte.get(recordToTest.getAmountComponents()) +
                        " Nanosekunden und wurde als ineffizient eingestuft.");
            } else {
                System.out.println("Ergebnis: Der Record " + recordToTest.getName() +
                        " ist nicht über dem festgelegten Grenzwert von "
                        + mapGrenzwerte.get(recordToTest.getAmountComponents()) +
                        " Nanosekunden und wurde als effizient eingestuft.");
            }
        } else {
            //Zuviele Komponenten
            System.out.println("Error: Der Leistungseffizienztest wird nur bis zu 10 Komponenten unterstützt. " +
                    "Der Record " + recordToTest.getName() + " weißt " + recordToTest.getAmountComponents() +
                    " Komponenten auf.");
        }
        System.out.println("--------------------------------------------------------");
    }

    /**
     * Erstellt dynamisch zur Laufzeit Klasse className mit kompletten Record als String.
     * Dynamisch erstellte Klasse kann danach mit loadClass am urlClassLoader aufgerufen werden
     *
     * @param className Name der Klasse, die erstellt werden soll
     * @throws IOException IOException
     */
    private void erstelleDynamischKlasseMitTestRecord(String className) throws IOException {
        //Spezifiziere Klassennamen für Record und erstelle Record als String innerhalb Testklasse
        String sourceCode = erstelleKlasseMitTestRecordAlString(className);

        //Schreibe String mit Record in Datei
        File parent = new File(System.getProperty("user.dir"));
        File sourceFile = new File(parent, className + ".java");
        FileWriter writer = new FileWriter(sourceFile);
        writer.write(sourceCode);
        writer.close();

        //Kompiliere mit 'enable-preview'
        JavaCompiler javaCompiler = ToolProvider.getSystemJavaCompiler();
        File parentDir = sourceFile.getParentFile();
        javaCompiler.run(null, null, null, "--enable-preview", "--release", "14",
                sourceFile.toString());

        //Füge Klassen dynamisch zu Custom Classloader hinzu
        urlClassLoader = URLClassLoader.newInstance(new URL[]{parentDir.toURI().toURL()});
    }

    /**
     * Initialisiert die Hashmap mit den festgelegten Grenzwerten für den Leistungseffizienztest
     *
     * @param mapGrenzwerte HashMap<Integer, Integer> mit key=ParameterAnzahl, value=GrenzwertInNanosekunden
     */
    private void initialisiereMapGrenzwerte(HashMap<Integer, Integer> mapGrenzwerte) {
        mapGrenzwerte.put(1, 10000000);
        mapGrenzwerte.put(2, 15000000);
        mapGrenzwerte.put(3, 20000000);
        mapGrenzwerte.put(4, 25000000);
        mapGrenzwerte.put(5, 30000000);
        mapGrenzwerte.put(6, 35000000);
        mapGrenzwerte.put(7, 40000000);
        mapGrenzwerte.put(8, 45000000);
        mapGrenzwerte.put(9, 50000000);
        mapGrenzwerte.put(10, 55000000);
    }

    /**
     * Erstellt die Klasse mit dem Testrecords als String und gibt diesen String zurück
     *
     * @param nameTestKlasse Name der generierten Testdatei
     */
    private String erstelleKlasseMitTestRecordAlString(String nameTestKlasse) {
        StringBuilder sb = new StringBuilder();

        String headerTestClass =
                "/**\n" +
                        " * Automatisch generierte Testklasse mit dem Record \"" + recordToTest.getName() +
                        "\" für den Leistungseffizienztest.\n" +
                        " */" + "\n" + "public class " + nameTestKlasse + "{\n";

        String recordFull = "public " + recordToTest.getRecordFull();

        return sb.append(headerTestClass).append(recordFull).append("\n}").toString();
    }

    /**
     * Führt den Test auf Wartbarkeit beim recordToTest durch
     *
     * @param recordToTest RecordToTest
     */
    void fuehreTestAufWartbarkeitDurch(RecordToTest recordToTest) throws ClassNotFoundException {
        System.out.println("Test auf Wartbarkeit (" + recordToTest.getName() + "):");

        boolean longFunction;
        boolean largeClass;
        boolean longParameterList;

        //Long Function
        longFunction = pruefeAufLongFunction(recordToTest);

        //Large Class
        largeClass = pruefeAufLargeClass(recordToTest);

        //Long Parameter List
        longParameterList = pruefeAufLongParameterList(recordToTest);

        if (!longFunction && !largeClass && !longParameterList) {
            System.out.println("Ergebnis: Beim Record " + recordToTest.getName() +
                    " wurde keine \"Long Function\", \"Large Class\" oder \"Long Parameter List\" erkannt, " +
                    "welche die Wartbarkeit verschlechtern könnte.");
        }
        System.out.println("--------------------------------------------------------");
    }

    /**
     * Prüft, ob der Record recordToTest eine zu große Klasse ist (Large Class).
     * Wenn ja, wird das Ergebnis auf der Konsole ausgegeben
     *
     * @param recordToTest RecordToTest
     * @throws ClassNotFoundException ClassNotFoundException
     */
    private boolean pruefeAufLargeClass(RecordToTest recordToTest) throws ClassNotFoundException {
        boolean zuvieleMethoden = false;
        boolean zuvieleFelder = false;
        boolean zuvieleLOCDerKlasse = false;

        //Prüfe Anzahl der Methoden
        if (recordToTest.getListAllDeclaredMethods().size() > 10) {
            System.out.println("Ergebnis: Beim Record " + recordToTest.getName() +
                    " wurden mehr als 10 Methoden erkannt (Large Class), " +
                    "welches die Wartbarkeit verschlechtern könnte.");
            zuvieleMethoden = true;
        }

        //Prüfe Anzahl der Felder
        if (getAnzahlDeklarierterFelder(recordToTest.getName() + "PerformanceTest" + "$" +
                recordToTest.getName()) > 10) {
            System.out.println("Ergebnis: Beim Record " + recordToTest.getName() +
                    " wurden mehr als 10 Felder erkannt (Large Class), welches die Wartbarkeit verschlechtern könnte.");
            zuvieleFelder = true;
        }

        //Prüfe LOC des Records gesamt
        if (recordToTest.getRecordFull().lines().count() > 160) {
            System.out.println("Ergebnis: Beim Record " + recordToTest.getName() +
                    " wurden mehr als 160 LOC erkannt (Large Class), welches die Wartbarkeit verschlechtern könnte.");
            zuvieleLOCDerKlasse = true;
        }

        return zuvieleMethoden || zuvieleFelder || zuvieleLOCDerKlasse;
    }

    /**
     * Prüft, ob der Record recordToTest eine zu lange Methode enthält (Long Function).
     * Wenn ja, wird das Ergebnis auf der Konsole ausgegeben
     *
     * @param recordToTest RecordToTest
     */
    private boolean pruefeAufLongFunction(RecordToTest recordToTest) {
        for (MethodToTest methodToTest : recordToTest.getListAllDeclaredMethods()) {
            if (methodToTest.getFullMethod().lines().count() > 15) {
                System.out.println("Ergebnis: Beim Record " + recordToTest.getName() +
                        " wurde eine zu lange Methode (Long Function) \"" + methodToTest.getName() + "\" mit mehr " +
                        "als 15 LOC gefunden, welche die Wartbarkeit verschlechtern könnte.");
                return true;
            }
        }
        return false;
    }

    /**
     * Gibt die Anzahl der Felder der Klasse exklusive der Felder, welche durch die Komponenten zur Laufzeit
     * erzeugt werden mit dem angegebenen className zurück
     *
     * @param className className
     * @return Anzahl der deklarierten Felder als int
     * @throws ClassNotFoundException ClassNotFoundException
     */
    private int getAnzahlDeklarierterFelder(String className) throws ClassNotFoundException {
        if (urlClassLoader != null && new File(System.getProperty("user.dir") + "/" + recordToTest.getName() +
                "PerformanceTest.java").exists()) {
            return urlClassLoader.loadClass(className).getDeclaredFields().length - recordToTest.getAmountComponents();
        }
        System.out.println("Error: Beim Durchsuchen des Records nach deklarierten Feldern ist ein Fehler aufgetreten.");
        return -1;
    }

    /**
     * Prüft, ob der Record recordToTest eine lange Parameterliste enthält (Long Parameter List).
     * Wenn ja, wird das Ergebnis auf der Konsole ausgegeben
     *
     * @param recordToTest ToTest.RecordToTest
     */
    private boolean pruefeAufLongParameterList(RecordToTest recordToTest) {
        if (recordToTest.getAmountComponents() > 5) {
            System.out.println("Ergebnis: Beim Record " + recordToTest.getName() +
                    " wurde eine lange Parameterliste mit mehr als 5 Parametern (Long Parameter List) gefunden, welche " +
                    "die Wartbarkeit verschlechtern könnte.");
            return true;
        }
        return false;
    }
}