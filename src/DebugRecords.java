import java.util.ArrayList;

public class DebugRecords {

    record test(int x, int y, int z){
        public test{
        }

        public int x(){
            return x+1;
        }

        public boolean equals(Object obj) {
            return true;
        }
    }
}
