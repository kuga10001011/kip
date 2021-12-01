package org.kuga.kip.datastore.value;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "range_values")
public class RangeValue extends Value<Range> {

    @Embedded
    Range value;

    public RangeValue() {}

    public RangeValue(Range value) {
        setValue(value);
    }

    public Range getValue() {
        return value;
    }

    public void setValue(Range value) {
        this.value = value;
    }

}
