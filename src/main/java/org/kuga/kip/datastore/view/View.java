package org.kuga.kip.datastore.view;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import org.kuga.kip.datastore.Datastore;
import org.kuga.kip.datastore.field.Field;
import org.kuga.kip.user.User;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "views")
public class View {

    @Id
    @GeneratedValue
    Long id;

    @Column(nullable = false)
    String name;

    @ManyToOne
    @JoinColumn(name = "datastore_id")
    @JsonBackReference
    Datastore datastore;

    @ManyToMany(cascade = {CascadeType.MERGE, CascadeType.REFRESH}, fetch = FetchType.EAGER)
    @JoinTable(name = "view_user_associations", joinColumns = @JoinColumn(name = "view_id"), inverseJoinColumns = @JoinColumn(name = "user_id"))
    @JsonManagedReference
    Set<User> users = new HashSet<>();

    @ManyToMany(cascade = {CascadeType.MERGE, CascadeType.REFRESH}, fetch = FetchType.EAGER)
    @JoinTable(name = "view_field_associations", joinColumns = @JoinColumn(name = "view_id"), inverseJoinColumns = @JoinColumn(name = "field_id"))
    @JsonManagedReference
    @OrderColumn
    Set<Field> fields = new LinkedHashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Datastore getDatastore() {
        return datastore;
    }

    public void setDatastore(Datastore datastore) {
        this.datastore = datastore;
    }

    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }

    public void addUsers(User ... users) {
        Arrays.stream(users).forEachOrdered(user -> {
            this.users.add(user);
            user.addViews(this);
        });
    }

    public void removeUsers(User ... users) {
        Arrays.stream(users).forEachOrdered(user -> {
            this.users.remove(user);
            user.deleteViews(this);
        });
    }

    public Set<Field> getFields() {
        return fields;
    }

    public void setFields(Set<Field> fields) {
        this.fields = fields;
    }

    public void addFields(Field ... fields) {
        Arrays.stream(fields).forEachOrdered(field -> {
            this.fields.add(field);
            field.addViews(this);
        });
    }

    public void deleteFields(Field ... fields) {
        Arrays.stream(fields).forEachOrdered(field -> {
            this.fields.remove(field);
            field.deleteViews(this);
        });
    }

}
