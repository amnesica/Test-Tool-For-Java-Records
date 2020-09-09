import ToTest.MethodToTest;
import ToTest.RecordToTest;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Klasse, die die Tests fuer Leistungseffizienz und Wartbarkeit durchfuehrt
 */
public class TestExecutor {

    //Long Function
    private final int longFunctionThreshold = 15;

    //Large Class
    private final int amountMethodsThreshold = 10;
    private final int amountFieldsThreshold = 10;
    private final int amountLOCsThreshold = 160;

    //Long Parameter List
    private final int threshold = 5;

    //Der zu testende Record
    private RecordToTest recordToTest;

    //Classloader fuer Reflection mit dynamisch erstellten Klassen
    private URLClassLoader urlClassLoader;

    /**
     * Fuehrt den Leistungseffizienztest mit dem angegebenen recordToTest durch. Test wird nicht durchgefuehrt,
     * wenn Record mehr als 10 Komponenten enthaelt
     *
     * @param recordToTest recordToTest
     */
    void fuehreLeistungseffizienztestDurch(RecordToTest recordToTest) throws IOException, ClassNotFoundException,
            IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        System.out.println("Test auf Leistungseffizienz (" + recordToTest.getName() + "):");

        this.recordToTest = recordToTest;

        //Bereite Leistungseffizienztest und Wartbarkeitstest vor, indem Record-Klasse dynamisch erstellt werden
        String className = recordToTest.getName() + "PerformanceTest";
        erstelleDynamischKlasseMitTestRecord(className);

        //Fuehre Leistungseffizienztest durch, wenn es nicht mehr als 10 Komponenten im Record gibt
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
                        " ist ueber dem festgelegten Grenzwert von "
                        + mapGrenzwerte.get(recordToTest.getAmountComponents()) +
                        " Nanosekunden und wurde als ineffizient eingestuft.");
            } else {
                System.out.println("Ergebnis: Der Record " + recordToTest.getName() +
                        " ist nicht ueber dem festgelegten Grenzwert von "
                        + mapGrenzwerte.get(recordToTest.getAmountComponents()) +
                        " Nanosekunden und wurde als effizient eingestuft.");
            }
        } else {
            //Zuviele Komponenten
            System.out.println("Error: Der Leistungseffizienztest wird nur bis zu 10 Komponenten unterstuetzt. " +
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
        //Spezifiziere Klassennamen fuer Record und erstelle Record als String innerhalb Testklasse
        String sourceCode = erstelleKlasseMitTestRecordAlString(className);

        //Spezifiziere Pfade und Dateinamen
        File parent = new File(System.getProperty("user.dir"));
        File sourceFile = new File(parent, className + ".java");

        //Schreibe String mit Record in Datei
        FileWriter writer = new FileWriter(sourceFile);
        writer.write(sourceCode);
        writer.close();

        //Kompiliere mit 'enable-preview'
        JavaCompiler javaCompiler = ToolProvider.getSystemJavaCompiler();
        File parentDir = sourceFile.getParentFile();
        javaCompiler.run(null, null, null, "--enable-preview", "--release", "14",
                sourceFile.toString());

        //Fuege dynamisch erstellte Klassen zur Laufzeit zum Classloader hinzu
        urlClassLoader = URLClassLoader.newInstance(new URL[]{parentDir.toURI().toURL()});
    }

    /**
     * Initialisiert die Hashmap mit den festgelegten Grenzwerten fuer den Leistungseffizienztest
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
     * Erstellt die Klasse mit dem Testrecords als String und gibt diesen String zurueck
     *
     * @param nameTestKlasse Name der generierten Testdatei
     */
    private String erstelleKlasseMitTestRecordAlString(String nameTestKlasse) {
        StringBuilder sb = new StringBuilder();

        //Fuege Imports aus fileToTest an, wenn welche vorhanden sind
        if (recordToTest.getNeededImportsAsString() != null) {
            sb.append(recordToTest.getNeededImportsAsString());
        }

        String headerTestClass =
                "/**\n" +
                        " * Automatisch generierte Testklasse mit dem Record \"" + recordToTest.getName() +
                        "\" fuer den Leistungseffizienztest.\n" +
                        " */" + "\n" + "public class " + nameTestKlasse + "{\n";

        String recordFull = "public " + recordToTest.getRecordFull();

        return sb.append(headerTestClass).append(recordFull).append("\n}").toString();
    }

    /**
     * Fuehrt den Test auf Wartbarkeit beim recordToTest durch
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
                    "welche die Wartbarkeit verschlechtern koennte.");
        } else {
            //Hinweis auf bewusst geringe Funktionalitaet von Records
            System.out.println("Hinweis: Eine Long Function, Large Class oder Long Parameter List kann ein Indiz " +
                    "dafuer sein, dass der Record " + recordToTest.getName() + " zu viel Funktionalitaet enthaelt und " +
                    "eine normale Klasse hier eventuell besser geeignet waere. Bei Records soll bewusst auf zu viel " +
                    "Funktionalitaet verzichtet werden.");
        }
        System.out.println("--------------------------------------------------------");
    }

    /**
     * Prueft, ob der Record recordToTest eine zu große Klasse ist (Large Class).
     * Wenn ja, wird das Ergebnis auf der Konsole ausgegeben
     *
     * @param recordToTest RecordToTest
     * @throws ClassNotFoundException ClassNotFoundException
     */
    private boolean pruefeAufLargeClass(RecordToTest recordToTest) throws ClassNotFoundException {
        boolean zuvieleMethoden = false;
        boolean zuvieleFelder = false;
        boolean zuvieleLOCDerKlasse = false;

        //Pruefe Anzahl der Methoden
        if (recordToTest.getListAllDeclaredMethods() != null && !recordToTest.getListAllDeclaredMethods().isEmpty()) {
            if (recordToTest.getListAllDeclaredMethods().size() > amountMethodsThreshold) {
                System.out.println("Ergebnis: Beim Record " + recordToTest.getName() +
                        " wurden mehr als 10 Methoden erkannt (" + recordToTest.getListAllDeclaredMethods().size() +
                        " Methoden) (Large Class), " +
                        "welches die Wartbarkeit verschlechtern koennte.");
                zuvieleMethoden = true;
            }
        }

        //Pruefe Anzahl der Felder
        int amountFields = getAnzahlDeklarierterFelder(recordToTest.getName() + "PerformanceTest" + "$" +
                recordToTest.getName());
        if (amountFields > amountFieldsThreshold) {
            System.out.println("Ergebnis: Beim Record " + recordToTest.getName() +
                    " wurden mehr als 10 Felder erkannt (" + amountFields + " Felder) (Large Class), welches die " +
                    "Wartbarkeit verschlechtern koennte.");
            zuvieleFelder = true;
        }

        //Pruefe LOC des Records gesamt
        if (recordToTest.getRecordFull().lines().count() > amountLOCsThreshold) {
            System.out.println("Ergebnis: Beim Record " + recordToTest.getName() +
                    " wurden mehr als 160 LOC erkannt (" + recordToTest.getRecordFull().lines().count() + " LOC) " +
                    "(Large Class), welches die Wartbarkeit verschlechtern koennte.");
            zuvieleLOCDerKlasse = true;
        }

        return zuvieleMethoden || zuvieleFelder || zuvieleLOCDerKlasse;
    }

    /**
     * Prueft, ob der Record recordToTest eine zu lange Methode enthaelt (Long Function).
     * Wenn ja, wird das Ergebnis auf der Konsole ausgegeben
     *
     * @param recordToTest RecordToTest
     */
    private boolean pruefeAufLongFunction(RecordToTest recordToTest) {
        ArrayList<String> listLongFunctions = new ArrayList<>();

        if (recordToTest.getListAllDeclaredMethods() != null && !recordToTest.getListAllDeclaredMethods().isEmpty()) {
            for (int i = 0; i < recordToTest.getListAllDeclaredMethods().size(); i++) {
                MethodToTest methodToTest = recordToTest.getListAllDeclaredMethods().get(i);

                if (methodToTest.getFullMethod().lines().count() > longFunctionThreshold) {
                    listLongFunctions.add("Ergebnis: Beim Record " + recordToTest.getName() +
                            " wurde eine zu lange Methode (Long Function) \"" + methodToTest.getName() + "\" mit " +
                            "mehr als 15 LOC (" + methodToTest.getFullMethod().lines().count() + " LOC) gefunden, " +
                            "welche die Wartbarkeit verschlechtern koennte.");
                }
            }
        }

        if (!listLongFunctions.isEmpty()) {
            for (String longFunction : listLongFunctions) {
                System.out.println(longFunction);
            }
            return true;
        } else {
            return false;
        }
    }

    /**
     * Gibt die Anzahl der Felder der Klasse exklusive der Felder, welche durch die Komponenten zur Laufzeit
     * erzeugt werden mit dem angegebenen className zurueck
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
     * Prueft, ob der Record recordToTest eine lange Parameterliste enthaelt (Long Parameter List).
     * Wenn ja, wird das Ergebnis auf der Konsole ausgegeben
     *
     * @param recordToTest ToTest.RecordToTest
     */
    private boolean pruefeAufLongParameterList(RecordToTest recordToTest) {
        StringBuilder sb = new StringBuilder();

        //Untersuche Anzahl Komponenten des Records
        if (recordToTest.getAmountComponents() > threshold) {
            sb.append("Ergebnis: Beim Record ").append(recordToTest.getName()).append(" wurden mehr als 5 Komponenten (").append(recordToTest.getAmountComponents()).append(" Komponenten)").append(" (Long Parameter List) gefunden, welches ").append("die Wartbarkeit verschlechtern koennte.\n");
        }

        //Alle Methoden auf Long Parameter List pruefen
        if (recordToTest.getListAllDeclaredMethods() != null && !recordToTest.getListAllDeclaredMethods().isEmpty()) {
            for (MethodToTest methodToTest : recordToTest.getListAllDeclaredMethods()) {
                if (methodToTest.getAmountParameters() > threshold) {
                    sb.append("Ergebnis: Beim Record ").append(recordToTest.getName()).append(" wurden in der Methode \"").append(methodToTest.getName()).append("\" mehr als 5 Parameter (").append(methodToTest.getAmountParameters()).append(" Parameter)").append(" (Long Parameter List) gefunden, ").append("welches die Wartbarkeit verschlechtern koennte.\n");
                }
            }
        }

        if (!sb.toString().isEmpty()) {
            //Loesche letzten Umbruch ("\n")
            System.out.println(sb.toString().substring(0, sb.toString().lastIndexOf("\n")));
            return true;
        } else {
            return false;
        }
    }
}
