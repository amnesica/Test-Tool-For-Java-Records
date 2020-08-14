import java.util.ArrayList;

public class DebugRecords {

    record Point(int x, int y, int z){
        public int y() {
            return y + 1;
        }
    }
}
