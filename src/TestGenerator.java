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
import java.util.Scanner;

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

    //Liste mit TestRecord Kopien als TestRecord fuer Positivtest
    private ArrayList<TestRecord> listTestRecordsPositivCopies;

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
     * Erstellt die Testrecords mit den festgelegten Testdaten (Integer-Werte) sowie weiteren moeglichen
     * benutzerdefinierten Grenzwerten, welche per Kommandozeile angefragt werden
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

        //equals-Invariante
        erstelleTestfallEqualsPositivtest(pathForNewTestFile);
        erstelleTestfallEqualsNegativtest(pathForNewTestFile);

        //hashCode-Invariante
        erstelleTestfallHashCodePositivtest(pathForNewTestFile);

        //toString-Invariante
        erstelleTestfallToStringPositivtest(pathForNewTestFile);
    }

    /**
     * Erstellt den Positivtest fuer die Invariante toString()
     *
     * @param pathForNewTestFile Pfad zur generierten Testdatei
     * @throws IOException IOException
     */
    private void erstelleTestfallToStringPositivtest(Path pathForNewTestFile) throws IOException {
        StringBuilder sb = new StringBuilder();

        String headerMethod = "@Test" + "\n" +
                "public void testeFunktionalitaetToStringPositivtest(){";

        String errorMessageEquals = "ToStringPositivtest: Um die Invariante toString() zu testen, sollte equals() " +
                "true ergeben. Der Test wird nicht weiter ausgefuehrt.";
        fuegeTestRecordsMitEqualsAnHeader(sb, headerMethod, errorMessageEquals);

        //erstelle Assertions mit toString
        String errorMessageToString = "ToStringPositivtest: Die Invariante toString() wurde verletzt. Die toString-" +
                "Rueckgabewerte sind unterschiedlich. Die Funktionalitaet des Records ist beeintraechtigt.";
        for (String listTestRecordName : listTestRecordNames) {
            sb.append("assertEquals(").append(listTestRecordName)
                    .append(".toString(),").append(listTestRecordName).append("copy.toString(),")
                    .append("\"").append(errorMessageToString).append("\"").append(");\n");
        }

        //schließe Methode ab
        sb.append("}\n");

        //schließe Klass ab
        sb.append("}\n");

        schreibeInDatei(pathForNewTestFile, sb.toString(), "//Testfall ToString - Positivtest");
    }

    /**
     * Erstellt den Positivtest fuer die Invariante hashCode()
     *
     * @param pathForNewTestFile Pfad zur generierten Testdatei
     * @throws IOException IOException
     */
    private void erstelleTestfallHashCodePositivtest(Path pathForNewTestFile) throws IOException {
        StringBuilder sb = new StringBuilder();

        String headerMethod = "@Test" + "\n" +
                "public void testeFunktionalitaetHashCodePositivtest(){";

        String errorMessageEquals = "HashCodePositivtest: Um die Invariante hashCode() zu testen, sollte equals() " +
                "true ergeben. Der Test wird nicht weiter ausgefuehrt.";
        fuegeTestRecordsMitEqualsAnHeader(sb, headerMethod, errorMessageEquals);


        //erstelle Assertions mit hashCode
        String errorMessageHashCode = "HashCodePositivtest: Die Invariante hashCode() wurde verletzt. Die " +
                "hashCode-Rueckgabewerte sind unterschiedlich. Die Funktionalitaet des Records ist beeintraechtigt.";
        for (String listTestRecordName : listTestRecordNames) {
            sb.append("assertEquals(").append(listTestRecordName)
                    .append(".hashCode(),").append(listTestRecordName).append("copy.hashCode(),")
                    .append("\"").append(errorMessageHashCode).append("\"").append(");\n");
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
     * an und erstellt danach die Assertions mit equals und jeweiligen TestRecord und seiner Kopie
     *
     * @param sb           StringBuilder
     * @param headerMethod headerMethod als String
     * @param errorMessage Message, wenn equals fehlschlaegt
     */
    private void fuegeTestRecordsMitEqualsAnHeader(StringBuilder sb, String headerMethod, String errorMessage) {
        sb.append(headerMethod);

        //fuege TestRecords mit Testdaten sowie Kopien der Testrecords ein
        for (int i = 0; i < listTestRecordsPositiv.size(); i++) {
            sb.append(listTestRecordsPositiv.get(i).getInitializedRecord()).append("\n");
            sb.append(listTestRecordsPositivCopies.get(i).getInitializedRecord()).append("\n").append("\n");
        }

        //erstelle Assertions mit equals
        for (String listTestRecordName : listTestRecordNames) {
            sb.append("assertTrue(").append(listTestRecordName)
                    .append(".equals(").append(listTestRecordName).append("copy), ")
                    .append("\"").append(errorMessage).append("\"").append(");\n");
        }

        sb.append("\n");
    }

    /**
     * Erstellt den Negativtest fuer die Invariante equals()
     *
     * @param pathForNewTestFile Pfad zur generierten Testdatei
     * @throws IOException IOException
     */
    private void erstelleTestfallEqualsNegativtest(Path pathForNewTestFile) throws IOException {
        StringBuilder sb = new StringBuilder();

        //erstelle originalen und Kopie des originalen TestRecords
        TestRecord testRecordOriginal = erstelleTestRecordFuerNegativTest(0, false);
        TestRecord testRecordKopie = erstelleTestRecordFuerNegativTest(5, true);

        //erstelle TestRecord Copies
        String ueberpruefungNegativtest = erstelleUeberpruefungNegativTest(testRecordOriginal, testRecordKopie);

        String headerMethod = "@Test" + "\n" +
                "public void testeFunktionalitaetEqualsNegativtest(){";
        sb.append(headerMethod);
        sb.append("\n");

        //fuege initialisierten originalen TestRecord und Kopie an StringBuilder an
        sb.append(testRecordOriginal.getInitializedRecord());
        sb.append("\n");
        sb.append(testRecordKopie.getInitializedRecord());
        sb.append("\n");

        //fuege ueberpruefungNegativtest an StringBuilder an
        sb.append(ueberpruefungNegativtest);
        sb.append("\n");

        //schließe Methode ab
        sb.append("}\n");

        schreibeInDatei(pathForNewTestFile, sb.toString(), "//Testfall Equals - Negativtest");
    }

    /**
     * Erstellt den Inhalt des Negativtests und die Ueberpruefung, ob die equals()-Methode falsch, d.h. mit "return true"
     * ueberschrieben wurde
     *
     * @param testRecordOriginal TestRecord Original
     * @param testRecordKopie    TestRecord Kopie des Originals
     * @return Inhalt der Negativtest Ueberpruefung als String
     */
    private String erstelleUeberpruefungNegativTest(TestRecord testRecordOriginal, TestRecord testRecordKopie) {
        StringBuilder sb = new StringBuilder();

        String equalsOriginalUndKopie = " if(" + testRecordOriginal.getName() + ".equals(" +
                testRecordKopie.getName() + ") && ";
        sb.append(equalsOriginalUndKopie);

        int index = 0;
        for (Object key : recordToTest.getComponentMap().keySet()) {
            String nameKomponente = key.toString();
            sb.append(testRecordOriginal.getName()).append(".").append(nameKomponente)
                    .append(" != ")
                    .append(testRecordKopie.getName()).append(".").append(nameKomponente)
                    .append(" ");
            if (index != recordToTest.getComponentMap().keySet().size() - 1) {
                sb.append("&& ");
            }
            index += 1;
        }

        //error message
        String errorMessage = "EqualsNegativtest: Die Methode equals() wurde so ueberschrieben, dass die " +
                "Funktionalitaet des Records beeintraechtigt ist.";
        sb.append("){\n" + "            fail(\"").append(errorMessage).append("\");\n").append("        }");

        return sb.toString();
    }

    /**
     * Erstellt einen Testrecord mit dem Integer-Wert valueForComponent fuer jede Komponente. Erstellt einen
     * originalen oder eine Kopie, je nach boolean recordShouldBeCopy
     *
     * @param valueForComponent  Integer-Wert fuer jede Komponente
     * @param recordShouldBeCopy Boolean, ob TestRecord eine Kopie sein soll
     * @return TestRecord
     */
    private TestRecord erstelleTestRecordFuerNegativTest(int valueForComponent, boolean recordShouldBeCopy) {
        StringBuilder sb = new StringBuilder();

        TestRecord testRecord = new TestRecord();
        String testRecordName;
        if (recordShouldBeCopy) {
            testRecordName = recordToTest.getName() + "copy";
        } else {
            testRecordName = recordToTest.getName();
        }
        testRecord.setName(testRecordName);

        String initializedTestRecord = recordToTest.getName() + " " + testRecordName + " = new " +
                recordToTest.getName() + "(";
        sb.append(initializedTestRecord);

        for (int i = 0; i < recordToTest.getAmountComponents(); i++) {
            //Wert fuer jede Komponente setzen
            sb.append(valueForComponent);
            if (i != recordToTest.getAmountComponents() - 1) {
                sb.append(",");
            } else {
                sb.append(");");
            }
        }

        testRecord.setInitializedRecord(sb.toString());
        return testRecord;
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

        String errorMessage = "EqualsPositivtest: Die Invariante equals() wurde verletzt. Die beiden erstellten " +
                "Records sind nach equals nicht gleich. Die Funktionalitaet des Records ist beeintraechtigt.";
        fuegeTestRecordsMitEqualsAnHeader(sb, headerMethod, errorMessage);

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