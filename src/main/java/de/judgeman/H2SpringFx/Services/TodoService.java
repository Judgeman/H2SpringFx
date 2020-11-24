package de.judgeman.H2SpringFx.Services;

import de.judgeman.H2SpringFx.Model.Todo;
import de.judgeman.H2SpringFx.Core.Repositories.TodoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

/**
 * Created by Paul Richter on Sun 20/09/2020
 */
@Service
public class TodoService {

    @Autowired
    private TodoRepository todoRepository;

    @Autowired
    private DataSourceService dataSourceService;

    public ArrayList<Todo> loadAllTodos() {
        return todoRepository.findAllByOrderByIdAsc();
    }

    public Todo saveNewTodo(String text) {
        Todo newTodo = new Todo();
        newTodo.setText(text);

        todoRepository.save(newTodo);

        return newTodo;
    }

    public void saveTodo(Todo todo) {
        todoRepository.save(todo);
    }

    public void deleteTodo(Todo todo) {
        todoRepository.delete(todo);
    }
}
