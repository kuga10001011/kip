package org.kuga.kip.datastore.field;

import com.fasterxml.jackson.annotation.JsonBackReference;
import org.json.JSONArray;
import org.json.JSONObject;
import org.kuga.kip.datastore.Datastore;
import org.kuga.kip.datastore.FieldValue;
import org.kuga.kip.datastore.value.Value;

import javax.persistence.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "fields")
public class Field {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column
    private Boolean required;

    @Enumerated(EnumType.STRING)
    private Type type;

    @ManyToOne
    @JoinColumn(name = "datastore_id")
    @JsonBackReference
    private Datastore datastore;

    @OneToMany(targetEntity = FieldValue.class)
    @JsonBackReference
    private Set<FieldValue> fieldValues = new HashSet<>();

    @ElementCollection
    @CollectionTable(name="field_allowed_values", joinColumns=@JoinColumn(name="field_id"))
    @Column(name="allowed_value")
    private Set<String> allowedValues = new HashSet<>();

    public Field() {}

    public Field(JSONObject fieldObject) {
        setField(fieldObject);
    }

    public void setField(JSONObject fieldObject) {
        this.name = fieldObject.getString("name");
        this.required = fieldObject.getBoolean("required");
        this.type = Type.valueOf(fieldObject.getString("type"));
        if (fieldObject.has("allowedValues") && fieldObject.get("allowedValues").getClass() == JSONArray.class) {
            for (Object allowedValueObject : fieldObject.getJSONArray("allowedValues")) {
                addAllowedValues(allowedValueObject.toString());
            }
        }
        if (fieldObject.has("id")) {
            this.id = fieldObject.getLong("id");
        }
    }

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

    public Boolean getRequired() {
        return required;
    }

    public void setRequired(Boolean required) {
        this.required = required;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type fieldType) {
        this.type = fieldType;
    }

    public Datastore getDatastore() {
        return datastore;
    }

    public void setDatastore(Datastore datastore) {
        this.datastore = datastore;
    }

    public Set<FieldValue> getFieldValues() {
        return fieldValues;
    }

    public void setFieldValues(Set<FieldValue> fieldValues) {
        this.fieldValues = fieldValues;
    }

    public void deleteFieldValues(FieldValue ... fieldValues) {
        Arrays.stream(fieldValues).forEachOrdered(fieldValue -> {
            this.fieldValues.remove(fieldValue);
        });
    }

    public void addFieldValues(FieldValue ... fieldValues) {
        this.fieldValues.addAll(Arrays.asList(fieldValues));
    }

    public Set<String> getAllowedValues() {
        return allowedValues;
    }

    public void setAllowedValues(Set<String> allowedValues) {
        this.allowedValues = allowedValues;
    }

    public void addAllowedValues(String ... allowedValues) {
        this.allowedValues.addAll(Arrays.asList(allowedValues));
    }

}
