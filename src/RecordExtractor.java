import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RecordExtractor {

    //Datei mit zu testenden Records
    private FileToTest fileToTest;

    //Index direkt vor dem Finden eines Records
    private int indexEntryPointRecordBeforeMatch = 0;

    //Index direkt nach dem Finden eines Records
    private int indexEntryPointRecordAfterMatch = 0;

    //Index des Endes der Komponenten-Liste
    private int indexEndOfComponentList = 0;


    /**
     * Überprüft, ob Testfälle erstellt werden sollen
     * TODO mehrere Records erkennen können
     *
     * @return true, wenn Testfälle generiert werden sollen
     */
    boolean pruefeObTestfaelleGeneriertWerdenSollen(FileToTest fileToTest) {
        if (!fileToTest.getFileContent().isEmpty() && fileToTest.getFileContent().contains("public class") &&
                fileToTest.getFileContent().contains("record")) {

            //Speichere FileToTest als Feld
            this.fileToTest = fileToTest;

            //Regulärer Ausdruck um Beginn des Records zu ermitteln (record\s\w+\()
            String beginOfRecordRegex = "(record\\s\\w+\\()";
            Pattern r = Pattern.compile(beginOfRecordRegex);
            Matcher m = r.matcher(fileToTest.getFileContent());

            boolean dateiEnthaeltRecords = false;

            //Suche nach Record in Datei
            if (m.find()) {
                RecordToTest recordToTest = new RecordToTest();
                fileToTest.fuegeRecordToTestZuListeHinzu(recordToTest);

                indexEntryPointRecordBeforeMatch = m.start();
                indexEntryPointRecordAfterMatch = m.end();

                //speichere Komponenten in RecordToTest
                speichereKomponenten(recordToTest);

                if (recordToTest.getComponentMap() != null) {
                    if (recordEnthaeltNurInteger(recordToTest.getComponentMap())) {

                        //extrahiere kompletten Record (mit Body)
                        speichereBody(recordToTest);
                        speichereKomplettenRecord(recordToTest);

                        //prüfe, ob automatisch generierte Methoden oder Konstruktor im Record überschrieben wurden
                        if (recordUeberschreibtMethodenOderKonstruktor(recordToTest)) {
                            return true;
                        } else {
                            System.err.println("Der angegebene Record überschreibt keine automatisch generierten Methoden und muss daher nicht getestet werden." + "\nTest-Tool wird beendet.");
                            return false;
                        }
                    } else {
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
     * Speichert den kompletten Record mit Header und Body als "RecordFull" in recordInfo ab
     */
    private void speichereKomplettenRecord(RecordToTest recordToTest) {
        if (recordToTest.getRecordHeader() != null && recordToTest.getRecordBody() != null) {
            recordToTest.setRecordFull(recordToTest.getRecordHeader().concat(recordToTest.getRecordBody()));
        }
    }


    /**
     * Speichert den Body des Records als "RecordBody" in recordInfo ab
     *
     * @param recordToTest recordToTest
     */
    private void speichereBody(RecordToTest recordToTest) {
        // + 1, da sonst die letzte ')'-Klammer des Headers mitgenommen werden würde
        int indexStartBody = indexEndOfComponentList + 1;
        int indexEndBody;

        //Set um die Klammern zu zählen, wenn keine Klammer mehr drinn ist, ist Record zuende
        ArrayList<Character> brackets = new ArrayList<>();

        if (fileToTest.getFileContent().charAt(indexStartBody) == '{') {
            for (int i = indexStartBody; i < fileToTest.getFileContent().length(); i++) {
                if (fileToTest.getFileContent().charAt(i) == '{') {
                    //speichere Klammer in Liste
                    brackets.add(fileToTest.getFileContent().charAt(i));
                }
                if (fileToTest.getFileContent().charAt(i) == '}') {
                    if (!brackets.isEmpty()) {
                        //wenn noch mehrere Klammern vorhanden sind -> entferne diese aus Liste
                        if (brackets.size() != 1) {
                            brackets.remove(0);
                        } else {
                            //letzte '}'-Klammer wurde gelesen (i+1 da sonst letzte '}'-Klammer fehlt)
                            indexEndBody = i + 1;

                            //extrahiere Body des Records
                            String body = fileToTest.getFileContent().substring(indexStartBody, indexEndBody);
                            recordToTest.setRecordBody(body);
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
     * @param recordToTest RecordToTest
     */
    private void speichereKomponenten(RecordToTest recordToTest) {
        for (int i = indexEntryPointRecordAfterMatch; i < fileToTest.getFileContent().length(); i++) {
            if (fileToTest.getFileContent().charAt(i) == ')') {
                //Ende der Parameterliste erreicht
                indexEndOfComponentList = i;

                //extrahiere Record Header mit Name und Komponentenliste (+ 1, damit letzte ')'-Klammer noch mitkommt)
                recordToTest.setRecordHeader(fileToTest.getFileContent().
                        substring(indexEntryPointRecordBeforeMatch, indexEndOfComponentList + 1));
                recordToTest.setName(extrahiereRecordName(fileToTest.getFileContent()));

                //Ausgabe des Namens auf Konsole
                System.out.println("Name des Records: " + recordToTest.getName());
                System.out.println("--------------------------------------------------------");

                //speichere String-Komponenten-Liste in Arraylist
                String componentsAsString = fileToTest.getFileContent().
                        substring(indexEntryPointRecordAfterMatch, indexEndOfComponentList);
                ArrayList<String> listWords = extrahiereKomponentenInArrayList(componentsAsString);

                //speichere einzelne Komponenten in Hashmap
                LinkedHashMap<Object, Object> componentMap = new LinkedHashMap<>();
                if (!listWords.isEmpty()) {
                    for (int j = 0; j < listWords.size(); j++) {
                        if (j % 2 == 0) {
                            //key=name, value=type
                            //hier andersherum, da sonst der vorherige Eintrag immer ersetzt wird,
                            //da Key gleich wäre (int)
                            componentMap.put(listWords.get(j + 1), listWords.get(j));
                        }
                    }
                    recordToTest.setComponentMap(componentMap);
                    recordToTest.setAmountComponents(componentMap.keySet().size());

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
    private String extrahiereRecordName(String fileContent) {
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
    private boolean recordEnthaeltNurInteger(HashMap<Object, Object> componentMap) {
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
    private ArrayList<String> extrahiereKomponentenInArrayList(String componentsAsString) {
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

    /**
     * Überprüft, ob der Record eine automatisch generierte Methode oder den kanonischen Konstruktor überschreibt
     *
     * @return true, wenn Konstruktor/Methode überschrieben wurde
     */
    private boolean recordUeberschreibtMethodenOderKonstruktor(RecordToTest recordToTest) {
        //Liste mit regulären Ausdrücken zum Durchsuchen des Records
        List<Pattern> methodenPatterns = new ArrayList<>();

        //Reguläre Ausdrücke zum Durchsuchen des Records
        String constructorRegex = "(public " + recordToTest.getName() + "(\\s)*\\{)";
        String equalsRegex = "(public boolean equals(\\s)*\\()";
        String toStringRegex = "(public String toString(\\s)*\\()";
        String hashCodeRegex = "(public int hashCode(\\s)*\\()";

        //Füge alle regulären Ausdrücke zur Liste hinzu
        Collections.addAll(methodenPatterns, Pattern.compile(constructorRegex), Pattern.compile(equalsRegex),
                Pattern.compile(toStringRegex), Pattern.compile(hashCodeRegex));

        //Füge alle Akzessoren zur Liste hinzu
        for (Object key : recordToTest.getComponentMap().keySet()) {
            String accessorRegex = "(public int " + key.toString() + "(\\s)*\\()";
            methodenPatterns.add(Pattern.compile(accessorRegex));
        }

        //Liste für gefundene Matches
        ArrayList<String> results = new ArrayList<>();

        //Durchsuche kompletten Record nach Patterns
        for (Pattern p : methodenPatterns) {
            Matcher m = p.matcher(recordToTest.getRecordFull());
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
            speichereUeberschriebeneMethoden(results, recordToTest);

            System.out.println("Gefundene überschriebene Methoden und Konstruktoren:");
            for (String result : results) {
                if (result.contains(recordToTest.getName())) {
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
    private void speichereUeberschriebeneMethoden(ArrayList<String> results, RecordToTest recordToTest) {
        ArrayList<String> overriddenMethods = new ArrayList<>();
        for (String result : results) {
            String lastWord = result.substring(result.lastIndexOf(" ") + 1);
            overriddenMethods.add(lastWord);
        }
        recordToTest.setListOverriddenMethods(overriddenMethods);
    }
}
