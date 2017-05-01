package old.school;

/**
 * Created by slavik on 02.12.16.
 */
public interface Missable {
    void setMiss(Missable x);

    String getName();

    Missable getMiss(int x);

    int getAge();
}
