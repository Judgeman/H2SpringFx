package de.judgeman.H2SpringFx.Core.Repositories;

import de.judgeman.H2SpringFx.Core.Model.Todo;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

/**
 * Created by Paul Richter on Sun 20/09/2020
 */
@Repository
public interface TodoRepository extends CrudRepository<Todo, Integer> {
    ArrayList<Todo> findAllByOrderByIdAsc();
}
