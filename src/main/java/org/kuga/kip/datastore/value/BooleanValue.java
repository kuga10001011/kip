package org.kuga.kip.datastore.value;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "boolean_values")
public class BooleanValue extends Value<Boolean> {

    @Column(name = "boolean_value")
    private Boolean value;

    public BooleanValue() {}

    public BooleanValue(Boolean value) {
        setValue(value);
    }

    @Override
    public Boolean getValue() {
        return value;
    }

    @Override
    public void setValue(Boolean value) {
        this.value = value;
    }

}
