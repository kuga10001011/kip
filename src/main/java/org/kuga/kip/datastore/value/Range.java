package org.kuga.kip.datastore.value;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class Range implements Serializable {

    private Long floor;

    private Long ceiling;

    public Long getFloor() {
        return floor;
    }

    public void setFloor(Long floor) {
        this.floor = floor;
    }

    public Long getCeiling() {
        return ceiling;
    }

    public void setCeiling(Long ceiling) {
        this.ceiling = ceiling;
    }

    public Range() {}

    public Range(Long floor, Long ceiling) {
        this.floor = floor;
        this.ceiling = ceiling;
    }

}
