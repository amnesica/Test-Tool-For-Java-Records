package anwendungsfaelle;

import java.util.ArrayList;

/**
 * Datei mit Record für vierten Anwendungsfall
 * - nicht nur Integer-Werte (Referenzobjekt)
 * - automatisch generierte Methode ueberschreiben
 * -> keine Tests
 * -> Warnung wegen Referenzobjekt als Komponente
 */
public class Anwendungsfall4 {
    record test4(int x, ArrayList<String>listStrings, int z) {
        //Überschriebene Methode
        public int x() {
            return x;
        }
    }
}