package de.judgeman.H2SpringFx.Setting.Model;

import javax.persistence.*;

/**
 * Created by Paul Richter on Tue 08/09/2020
 */
@Entity(name = "DATABASE_CONNECTION")
public class DatabaseConnection {

    @Id
    @Column
    private String id;

    @Column (name = "DATABASE_TYPE")
    @Enumerated(EnumType.STRING)
    private DatabaseType databaseType;

    @Column (name = "DRIVER_CLASS_NAME")
    private String driverClassName;

    @Column (name = "SQL_DIALECT")
    private String sqlDialect;

    @Column (name = "JDBC_CONNECTION_PREFIX")
    private String jdbcConnectionPrefix;

    @Column (name = "JDBC_CONNECTION_PATH")
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

    public String getSqlDialect() {
        return sqlDialect;
    }

    public void setSqlDialect(String sqlDialect) {
        this.sqlDialect = sqlDialect;
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

    public DatabaseType getDatabaseType() {
        return databaseType;
    }

    public void setDatabaseType(DatabaseType databaseType) {
        this.databaseType = databaseType;
    }

    @Override
    public String toString() {
        return id;
    }
}
