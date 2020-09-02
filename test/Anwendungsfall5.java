package anwendungsfaelle;

/**
 * Datei mit Record fuer fünften Durchlauf
 * - nur Integer-Werte
 * - automatisch generierte equals-Methode ueberschreiben
 * -> Test des Negativtests
 */
public class Anwendungsfall5 {
    record test5(int x, int y) {
        //Überschriebene equals-Methode
        public boolean equals(Object o) {
            return true;
        }
    }
}