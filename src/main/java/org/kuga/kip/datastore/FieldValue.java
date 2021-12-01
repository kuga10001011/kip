package org.kuga.kip.datastore;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import org.kuga.kip.datastore.field.Field;
import org.kuga.kip.datastore.row.Row;
import org.kuga.kip.datastore.value.Value;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "row_field_value_associations")
public class FieldValue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(targetEntity = Row.class)
    @JoinColumn(name = "row_id", referencedColumnName = "id")
    @JsonBackReference
    private Row row;

    @ManyToOne(targetEntity = Field.class, cascade = {CascadeType.MERGE, CascadeType.REFRESH, CascadeType.PERSIST, CascadeType.DETACH}, fetch = FetchType.EAGER)
    @JoinColumn(name = "field_id", referencedColumnName = "id")
    @JsonManagedReference
    private Field field;

    @OneToOne(targetEntity = Value.class, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "value_id", referencedColumnName = "id")
    @JsonManagedReference
    private Value<?> value;

    public FieldValue() {}

    public FieldValue(Field field, Value<?> value) {
        setField(field);
        setValue(value);
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Row getRow() {
        return row;
    }

    public void setRow(Row row) {
        this.row = row;
    }

    public Field getField() {
        return field;
    }

    public void setField(Field field) {
        this.field = field;
        field.addFieldValues(this);
    }

    public Value<?> getValue() {
        return value;
    }

    public void setValue(Value<?> value) {
        this.value = value;
        value.setFieldValue(this);
    }

}
