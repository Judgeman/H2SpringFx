package de.judgeman.H2SpringFx.ViewControllers;

import de.judgeman.H2SpringFx.Services.*;
import de.judgeman.H2SpringFx.Setting.Model.DatabaseConnection;
import de.judgeman.H2SpringFx.Setting.Model.DatabaseType;
import de.judgeman.H2SpringFx.ViewControllers.Abstract.BaseViewController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;

/**
 * Created by Paul Richter on Thu 03/09/2020
 */
@Component
public class DataSourceSelectionViewController extends BaseViewController {

    @Value("${database.selection.sql_dialect.h2}")
    private String DATABASE_SQL_DIALECT_H2;
    @Value("${database.selection.driver.h2}")
    private String DATABASE_DRIVER_CLASSNAME_H2;
    @Value("${database.selection.connection_prefix.h2}")
    private String DATABASE_CONNECTION_PREFIX_H2;

    @Value("${database.selection.sql_dialect.sql}")
    private String DATABASE_SQL_DIALECT_SQL;
    @Value("${database.selection.driver.sql}")
    private String DATABASE_DRIVER_CLASSNAME_SQL;
    @Value("${database.selection.connection_prefix.sql}")
    private String DATABASE_CONNECTION_PREFIX_SQL;

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
    private ComboBox<DatabaseType> typeComboBox;
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
    private Button chooseFileButton;
    @FXML
    private TextField hostTextField;
    @FXML
    private TextField portTextField;
    @FXML
    private TextField databaseNameTextField;

    @FXML
    private Label labelDatabaseName;
    @FXML
    private Label labelPort;
    @FXML
    private Label labelHost;
    @FXML
    private Label labelPath;


    @FXML
    private void initialize() {
        fillConnectionTypeComboBox();
    }

    private void setVisibilityOfBackButton() {
        backButton.setVisible(settingService.existAnyDatabaseConnections());
    }

    private void selectFirstConnectionType() {
        ObservableList<DatabaseType> comboBoxItems = typeComboBox.getItems();
        if (comboBoxItems != null && comboBoxItems.size() > 0) {
            typeComboBox.setValue(comboBoxItems.get(0));
        }
    }

    @FXML
    private void saveDatabaseConnection() {
        if (!testConnection(false)) {
            return;
        }

        settingService.saveNewConnection(settingService.createNewDatabaseConnection(typeComboBox.getValue(),
                                                                   getDriverClassNameForType(typeComboBox.getValue()),
                                                                   getDialectForType(typeComboBox.getValue()),
                                                                   getConnectionPrefix(typeComboBox.getValue()),
                                                                   getJDBCConnectionPathWithoutPrefix(typeComboBox.getValue()),
                                                                   nameTextField.getText(),
                                                                   usernameTextField.getText(),
                                                                   passwordTextField.getText()));

        dataSourceService.setCurrentDataSourceName(SettingService.NAME_PRIMARY_DATASOURCE);

        viewService.registerLastView(ViewService.FILE_PATH_DATASOURCE_SELECTION_VIEW);
        viewService.showNewView(ViewService.FILE_PATH_TODO_VIEW);
    }

    private String getJDBCConnectionPathWithoutPrefix(DatabaseType type) {
        switch (type) {
            case H2:
                return pathTextField.getText();
            case Sql:
                return String.format("//%s:%s;databaseName=%s", hostTextField.getText(), portTextField.getText(), databaseNameTextField.getText());
        }

        return null;
    }

    private String getConnectionPrefix(DatabaseType type) {
        switch (type) {
            case H2:
                return DATABASE_CONNECTION_PREFIX_H2;
            case Sql:
                return DATABASE_CONNECTION_PREFIX_SQL;
        }

        return null;
    }

    private void fillConnectionTypeComboBox() {
        typeComboBox.setItems(FXCollections.observableArrayList(DatabaseType.H2, DatabaseType.Sql));
    }

    @FXML
    private boolean testConnection() {
        return testConnection(true);
    }

    private boolean testConnection(boolean showSuccessMessage) {
        try {
            DataSource newDataSource = dataSourceService.createNewDataSource(getDriverClassNameForType(typeComboBox.getValue()),
                                                                             getFullJDBCUrlPath(typeComboBox.getValue()),
                                                                             usernameTextField.getText(),
                                                                             passwordTextField.getText());
            newDataSource.getConnection().isValid(10);

            if (showSuccessMessage) {
                viewService.showInformationDialog(languageService.getLocalizationText("datasourceSelection.dialog.connectionTest.title"),
                                                  languageService.getLocalizationText("datasourceSelection.dialog.connectionTest.isValid"));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            viewService.showInformationDialog(languageService.getLocalizationText("datasourceSelection.dialog.error.title"), ex.getMessage());

            return false;
        }

        return true;
    }

    @FXML
    private void backButtonClicked() throws IOException {
        viewService.getMainViewController().showViewBefore();
    }

    @FXML
    private void onChangeConnectionType() {
        DatabaseType type = typeComboBox.getValue();

        disableAllInputElements();

        switch (type) {
            case H2:
                enableAllRelevantH2InputElements();
                break;
            case Sql:
                enableAllRelevantSQLInputElements();
                break;
        }
    }

    private void disableAllInputElements() {
        hostTextField.setDisable(true);
        portTextField.setDisable(true);
        databaseNameTextField.setDisable(true);
        pathTextField.setDisable(true);
        chooseFileButton.setDisable(true);

        labelPath.setDisable(true);
        labelHost.setDisable(true);
        labelPort.setDisable(true);
        labelDatabaseName.setDisable(true);
    }

    private void enableAllRelevantSQLInputElements() {
        hostTextField.setDisable(false);
        portTextField.setDisable(false);
        databaseNameTextField.setDisable(false);

        labelHost.setDisable(false);
        labelPort.setDisable(false);
        labelDatabaseName.setDisable(false);
    }

    private void enableAllRelevantH2InputElements() {
        pathTextField.setDisable(false);
        chooseFileButton.setDisable(false);

        labelPath.setDisable(false);
    }

    private String getFullJDBCUrlPath(DatabaseType type) {
        switch (type) {
            case H2:
                return String.format("%s%s%s", DATABASE_CONNECTION_PREFIX_H2, pathTextField.getText(), ";create=false");
            case Sql:
                return String.format("%s//%s:%s;databaseName=%s", DATABASE_CONNECTION_PREFIX_SQL, hostTextField.getText(), portTextField.getText(), databaseNameTextField.getText());
        }

        return null;
    }

    private String getDialectForType(DatabaseType type) {
        switch (type) {
            case H2:
                return DATABASE_SQL_DIALECT_H2;
            case Sql:
                return DATABASE_SQL_DIALECT_SQL;
        }

        return null;
    }

    private String getDriverClassNameForType(DatabaseType type) {
        switch (type) {
            case H2:
                return DATABASE_DRIVER_CLASSNAME_H2;
            case Sql:
                return DATABASE_DRIVER_CLASSNAME_SQL;
        }

        return null;
    }

    @FXML
    private void chooseDatabasePath() {
        File directory = viewService.getDirectoryFromUser(languageService.getLocalizationText("datasourceSelection.chooseDirectoryForExistingDatabase"));
        if (directory != null) {
            pathTextField.setText(createH2DataBasePath(directory));
        }
    }

    private String createH2DataBasePath(File directory) {
        String directoryName = directory.getName();
        return String.format("%s/%s", directory.getAbsolutePath(), directoryName);
    }

    @Override
    public void afterViewIsInitialized() {
        selectFirstConnectionType();
        setVisibilityOfBackButton();
    }
}
