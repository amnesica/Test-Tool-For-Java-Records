import ToTest.FileToTest;
import ToTest.MethodToTest;
import ToTest.RecordToTest;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Klasse, die Informationen ueber die Records in der Datei FileToTest extrahiert und prueft, ob Testfaelle generiert
 * werden sollen
 */
public class RecordExtractor {

    //Datei mit zu testenden Records
    private FileToTest fileToTest;

    /**
     * ueberprueft, ob Testfaelle erstellt werden sollen. Die Methode sucht nach Records in der Datei fileToTest und
     * extrahiert Basis-Informationen ueber die gefundenen Records
     *
     * @return true, wenn Testfaelle generiert werden sollen
     */
    boolean pruefeObTestfaelleGeneriertWerdenSollen(FileToTest fileToTest) {
        if (!fileToTest.getFileContent().isEmpty() && fileToTest.getFileContent().contains("public class") &&
                fileToTest.getFileContent().contains("record")) {

            //Speichere FileToTest als Feld
            this.fileToTest = fileToTest;

            //Speichere Imports aus FileToTest
            String imports = extrahiereImportsAusFile(fileToTest);

            //Regulaerer Ausdruck um Beginn des Records zu ermitteln (record\s\w+\()
            String beginOfRecordRegex = "(record\\s\\w+\\()";
            Pattern r = Pattern.compile(beginOfRecordRegex);
            Matcher m = r.matcher(fileToTest.getFileContent());

            //Suche nach allen Records in Datei
            while (m.find()) {
                RecordToTest recordToTest = new RecordToTest();
                fileToTest.fuegeRecordZuListeDerZuTestendenRecordsHinzu(recordToTest);

                recordToTest.setIndexEntryPointRecordBeforeMatch(m.start());
                recordToTest.setIndexEntryPointRecordAfterMatch(m.end());

                //speichere benoetigte Imports in RecordToTest
                recordToTest.setNeededImportsAsString(imports);

                //speichere Komponenten in RecordToTest
                speichereKomponenten(recordToTest);
            }

            for (RecordToTest recordToTest : fileToTest.getListRecords()) {
                if (recordToTest.getComponentMap() != null) {
                    if (recordEnthaeltNurInteger(recordToTest.getComponentMap(), recordToTest)) {

                        //extrahiere kompletten Record (mit Body) und Body an sich
                        speichereBody(recordToTest);
                        speichereKomplettenRecord(recordToTest);

                        //pruefe, ob automatisch generierte Methoden oder Konstruktor im Record ueberschrieben wurden
                        if (recordUeberschreibtMethodenOderKonstruktor(recordToTest)) {
                            //Setze Record soll getestet werden (funktional)
                            recordToTest.setGenerateFunctionalTestcases(true);
                        }

                        //Setze Record soll getestet werden auf Wartbarkeit und Leistungseffizienz
                        // (nicht-funktional), da Komponenten alle Integer-Werte sind
                        recordToTest.setExecuteNonFunctionalTestcases(true);

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

        //Gibt Rueckmeldung ueber die gefundenen Records auf der Konsole
        if (fileToTest.getListRecords() != null && !fileToTest.getListRecords().isEmpty()) {
            for (RecordToTest recordToTest : fileToTest.getListRecords()) {

                //funktionaler Test
                if (!recordToTest.isGenerateFunctionalTestcases()) {
                    if (recordToTest.getListFoundObjects() != null &&
                            recordToTest.getListFoundDataTypes() != null &&
                            recordToTest.getListFoundObjects().isEmpty() &&
                            recordToTest.getListFoundDataTypes().isEmpty()) {

                        System.out.println("Hinweis: Der Record " + recordToTest.getName() +
                                " ueberschreibt keine automatisch generierte Methode und muss " +
                                "daher funktional nicht getestet werden.");
                    } else {

                        //Record enthaelt nicht nur Integer als Komponenten
                        if (recordToTest.getListFoundObjects() != null &&
                                recordToTest.getListFoundDataTypes() != null) {
                            gebeFehlermeldungWennRecordNichtNurIntegerEnthaelt(recordToTest);
                        } else {
                            //Record enthaelt gar keine Komponenten
                            System.out.println("Hinweis: Der Record " + recordToTest.getName() +
                                    " enthaelt keine Komponenten und muss " +
                                    "daher funktional nicht getestet werden.");
                        }
                    }

                } else {
                    gebeRecordInformationenAufKonsole(recordToTest);
                }

                //nicht-funktionaler Test - Wartbarkeit und Leistungseffizienz
                if (!recordToTest.isExecuteNonFunctionalTestcases()) {
                    //Nicht alle Komponenten des Records sind Integer
                    System.out.println("Hinweis: Fuer den Record " + recordToTest.getName() +
                            " wird kein Test auf Leistungseffizienz und Wartbarkeit (nicht-funktional) durchgefuehrt.");
                }
                System.out.println("--------------------------------------------------------");
            }
            return true;
        } else {
            System.out.println("Error: Es wurde kein Record in der angegebenen Datei gefunden." +
                    "\nTest-Tool wird beendet.");
            System.out.println("--------------------------------------------------------");
            return false;
        }

    }

    /**
     * Extrahiert die Imports aus der FileToTest und gibt sie als String zurueck
     *
     * @param fileToTest FileToTest
     * @return String
     */
    private String extrahiereImportsAusFile(FileToTest fileToTest) {
        StringBuilder sb = new StringBuilder();
        String beginOfImport = "(import[\\s]*(.)*(;))";
        Pattern r = Pattern.compile(beginOfImport);
        Matcher m = r.matcher(fileToTest.getFileContent());

        //Suche nach allen Records in Datei
        while (m.find()) {
            sb.append(fileToTest.getFileContent(), m.start(), m.end()).append("\n");
        }

        return sb.toString();
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
                    //extrahiere Parameter aus Header in Liste von Woertern
                    ArrayList<String> listWords = extrahiereKomponentenInArrayList(methodHeader);

                    if (!listWords.isEmpty()) {

                        //Sicherstellen, dass listWords kein Validierungs-Konstruktor ist
                        if (!listWords.contains("<") && !listWords.contains(">") && !listWords.contains("<=") &&
                                !listWords.contains(">=") && !listWords.contains("==") && !listWords.contains("!=") &&
                                !listWords.contains("&&") && !listWords.contains("||") && !listWords.contains("(") &&
                                !listWords.contains(")")) {

                            //Speichere Parameter in HashMap
                            LinkedHashMap<Object, Object> parameterMap = speichereKomponentenInMap(listWords);

                            //Speichere hashMap und Anzahl Parameter an methodToTest
                            methodToTest.setParameterMap(parameterMap);
                            methodToTest.setAmountParameters(parameterMap.keySet().size());
                        } else {
                            //bei Validierungs-Konstruktor
                            methodToTest.setAmountParameters(0);
                        }
                    }
                } else {
                    //Es wurde nur "()" gefunden -> keine Parameter
                    methodToTest.setAmountParameters(0);
                }
            }
        }
    }

    /**
     * Gibt den Header einer Methode als String zurueck
     *
     * @param fullMethod Komplette Methode als String
     * @return String
     */
    private String leseMethodHeaderAus(String fullMethod) {
        String header;
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
        // + 1, da sonst die letzte ')'-Klammer des Headers mitgenommen werden wuerde
        int indexStartBody = recordToTest.getIndexEndOfComponentList() + 1;

        String body = leseBodyAus(fileToTest.getFileContent(), indexStartBody);

        if (body != null) {
            //Speichere body ab
            recordToTest.setRecordBody(body);
        }
    }

    /**
     * Gibt den extrahierten Body eines Strings zurueck, der einen Body enthaelt (Klasse, Methode, Konstruktor)
     *
     * @param stringMitBody  String, der einen Body enthaelt
     * @param indexStartBody StartIndex
     * @return Body des Strings
     */
    private String leseBodyAus(String stringMitBody, int indexStartBody) {
        //Set um die Klammern zu zaehlen, wenn keine Klammer mehr drinn ist, ist Record zuende
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
     * ueberschrieben Methoden oder Konstruktoren
     *
     * @param recordToTest recordToTest
     */
    private void gebeRecordInformationenAufKonsole(RecordToTest recordToTest) {
        //Ausgabe des Namens auf Konsole
        System.out.println("Name des Records: " + recordToTest.getName());

        //Ausgabe ueberschriebenen Methoden und Konstruktoren auf der Konsole
        if (recordToTest.getListOverriddenMethods() != null && !recordToTest.getListOverriddenMethods().isEmpty()) {
            System.out.println("Gefundene ueberschriebene Methoden und Konstruktoren:");
            for (String overriddenMethod : recordToTest.getListOverriddenMethods()) {
                if (overriddenMethod.contains(recordToTest.getName())) {
                    //wenn Name des Records enthalten ist -> Konstruktor wurde ueberschrieben
                    System.out.println("Konstruktor: " + overriddenMethod);
                } else {
                    //sonst Methode
                    System.out.println("Methode: " + overriddenMethod);
                }
            }
        }
    }

    /**
     * Speichert die Komponenten des Records in einer Hashmap ab, um spaeter zu erkennen, ob es sich nur um
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

                //mache Reformat Code-Modifikation bei listWords rueckgaengig
                //wenn "ArrayList<String> listString" (2 Elemente) zu "ArrayList<String>listString" (1 Element) wurde
                for (int k = 0; k < listWords.size(); k++) {
                    if (listWords.get(k).contains(">")) {
                        int indexToSplit = listWords.get(k).indexOf(">");
                        String value = listWords.get(k).substring(0, indexToSplit + 1);
                        String key = listWords.get(k).substring(indexToSplit + 1);

                        //entferne alten Wert
                        listWords.remove(k);

                        //fuege neue Werte in Liste an derselben Stelle ein
                        listWords.add(k, value);
                        listWords.add(k + 1, key);
                    }
                }

                //speichere einzelne Komponenten in Hashmap
                LinkedHashMap<Object, Object> componentMap;
                if (!listWords.isEmpty()) {

                    //fuelle Map mit Komponenten
                    componentMap = speichereKomponentenInMap(listWords);

                    //Setze Map in recordToTest und Anzahl Komponenten
                    recordToTest.setComponentMap(componentMap);
                    recordToTest.setAmountComponents(componentMap.keySet().size());
                } else {
                    //keine Komponenten
                    recordToTest.setAmountComponents(0);
                }
                break;
            }
        }
    }

    /**
     * Speichert die Komponenten oder Parameter aus einer Liste an Woertern in einer LinkedHashMap<Object, Object>
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
                //da Key gleich waere (int)
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
     * ueberprueft, ob der Record nur Integer als Komponenten enthaelt
     *
     * @param componentMap HashMap mit Komponenten, wobei key=Bezeichner, value=type
     * @param recordToTest RecordToTest
     * @return true, wenn Record nur Integer als Komponenten enthaelt
     */
    private boolean recordEnthaeltNurInteger(HashMap<Object, Object> componentMap, RecordToTest recordToTest) {
        //Liste um zu pruefen, ob Record nur Integer-Werte enthaelt
        ArrayList<String> dataTypes = new ArrayList<>();
        Collections.addAll(dataTypes, "boolean", "char", "byte", "short",
                "long", "float", "double", "String");

        ArrayList<String> listFoundKeysNotInt = new ArrayList<>();
        ArrayList<String> listFoundObjects = new ArrayList<>();
        ArrayList<String> listFoundDataTypes = new ArrayList<>();

        for (Object key : componentMap.values()) {
            for (String type : dataTypes) {
                //pruefe auf Datentypen in Java. Mit "!key.equals("int")" werden auch benutzerdefinierte Objekte geprueft
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

        //Falls Nicht-Integer Werte gefunden worden d.h. Liste nicht leer ist -> gebe false zurueck
        //Wenn nur Integer-Komponenten gefunden worden d.h. Liste leer ist -> gebe true aus
        return listFoundKeysNotInt.isEmpty();
    }

    /**
     * Gibt eine Fehlermeldung beruhend auf den Inhalten in den Listen listFoundObjects und listFoundDataTypes,
     * als nach Nicht-Record Komponenten gesucht wurde aus. Wenn in der Parameterliste ein Objekt entdeckt wurde,
     * wird ein Hinweis ausgegeben, dass Objekte weiterhin veraendert werden koennen
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
                        "\nHinweis: Objekte koennen weiterhin veraendert werden! Dies koennte die gewuenschte " +
                        "Funktionalitaet des Records beeintraechtigen!");
            } else {
                System.out.println("Warnung: Komponente mit Objekt gefunden: ");
                for (String foundObjects : listFoundObjects) {
                    System.out.println(foundObjects);
                }
                System.out.println("Hinweis: Objekte koennen weiterhin veraendert werden! Dies koennte die gewuenschte " +
                        "Funktionalitaet des Records beeintraechtigen!");
            }

        } else if (!listFoundDataTypes.isEmpty()) {
            //Warnung, wenn Komponente ein Objekt ist -> Objekte koennen weiterhin veraendert werden
            System.out.println("Warnung: Komponente(n) mit Nicht-Integer-Wert(en) gefunden: ");
            for (String foundTypes : listFoundDataTypes) {
                System.out.println(foundTypes);
            }
            for (String foundObjects : listFoundObjects) {
                System.out.println(foundObjects);
            }
            System.out.println("Hinweis: Objekte koennen weiterhin veraendert werden! Dies koennte die gewuenschte " +
                    "Funktionalitaet des Records beeintraechtigen!");
        }
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
     * ueberprueft, ob der Record eine automatisch generierte Methode oder den kanonischen Konstruktor ueberschreibt.
     * Außerdem werden Informationen ueber die ueberschrieben Methoden und Konstruktoren sowie alle weiteren
     * explizit deklarierten Methoden gesammelt
     *
     * @return true, wenn Konstruktor/Methode ueberschrieben wurde
     */
    private boolean recordUeberschreibtMethodenOderKonstruktor(RecordToTest recordToTest) {
        //Liste mit regulaeren Ausdruecken zum Durchsuchen des Records nach ueberschriebenen automatisch
        // generierten Methoden
        List<Pattern> ueberschriebeneMethodenPatterns = erstellePatternListeUeberschriebeneMethoden(recordToTest);

        //Liste mit regulaeren Ausdruecken zum Durchsuchen des Records nach allen anderen deklarierten Methoden
        List<Pattern> sonstigeMethodenPatterns = erstellePatternListeSonstigeMethoden();

        //Listen fuer gefundene Matches
        ArrayList<String> results = new ArrayList<>();
        ArrayList<MethodToTest> listAllDeclaredMethods = new ArrayList<>();

        //Durchsuche kompletten Record nach Patterns fuer ueberschriebene Methoden und Konstruktoren
        for (Pattern p : ueberschriebeneMethodenPatterns) {
            Matcher m = p.matcher(recordToTest.getRecordFull());

            //Bei erstem Match gebe true zurueck
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

        //Durchsuche kompletten Record nach Patterns fuer sonstige Methoden
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
            //Speichere ueberschriebene Methoden und Konstruktoren in recordInfo ab
            speichereUeberschriebeneMethoden(results, recordToTest);
        }
        if (!listAllDeclaredMethods.isEmpty()) {
            //Speichere alle gefundenen Methoden in listAllDeclaredMethods im Record ab
            recordToTest.setListAllDeclaredMethods(listAllDeclaredMethods);
        }

        return !results.isEmpty();
    }

    /**
     * Erstellt eine Liste mit Pattern, um alle sonstigen Methoden im Record erkennen zu koennen
     *
     * @return List<Pattern>
     */
    private List<Pattern> erstellePatternListeSonstigeMethoden() {
        List<Pattern> sonstigeMethodenPatterns = new ArrayList<>();

        //Regulaere Ausdruecke zum Finden von allen anderen deklarierten Methoden im Record
        String otherPublicRegex = "(public ((?!record)\\w)+ (\\w)+(\\s)*\\()";
        String otherPrivateRegex = "(private ((?!record)\\w)+ (\\w)+(\\s)*\\()";
        String otherProtectedRegex = "(protected ((?!record)\\w)+ (\\w)+(\\s)*\\()";
        String otherPackagePrivateRegex = "( {2}((?!record)\\w)+ (\\w)+(\\s)*\\()";

        //Fuege alle regulaeren Ausdruecke zum Durchsuchen des Records nach allen anderen deklarierten Methoden
        // zur Liste hinzu
        Collections.addAll(sonstigeMethodenPatterns, Pattern.compile(otherPublicRegex),
                Pattern.compile(otherPrivateRegex), Pattern.compile(otherProtectedRegex),
                Pattern.compile(otherPackagePrivateRegex));

        return sonstigeMethodenPatterns;
    }

    /**
     * Erstellt eine Liste mit Pattern, um die ueberschriebenen automatisch generierten Methoden und Konstruktoren
     * erkennen zu koennen
     *
     * @param recordToTest RecordToTest
     * @return List<Pattern>
     */
    private List<Pattern> erstellePatternListeUeberschriebeneMethoden(RecordToTest recordToTest) {
        List<Pattern> ueberschriebeneMethodenPatterns = new ArrayList<>();

        //Regulaere Ausdruecke zum Durchsuchen des Records nach ueberschriebenen automatisch generierten Methoden
        String constructorRegex = "(public " + recordToTest.getName() + "(\\s)*\\{)";
        String equalsRegex = "(public boolean equals(\\s)*\\()";
        String toStringRegex = "(public String toString(\\s)*\\()";
        String hashCodeRegex = "(public int hashCode(\\s)*\\()";

        //Fuege alle regulaeren Ausdruecke zum Durchsuchen des Records nach ueberschriebenen automatisch generierten
        // Methoden zur Liste hinzu
        Collections.addAll(ueberschriebeneMethodenPatterns, Pattern.compile(constructorRegex),
                Pattern.compile(equalsRegex), Pattern.compile(toStringRegex), Pattern.compile(hashCodeRegex));

        //Fuege alle regulaeren Ausdruecke fuer ueberschriebene Akzessoren zur Liste hinzu
        for (Object key : recordToTest.getComponentMap().keySet()) {
            String accessorRegex = "(public int " + key.toString() + "(\\s)*\\()";
            ueberschriebeneMethodenPatterns.add(Pattern.compile(accessorRegex));
        }

        return ueberschriebeneMethodenPatterns;
    }

    /**
     * Speichert die Bezeichner der ueberschriebenen Methoden und Konstruktoren in RecordToTest
     * als ListOverriddenMethods ab. Die Liste an gefundenen Methoden und Konstruktoren muss vorher
     * konvertiert werden, damit auch nur immer das letzte Wort (d.h. Bezeichner) gespeichert wird
     *
     * @param results Liste von gefundenen Methoden und Konstruktoren
     */
    private void speichereUeberschriebeneMethoden(ArrayList<String> results, RecordToTest recordToTest) {
        ArrayList<String> overriddenMethods = new ArrayList<>();
        for (String result : results) {

            //Wenn letzte Character eine Leerstelle ist, loesche diese
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
