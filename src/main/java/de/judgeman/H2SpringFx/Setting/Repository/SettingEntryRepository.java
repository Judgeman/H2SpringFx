package de.judgeman.H2SpringFx.Setting.Repository;

import de.judgeman.H2SpringFx.Setting.Model.SettingEntry;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by Paul Richter on Mon 30/03/2020
 */
@Repository
public interface SettingEntryRepository extends CrudRepository<SettingEntry, String> {

}
