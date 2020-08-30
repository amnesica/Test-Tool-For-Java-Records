import java.util.ArrayList;

public class DebugRecords {
    //keine Ausführung - check
    record test1() {
    }

    //keine Ausführung funktional - check
    //Leistungseffizienz -> effizient - check
    //Wartbarkeit -> keine Smells - check
    record test2(int x) {
    }

    //keine Ausführung funktional - check
    //Leistungseffizienz -> kein Test, da mehr als 10 Komponenten! - check
    //Wartbarkeit -> mehr als 10 Felder (Large Class), mehr als 5 Komponenten (Long Parameter List) - check
    record test25(int x, int y, int z, int a, int b, int c, int d, int e, int f, int g, int h, int i) {
        public static int hello1;
        public static int hello2;
        public static int hello3;
        public static int hello4;
        public static int hello5;
        public static int hello6;
        public static int hello7;
        public static int hello8;
        public static int hello9;
        public static int hello10;
        public static int hello11;
    }

    //Ausführung -> alle JUnit-Tests laufen durch (mit benutzerdef. Grenzwerten) - check
    //Leistungseffizienz -> effizient - check
    //Wartbarkeit -> keine Smells - check
    record test3(int x) {
        public int x() {
            return x;
        }
    }

    //Ausführung -> JUnit-Tests schlagen fehl - check
    //Leistungseffizienz -> effizient - check
    //Wartbarkeit -> keine Smells - check
    record test4(int x) {
        public int x() {
            return x + 1;
        }
    }

    //keine Ausführung - check
    record test5(String s) {
    }

    //keine Ausführung -> Warnung Object - check
    record test55(Object o) {
        public Object o() {
            return o;
        }
    }

    //Ausführung -> Fehlermeldung String - check
    record test6(String s, int x) {
    }

    //Ausführung -> Fehlermeldung String - check
    record test7(String s, int x) {

        public int x() {
            return x + 1;
        }
    }

    //Ausführung -> Fehlermeldung Objekte - check
    record test8(String s, int x, ArrayList<String>listString, Object o) {

        public int x() {
            return x + 1;
        }
    }

    //Ausführung -
    // -> Fehlermeldung Objekte -> keine JUnit-Tests - check
    // Kein Test auf Wartbarkeit und Leistungseffizienz - check
    record test9(String s, int x, int y) {
        public static int a = 2;
        public static String s = "t";
        public static ArrayList<Integer> e;
        public static double f = 1.0;
        public static float g = 1f;
        public static float h = 1f;
        public static int r = 2;
        public static int b = 2;
        public static int c = 2;
        public static int d = 2;
        public static int k = 2;

        //Konstruktor mit Validierung
        public test9 {
            if (x > y) {
                x = 1;
                s = "test";
                g = 2f;
            }

            int r = 0;

            //lange Ausführung
            for (int i = 0; i < Integer.MAX_VALUE; i++) {
                r = i + 1 - 1;

                ArrayList<String> arrayList = new ArrayList<String>();
                arrayList.add(String.valueOf(r));
                arrayList.add("hallo");
            }
        }

        //Überschriebener Akzessor
        public int x() {
            return x + 1;
        }

        //Überschriebener Akzessor
        public int y() {
            return y;
        }

        //Eine Methode
        public void doSomething(int a, int b, int c, int d, int e, int p, int f, int g, int h, int k, String o, double d1) {
            int result = 0;
            for (int i = 0; i < 100; i++) {
                result += i;
                doSomethingElse("a");
            }
        }

        //Eine andere Methode
        private void doSomethingElse(String a) {
            int result2 = 0;
            for (int i = 0; i < 100; i++) {
                result2 += i;
            }
        }

        //Eine andere Methode
        private void doSomethingElse2(String a) {
            int result2 = 0;
            for (int i = 0; i < 100; i++) {
                result2 += i;
            }
        }

        //Eine andere Methode
        private void doSomethingElse3(String a, String b, String c, String f, String k, String s) {
            int result2 = 0;
            for (int i = 0; i < 100; i++) {
                result2 += i;
            }
        }

        //Eine andere Methode
        private void doSomethingElse4(String a) {
            int result2 = 0;
            for (int i = 0; i < 100; i++) {
                result2 += i;
            }
        }

        //Eine andere Methode
        private void doSomethingElse5(String a) {
            int result2 = 0;
            for (int i = 0; i < 100; i++) {
                result2 += i;
            }
        }

        //Eine andere Methode
        private void doSomethingElse6(String a) {
            int result2 = 0;
            for (int i = 0; i < 100; i++) {
                result2 += i;
            }
        }

        //Eine andere Methode
        private void doSomethingElse7(String a) {
            int result2 = 0;
            for (int i = 0; i < 100; i++) {
                result2 += i;
            }
        }

        //Eine andere Methode
        private void doSomethingElse8(String a) {
            int result2 = 0;
            for (int i = 0; i < 100; i++) {
                result2 += i;
            }
        }

        //Eine andere Methode
        private void doSomethingElse9(String a) {
            int result2 = 0;
            for (int i = 0; i < 100; i++) {
                result2 += i;
            }
        }

        //Eine andere Methode
        private void doSomethingElse10(String a) {
            int result2 = 0;
            for (int i = 0; i < 100; i++) {
                result2 += i;
            }
        }

        //Eine andere Methode
        private void doSomethingElse11(String a) {
            int result2 = 0;
            for (int i = 0; i < 100; i++) {
                result2 += i;
            }
        }
    }

    //Ausführung
    // -> JUnit-Tests schlagen fehl - check
    // -> Long Parameter List (12 Parameter Methode, 6 Parameter Klasse) - check
    // -> Large Class (180 LOC, 11 Felder, 16 Methoden) - check
    // -> Long Function (somethingElse6, Konstruktor) - check
    // -> Leistungseffizienz: ineffizient - check?
    record test10(int s, int x, int y) {
        public static int a = 2;
        public static String u = "t";
        public static ArrayList<Integer> e;
        public static double f = 1.0;
        public static float g = 1f;
        public static float h = 1f;
        public static int r = 2;
        public static int b = 2;
        public static int c = 2;
        public static int d = 2;
        public static int k = 2;

        //Konstruktor mit Validierung
        public test10 {
            if (x > y) {
                x = 1;
                s = 1000;
                g = 2f;
            }

            //Ein langer Kommentar
            //Ein langer Kommentar
            //Ein langer Kommentar
            //Ein langer Kommentar
            //Ein langer Kommentar
            //Ein langer Kommentar
            //Ein langer Kommentar
            //Ein langer Kommentar
            //Ein langer Kommentar
            //Ein langer Kommentar
            //Ein langer Kommentar
            //Ein langer Kommentar

            int r = 0;

            //lange Ausführung
            for (int i = 0; i < Integer.MAX_VALUE / 2; i++) {
                r = i + 1 - 1;
            }
        }

        //Keine überschriebene Methode
        public boolean equals() {
            return true;
        }

        //Überschriebener Akzessor
        public int x() {
            return x + 1;
        }

        //Überschriebener Akzessor
        public int y() {
            return y;
        }

        //Eine Methode
        public void doSomething(int a, int b, int c, int d, int e, int p, int f, int g, int h, int k, String o, double d1) {
            int result = 0;
            for (int i = 0; i < 100; i++) {
                result += i;
                doSomethingElse("a");
            }
        }

        //Eine andere Methode
        private void doSomethingElse(String a) {
            int result2 = 0;
            for (int i = 0; i < 100; i++) {
                result2 += i;
            }
        }

        //Eine andere Methode
        private void doSomethingElse2(String a) {
            int result2 = 0;
            for (int i = 0; i < 100; i++) {
                result2 += i;
            }
        }

        //Eine andere Methode
        private void doSomethingElse3(String a, String b, String c, String f, String k, String s) {
            int result2 = 0;
            for (int i = 0; i < 100; i++) {
                result2 += i;
            }
        }

        //Eine andere Methode
        private void doSomethingElse4(String a) {
            int result2 = 0;
            for (int i = 0; i < 100; i++) {
                result2 += i;
            }
        }

        //Eine andere Methode
        private void doSomethingElse5(String a) {
            int result2 = 0;
            for (int i = 0; i < 100; i++) {
                result2 += i;
            }
        }

        //Eine andere Methode
        private void doSomethingElse6(String a) {
            int result2 = 0;
            for (int i = 0; i < 100; i++) {
                result2 += i;

                //Ein langer Kommentar
                //Ein langer Kommentar
                //Ein langer Kommentar
                //Ein langer Kommentar
                //Ein langer Kommentar
                //Ein langer Kommentar
                //Ein langer Kommentar
                //Ein langer Kommentar
                //Ein langer Kommentar
                //Ein langer Kommentar
                //Ein langer Kommentar
                //Ein langer Kommentar
                //Ein langer Kommentar
                //Ein langer Kommentar
                //Ein langer Kommentar
                //Ein langer Kommentar
                //Ein langer Kommentar
                //Ein langer Kommentar
                //Ein langer Kommentar
                //Ein langer Kommentar
                //Ein langer Kommentar
                //Ein langer Kommentar
                //Ein langer Kommentar
                //Ein langer Kommentar
            }
        }

        //Eine andere Methode
        private void doSomethingElse7(String a) {
            int result2 = 0;
            for (int i = 0; i < 100; i++) {


                result2 += i;
            }
        }

        //Eine andere Methode
        private void doSomethingElse8(String a) {
            int result2 = 0;
            for (int i = 0; i < 100; i++) {
                result2 += i;
            }
        }

        //Eine andere Methode
        private void doSomethingElse9(String a) {
            int result2 = 0;
            for (int i = 0; i < 100; i++) {
                result2 += i;
            }
        }

        //Eine andere Methode
        private void doSomethingElse10(String a) {
            int result2 = 0;
            for (int i = 0; i < 100; i++) {
                result2 += i;
            }
        }

        //Eine andere Methode
        private void doSomethingElse11(String a) {
            int result2 = 0;
            for (int i = 0; i < 100; i++) {
                result2 += i;
            }
        }
    }

    //Ausführung
    // -> JUnit-Tests schlagen fehl - check
    // -> Long Parameter List (doSomething2, doSomething3) - check
    // -> Large Class (12 Felder) - check
    //Leistungseffizienz -> effizient - check
    record Point(int x, int y, int z) {

        private static int a;
        private static int b;
        private static int c;
        private static int a2;
        private static String a3;
        private static String xb;
        private static String xc;
        private static String xd;
        private static String xe;
        private static String xf;
        private static String xg;
        private static String xh;

        public int x() {
            return x + 1;
        }

        public void doSomething1(int a, int b) {
            //do something
        }

        public void doSomething2(int e, int f, String g, Object ab, int er, int z, int a, int b) {
            //do something
        }

        public void doSomething3(String a, String b, String c, String d, String e, String f) {
            //do something
        }

        public void doSomething4() {
            //do something
        }
    }

    //Ausführung
    // -> Kein funktionaler Test - check
    // -> Long Function (doSomething1) - check
    //Leistungseffizienz -> effizient - check
    record Point2(int x, int y, int z) {

        public void doSomething1() {
            //do something
            //Ein langer Kommentar
            //Ein langer Kommentar
            //Ein langer Kommentar
            //Ein langer Kommentar
            //Ein langer Kommentar
            //Ein langer Kommentar
            //Ein langer Kommentar
            //Ein langer Kommentar
            //Ein langer Kommentar
            //Ein langer Kommentar
            //Ein langer Kommentar
            //Ein langer Kommentar
            //Ein langer Kommentar
            //Ein langer Kommentar
            //Ein langer Kommentar
            //Ein langer Kommentar
            //Ein langer Kommentar
            //Ein langer Kommentar
            //Ein langer Kommentar
            //Ein langer Kommentar
            //Ein langer Kommentar
            //Ein langer Kommentar
            //Ein langer Kommentar
            //Ein langer Kommentar
        }

        public void doSomething2() {
            //do something
        }

        public void doSomething3() {
            //do something
        }

        public void doSomething4() {
            //do something
        }
    }
}