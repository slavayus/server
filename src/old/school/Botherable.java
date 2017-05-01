package old.school;

/**
 * Created by slavik on 02.12.16.
 */
public interface Botherable {
    void setBother(Botherable x);

    Botherable getBother(int x);

    String getName();

    int getAge();
}