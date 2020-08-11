import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Einstiegspunkt des Test-Tools.
 */
public class Main {

    public static void main(String[] args){
        //get Dateiname von *.java-Datei, die Records enthält
        if(args != null && args[0] != null && !args[0].isEmpty() && args[0].contains(".java")){
            String filename = args[0];
            System.out.println("Dateiname der Test-Datei:" + filename); //test only

            //TODO starte Test-Tool
            startTestTool(filename);
        }
    }

    private static void startTestTool(String filename) {

        //Pfad zur Datei mit den zu testenden Records
        Path path = Paths.get(System.getProperty("user.dir") + "/src/" + filename);

        try {
            //extrahiere Inhalt der Datei in String
            String fileContent =  new String(Files.readAllBytes(path));

            if(!fileContent.isEmpty() && fileContent.contains("public class") && fileContent.contains("record")){
                //extrahiere Records
                ExtractedRecord extractedRecord;

                int entryPointRecord = 0;
                int endOfComponentList = 0;

                //begin of record regex (record\s\w+\()
                String beginOfRecordPatternRegex = "(record\\s\\w+\\()";

                Pattern r = Pattern.compile(beginOfRecordPatternRegex);
                Matcher m = r.matcher(fileContent);
                if(m.find()){
                    extractedRecord = new ExtractedRecord();
                    entryPointRecord = m.end();
                    System.out.println("Beginning of record is: " + m.group());

                    //schaue auf Komponenten des Records
                    for(int i = entryPointRecord; i < fileContent.length(); i++){
                        if(fileContent.charAt(i) == ')'){
                            //Ende der Parameterliste erreicht
                            endOfComponentList = i;
                        }
                    }

                    //extrahiere Komponenten-Liste

                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
