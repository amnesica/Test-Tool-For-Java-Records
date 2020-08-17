package generated;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/**
 * Automatisch generierte Testklasse mit JUnit-Tests für den Record "Point"
 */
public class PointTest {

    //zu testender Record
    record Point(int x, int y, int z) {
        public int x() {
            return x;
        }

        public void doSomething() {
            //do something
        }
    }


    //zu testender Record für Negativtest
    record PointNegativTest(int x, int y, int z) {
        public int x() {
            return x;
        }

        public void doSomething() {
            //do something
        }

        public int y() {
            return y + 5;
        }
    }


    //Testfall Equals - Positivtest
    @Test
    public void testeFunktionalitaetEqualsPositivtest() {
        Point Point0 = new Point(1, 2147483647, -2147483648);
        Point Point0copy = new Point(Point0.x(), Point0.y(), Point0.z());

        Point Point1 = new Point(-1, 0, 2147483646);
        Point Point1copy = new Point(Point1.x(), Point1.y(), Point1.z());

        Point Point2 = new Point(-2147483647, 1, 2147483647);
        Point Point2copy = new Point(Point2.x(), Point2.y(), Point2.z());

        assertTrue(Point0.equals(Point0copy));
        assertTrue(Point1.equals(Point1copy));
        assertTrue(Point2.equals(Point2copy));

    }


    //Testfall Equals - Negativtest
    @Test
    public void testeFunktionalitaetEqualsNegativtest() {
        PointNegativTest PointNegativTest0 = new PointNegativTest(1, 2147483647, -2147483648);
        PointNegativTest PointNegativTest0copy = new PointNegativTest(PointNegativTest0.x(), PointNegativTest0.y(), PointNegativTest0.z());

        PointNegativTest PointNegativTest1 = new PointNegativTest(-1, 0, 2147483646);
        PointNegativTest PointNegativTest1copy = new PointNegativTest(PointNegativTest1.x(), PointNegativTest1.y(), PointNegativTest1.z());

        PointNegativTest PointNegativTest2 = new PointNegativTest(-2147483647, 1, 2147483647);
        PointNegativTest PointNegativTest2copy = new PointNegativTest(PointNegativTest2.x(), PointNegativTest2.y(), PointNegativTest2.z());

        assertFalse(PointNegativTest0.equals(PointNegativTest0copy));
        assertFalse(PointNegativTest1.equals(PointNegativTest1copy));
        assertFalse(PointNegativTest2.equals(PointNegativTest2copy));
    }


    //Testfall HashCode - Positivtest
    @Test
    public void testeFunktionalitaetHashCodePositivtest() {
        Point Point0 = new Point(1, 2147483647, -2147483648);
        Point Point0copy = new Point(Point0.x(), Point0.y(), Point0.z());

        Point Point1 = new Point(-1, 0, 2147483646);
        Point Point1copy = new Point(Point1.x(), Point1.y(), Point1.z());

        Point Point2 = new Point(-2147483647, 1, 2147483647);
        Point Point2copy = new Point(Point2.x(), Point2.y(), Point2.z());

        assertTrue(Point0.equals(Point0copy));
        assertTrue(Point1.equals(Point1copy));
        assertTrue(Point2.equals(Point2copy));

        assertEquals(Point0.hashCode(), Point0copy.hashCode());
        assertEquals(Point1.hashCode(), Point1copy.hashCode());
        assertEquals(Point2.hashCode(), Point2copy.hashCode());
    }


    //Testfall ToString - Positivtest
    @Test
    public void testeFunktionalitaetToStringPositivtest() {
        Point Point0 = new Point(1, 2147483647, -2147483648);
        Point Point0copy = new Point(Point0.x(), Point0.y(), Point0.z());

        Point Point1 = new Point(-1, 0, 2147483646);
        Point Point1copy = new Point(Point1.x(), Point1.y(), Point1.z());

        Point Point2 = new Point(-2147483647, 1, 2147483647);
        Point Point2copy = new Point(Point2.x(), Point2.y(), Point2.z());

        assertTrue(Point0.equals(Point0copy));
        assertTrue(Point1.equals(Point1copy));
        assertTrue(Point2.equals(Point2copy));

        assertEquals(Point0.toString(), Point0copy.toString());
        assertEquals(Point1.toString(), Point1copy.toString());
        assertEquals(Point2.toString(), Point2copy.toString());
    }
}

