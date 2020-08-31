package anwendungsfaelle;

/**
 * Datei mit Record fuer zweiten Durchlauf
 * - nur Integer-Werte
 * - Konstruktor ueberschreiben (Invariante nicht verletzen)
 * - Ineffiziente Implementierung (wegen Leistungseffizienztest)
 * - zu lange Methode (24 LOC) mit zu vielen Parametern (10 Parameter) fuer Wartbarkeitstest
 * -> Funktionalitaetstest, Leistungseffizienztest, Wartbarkeitstest
 */
public class Anwendungsfall2 {
    record test2(int x, int y, int z) {
        //Ueberschriebener Konstruktor
        public test2 {
            if (x > y) {
                try {
                    throw new Exception();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            //lange dauernde Methode mit Bubblesort-Algorithmus
            doSomething(10, 9, 8, 7, 6, 5, 4, 3, 2, 1);
        }

        //Bubblesort mit 10 Parametern
        private void doSomething(int x, int y, int z, int u, int v, int a, int b, int c, int d, int e) {
            //mache bubblesort mit Komponenten
            System.err.println("Original " + x + " " + y + " " + z + " " + u + " " + v + " " + a + " " + b + " " + c + " " + d + " " + e);

            //Speichere Komponenten in int array
            int[] list = {x, y, z, u, v, a, b, c, d, e};

            //wende bubbleSort auf die Komponenten an
            //Quelle: https://stackoverflow.com/questions/11644858/bubblesort-implementation
            for (int i = 0; i < list.length; i++) {
                for (int j = i + 1; j < list.length; j++) {
                    if (list[i] > list[j]) {
                        int temp = list[i];
                        list[i] = list[j];
                        list[j] = temp;
                    }
                }
            }

            System.err.println("Nach bubblesort:");
            for (int sortedValue : list) {
                System.err.println(sortedValue);
            }
        }
    }
}
