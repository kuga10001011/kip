package org.kuga.kip.datastore;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import org.kuga.kip.datastore.row.Row;
import org.kuga.kip.datastore.field.Field;
import org.kuga.kip.user.User;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "datastores")
public class Datastore {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name;

    @ManyToMany(targetEntity = User.class)
    @JsonBackReference
    private Set<User> users = new HashSet<>();

    @OneToMany(targetEntity = Field.class, cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JsonManagedReference
    private Set<Field> fields = new HashSet<>();

    @OneToMany(targetEntity = Row.class, cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JsonManagedReference
    private Set<Row> rows = new HashSet<>();

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

    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }

    public void addUsers(User ... users) {
        Arrays.stream(users).forEachOrdered(user -> {
            this.users.add(user);
            user.addDatastores(this);
        });
    }

    public Set<Row> getRows() {
        return rows;
    }

    public void setRows(Set<Row> rows) {
        this.rows = rows;
    }

    public void addRows(Row ... rows) {
        Arrays.stream(rows).forEachOrdered(row -> {
            this.rows.add(row);
            row.setDatastore(this);
        });
    }

    public void deleteRows(Row ... rows) {
        Arrays.stream(rows).forEachOrdered(row -> {
            this.rows.remove(row);
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
            field.setDatastore(this);
        });
    }

    public void deleteFields(Field ... fields) {
        Arrays.stream(fields).forEachOrdered(field -> {
            this.fields.remove(field);
        });
    }

}
