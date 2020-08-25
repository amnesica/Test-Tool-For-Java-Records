import java.util.ArrayList;

public class TestRecords {
    //keine Ausführung - check
    record test1() {
    }

    //keine Ausführung - check
    record test2(int x) {
    }

    //Ausführung -> alle JUnit-Tests laufen durch - check
    record test3(int x) {
        public int x() {
            return x;
        }
    }

    //Ausführung -> JUnit-Tests schlagen fehl - check
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

    //Ausführung -> Fehlermeldung String
    record test7(String s, int x) { -check

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

    //Ausführung - check
    // -> Fehlermeldung Objekte -> keine JUnit-Tests
    // -> String -> kein nicht-funktionaler Test
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


                //langer Umbruch















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

    //Ausführung - check
    // -> JUnit-Tests schlagen fehl
    // -> Long Parameter List (12 Parameter Methode, 6 Parameter Klasse)
    // -> Large Class (179 LOC, 11 Felder)
    // -> Long Function (somethingElse6, somethingElse7, Konstruktor)
    // -> Leistungseffizienz: ineffizient
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

            int r = 0;

            //lange Ausführung
            for (int i = 0; i < Integer.MAX_VALUE / 2; i++) {
                r = i + 1 - 1;
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


                //langer Umbruch










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

    //Ausführung - check
    // -> JUnit-Tests schlagen fehl
    // -> Long Parameter List (doSomething2, doSomething3)
    // -> Large Class (12 Felder)
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

    //Ausführung - check
    // -> Kein funktionaler Test
    // -> Long Function (doSomething1)
    record Point2(int x, int y, int z) {

        public void doSomething1() {
            //do something


            //long method











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