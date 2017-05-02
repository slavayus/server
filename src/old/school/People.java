package old.school;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by slavik on 30.10.16.
 */
public class People extends Man implements Botherable, Missable, Chatable,Comparable {
    private List<Missable> miss = new ArrayList<>();
    private List<Botherable> bother = new ArrayList<>();
    private List<Chatable> chat = new ArrayList<>();

    public People(int age , String name) {
        super(age, name);
    }


    public void setMiss(Missable x) {
        miss.add(x);
    }

    public void setBother(Botherable x) {
        bother.add(x);
    }

    public void setChat(Chatable x) {
        chat.add(x);
    }

    public int getLengthListMiss() {
        return miss.size();
    }

    public Missable getMiss(int x) throws IndexOutOfBoundsException {
        if (x < 0 || x > miss.size()) {
            throw new IndexOutOfBoundsException();
        } else {
            return miss.get(x);
        }
    }

    public Botherable getBother(int x) throws IndexOutOfBoundsException {
        if (x < 0 || x > bother.size()) {
            throw new IndexOutOfBoundsException();
        } else {
            return bother.get(x);
        }
    }

    public int getLengthListBother() {
        return bother.size();
    }

    public int getLengthListChat() {
        return chat.size();
    }

    public Chatable getChat(int x) throws IndexOutOfBoundsException {
        if (x < 0 || x > chat.size()) {
            throw new IndexOutOfBoundsException();
        } else {
            return chat.get(x);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (super.equals(obj))
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        People other = (People) obj;
        if (name != other.name)
            return false;

        if (age != other.age)
            return false;

        for (int i = 0; i != getLengthListMiss(); i++) {
            if (miss.get(i).getName() != other.miss.get(i).getName())
                return false;
            if (miss.get(i).getAge() != other.miss.get(i).getAge())
                return false;
        }

        for (int i = 0; i != getLengthListBother(); i++) {
            if (bother.get(i).getName() != other.bother.get(i).getName())
                return false;
            if (bother.get(i).getAge() != other.bother.get(i).getAge())
                return false;
        }

        for (int i = 0; i != getLengthListChat(); i++) {
            if (chat.get(i).getName() != other.chat.get(i).getName())
                return false;
            if (chat.get(i).getAge() != other.chat.get(i).getAge())
                return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int result = 31 + age;
        for (char x : name.toCharArray()) {
            result += (int) x;
        }
        return result;
    }

    @Override
    public String toString() {
        StringBuffer output = new StringBuffer();
        output.append(getClass().getName()).append(name).append(age);
        return output.toString();
    }

    @Override
    public int compareTo(Object o) {
        return this.age-((People)o).age;
    }
}
