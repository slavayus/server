package old.school;

import old.school.annotation.Column;
import old.school.annotation.Entity;
import old.school.annotation.PrimaryKey;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.time.ZonedDateTime;

/**
 * Created by slavik on 30.10.16.
 */
@Entity(tableName = "PEOPLE")
public abstract class Man implements Serializable, Comparable {
    private static final long serialVersionUID = 2;

    @Column(fieldName =  "id",atributeName = "ID", type = "SERIAL PRIMARY KEY")
    @PrimaryKey(columName = "ID")
    private String id;

    @Column(fieldName = "age",atributeName = "AGE", type = "INTEGER CONSTRAINT positive_age CHECK (AGE>0) NOT NULL")
    protected int age;

    @Column(fieldName = "name", atributeName = "NAME", type = "TEXT NOT NULL")
    protected String name;


    @Column(fieldName = "time",atributeName = "CREATE_DATE", type = "TIMESTAMP")
    private ZonedDateTime time;


    public Man(String name) {
        setName(name);
    }

    public Man(int age, String name) {
        setAge(age);
        setName(name);
    }

    protected Man() {
    }

    public void setTime(ZonedDateTime zonedDateTime) {
        this.time = zonedDateTime;
    }


    public boolean setName(String name) {
        String wrongChars = "qwertyuiopasdfghjklzxcvbnmйцукенгшщзхъфывапролдячсмитьбюё";

        for (int i = 0; i < name.length(); i++) {
            if (!wrongChars.contains(name.substring(i, i + 1).toLowerCase())) {
                return false;
            }
        }

        this.name = name;
        return true;
    }

    public String getName() {
        return name;
    }

    public boolean setAge(int age) {
        if (age < 0) {
            return false;
        }
        this.age = age;
        return true;
    }

    public int getAge() {
        return age;
    }

    public ZonedDateTime getTime() {
        return time;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public int compareTo(@NotNull Object o) {
       return this.getAge() - ((Man)o).getAge();
    }
}
