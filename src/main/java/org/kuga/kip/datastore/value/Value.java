package org.kuga.kip.datastore.value;

import com.fasterxml.jackson.annotation.JsonBackReference;
import org.json.JSONObject;
import org.kuga.kip.datastore.FieldValue;
import org.kuga.kip.datastore.field.Type;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToOne;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class Value<T extends Serializable> {

    @Id
    @GeneratedValue
    private Long id;

    @OneToOne(targetEntity = FieldValue.class)
    @JsonBackReference
    private FieldValue fieldValue;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public FieldValue getFieldValue() {
        return fieldValue;
    }

    public void setFieldValue(FieldValue fieldValue) {
        this.fieldValue = fieldValue;
    }

    public abstract T getValue();

    public abstract void setValue(T value);

    public static Value<?> build(JSONObject valueObject, Type type) throws ParseException, MalformedURLException {
        switch (type) {
            case BOOLEAN:
                return new BooleanValue(valueObject.getBoolean("value"));
            case DATE:
                return new DateValue(valueObject.getString("value"));
            case LIST:
                return new ListValue(valueObject.getJSONArray("value"));
            case RANGE:
                return new RangeValue(new Range(valueObject.getLong("floor"), valueObject.getLong("ceiling")));
            case URL:
                return new URLValue(new URL(valueObject.getString("value")));
            default:
                return new StringValue(valueObject.getString("value"));
        }
    }

}
