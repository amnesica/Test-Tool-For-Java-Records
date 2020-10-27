# Test-Tool-For-Java-Records
Tool which ensures the quality of records in Java. The tool is part of my Bachelor thesis. 

In the bachelor thesis the new type *Record* in Java was analyzed which is an implementation of a data class in Java. For information on records in Java see the documentation [here](https://docs.oracle.com/en/java/javase/14/docs/api/java.base/java/lang/Record.html). 

To ensure the quality of such records, this tool was created that tests records which contain only integer values in terms of performance efficiency and maintainability. If automatically generated methods in the record are overwritten, the functionality is tested as well. For this purpose JUnit tests are automatically created for the invariants of a record.

When testing for performance efficiency, the implementation of the record is classified as efficient or inefficient. For maintainability, the record is checked for the code smells "Large Class", "Long Function" and "Long Parameter List". 

## Starting the tool
To start the tool you can either use the JAR file or compile and start the program from the command line. Java 14 must be installed. The tool takes a valid Java file with records as input.

### Using the JAR file 
```sh
java --enable-preview -jar Test-Tool-For-Java-Records.jar <PathToClassWithRecords>
```

### Compile and start the program via command line
```sh
javac --enable-preview -source 14 *.java
java --enable-preview Main <PathToClassWithRecords>
```

## Screenshot
<div style="display:flex;">
<img alt="Screenshot" src="/static/Screenshot.png" width="23%">
</div>


