package org.kuga.kip.datastore.value;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.net.URL;

@Entity
@Table(name = "url_values")
public class URLValue extends Value<URL> {

    @Column(name = "url_value")
    private URL value;

    public URLValue() {}

    public URLValue(URL value) {
        setValue(value);
    }

    public URL getValue() {
        return value;
    }

    public void setValue(URL value) {
        this.value = value;
    }

}
