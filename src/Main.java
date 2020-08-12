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
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        //Reguläre Ausdrücke zum Durchsuchen des Records
        String constructorRegex = "(public " + recordInfo.getName() + "(\\s)*\\{)";
        String equalsRegex = "(public boolean equals(\\s)*\\()";
        String toStringRegex = "(public String toString(\\s)*\\()";
        String hashCodeRegex = "(public int hashCode(\\s)*\\()";

        //Liste mit regulären Ausdrücken zum Durchsuchen des Records
        List<Pattern> methodenPatterns = new ArrayList<>();
        Collections.addAll(methodenPatterns, Pattern.compile(constructorRegex), Pattern.compile(equalsRegex),
                Pattern.compile(toStringRegex), Pattern.compile(hashCodeRegex));

        //Durchsuche kompletten Record nach Patterns
        for (Pattern p : methodenPatterns) {
            Matcher m = p.matcher(recordInfo.getRecordFull());
            //Bei erstem Match gebe true zurück
            if (m.find()) {
                String output = m.group(0);
                if (m.group(0).contains(recordInfo.getName())) {
                    //wenn Name des Records enthalten ist -> Konstruktor wurde überschrieben
                    System.out.println("Überschriebener Konstruktor: " + output.replace("(", ""));
                } else {
                    System.out.println("Überschriebene Methode: " + output.replace("(", ""));
                }
                return true;
            }
        }
        return false;
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
     * Integer-Werte handelt
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
                            //hier andersherum, da sonst der vorherige Eintrag immer ersetzt wird, da Key gleich wäre (int)
                            componentMap.put(listWords.get(j + 1), listWords.get(j));
                        }
                    }
                    recordInfo.setComponentMap(componentMap);

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
