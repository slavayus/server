package old.school;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * Created by slavik on 30.10.16.
 */
public abstract class Man implements Serializable{
    private static final long serialVersionUID =2;
    protected String name;
    protected int age;
    private  ZonedDateTime time;

    public Man(String name) {
        setName(name);
    }

    public Man(int age, String name) {
        setAge(age);
        setName(name);
    }

    protected Man() {
    }

    public void setTime(ZonedDateTime zonedDateTime){
        this.time = zonedDateTime;
    }


    public boolean setName(String name) {
        String wrongChars = "qwertyuiopasdfghjklzxcvbnmйцукенгшщзхъфывапролдячсмитьбюё";

        for(int i=0; i<name.length(); i++){
            if(!wrongChars.contains(name.substring(i,i+1).toLowerCase())){
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
        if(age<0){
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
}