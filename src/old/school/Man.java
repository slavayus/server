package old.school;

import java.io.Serializable;

/**
 * Created by slavik on 30.10.16.
 */
public abstract class Man implements Serializable{
    protected String name;
    protected int age;

    protected Man(String name) {
        setName(name);
    }

    protected Man(int age, String name) {
        setAge(age);
        setName(name);
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
}