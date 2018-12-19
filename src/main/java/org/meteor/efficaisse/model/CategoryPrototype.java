package org.meteor.efficaisse.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
public class CategoryPrototype implements Serializable{
    private static final long serialVersionUID = 1L;
    @Id
    private String name;

    @Column(unique = true, nullable = false)
    private String photo;

    @OneToMany(mappedBy = "prototype",fetch = FetchType.EAGER)
     private List<Category> instances;


    public List<Category> getInstances() {
        return instances;
    }

    public void setInstances(List<Category> instances) {
        this.instances = instances;
    }

    private String tag;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
