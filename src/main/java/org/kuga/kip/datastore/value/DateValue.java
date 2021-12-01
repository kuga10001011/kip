package org.kuga.kip.datastore.value;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.sql.Date;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Entity
@Table(name = "date_values")
public class DateValue extends Value<Date> {

    @Transient
    private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);

    @Column(name = "date_value")
    @Basic
    private Date value;

    public DateValue() {}

    public DateValue(String value) {
        LocalDateTime localDateTime = LocalDateTime.parse(value, dateTimeFormatter);
        setValue(Date.valueOf(localDateTime.toLocalDate()));
    }

    @Override
    public Date getValue() {
        return value;
    }

    public void setValue(Date value) {
        this.value = value;
    }

}
