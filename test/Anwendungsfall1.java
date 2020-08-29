package anwendungsfaelle;

/**
 * Datei mit Record für ersten Durchlauf
 * - nur Integer-Werte
 * - automatisch generierte Methode ueberschreiben (Invariante verletzen)
 * -> Funktionalitaetstest, Leistungseffizienztest, Wartbarkeitstest
 */
public class Anwendungsfall1 {
    record test1(int x, int y, int z) {
        //Ueberschriebene Methode
        public int x() {
            return x + 1;
        }

        //Überschriebene equals-Methode zum Testen des Negativtests
        public boolean equals(Object o){
            return true;
        }
    }
}
