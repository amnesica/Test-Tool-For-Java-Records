package generated;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/**
 * Automatisch generierte Testklasse mit JUnit-Tests für den Record "Point2"
 */
public class Point2Test {

    //zu testender Record
    record Point2(int x, int y, int z, int a) {
        public Point2 {
            if (x > a) {
                try {
                    throw new Exception();
                } catch (Exception e) {
                }
            }
        }

        public int a() {
            return a + 100;
        }
    }


    //zu testender Record für Negativtest
    record Point2NegativTest(int x, int y, int z, int a) {
        public Point2NegativTest {
            if (x > a) {
                try {
                    throw new Exception();
                } catch (Exception e) {
                }
            }
        }

        public int a() {
            return a + 100;
        }

        public int x() {
            return x + 5;
        }
    }


    //Testfall Equals - Positivtest
    @Test
    public void testeFunktionalitaetEqualsPositivtest() {
        Point2 Point20 = new Point2(1, 2147483647, -2147483648, -1);
        Point2 Point20copy = new Point2(Point20.x(), Point20.y(), Point20.z(), Point20.a());

        Point2 Point21 = new Point2(0, 2147483646, -2147483647, 1);
        Point2 Point21copy = new Point2(Point21.x(), Point21.y(), Point21.z(), Point21.a());

        Point2 Point22 = new Point2(2147483647, -2147483648, -1, 0);
        Point2 Point22copy = new Point2(Point22.x(), Point22.y(), Point22.z(), Point22.a());

        Point2 Point23 = new Point2(2147483646, -2147483647, 1, 2147483647);
        Point2 Point23copy = new Point2(Point23.x(), Point23.y(), Point23.z(), Point23.a());

        assertTrue(Point20.equals(Point20copy));
        assertTrue(Point21.equals(Point21copy));
        assertTrue(Point22.equals(Point22copy));
        assertTrue(Point23.equals(Point23copy));

    }


    //Testfall Equals - Negativtest
    @Test
    public void testeFunktionalitaetEqualsNegativtest() {
        Point2NegativTest Point2NegativTest0 = new Point2NegativTest(1, 2147483647, -2147483648, -1);
        Point2NegativTest Point2NegativTest0copy = new Point2NegativTest(Point2NegativTest0.x(), Point2NegativTest0.y(), Point2NegativTest0.z(), Point2NegativTest0.a());

        Point2NegativTest Point2NegativTest1 = new Point2NegativTest(0, 2147483646, -2147483647, 1);
        Point2NegativTest Point2NegativTest1copy = new Point2NegativTest(Point2NegativTest1.x(), Point2NegativTest1.y(), Point2NegativTest1.z(), Point2NegativTest1.a());

        Point2NegativTest Point2NegativTest2 = new Point2NegativTest(2147483647, -2147483648, -1, 0);
        Point2NegativTest Point2NegativTest2copy = new Point2NegativTest(Point2NegativTest2.x(), Point2NegativTest2.y(), Point2NegativTest2.z(), Point2NegativTest2.a());

        Point2NegativTest Point2NegativTest3 = new Point2NegativTest(2147483646, -2147483647, 1, 2147483647);
        Point2NegativTest Point2NegativTest3copy = new Point2NegativTest(Point2NegativTest3.x(), Point2NegativTest3.y(), Point2NegativTest3.z(), Point2NegativTest3.a());

        assertFalse(Point2NegativTest0.equals(Point2NegativTest0copy));
        assertFalse(Point2NegativTest1.equals(Point2NegativTest1copy));
        assertFalse(Point2NegativTest2.equals(Point2NegativTest2copy));
        assertFalse(Point2NegativTest3.equals(Point2NegativTest3copy));
    }


    //Testfall HashCode - Positivtest
    @Test
    public void testeFunktionalitaetHashCodePositivtest() {
        Point2 Point20 = new Point2(1, 2147483647, -2147483648, -1);
        Point2 Point20copy = new Point2(Point20.x(), Point20.y(), Point20.z(), Point20.a());

        Point2 Point21 = new Point2(0, 2147483646, -2147483647, 1);
        Point2 Point21copy = new Point2(Point21.x(), Point21.y(), Point21.z(), Point21.a());

        Point2 Point22 = new Point2(2147483647, -2147483648, -1, 0);
        Point2 Point22copy = new Point2(Point22.x(), Point22.y(), Point22.z(), Point22.a());

        Point2 Point23 = new Point2(2147483646, -2147483647, 1, 2147483647);
        Point2 Point23copy = new Point2(Point23.x(), Point23.y(), Point23.z(), Point23.a());

        assertTrue(Point20.equals(Point20copy));
        assertTrue(Point21.equals(Point21copy));
        assertTrue(Point22.equals(Point22copy));
        assertTrue(Point23.equals(Point23copy));

        assertEquals(Point20.hashCode(), Point20copy.hashCode());
        assertEquals(Point21.hashCode(), Point21copy.hashCode());
        assertEquals(Point22.hashCode(), Point22copy.hashCode());
        assertEquals(Point23.hashCode(), Point23copy.hashCode());
    }


    //Testfall ToString - Positivtest
    @Test
    public void testeFunktionalitaetToStringPositivtest() {
        Point2 Point20 = new Point2(1, 2147483647, -2147483648, -1);
        Point2 Point20copy = new Point2(Point20.x(), Point20.y(), Point20.z(), Point20.a());

        Point2 Point21 = new Point2(0, 2147483646, -2147483647, 1);
        Point2 Point21copy = new Point2(Point21.x(), Point21.y(), Point21.z(), Point21.a());

        Point2 Point22 = new Point2(2147483647, -2147483648, -1, 0);
        Point2 Point22copy = new Point2(Point22.x(), Point22.y(), Point22.z(), Point22.a());

        Point2 Point23 = new Point2(2147483646, -2147483647, 1, 2147483647);
        Point2 Point23copy = new Point2(Point23.x(), Point23.y(), Point23.z(), Point23.a());

        assertTrue(Point20.equals(Point20copy));
        assertTrue(Point21.equals(Point21copy));
        assertTrue(Point22.equals(Point22copy));
        assertTrue(Point23.equals(Point23copy));

        assertEquals(Point20.toString(), Point20copy.toString());
        assertEquals(Point21.toString(), Point21copy.toString());
        assertEquals(Point22.toString(), Point22copy.toString());
        assertEquals(Point23.toString(), Point23copy.toString());
    }
}

