package ToTest;

import java.util.Objects;

/**
 * Klasse, die eine Methode innerhalb eines Records repräsentiert. Hier werden Name der Methode, sowie die Indizes
 * der Methode innerhalb des Records sowie die komplette Methode als String gespeichert
 */
public class MethodToTest {

    //Name der Methode
    private String name;

    //StartIndex der Methode innerhalb des Records
    private int startIndex;

    //EndIndex der Methode innerhalb des Records
    private int endIndex;

    //Komplette Methode als String
    private String fullMethod;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getStartIndex() {
        return startIndex;
    }

    public void setStartIndex(int startIndex) {
        this.startIndex = startIndex;
    }

    public int getEndIndex() {
        return endIndex;
    }

    public void setEndIndex(int endIndex) {
        this.endIndex = endIndex;
    }

    public String getFullMethod() {
        return fullMethod;
    }

    public void setFullMethod(String fullMethod) {
        this.fullMethod = fullMethod;
    }

    @Override
    public String toString() {
        return "MethodToTest{" +
                "name='" + name + '\'' +
                ", startIndex=" + startIndex +
                ", endIndex=" + endIndex +
                ", content='" + fullMethod + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MethodToTest that = (MethodToTest) o;
        return startIndex == that.startIndex &&
                endIndex == that.endIndex &&
                Objects.equals(name, that.name) &&
                Objects.equals(fullMethod, that.fullMethod);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, startIndex, endIndex, fullMethod);
    }
}
