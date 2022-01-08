package de.judgeman.H2SpringFx.Tests.ServiceTests;

import de.judgeman.H2SpringFx.Core.Model.Todo;
import de.judgeman.H2SpringFx.Services.DataSourceService;
import de.judgeman.H2SpringFx.Services.SettingService;
import de.judgeman.H2SpringFx.Services.TodoService;
import de.judgeman.H2SpringFx.Setting.Model.DatabaseConnection;
import de.judgeman.H2SpringFx.Setting.Model.DatabaseType;
import liquibase.exception.LiquibaseException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;

/**
 * Created by Paul Richter on Thu 25/02/2021
 */
@SpringBootTest
public class TodoServiceTests {

    @Autowired
    private TodoService todoService;
    @Autowired
    private SettingService settingService;
    @Autowired
    private DataSourceService dataSourceService;

    public void initTestDatabase() throws LiquibaseException {
        DatabaseConnection databaseConnection = settingService.createNewDatabaseConnection(DatabaseType.H2,
                                                                                           "org.h2.Driver",
                                                                                           "org.hibernate.dialect.H2Dialect",
                                                                                           "jdbc:h2:",
                                                                                           "mem:primary;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
                                                                                           "TestDatabase",
                                                                                           "SA",
                                                                                           "Nummer!22");
        settingService.saveNewConnection(databaseConnection);
        dataSourceService.initializePrimaryDataSource(databaseConnection);
        dataSourceService.setCurrentDataSourceName(SettingService.NAME_PRIMARY_DATASOURCE);
    }

    @Test
    public void loadAllTodosTest() throws LiquibaseException {
        initTestDatabase();
        ArrayList<Todo> allSavedTodos = todoService.loadAllTodos();
        Assertions.assertEquals(0, allSavedTodos.size());

        Todo todo = new Todo();
        todo.setText("Test Todo");

        todoService.saveTodo(todo);

        allSavedTodos = todoService.loadAllTodos();
        Assertions.assertEquals(1, allSavedTodos.size());
        Assertions.assertEquals(todo.getText(), allSavedTodos.get(0).getText());

        todoService.deleteTodo(todo);
        allSavedTodos = todoService.loadAllTodos();
        Assertions.assertEquals(0, allSavedTodos.size());

        todoService.saveNewTodo("Second Test Todo");
        allSavedTodos = todoService.loadAllTodos();
        Assertions.assertEquals(1, allSavedTodos.size());
    }
}
