package org.kuga.kip.datastore.row;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import org.kuga.kip.datastore.Datastore;
import org.kuga.kip.datastore.FieldValue;
import org.kuga.kip.datastore.value.Value;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "rows")
public class Row {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(targetEntity = FieldValue.class, cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JsonManagedReference
    private Set<FieldValue> fieldValues = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "datastore_id")
    @JsonBackReference
    private Datastore datastore;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Set<FieldValue> getFieldValues() {
        return fieldValues;
    }

    public void setFieldValues(Set<FieldValue> fieldValues) {
        fieldValues.stream().forEachOrdered(fieldValue -> {
            this.fieldValues.add(fieldValue);
            fieldValue.setRow(this);
        });
    }

    public void addFieldValues(FieldValue ... fieldValues) {
        for (FieldValue fieldValue : fieldValues) {
            this.fieldValues.add(fieldValue);
            fieldValue.setRow(this);
        }
    }

    public Datastore getDatastore() {
        return datastore;
    }

    public void setDatastore(Datastore datastore) {
        this.datastore = datastore;
    }

}
