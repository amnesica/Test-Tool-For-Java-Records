package anwendungsfaelle;

/**
 * Datei mit Record für zweiten Anwendungsfall
 * - nur Integer-Werte
 * - Konstruktor ueberschreiben (Invariante nicht verletzen)
 * - Ineffiziente Implementierung (wegen Leistungseffizienztest)
 * - zu lange Methode (18 LOC) mit zu vielen Parametern (10 Parameter) für Wartbarkeitstest
 * -> Funktionalitätstest, Leistungseffizienztest, Wartbarkeitstest
 */
public class Anwendungsfall2 {
    record test2(int x, int y, int z) {
        //Überschriebener Konstruktor
        public test2 {
            if (x > y) {
                try {
                    throw new Exception();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            //ineffiziente Methode
            doSomething(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        }

        //Eine Methode
        private void doSomething(int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7, int i8, int i9) {
            int r = i + i1 + i2 + i3 + i4 + i5 + i6 + i7 + i8 + i9;

            //lange Ausführung
            for (int j = 0; j < Integer.MAX_VALUE / 2; j++) {
                r = j + 1 - 1;
            }

            //Aufruf einer anderen Methode
            if (r > 100) {
                doSomethingElse("a");
            } else {
                r = 1;
                int a = 1;
                int b = 1;
                int c = 1;
                int d = 1;
            }
        }

        //Eine andere Methode
        private void doSomethingElse(String a) {
            int result2 = 0;

            for (int i = 0; i < 100; i++) {
                result2 += i;
            }
        }
    }
}
