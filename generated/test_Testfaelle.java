import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class test_Testfaelle {

    //zu testender Record
    record test(int x, int y, int z) {
        public test {}

        public int x() {
            return x + 1;
        }

        public boolean equals(Object obj) {
            return true;
        }
    }

    @Test
    public void testeFunktionalitaetEqualsPositivtest(){
        record test0 = new test(1,2147483647,-2147483648);
        record test0copy = new test(test0.x(), test0.y(), test0.z());

        record test1 = new test(-1,0,2147483646);
        record test1copy = new test(test1.x(), test1.y(), test1.z());

        record test2 = new test(-2147483647,1,2147483647);
        record test2copy = new test(test2.x(), test2.y(), test2.z());

        assertTrue(test0.equals(test0copy));
        assertTrue(test1.equals(test1copy));
        assertTrue(test2.equals(test2copy));
    }
}



