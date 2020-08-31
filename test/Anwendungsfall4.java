package anwendungsfaelle;

import java.util.ArrayList;

/**
 * Datei mit Record fuer vierten Durchlauf
 * - nicht nur Integer-Werte (Referenzobjekt)
 * - automatisch generierte Methode ueberschreiben
 * -> keine Tests
 * -> Warnung wegen Referenzobjekt als Komponente
 */
public class Anwendungsfall4 {
    record test4(int x, ArrayList<String>listStrings, int z) {
        //Ueberschriebene Methode
        public int x() {
            return x;
        }
    }
}