package ToTest;

import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * Klasse für einen Record, der vom Programm aus der Datei herausgelesen wurde und getestet werden soll.
 */
public class RecordToTest {
    //Name des Records
    private String name;

    //Bestandteile des Records
    private String recordHeader;
    private String recordBody;

    //Kompletter Record
    private String recordFull;

    //Index direkt vor dem Finden eines Records
    private int indexEntryPointRecordBeforeMatch = 0;

    //Index direkt nach dem Finden eines Records
    private int indexEntryPointRecordAfterMatch = 0;

    //Index des Endes der Komponenten-Liste
    private int indexEndOfComponentList = 0;

    //Boolean, ob Record getestet werden soll
    private boolean recordShouldBeTested;

    //Komponenten des Records
    private LinkedHashMap<Object, Object> componentMap;
    private ArrayList<String> listFoundObjects;
    private ArrayList<String> listFoundDataTypes;

    //Anzahl der Komponenten
    private int amountComponents;

    //Ueberschriebene Methoden des Records
    private ArrayList<String> listOverriddenMethods;

    //Liste mit allen Methoden im Body des zu testenden Records mit Start- und
    // Endindex als Parameter
    private ArrayList<MethodToTest> listAllDeclaredMethods;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LinkedHashMap<Object, Object> getComponentMap() {
        return componentMap;
    }

    public void setComponentMap(LinkedHashMap<Object, Object> componentMap) {
        this.componentMap = componentMap;
    }

    public String getRecordHeader() {
        return recordHeader;
    }

    public void setRecordHeader(String recordHeader) {
        this.recordHeader = recordHeader;
    }

    public String getRecordBody() {
        return recordBody;
    }

    public void setRecordBody(String recordBody) {
        this.recordBody = recordBody;
    }

    public String getRecordFull() {
        return recordFull;
    }

    public void setRecordFull(String recordFull) {
        this.recordFull = recordFull;
    }

    public ArrayList<String> getListOverriddenMethods() {
        return listOverriddenMethods;
    }

    public void setListOverriddenMethods(ArrayList<String> listOverriddenMethods) {
        this.listOverriddenMethods = listOverriddenMethods;
    }

    public int getAmountComponents() {
        return amountComponents;
    }

    public void setAmountComponents(int amountComponents) {
        this.amountComponents = amountComponents;
    }

    public ArrayList<String> getListFoundObjects() {
        return listFoundObjects;
    }

    public void setListFoundObjects(ArrayList<String> listFoundObjects) {
        this.listFoundObjects = listFoundObjects;
    }

    public ArrayList<String> getListFoundDataTypes() {
        return listFoundDataTypes;
    }

    public void setListFoundDataTypes(ArrayList<String> listFoundDataTypes) {
        this.listFoundDataTypes = listFoundDataTypes;
    }

    public boolean isRecordShouldBeTested() {
        return recordShouldBeTested;
    }

    public void setRecordShouldBeTested(boolean recordShouldBeTested) {
        this.recordShouldBeTested = recordShouldBeTested;
    }

    public int getIndexEntryPointRecordBeforeMatch() {
        return indexEntryPointRecordBeforeMatch;
    }

    public void setIndexEntryPointRecordBeforeMatch(int indexEntryPointRecordBeforeMatch) {
        this.indexEntryPointRecordBeforeMatch = indexEntryPointRecordBeforeMatch;
    }

    public int getIndexEntryPointRecordAfterMatch() {
        return indexEntryPointRecordAfterMatch;
    }

    public void setIndexEntryPointRecordAfterMatch(int indexEntryPointRecordAfterMatch) {
        this.indexEntryPointRecordAfterMatch = indexEntryPointRecordAfterMatch;
    }

    public int getIndexEndOfComponentList() {
        return indexEndOfComponentList;
    }

    public void setIndexEndOfComponentList(int indexEndOfComponentList) {
        this.indexEndOfComponentList = indexEndOfComponentList;
    }

    public ArrayList<MethodToTest> getListAllDeclaredMethods() {
        return listAllDeclaredMethods;
    }

    public void setListAllDeclaredMethods(ArrayList<MethodToTest> listAllDeclaredMethods) {
        this.listAllDeclaredMethods = listAllDeclaredMethods;
    }
}