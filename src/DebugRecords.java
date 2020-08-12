import java.util.ArrayList;

public class DebugRecords {

    record test(int x, int y, int z){
        public test{
        }

        public boolean equals(Object obj) {
            return true;
        }
    }
}
