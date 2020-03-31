package de.judgeman.H2SpringFx.Model;

import javax.persistence.*;

/**
 * Created by Paul Richter on Mon 30/03/2020
 */
@Entity
public class SettingEntry {

    @Id
    @Column(nullable = false)
    private String key;

    @Column(nullable = false)
    private String value;

    public SettingEntry() {

    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
