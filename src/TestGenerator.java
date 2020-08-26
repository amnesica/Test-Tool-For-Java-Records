import ToTest.RecordToTest;

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
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Klasse, die die Testfaelle fuer Funktionalitaet generiert
 */
public class TestGenerator {

    //Der zu testende Record
    private RecordToTest recordToTest;

    //Liste mit den erstellten Testrecords fuer Positivtest
    private ArrayList<TestRecord> listTestRecordsPositiv;

    //Liste mit Namen der erstellten Testrecords als String
    private ArrayList<String> listTestRecordNames;

    //Liste mit TestRecord Kopien als TestRecord
    private ArrayList<TestRecord> listTestRecordsPositivCopies;

    //Name des TestRecords fuer den NegativTest
    private String nameNegativTestRecord;

    /**
     * Generiert die Testfaelle fuer Funktionalitaet fuer den vorliegenden Record in RecordToTest
     *
     * @param recordToTest RecordToTest
     */
    void generierefunktionaleTestfaelle(RecordToTest recordToTest) {

        //Speichere recordToTest als Feld ab
        this.recordToTest = recordToTest;

        //Spezifiziere Pfade fuer JUnit-Testklasse und Name der Datei
        Path pathForNewTestDirectory = Paths.get(System.getProperty("user.dir") + File.separator + "generated" +
                File.separator);
        String nameTestKlasse = recordToTest.getName() + "Test";
        Path pathForNewTestFile = Paths.get(System.getProperty("user.dir") + File.separator + "generated" +
                File.separator + nameTestKlasse + ".java");

        try {
            erstelleNeueTestDatei(pathForNewTestDirectory, pathForNewTestFile);

            erstelleGrundlegendeTestStrukturJUnitInDatei(pathForNewTestFile, nameTestKlasse);

            fuegeZuTestendenRecordEin(pathForNewTestFile, recordToTest);

            erstelleTestfaelleFunktionalitaet(pathForNewTestFile);

            System.out.println("--------------------------------------------------------");
            System.out.println("Test auf Funktionalitaet (" + recordToTest.getName() + "):");
            System.out.println("Testdatei fuer " + recordToTest.getName() +
                    " wurde erfolgreich erstellt und kann ausgefuehrt werden.");
            System.out.println("Pfad der generierten Datei: " + pathForNewTestFile);
            System.out.println("--------------------------------------------------------");
        } catch (IOException e) {
            System.out.println("Error: Beim Erstellen der Testdatei gab es einen Fehler.");
            e.printStackTrace();
        }
    }

    /**
     * Erstellt die Testrecords mit den festgelegten Testdaten (Integer-Werte) sowie moeglichen benutzerdefinierten
     * Grenzwerten, welche per Kommandozeile angefragt werden
     */
    private void erstelleTestRecords() {
        //Initialisiere Listen
        listTestRecordNames = new ArrayList<>();
        listTestRecordsPositiv = new ArrayList<>();

        //generiere Liste mit Testwerten fuer Testrecords
        ArrayList<Integer> listTestWerte = new ArrayList<>();
        Collections.addAll(listTestWerte, 1, Integer.MAX_VALUE, Integer.MIN_VALUE, -1, 0,
                Integer.MAX_VALUE - 1, Integer.MIN_VALUE + 1);

        //Moeglichkeit, benutzerdefinierte Grenzwerte einzufuegen (und diese der Liste listTestWerte hinzufuegen)
        ArrayList<Integer> listCustomValues = getBenutzerdefinierteGrenzwerte(recordToTest);
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

                //haenge ein "," oder ein ");" an, um Record abzuschließen
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

                    //haenge ein "," oder ein ");" an, um Record abzuschließen
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

                //reset anzahlAbgedeckterTestWerte fuer den naechsten TestRecord
                anzahlAbgedeckterTestWerte = 0;
                testRecord.setInitializedRecord(initializedTestRecord);
                listTestRecordsPositiv.add(testRecord);
                anzahlErstellteTestRecords += 1;
            }
        }
    }


    /**
     * Fragt auf der Kommandozeile nach benutzerdefinierten Werten und fuegt diese der Liste mit den zu testenden
     * Werten hinzu
     *
     * @param recordToTest RecordToTest
     * @return ArrayList<Integer> mit den hinzugefuegten Werten
     */
    private ArrayList<Integer> getBenutzerdefinierteGrenzwerte(RecordToTest recordToTest) {
        ArrayList<Integer> listCustomValues = new ArrayList<>();
        Scanner scanner = new Scanner(System.in);

        String decision = null;
        boolean repeat = true;
        while (repeat) {
            System.out.println("Wollen Sie fuer den Record " + recordToTest.getName() +
                    " benutzerdefinierte Grenzwerte hinzufuegen? (j/n)");
            decision = scanner.nextLine();

            switch (decision) {
                case "j", "n" -> repeat = false;
                default -> System.out.println("Bitte geben Sie nur 'j' oder 'n' an.");
            }
        }

        if (decision.equals("j")) {
            System.out.println("Geben Sie benutzerdefinierte Grenzwerte mit einem \"Enter\" getrennt ein. " +
                    "Wenn sie fertig sind druecken Sie nochmals \"Enter\"!");

            boolean repeat2 = true;
            while (repeat2) {
                String stringCustomValue = scanner.nextLine();
                if (!stringCustomValue.equals("")) {
                    try {
                        //fuege Wert der Liste hinzu
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
     * Fuegt den zu testenden Record in die generierte Testdatei ein
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
     * Erstellt die Testfaelle fuer das Qualitaetskriterium "Funktionelle Eignung".
     * Es wird ein Positiv- und ein Negativtest erstellt. Zuvor werden die Testrecords erstellt
     *
     * @param pathForNewTestFile Pfad zur generierten Testdatei
     * @throws IOException IOException
     */
    private void erstelleTestfaelleFunktionalitaet(Path pathForNewTestFile) throws IOException {
        //Erstelle TestRecords und speichere diese in Liste testRecordsPositiv
        erstelleTestRecords();

        //fuege manipulierten zu testenden Record fuer Negativtests ein
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
     * Erstellt den Positivtest fuer "toString"
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
     * Erstellt den Positivtest fuer "hashCode"
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
     * Schreibt einen String content in eine Datei mit dem Pfad pathForNewTestFile und fuegt darueber einen
     * Kommentar comment ein
     *
     * @param pathForNewTestFile Pfad zur generierten Testdatei
     * @param content            Einzufuegender Inhalt als String
     * @param comment            Kommentar, welcher den einzufuegenden Inhalt kommentiert
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
     * Fuegt die TestRecords in listTestRecordsPositiv sowie listTestRecordsPositivCopies an den Header headerMethod
     * an und erstellt danach die Assertions mit Equals und jeweiligen TestRecord und seiner Kopie
     *
     * @param sb           StringBuilder
     * @param headerMethod headerMethod als String
     */
    private void fuegeTestRecordsMitEqualsAnHeader(StringBuilder sb, String headerMethod) {
        sb.append(headerMethod);

        //fuege TestRecords mit Testdaten sowie Kopien der Testrecords ein
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
     * Erstellt den Negativtest fuer "equals"
     *
     * @param pathForNewTestFile Pfad zur generierten Testdatei
     * @throws IOException IOException
     */
    private void erstelleTestfallEqualsNegativtest(Path pathForNewTestFile) throws IOException {
        ArrayList<String> testRecordsNegativ = new ArrayList<>();
        ArrayList<String> testRecordsNegativCopies = new ArrayList<>();
        StringBuilder sb = new StringBuilder();

        //baue bereits erstellte Records fuer Negativtest um
        for (TestRecord testRecord : listTestRecordsPositiv) {
            String testRecordNegativ = testRecord.getInitializedRecord()
                    .replace(recordToTest.getName(), nameNegativTestRecord);
            testRecordsNegativ.add(testRecordNegativ);
        }

        //baue bereits erstellte Kopier-Records fuer Negativtest um
        for (TestRecord testRecordCopy : listTestRecordsPositivCopies) {
            String testRecordNegativCopy = testRecordCopy.getInitializedRecord()
                    .replace(recordToTest.getName(), nameNegativTestRecord);
            testRecordsNegativCopies.add(testRecordNegativCopy);
        }

        String headerMethod = "@Test" + "\n" +
                "public void testeFunktionalitaetEqualsNegativtest(){";
        sb.append(headerMethod);

        //fuege TestRecords mit Testdaten sowie Kopien der Testrecords ein
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
     * Erstellt einen Record fuer den Negativtest bei der Funktionalitaet. Hierbei wird bewusst ein nicht
     * ueberschriebener Akzessor des originalen Records ueberschrieben. Wenn bereits alle Akzessoren
     * ueberschrieben wurden, wird bei dem Akzessor der ersten Komponente ein "+ 1" beim "return" rangehaengt,
     * um den gewuenschten Effekt zu erzielen
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
            //Akzessor wurde bereits ueberschrieben -> veraendere Rueckgabewert
            manipuliereVorhandenenAkzessor(methodToOverrideBadly, testRecordNegativ);
        } else {
            //fuege Akzessor am Ende des Records an
            erstelleNeuenManipuliertenAkzessor(methodToOverrideBadly, testRecordNegativ);
        }
        erstelleNegativTestRecord(testRecordNegativ);

        schreibeInDatei(pathForNewTestFile, testRecordNegativ.getRecordNegativTestFull(),
                "//zu testender Record fuer Negativtest");
    }

    /**
     * Erstellt einen NegativTestRecord fuer den Negativtest bei der Funktionalitaet
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

        } else { //fuege Methode am Ende des Records an
            if (testRecordNegativ.getRecordNegativTestFull().endsWith("}")) {
                String oldNegativTestRecord = testRecordNegativ.getRecordNegativTestFull()
                        .substring(0, testRecordNegativ.getRecordNegativTestFull().length() - 1);

                //Ersetze alten RecordNegativTest mit "}" am Ende, um Record abzuschließen
                testRecordNegativ.setRecordNegativTestFull(oldNegativTestRecord.concat(accessorOverridden + "\n}"));
            }
        }

        //Record fuer NegativTest soll <name>NegativTest heißen
        ueberschreibeAlleBezeichnerFuerNegativTest(testRecordNegativ);
    }

    /**
     * Erstellt einen manipulierten Akzessor, welcher noch nicht im TestRecord ueberschrieben wurde.
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

            //Set um die Klammern zu zaehlen, wenn keine Klammer mehr drinn ist, ist Record zuende
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
     * ueberschreibt alle alten Bezeichner damit Negativtest-Record <name>NegativTest heißt
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
     * Veraendert den Rueckgabewert der ausgewaehlten Akzessormethode zum Erstellen des
     * Negativtest-Records. Der Rueckgabewert wird um 5 erhoeht. (TODO willkuerlich)
     *
     * @param testRecordNegativ TestRecord
     */
    private void veraendereRueckgabeWertDerMethode(TestRecord testRecordNegativ) {
        String regexReturn = "(return(\\s)*([\\d\\w\\s,.\\[\\]{}()<>*+\\-=!?^$|%])*;)";
        Pattern r = Pattern.compile(regexReturn);
        Matcher m = r.matcher(testRecordNegativ.getAccessorToOverride());

        String manipulatedAccessor = testRecordNegativ.getAccessorToOverride();
        String overriddenManipulatedAccessor;

        //manipuliere Akzessor fuer jedes Match (da mehrere "returns" moeglich)
        while (m.find()) {
            int indexEndOfReturn = m.end() - 1;

            //fuegt "+ 5" an letzter Stelle vor dem ";" ein
            overriddenManipulatedAccessor = manipulatedAccessor.substring(0, indexEndOfReturn)
                    + "+ 5"
                    + manipulatedAccessor.substring(indexEndOfReturn);

            //Ersetze extrahierten Akzessor mit manipulierter Methode
            testRecordNegativ.setAccessorOverridden(overriddenManipulatedAccessor);
        }
    }

    /**
     * Bestimmt einen nicht ueberschriebene Akzessor des zu testenden Records.
     * Wenn alle Akzessoren ueberschrieben wurden, wird die erste Komponente
     * in der HashMap ausgewaehlt
     *
     * @return Name der zu ueberschreibenden Methode
     */
    private String bestimmeZuUeberschreibendeMethodeFuerNegativTest() {
        for (Object key : recordToTest.getComponentMap().keySet()) {
            if (!recordToTest.getListOverriddenMethods().contains(key.toString())) {
                return key.toString();
            }
        }

        //Wenn alle Akzessoren ueberschrieben wurden, waehle erste Komponente des Records aus
        Map.Entry<Object, Object> entry = recordToTest.getComponentMap().entrySet().iterator().next();
        return (String) entry.getKey();
    }

    /**
     * Erstellt den Positivtest fuer "equals"
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
     * Erstellt die grundlegende Struktur der Testdatei mit Importen fuer JUnit und dem Package,
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
                " * Automatisch generierte Testklasse mit JUnit-Tests fuer den Record \"" + recordToTest.getName() +
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
     * Erstellt eine neue Datei fuer die generierten Testfaelle und eine initiale Testdatei
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
}