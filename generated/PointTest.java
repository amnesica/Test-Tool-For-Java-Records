package generated;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/**
 * Automatisch generierte Testklasse mit JUnit-Tests für den Record "Point"
 */
public class PointTest{

//zu testender Record
record Point(int x, int y, int z, int a, int b) {
    
        public int x(){
            return x;
        }
        
        public void doSomething1(){
        //do something
        }
        
        public void doSomething2(){
        //do something
        }
        
        public void doSomething3(){
        //do something
        }
        
        public void doSomething4(){
        //do something
        }
    }


//zu testender Record für Negativtest
record PointNegativTest(int x, int y, int z, int a, int b) {
    
        public int x(){
            return x;
        }
        
        public void doSomething1(){
        //do something
        }
        
        public void doSomething2(){
        //do something
        }
        
        public void doSomething3(){
        //do something
        }
        
        public void doSomething4(){
        //do something
        }
    public int y(){
return y+ 5;
}
}


//Testfall Equals - Positivtest
@Test
public void testeFunktionalitaetEqualsPositivtest(){Point Point0 = new Point(1,2147483647,-2147483648,-1,0);
Point Point0copy = new Point(Point0.x(),Point0.y(),Point0.z(),Point0.a(),Point0.b());

Point Point1 = new Point(2147483646,-2147483647,1,2147483647,-2147483648);
Point Point1copy = new Point(Point1.x(),Point1.y(),Point1.z(),Point1.a(),Point1.b());

assertTrue(Point0.equals(Point0copy));
assertTrue(Point1.equals(Point1copy));

}



//Testfall Equals - Negativtest
@Test
public void testeFunktionalitaetEqualsNegativtest(){PointNegativTest PointNegativTest0 = new PointNegativTest(1,2147483647,-2147483648,-1,0);
PointNegativTest PointNegativTest0copy = new PointNegativTest(PointNegativTest0.x(),PointNegativTest0.y(),PointNegativTest0.z(),PointNegativTest0.a(),PointNegativTest0.b());

PointNegativTest PointNegativTest1 = new PointNegativTest(2147483646,-2147483647,1,2147483647,-2147483648);
PointNegativTest PointNegativTest1copy = new PointNegativTest(PointNegativTest1.x(),PointNegativTest1.y(),PointNegativTest1.z(),PointNegativTest1.a(),PointNegativTest1.b());

assertFalse(PointNegativTest0.equals(PointNegativTest0copy));
assertFalse(PointNegativTest1.equals(PointNegativTest1copy));
}



//Testfall HashCode - Positivtest
@Test
public void testeFunktionalitaetHashCodePositivtest(){Point Point0 = new Point(1,2147483647,-2147483648,-1,0);
Point Point0copy = new Point(Point0.x(),Point0.y(),Point0.z(),Point0.a(),Point0.b());

Point Point1 = new Point(2147483646,-2147483647,1,2147483647,-2147483648);
Point Point1copy = new Point(Point1.x(),Point1.y(),Point1.z(),Point1.a(),Point1.b());

assertTrue(Point0.equals(Point0copy));
assertTrue(Point1.equals(Point1copy));

assertEquals(Point0.hashCode(),Point0copy.hashCode());
assertEquals(Point1.hashCode(),Point1copy.hashCode());
}



//Testfall ToString - Positivtest
@Test
public void testeFunktionalitaetToStringPositivtest(){Point Point0 = new Point(1,2147483647,-2147483648,-1,0);
Point Point0copy = new Point(Point0.x(),Point0.y(),Point0.z(),Point0.a(),Point0.b());

Point Point1 = new Point(2147483646,-2147483647,1,2147483647,-2147483648);
Point Point1copy = new Point(Point1.x(),Point1.y(),Point1.z(),Point1.a(),Point1.b());

assertTrue(Point0.equals(Point0copy));
assertTrue(Point1.equals(Point1copy));

assertEquals(Point0.toString(),Point0copy.toString());
assertEquals(Point1.toString(),Point1copy.toString());
}
}

