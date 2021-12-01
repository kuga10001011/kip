package org.kuga.kip.datastore.value;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "string_values")
public class StringValue extends Value<String> {

    @Column(name = "string_value")
    private String value;

    public StringValue() {}

    public StringValue(String value) {
        setValue(value);
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public void setValue(String value) {
        this.value = value;
    }

}
