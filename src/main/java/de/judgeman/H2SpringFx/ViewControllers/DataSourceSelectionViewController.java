package de.judgeman.H2SpringFx.ViewControllers;

import de.judgeman.H2SpringFx.Model.DatabaseConnection;
import de.judgeman.H2SpringFx.Services.*;
import de.judgeman.H2SpringFx.ViewControllers.Abstract.BaseViewController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileUrlResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;

/**
 * Created by Paul Richter on Thu 03/09/2020
 */
@Component
public class DataSourceSelectionViewController extends BaseViewController {

    private static final String DATABASE_TYPE_H2 = "H2";
    private static final String DATABASE_SQL_DIALECT_H2 = "org.hibernate.dialect.H2Dialect";
    private static final String DATABASE_DRIVER_CLASSNAME_H2 = "org.h2.Driver";
    private static final String DATABASE_CONNECTION_PREFIX_H2 = "jdbc:h2:file:";

    private static final String DATABASE_TYPE_SQL = "SQL";
    private static final String DATABASE_SQL_DIALECT_SQL = "org.hibernate.dialect.SQLServerDialect";
    private static final String DATABASE_DRIVER_CLASSNAME_SQL = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
    private static final String DATABASE_CONNECTION_PREFIX_SQL = "jdbc:sqlserver:";

    private final Logger logger = LogService.getLogger(this.getClass());

    @Autowired
    private ViewService viewService;
    @Autowired
    private DataSourceService dataSourceService;
    @Autowired
    private LanguageService languageService;
    @Autowired
    private SettingService settingService;

    @FXML
    private ComboBox<String> typeComboBox;
    @FXML
    private TextField pathTextField;
    @FXML
    private TextField nameTextField;
    @FXML
    private TextField usernameTextField;
    @FXML
    private TextField passwordTextField;
    @FXML
    private Button backButton;

    @FXML
    public void initialize() {
        fillConnectionTypeComboBox();
        selectFirstConnectionType();
        setVisibilityOfBackButton();
    }

    private void setVisibilityOfBackButton() {
        backButton.setVisible(settingService.existAnyDatabaseConnections());
    }

    private void selectFirstConnectionType() {
        ObservableList<String> comboBoxItems = typeComboBox.getItems();
        if (comboBoxItems != null && comboBoxItems.size() > 0) {
            typeComboBox.setValue(comboBoxItems.get(0));
        }
    }

    public void saveDatabaseConnection() throws IOException {

        // TODO: check connection is valid!?

        DatabaseConnection newDatabaseConnection = settingService.saveNewConnection(getDriverClassNameForType(typeComboBox.getValue()),
                                                                                                getSqlDialectForType(typeComboBox.getValue()),
                                                                                                getConnectionPrefix(typeComboBox.getValue()),
                                                                                                pathTextField.getText(),
                                                                                                nameTextField.getText(),
                                                                                                usernameTextField.getText(),
                                                                                                passwordTextField.getText());

        viewService.registerLastView(ViewService.FILE_PATH_DATASOURCE_SELECTION_VIEW);
        viewService.showNewView(ViewService.FILE_PATH_TODO_VIEW);
    }

    private String getConnectionPrefix(String type) {
        if (type.equals(DATABASE_TYPE_H2)) {
            return DATABASE_CONNECTION_PREFIX_H2;
        } else if (type.equals(DATABASE_TYPE_SQL)) {
            return DATABASE_CONNECTION_PREFIX_SQL;
        }

        return null;
    }

    private void fillConnectionTypeComboBox() {
        // TODO: move this value to connectionService?
        typeComboBox.setItems(FXCollections.observableArrayList(DATABASE_TYPE_H2, DATABASE_TYPE_SQL));
    }

    public void testConnection() {
        if (!areAllInputsValid()) {
            // TODO: show information whats wrong
            return;
        }

        try {
            DataSource newDataSource = dataSourceService.createNewDataSource(getDriverClassNameForType(typeComboBox.getValue()),
                                                                             getUrlPath(typeComboBox.getValue(),
                                                                             pathTextField.getText()),
                                                                             usernameTextField.getText(),
                                                                             passwordTextField.getText());
            newDataSource.getConnection().isValid(10);
            // TODO: check for structure!?

            viewService.showInformationDialog(languageService.getLocalizationText("datasourceSelection.dialog.connectionTest.title"),
                                              languageService.getLocalizationText("datasourceSelection.dialog.connectionTest.isValid"));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void backButtonClicked() throws IOException {
        viewService.getMainViewController().showViewBefore();
    }

    private boolean areAllInputsValid() {
        // TODO: check for all inputs
        return true;
    }

    private String getUrlPath(String type, String path) {
        if (type.equals(DATABASE_TYPE_H2)) {
            return String.format("%s%s%s", DATABASE_CONNECTION_PREFIX_H2, path, ";create=false");
        } else if (type.equals(DATABASE_TYPE_SQL)) {
            return String.format("%s%s", DATABASE_CONNECTION_PREFIX_SQL, path);
        }

        return null;
    }

    private String getSqlDialectForType(String type) {
        if (type.equals(DATABASE_TYPE_H2)) {
            return DATABASE_SQL_DIALECT_H2;
        } else if (type.equals(DATABASE_TYPE_SQL)) {
            return DATABASE_SQL_DIALECT_SQL;
        }

        return null;
    }

    private String getDriverClassNameForType(String type) {
        if (type.equals(DATABASE_TYPE_H2)) {
            return DATABASE_DRIVER_CLASSNAME_H2;
        } else if (type.equals(DATABASE_TYPE_SQL)) {
            return DATABASE_DRIVER_CLASSNAME_SQL;
        }

        return null;
    }

    public void createNewH2Database() throws IOException {
        File directory = viewService.getDirectoryFromUser(languageService.getLocalizationText("datasourceSelection.chooseDirectoryForNewDatabase"));
        if (directory != null) {
            if (!directory.isDirectory()) {
                // TODO: show error message dialog
            }

            try {
                String databasePath = createH2DataBasePath(directory);
                createNewH2DatabaseFile(databasePath);

                // TODO: file out all data in the view
            } catch (SQLException ex) {
                ex.printStackTrace();
                viewService.showInformationDialog(languageService.getLocalizationText("datasourceSelection.dialog.error.title"), ex.getMessage());
            }
        }
    }

    public void chooseDatabasePath() {
        File directory = viewService.getDirectoryFromUser(languageService.getLocalizationText("datasourceSelection.chooseDirectoryForExistingDatabase"));
        if (directory != null) {
            pathTextField.setText(createH2DataBasePath(directory));
        }
    }

    private String createH2DataBasePath(File directory) {
        String directoryName = directory.getName();
        return String.format("%s/%s", directory.getAbsolutePath(), directoryName);
    }

    private void createNewH2DatabaseFile(String databasePath) throws SQLException {
        // TODO: input for username and password??
        DataSource dataSource = dataSourceService.createNewDataSource(getDriverClassNameForType(typeComboBox.getValue()),
                                                                      getUrlPath(typeComboBox.getValue(), databasePath),
                                                             "SA",
                                                             "");

        // TODO: ask user for overriding if the file exists already

        URL databaseCreateSchemaUrl = getClass().getClassLoader().getResource("config/databaseSchema_model_create.sql");
        URL databaseDropSchemaUrl = getClass().getClassLoader().getResource("config/databaseSchema_drop.sql");

        assert databaseCreateSchemaUrl != null;
        assert databaseDropSchemaUrl != null;

        Resource schemaCreateScript = new FileUrlResource(databaseCreateSchemaUrl);
        Resource schemaDropScript = new FileUrlResource(databaseDropSchemaUrl);

        ScriptUtils.executeSqlScript(dataSource.getConnection(), schemaDropScript);
        ScriptUtils.executeSqlScript(dataSource.getConnection(), schemaCreateScript);

        dataSource.getConnection().close();
    }
}
