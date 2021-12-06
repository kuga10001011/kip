package org.kuga.kip.user;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import org.kuga.kip.datastore.Datastore;
import org.kuga.kip.datastore.field.Field;
import org.kuga.kip.datastore.view.View;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String username;

    @Column
    @Email
    private String email;

    @Column
    private String password;

    @ManyToMany(cascade = {CascadeType.MERGE, CascadeType.REFRESH}, fetch = FetchType.EAGER)
    @JoinTable(name = "datastore_user_associations", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "datastore_id"))
    @JsonManagedReference
    private Set<Datastore> datastores = new HashSet<>();

    @ManyToMany(cascade = {CascadeType.MERGE, CascadeType.REFRESH}, fetch = FetchType.EAGER)
    @JoinTable(name = "user_role_associations", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    @JsonManagedReference
    private Set<Role> roles = new HashSet<>();

    @OneToMany(targetEntity = View.class, cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JsonManagedReference
    private Set<View> views = new HashSet<>();

    public User() {}

    public User(String username, String password, String email) {
        setUsername(username);
        setPassword(password);
        setEmail(email);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Set<Datastore> getDatastores() {
        return datastores;
    }

    public void setDatastores(Set<Datastore> datastores) {
        this.datastores = datastores;
    }

    public void addDatastores(Datastore ... datastores) {
        this.datastores.addAll(Arrays.asList(datastores));
    }

    public void deleteDatastore(Datastore datastore) {
        this.datastores.remove(datastore);
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public void addRoles(Role ... roles) {
        Arrays.stream(roles).forEachOrdered(role -> {
            this.roles.add(role);
            role.getUsers().add(this);
        });
    }

    public Set<View> getViews() {
        return views;
    }

    public void setViews(Set<View> views) {
        this.views = views;
    }

    public void addViews(View ... views) {
        this.views.addAll(Arrays.asList(views));
    }

    public void deleteViews(View ... views) {
        Arrays.asList(views).forEach(this.views::remove);
    }

}
