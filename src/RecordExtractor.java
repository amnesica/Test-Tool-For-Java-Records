import ToTest.FileToTest;
import ToTest.MethodToTest;
import ToTest.RecordToTest;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Klasse, die Informationen über die Records in der Datei FileToTest extrahiert und prüft, ob Testfaelle generiert
 * werden sollen
 */
public class RecordExtractor {

    //Datei mit zu testenden Records
    private FileToTest fileToTest;

    /**
     * Überprüft, ob Testfälle erstellt werden sollen. Die Methode sucht nach Records in der Datei fileToTest und
     * extrahiert Basis-Informationen über die gefundenen Records
     *
     * @return true, wenn Testfälle generiert werden sollen
     */
    boolean pruefeObTestfaelleGeneriertWerdenSollen(FileToTest fileToTest) {
        if (!fileToTest.getFileContent().isEmpty() && fileToTest.getFileContent().contains("public class") &&
                fileToTest.getFileContent().contains("record")) {

            //Speichere ToTest.FileToTest als Feld
            this.fileToTest = fileToTest;

            //Regulärer Ausdruck um Beginn des Records zu ermitteln (record\s\w+\()
            String beginOfRecordRegex = "(record\\s\\w+\\()";
            Pattern r = Pattern.compile(beginOfRecordRegex);
            Matcher m = r.matcher(fileToTest.getFileContent());

            //Suche nach allen Records in Datei
            while (m.find()) {
                RecordToTest recordToTest = new RecordToTest();
                fileToTest.fuegeRecordZuListeDerZuTestendenRecordsHinzu(recordToTest);

                recordToTest.setIndexEntryPointRecordBeforeMatch(m.start());
                recordToTest.setIndexEntryPointRecordAfterMatch(m.end());

                //speichere Komponenten in RecordToTest
                speichereKomponenten(recordToTest);
            }

            for (RecordToTest recordToTest : fileToTest.getListRecords()) {
                if (recordToTest.getComponentMap() != null) {
                    if (recordEnthaeltNurInteger(recordToTest.getComponentMap(), recordToTest)) {

                        //extrahiere kompletten Record (mit Body) und Body an sich
                        speichereBody(recordToTest);
                        speichereKomplettenRecord(recordToTest);

                        //prüfe, ob automatisch generierte Methoden oder Konstruktor im Record überschrieben wurden
                        if (recordUeberschreibtMethodenOderKonstruktor(recordToTest)) {
                            //Setze Record soll getestet werden (funktional und nicht-funktional)
                            recordToTest.setGenerateTestcasesForRecord(true);
                        }

                        recordToTest.setExecuteTestcasesForRecord(true);

                        //extrahiere Methoden des Records
                        speichereMethoden(recordToTest);
                    }
                }
            }
        } else {
            //Es wurde gar kein Record gefunden
            System.out.println("Error: Es wurde kein Record in der angegebenen Datei gefunden." +
                    "\nTest-Tool wird beendet.");
            return false;
        }

        //Gibt Rückmeldung über die gefundenen Records auf der Konsole
        if (fileToTest.getListRecords() != null && !fileToTest.getListRecords().isEmpty()) {
            for (RecordToTest recordToTest : fileToTest.getListRecords()) {
                if (!recordToTest.isGenerateTestcasesForRecord()) {
                    if (recordToTest.getListFoundObjects().isEmpty() &&
                            recordToTest.getListFoundDataTypes().isEmpty()) {
                        System.out.println("Hinweis: Der Record " + recordToTest.getName() +
                                " überschreibt keine automatisch generierte Methode und muss " +
                                "daher funktional nicht getestet werden.");
                        System.out.println("--------------------------------------------------------");
                    } else {
                        gebeFehlermeldungWennRecordNichtNurIntegerEnthaelt(recordToTest);
                    }
                } else {
                    gebeRecordInformationenAufKonsole(recordToTest);
                }
            }
            return true;
        } else {
            System.out.println("Error: Es wurde kein Record in der angegebenen Datei gefunden." +
                    "\nTest-Tool wird beendet.");
            return false;
        }
    }

    /**
     * Speichert die Methoden in recordToTest.getListAllDeclaredMethods() als String ab
     *
     * @param recordToTest RecordToTest
     */
    private void speichereMethoden(RecordToTest recordToTest) {
        if (recordToTest.getListAllDeclaredMethods() != null && recordToTest.getRecordFull() != null) {
            for (MethodToTest methodToTest : recordToTest.getListAllDeclaredMethods()) {
                String fullMethod = leseBodyAus(recordToTest.getRecordFull(), methodToTest.getStartIndex());
                methodToTest.setFullMethod(fullMethod);

                //Speichere Parameter und Parameteranzahl der Methode
                String methodHeader = leseMethodHeaderAus(methodToTest.getFullMethod());
                methodToTest.setMethodHeader(methodHeader);

                if (!methodHeader.equals("")) {
                    //extrahiere Parameter aus Header in Liste von Wörtern
                    ArrayList<String> listWords = extrahiereKomponentenInArrayList(methodHeader);
                    if (!listWords.isEmpty()) {

                        //Speichere Parameter in HashMap
                        LinkedHashMap<Object, Object> parameterMap = speichereKomponentenInMap(listWords);

                        //Speichere hashMap und Anzahl Parameter an methodToTest
                        methodToTest.setParameterMap(parameterMap);
                        methodToTest.setAmountParameters(parameterMap.keySet().size());
                    }
                } else {
                    //Es wurde nur "()" gefunden -> keine Parameter
                    methodToTest.setAmountParameters(0);
                }
            }
        }
    }

    /**
     * Gibt den Header einer Methode als String zurück
     *
     * @param fullMethod Komplette Methode als String
     * @return String
     */
    private String leseMethodHeaderAus(String fullMethod) {
        String header = null;
        int startIndex = 0;
        int endIndex = 0;
        for (int i = 0; i < fullMethod.length(); i++) {
            if (fullMethod.charAt(i) == '(') {
                // + 1, um "("-Klammer weg zu bekommen
                startIndex = i + 1;
                break;
            }
        }
        for (int j = startIndex; j < fullMethod.length(); j++) {
            if (fullMethod.charAt(j) == ')') {
                // - 1, um ")"-Klammer weg zu bekommen
                endIndex = j;
                break;
            }
        }

        if (startIndex != endIndex) {
            header = fullMethod.substring(startIndex, endIndex);
        } else {
            header = "";
        }
        return header;
    }

    /**
     * Speichert den kompletten Record mit Header und Body als "RecordFull" in recordToTest ab
     *
     * @param recordToTest RecordToTest
     */
    private void speichereKomplettenRecord(RecordToTest recordToTest) {
        if (recordToTest.getRecordHeader() != null && recordToTest.getRecordBody() != null) {
            recordToTest.setRecordFull(recordToTest.getRecordHeader().concat(recordToTest.getRecordBody()));
        }
    }

    /**
     * Speichert den Body des Records als "RecordBody" in recordToTest ab
     *
     * @param recordToTest recordToTest
     */
    private void speichereBody(RecordToTest recordToTest) {
        // + 1, da sonst die letzte ')'-Klammer des Headers mitgenommen werden würde
        int indexStartBody = recordToTest.getIndexEndOfComponentList() + 1;

        String body = leseBodyAus(fileToTest.getFileContent(), indexStartBody);
        if (body != null) {
            recordToTest.setRecordBody(body);
        }
    }

    /**
     * Gibt den extrahierten Body eines Strings zurück, der einen Body enthält (Klasse, Methode, Konstruktor)
     *
     * @param stringMitBody  String, der einen Body enthält
     * @param indexStartBody StartIndex
     * @return Body des Strings
     */
    private String leseBodyAus(String stringMitBody, int indexStartBody) {
        //Set um die Klammern zu zählen, wenn keine Klammer mehr drinn ist, ist Record zuende
        ArrayList<Character> brackets = new ArrayList<>();

        int indexAnfang = 0;
        for (int j = indexStartBody; j < stringMitBody.length(); j++) {
            if (stringMitBody.charAt(j) == '{') {
                //Setze Anfangsindex und breche Schleife ab
                indexAnfang = j;
                break;
            }
        }

        //Lese Body aus
        if (stringMitBody.charAt(indexAnfang) == '{') { //old: indexStartBody
            for (int i = indexStartBody; i < stringMitBody.length(); i++) {
                if (stringMitBody.charAt(i) == '{') {
                    //speichere Klammer in Liste
                    brackets.add(stringMitBody.charAt(i));
                }
                if (stringMitBody.charAt(i) == '}') {
                    if (!brackets.isEmpty()) {
                        //wenn noch mehrere Klammern vorhanden sind -> entferne diese aus Liste
                        if (brackets.size() != 1) {
                            brackets.remove(0);
                        } else {
                            //letzte '}'-Klammer wurde gelesen (i+1 da sonst letzte '}'-Klammer fehlt)
                            int indexEndBody = i + 1;

                            //extrahiere Body des stringMitBody
                            return stringMitBody.substring(indexStartBody, indexEndBody);
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     * Gibt Informationen zum recordToTest auf der Konsole aus: Name,
     * überschrieben Methoden oder Konstruktoren
     *
     * @param recordToTest recordToTest
     */
    private void gebeRecordInformationenAufKonsole(RecordToTest recordToTest) {
        //Ausgabe des Namens auf Konsole
        System.out.println("Name des Records: " + recordToTest.getName());

        //Ausgabe überschriebenen Methoden und Konstruktoren auf der Konsole
        if (recordToTest.getListOverriddenMethods() != null && !recordToTest.getListOverriddenMethods().isEmpty()) {
            System.out.println("Gefundene überschriebene Methoden und Konstruktoren:");
            for (String overriddenMethod : recordToTest.getListOverriddenMethods()) {
                if (overriddenMethod.contains(recordToTest.getName())) {
                    //wenn Name des Records enthalten ist -> Konstruktor wurde überschrieben
                    System.out.println("Konstruktor: " + overriddenMethod);
                } else {
                    //sonst Methode
                    System.out.println("Methode: " + overriddenMethod);
                }
            }
        }
        System.out.println("--------------------------------------------------------");
    }

    /**
     * Speichert die Komponenten des Records in einer Hashmap ab, um später zu erkennen, ob es sich nur um
     * Integer-Werte handelt. Speichert außerdem die Anzahl der Komponenten ab
     *
     * @param recordToTest recordToTest
     */
    private void speichereKomponenten(RecordToTest recordToTest) {
        for (int i = recordToTest.getIndexEntryPointRecordAfterMatch(); i < fileToTest.getFileContent().length(); i++) {
            if (fileToTest.getFileContent().charAt(i) == ')') {
                //Ende der Parameterliste erreicht
                recordToTest.setIndexEndOfComponentList(i);

                //extrahiere Record Header mit Name und Komponentenliste (+ 1, damit letzte ')'-Klammer noch mitkommt)
                recordToTest.setRecordHeader(fileToTest.getFileContent().
                        substring(recordToTest.getIndexEntryPointRecordBeforeMatch(),
                                recordToTest.getIndexEndOfComponentList() + 1));
                recordToTest.setName(extrahiereRecordName(fileToTest.getFileContent(), recordToTest));

                //speichere String-Komponenten-Liste in Arraylist
                String componentsAsString = fileToTest.getFileContent().
                        substring(recordToTest.getIndexEntryPointRecordAfterMatch(),
                                recordToTest.getIndexEndOfComponentList());
                ArrayList<String> listWords = extrahiereKomponentenInArrayList(componentsAsString);

                //speichere einzelne Komponenten in Hashmap
                LinkedHashMap<Object, Object> componentMap;
                if (!listWords.isEmpty()) {
                    //fülle Map mit Komponenten
                    componentMap = speichereKomponentenInMap(listWords);

                    //Setze Map in recordToTest und Anzahl Komponenten
                    recordToTest.setComponentMap(componentMap);
                    recordToTest.setAmountComponents(componentMap.keySet().size());

                    //beende Schleife nach Extrahieren
                    break;
                }
            }
        }
    }

    /**
     * Speichert die Komponenten oder Parameter aus einer Liste an Wörtern in einer LinkedHashMap<Object, Object>
     * wobei der key=name und value=type ist
     *
     * @param listWords ArrayList<String>
     * @return LinkedHashMap<Object, Object> mit key=name und value=type
     */
    private LinkedHashMap<Object, Object> speichereKomponentenInMap(ArrayList<String> listWords) {
        LinkedHashMap<Object, Object> componentMap = new LinkedHashMap<>();
        for (int j = 0; j < listWords.size(); j++) {
            if (j % 2 == 0) {
                //key=name, value=type
                //hier andersherum, da sonst der vorherige Eintrag immer ersetzt wird,
                //da Key gleich wäre (int)
                componentMap.put(listWords.get(j + 1), listWords.get(j));
            }
        }
        return componentMap;
    }


    /**
     * Extrahiert den Namen eines Records aus dem Dateiinhalt der Datei, welche als
     * Parameter dem Programm mitgegeben wurde
     *
     * @param fileContent Dateiinhalt als String
     * @return Name des Records als String
     */
    private String extrahiereRecordName(String fileContent, RecordToTest recordToTest) {
        ArrayList<String> listWords = new ArrayList<>();
        Scanner s = new Scanner(fileContent.substring(recordToTest.getIndexEntryPointRecordBeforeMatch(),
                recordToTest.getIndexEntryPointRecordAfterMatch()));
        while (s.hasNext()) {
            String word = s.next();
            listWords.add(word.replace("(", ""));
        }

        //letztes Wort ist Name des Records
        return listWords.get(listWords.size() - 1);
    }

    /**
     * Überprüft, ob der Record nur Integer als Komponenten enthält
     *
     * @param componentMap HashMap mit Komponenten, wobei key=Bezeichner, value=type
     * @param recordToTest RecordToTest
     * @return true, wenn Record nur Integer als Komponenten enthält
     */
    private boolean recordEnthaeltNurInteger(HashMap<Object, Object> componentMap, RecordToTest recordToTest) {
        //Liste um zu prüfen, ob Record nur Integer-Werte enthält
        ArrayList<String> dataTypes = new ArrayList<>();
        Collections.addAll(dataTypes, "boolean", "char", "byte", "short",
                "long", "float", "double", "String");

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

        //Speichere Listen an recordToTest ab
        recordToTest.setListFoundObjects(listFoundObjects);
        recordToTest.setListFoundDataTypes(listFoundDataTypes);

        //Falls Nicht-Integer Werte gefunden worden d.h. Liste nicht leer ist -> gebe false zurück
        //Wenn nur Integer-Komponenten gefunden worden d.h. Liste leer ist -> gebe true aus
        return listFoundKeysNotInt.isEmpty();
    }

    /**
     * Gibt eine Fehlermeldung beruhend auf den Inhalten in den Listen listFoundObjects und listFoundDataTypes,
     * als nach Nicht-Record Komponenten gesucht wurde aus. Wenn in der Parameterliste ein Objekt entdeckt wurde,
     * wird ein Hinweis ausgegeben, dass Objekte weiterhin verändert werden können
     *
     * @param recordToTest recordToTest
     */
    private void gebeFehlermeldungWennRecordNichtNurIntegerEnthaelt(RecordToTest recordToTest) {
        //Ausgabe des Namens auf Konsole
        System.out.println("Name des Records: " + recordToTest.getName());

        ArrayList<String> listFoundObjects = recordToTest.getListFoundObjects();
        ArrayList<String> listFoundDataTypes = recordToTest.getListFoundDataTypes();

        //Gebe Error oder Warnung heraus
        if (!listFoundDataTypes.isEmpty() && listFoundObjects.isEmpty()) {
            if (listFoundDataTypes.size() == 1) {
                System.out.println("Error: Komponente mit Nicht-Integer-Wert gefunden:\n" + listFoundDataTypes.get(0));
            } else {
                System.out.println("Error: Komponenten mit Nicht-Integer-Wert gefunden: ");
                for (String foundTypes : listFoundDataTypes) {
                    System.out.println(foundTypes);
                }
            }

        } else if (!listFoundObjects.isEmpty() && listFoundDataTypes.isEmpty()) {
            if (listFoundObjects.size() == 1) {
                System.out.println("Warnung: Komponente mit Objekt gefunden:\n" + listFoundObjects.get(0) +
                        "\nHinweis: Objekte können weiterhin verändert werden!");
            } else {
                System.out.println("Warnung: Komponente mit Objekt gefunden: ");
                for (String foundObjects : listFoundObjects) {
                    System.out.println(foundObjects);
                }
                System.out.println("Hinweis: Objekte können weiterhin verändert werden!");
            }

        } else if (!listFoundDataTypes.isEmpty()) {
            //Warnung, wenn Komponente ein Objekt ist -> Objekte können weiterhin verändert werden
            System.out.println("Warnung: Komponente(n) mit Nicht-Integer-Wert(en) gefunden: ");
            for (String foundTypes : listFoundDataTypes) {
                System.out.println(foundTypes);
            }
            for (String foundObjects : listFoundObjects) {
                System.out.println(foundObjects);
            }
            System.out.println("Hinweis: Objekte können weiterhin verändert werden!");
        }
        System.out.println("--------------------------------------------------------");
    }

    /**
     * Extrahiert die Komponenten des Records in eine ArrayListe, wobei jedes entdeckte Wort ein einzelnes
     * Element der Liste ist
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
     * Überprüft, ob der Record eine automatisch generierte Methode oder den kanonischen Konstruktor überschreibt.
     * Außerdem werden Informationen über die überschrieben Methoden und Konstruktoren sowie alle weiteren
     * explizit deklarierten Methoden gesammelt
     *
     * @return true, wenn Konstruktor/Methode überschrieben wurde
     */
    private boolean recordUeberschreibtMethodenOderKonstruktor(RecordToTest recordToTest) {
        //Liste mit regulären Ausdrücken zum Durchsuchen des Records nach überschriebenen automatisch
        // generierten Methoden
        List<Pattern> ueberschriebeneMethodenPatterns = erstellePatternListeUeberschriebeneMethoden(recordToTest);

        //Liste mit regulären Ausdrücken zum Durchsuchen des Records nach allen anderen deklarierten Methoden
        List<Pattern> sonstigeMethodenPatterns = erstellePatternListeSonstigeMethoden();

        //Listen für gefundene Matches
        ArrayList<String> results = new ArrayList<>();
        ArrayList<MethodToTest> listAllDeclaredMethods = new ArrayList<>();

        //Durchsuche kompletten Record nach Patterns für überschriebene Methoden und Konstruktoren
        for (Pattern p : ueberschriebeneMethodenPatterns) {
            Matcher m = p.matcher(recordToTest.getRecordFull());

            //Bei erstem Match gebe true zurück
            if (m.find()) {
                int indexStart = m.start();
                int indexEnd = m.end();
                String output = m.group(0);
                MethodToTest methodToTest = new MethodToTest();

                //Formatiere output for dem Speichern
                if (m.group(0).contains("{")) {
                    String formattedOutput = output.replace("{", "");
                    results.add(formattedOutput);

                    methodToTest.setName(formattedOutput);
                    methodToTest.setStartIndex(indexStart);
                    methodToTest.setEndIndex(indexEnd);

                    listAllDeclaredMethods.add(methodToTest);
                } else if (m.group(0).contains("(")) {
                    String formattedOutput = output.replace("(", "");
                    results.add(formattedOutput);

                    methodToTest.setName(formattedOutput);
                    methodToTest.setStartIndex(indexStart);
                    methodToTest.setEndIndex(indexEnd);

                    listAllDeclaredMethods.add(methodToTest);
                }
            }
        }

        //Durchsuche kompletten Record nach Patterns für sonstige Methoden
        for (Pattern p : sonstigeMethodenPatterns) {
            Matcher m = p.matcher(recordToTest.getRecordFull());

            //while hier, da es mehrere weitere Methoden geben kann
            while (m.find()) {
                int indexStart = m.start();
                int indexEnd = m.end();
                String output = m.group(0);
                MethodToTest methodToTest = new MethodToTest();

                //Formatiere output for dem Speichern
                if (m.group(0).contains("{")) {
                    String formattedOutput = output.replace("{", "");

                    methodToTest.setName(formattedOutput);
                    methodToTest.setStartIndex(indexStart);
                    methodToTest.setEndIndex(indexEnd);

                    //verhindere Doppelungen
                    if (!listAllDeclaredMethods.contains(methodToTest)) {
                        listAllDeclaredMethods.add(methodToTest);
                    }
                } else if (m.group(0).contains("(")) {
                    String formattedOutput = output.replace("(", "");

                    methodToTest.setName(formattedOutput);
                    methodToTest.setStartIndex(indexStart);
                    methodToTest.setEndIndex(indexEnd);

                    if (!listAllDeclaredMethods.contains(methodToTest)) {
                        listAllDeclaredMethods.add(methodToTest);
                    }
                }
            }
        }

        //Printe gefundene Ergebnisse oder Error wenn nicht gefunden wurde und speichere Ergebnisse ab
        if (!results.isEmpty()) {
            //Speichere überschriebene Methoden und Konstruktoren in recordInfo ab
            speichereUeberschriebeneMethoden(results, recordToTest);
        }
        if (!listAllDeclaredMethods.isEmpty()) {
            //Speichere alle gefundenen Methoden in listAllDeclaredMethods im Record ab
            recordToTest.setListAllDeclaredMethods(listAllDeclaredMethods);
        }

        return !results.isEmpty();
    }

    /**
     * Erstellt eine Liste mit Pattern, um alle sonstigen Methoden im Record erkennen zu können
     *
     * @return List<Pattern>
     */
    private List<Pattern> erstellePatternListeSonstigeMethoden() {
        List<Pattern> sonstigeMethodenPatterns = new ArrayList<>();

        //Reguläre Ausdrücke zum Finden von allen anderen deklarierten Methoden im Record
        String otherPublicRegex = "(public ((?!record)\\w)+ (\\w)+(\\s)*\\()";
        String otherPrivateRegex = "(private ((?!record)\\w)+ (\\w)+(\\s)*\\()";
        String otherProtectedRegex = "(protected ((?!record)\\w)+ (\\w)+(\\s)*\\()";
        String otherPackagePrivateRegex = "( {2}((?!record)\\w)+ (\\w)+(\\s)*\\()";

        //Füge alle regulären Ausdrücke zum Durchsuchen des Records nach allen anderen deklarierten Methoden
        // zur Liste hinzu
        Collections.addAll(sonstigeMethodenPatterns, Pattern.compile(otherPublicRegex),
                Pattern.compile(otherPrivateRegex), Pattern.compile(otherProtectedRegex),
                Pattern.compile(otherPackagePrivateRegex));

        return sonstigeMethodenPatterns;
    }

    /**
     * Erstellt eine Liste mit Pattern, um die überschriebenen automatisch generierten Methoden und Konstruktoren
     * erkennen zu können
     *
     * @param recordToTest RecordToTest
     * @return List<Pattern>
     */
    private List<Pattern> erstellePatternListeUeberschriebeneMethoden(RecordToTest recordToTest) {
        List<Pattern> ueberschriebeneMethodenPatterns = new ArrayList<>();

        //Reguläre Ausdrücke zum Durchsuchen des Records nach überschriebenen automatisch generierten Methoden
        String constructorRegex = "(public " + recordToTest.getName() + "(\\s)*\\{)";
        String equalsRegex = "(public boolean equals(\\s)*\\()";
        String toStringRegex = "(public String toString(\\s)*\\()";
        String hashCodeRegex = "(public int hashCode(\\s)*\\()";

        //Füge alle regulären Ausdrücke zum Durchsuchen des Records nach überschriebenen automatisch generierten
        // Methoden zur Liste hinzu
        Collections.addAll(ueberschriebeneMethodenPatterns, Pattern.compile(constructorRegex),
                Pattern.compile(equalsRegex), Pattern.compile(toStringRegex), Pattern.compile(hashCodeRegex));

        //Füge alle regulären Ausdrücke für überschriebene Akzessoren zur Liste hinzu
        for (Object key : recordToTest.getComponentMap().keySet()) {
            String accessorRegex = "(public int " + key.toString() + "(\\s)*\\()";
            ueberschriebeneMethodenPatterns.add(Pattern.compile(accessorRegex));
        }

        return ueberschriebeneMethodenPatterns;
    }

    /**
     * Speichert die Bezeichner der überschriebenen Methoden und Konstruktoren in RecordToTest
     * als ListOverriddenMethods ab. Die Liste an gefundenen Methoden und Konstruktoren muss vorher
     * konvertiert werden, damit auch nur immer das letzte Wort (d.h. Bezeichner) gespeichert wird
     *
     * @param results Liste von gefundenen Methoden und Konstruktoren
     */
    private void speichereUeberschriebeneMethoden(ArrayList<String> results, RecordToTest recordToTest) {
        ArrayList<String> overriddenMethods = new ArrayList<>();
        for (String result : results) {

            //Wenn letzte Character eine Leerstelle ist, lösche diese
            if (result.charAt(result.length() - 1) == ' ') {
                result = result.substring(0, result.length() - 1);
            }

            //Extrahiere letztes Wort aus String result
            String lastWord = result.substring(result.lastIndexOf(" ") + 1);
            overriddenMethods.add(lastWord);
        }
        recordToTest.setListOverriddenMethods(overriddenMethods);
    }
}
