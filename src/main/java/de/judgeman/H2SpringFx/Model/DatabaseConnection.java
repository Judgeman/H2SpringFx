package de.judgeman.H2SpringFx.Model;

import javax.persistence.*;

/**
 * Created by Paul Richter on Tue 08/09/2020
 */
@Entity
public class DatabaseConnection {

    @Id
    @Column
    private String id;

    @Column
    private String driverClassName;

    @Column
    private String sqlDialact;

    @Column
    private String jdbcConnectionPrefix;

    @Column
    private String jdbcConnectionPath;

    @Column
    private String username;

    @Column
    private String password;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDriverClassName() {
        return driverClassName;
    }

    public void setDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
    }

    public String getSqlDialact() {
        return sqlDialact;
    }

    public void setSqlDialact(String sqlDialact) {
        this.sqlDialact = sqlDialact;
    }

    public String getJdbcConnectionPrefix() {
        return jdbcConnectionPrefix;
    }

    public void setJdbcConnectionPrefix(String jdbcConnectionPrefix) {
        this.jdbcConnectionPrefix = jdbcConnectionPrefix;
    }

    public String getJdbcConnectionPath() {
        return jdbcConnectionPath;
    }

    public void setJdbcConnectionPath(String jdbcConnectionPath) {
        this.jdbcConnectionPath = jdbcConnectionPath;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return id;
    }
}
