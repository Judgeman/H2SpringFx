create sequence hibernate_sequence start with 1 increment by 1
create table database_connection (id varchar(255) not null, driver_class_name varchar(255), jdbc_connection_path varchar(255), jdbc_connection_prefix varchar(255), password varchar(255), sql_dialact varchar(255), username varchar(255), primary key (id))
create table setting_entry (key varchar(255) not null, value varchar(255) not null, primary key (key))
