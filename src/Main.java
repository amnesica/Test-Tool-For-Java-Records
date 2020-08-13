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
        Path pathForNewTestDirectory = Paths.get(System.getProperty("user.dir") + "/generated/");
        Path pathForNewTestFile = Paths.get(System.getProperty("user.dir") + "/generated/" + recordInfo.getName() + "_Testfaelle.java");
        String nameTestKlasse = recordInfo.getName() + "_Testfaelle";

        try {
            erstelleNeueTestJavaDatei(pathForNewTestDirectory, pathForNewTestFile);

            erstelleGrundlegendeTestStrukturInDatei(pathForNewTestFile, nameTestKlasse);

            erstelleTestfaelleFunktionalitaet(pathForNewTestFile, nameTestKlasse);
        } catch (IOException e) {
            System.err.println("Error: Beim Erstellen der Testdatei gab es einen Fehler.\nTest-Tool wird beendet.");
            e.printStackTrace();
        }
    }

    /**
     * Erstellt die Testrecords mit den Testdaten und fügt diese in der Testdatei ein.
     * @param pathForNewTestFile Pfad zur generierten Testdatei
     * @param nameTestKlasse Name der Testklasse
     * @throws IOException IOException
     */
    private static ArrayList<String> erstelleTestRecords(Path pathForNewTestFile, String nameTestKlasse) throws IOException {
        //füge kompletten zu testenden Record ein
        fuegeZuTestendenRecordEin(pathForNewTestFile);

        //Liste mit den erstellten Testrecords
        ArrayList<String> testRecords = new ArrayList<>();

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
            String testRecord = "record " + recordInfo.getName() + i + " = new " + recordInfo.getName() + "(";

            for(int j = 0; j < recordInfo.getAmountComponents(); j++){
                if (indexListTestWerte >= listTestWerte.size()) {
                    indexListTestWerte = 0;
                }
                testRecord = testRecord.concat(String.valueOf(listTestWerte.get(indexListTestWerte)));
                indexListTestWerte+=1;

                //hänge ein "," oder ein ");" an, um Record abzuschließen
                if(j == recordInfo.getAmountComponents() -1){
                    testRecord = testRecord.concat(");");
                }else{
                    testRecord = testRecord.concat(",");
                }
            }
            testRecords.add(testRecord);
        }
        return testRecords;
    }

    /**
     * Fügt den zu testenden Record in die Testdatei ein.
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
    private static void erstelleTestfaelleFunktionalitaet(Path pathForNewTestFile, String nameTestKlasse) throws IOException {
        ArrayList<String> testRecords;
        testRecords = erstelleTestRecords(pathForNewTestFile, nameTestKlasse);

        erstelleTestfallEqualsPositivtest(pathForNewTestFile, testRecords);

    }

    private static void erstelleTestfallEqualsPositivtest(Path pathForNewTestFile, ArrayList<String> testRecords) {
        //TODO hier weitermachen!
    }

    /**
     * Erstellt die grundlegende Struktur der Testdatei mit imports für JUnit.
     * TODO: Letzter Testfall muss Klasse mit "}" abschließen
     *
     * @param pathForNewTestFile Pfad zur generierten Testdatei
     * @param nameTestKlasse     Name der generierten Testdatei
     */
    private static void erstelleGrundlegendeTestStrukturInDatei(Path pathForNewTestFile, String nameTestKlasse) throws IOException {
        String headerTestClass = "import static org.junit.jupiter.api.Assertions.*;\n" +
                "\n" +
                "import org.junit.jupiter.api.Test;\n" +
                "\n" +
                "public class " + nameTestKlasse + "{";

        //schreibe in Datei, damit sie auch erstellt wird
        FileWriter myWriter = null;
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
        File newTestFile = new File(pathForNewTestFile.toString());

        //erstelle Ordner, wenn dieser nicht existiert
        Files.createDirectories(pathForNewTestDirectory);

        //schreibe in Datei, damit sie auch erstellt wird
        FileWriter myWriter = null;
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
            return true;
        } else {
            System.err.println("Der Record überschreibt keine Methoden oder Konstruktoren.\nTest-Tool wird beendet.");
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

                //speichere String-Komponenten-Liste in Arraylist
                String componentsAsString = fileContent.substring(indexEntryPointRecordAfterMatch, indexEndOfComponentList);
                ArrayList<String> listWords = extrahiereKomponentenInArrayList(componentsAsString);

                //speichere einzelne Komponenten in Hashmap
                HashMap<Object, Object> componentMap = new HashMap<>();
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
