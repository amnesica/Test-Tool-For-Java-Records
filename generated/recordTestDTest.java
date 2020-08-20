package generated;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Automatisch generierte Testklasse mit JUnit-Tests für den Record "recordTestD"
 */
public class recordTestDTest {

    //Testfall Equals - Positivtest
    @Test
    public void testeFunktionalitaetEqualsPositivtest() {
        recordTestD recordTestD0 = new recordTestD(1, 2147483647, -2147483648, -1, 0, 2147483646, -2147483647, 1, 2147483647, -2147483648);
        recordTestD recordTestD0copy = new recordTestD(recordTestD0.x(), recordTestD0.y(), recordTestD0.z(), recordTestD0.u(), recordTestD0.v(), recordTestD0.a(), recordTestD0.b(), recordTestD0.c(), recordTestD0.d(), recordTestD0.e());

        assertTrue(recordTestD0.equals(recordTestD0copy));

    }

    //Testfall Equals - Negativtest
    @Test
    public void testeFunktionalitaetEqualsNegativtest() {
        recordTestDNegativTest recordTestDNegativTest0 = new recordTestDNegativTest(1, 2147483647, -2147483648, -1, 0, 2147483646, -2147483647, 1, 2147483647, -2147483648);
        recordTestDNegativTest recordTestDNegativTest0copy = new recordTestDNegativTest(recordTestDNegativTest0.x(), recordTestDNegativTest0.y(), recordTestDNegativTest0.z(), recordTestDNegativTest0.u(), recordTestDNegativTest0.v(), recordTestDNegativTest0.a(), recordTestDNegativTest0.b(), recordTestDNegativTest0.c(), recordTestDNegativTest0.d(), recordTestDNegativTest0.e());

        assertFalse(recordTestDNegativTest0.equals(recordTestDNegativTest0copy));
    }

    //Testfall HashCode - Positivtest
    @Test
    public void testeFunktionalitaetHashCodePositivtest() {
        recordTestD recordTestD0 = new recordTestD(1, 2147483647, -2147483648, -1, 0, 2147483646, -2147483647, 1, 2147483647, -2147483648);
        recordTestD recordTestD0copy = new recordTestD(recordTestD0.x(), recordTestD0.y(), recordTestD0.z(), recordTestD0.u(), recordTestD0.v(), recordTestD0.a(), recordTestD0.b(), recordTestD0.c(), recordTestD0.d(), recordTestD0.e());

        assertTrue(recordTestD0.equals(recordTestD0copy));

        assertEquals(recordTestD0.hashCode(), recordTestD0copy.hashCode());
    }

    //Testfall ToString - Positivtest
    @Test
    public void testeFunktionalitaetToStringPositivtest() {
        recordTestD recordTestD0 = new recordTestD(1, 2147483647, -2147483648, -1, 0, 2147483646, -2147483647, 1, 2147483647, -2147483648);
        recordTestD recordTestD0copy = new recordTestD(recordTestD0.x(), recordTestD0.y(), recordTestD0.z(), recordTestD0.u(), recordTestD0.v(), recordTestD0.a(), recordTestD0.b(), recordTestD0.c(), recordTestD0.d(), recordTestD0.e());

        assertTrue(recordTestD0.equals(recordTestD0copy));

        assertEquals(recordTestD0.toString(), recordTestD0copy.toString());
    }

    //zu testender Record
    record recordTestD(int x, int y, int z, int u, int v, int a, int b, int c, int d, int e) {

        public recordTestD {
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

        public int x() {
            return x + 1;
        }
    }

    //zu testender Record für Negativtest
    record recordTestDNegativTest(int x, int y, int z, int u, int v, int a, int b, int c, int d, int e) {

        public recordTestDNegativTest {
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

        public int x() {
            return x + 1;
        }

        public int y() {
            return y + 5;
        }
    }
}

