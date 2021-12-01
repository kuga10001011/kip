package org.kuga.kip.datastore.value;

import org.json.JSONArray;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

@Entity
@Table(name = "list_values")
public class ListValue extends Value<HashSet<String>> {

    @CollectionTable(name="list_items", joinColumns=@JoinColumn(name="list_value_id"))
    @ElementCollection
    @Column(name="list_item")
    private Set<String> value = new HashSet<>();

    public ListValue() {}

    public ListValue(JSONArray value) {
        Iterator<Object> valueIterator = value.iterator();
        while(valueIterator.hasNext()) {
            this.value.add(valueIterator.next().toString());
        }
    }

    @Override
    public HashSet<String> getValue() {
        return new HashSet<>(value);
    }

    @Override
    public void setValue(HashSet<String> value) {
        this.value = value;
    }

    public void setValue(JSONArray value) {
        for (Object listItem : value) {
            this.value.add(listItem.toString());
        }
    }

}
