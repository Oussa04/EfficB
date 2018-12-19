package org.meteor.efficaisse.model;

import org.hibernate.annotations.Formula;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;


@Entity
public class StoreType  implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    private String name;

    private int type;


    @Formula(value = "(select count(s.id) from Store s where s.type_name = name)")
    private int count;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
