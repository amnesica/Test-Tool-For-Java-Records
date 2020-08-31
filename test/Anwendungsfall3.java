package anwendungsfaelle;

/**
 * Datei mit Record fuer dritten Durchlauf
 * - nur Integer-Werte
 * - keine ueberschriebenen Konstruktoren/Methoden
 * -> Leistungseffizienztest, Wartbarkeitstest
 */
public class Anwendungsfall3 {
    record test3(int x, int y, int z) {
        //Eine Methode
        public int doSomething(){
            return x + y + z;
        }
    }
}
