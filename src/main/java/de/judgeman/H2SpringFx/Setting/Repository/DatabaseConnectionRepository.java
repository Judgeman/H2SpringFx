package de.judgeman.H2SpringFx.Setting.Repository;

import de.judgeman.H2SpringFx.Setting.Model.DatabaseConnection;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by Paul Richter on Tue 08/09/2020
 */
@Repository
public interface DatabaseConnectionRepository extends CrudRepository<DatabaseConnection, String> {
    List<DatabaseConnection> findAll();
    List<DatabaseConnection> findAll(Pageable limit);
}
