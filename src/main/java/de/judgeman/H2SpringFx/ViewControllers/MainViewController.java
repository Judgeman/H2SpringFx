package de.judgeman.H2SpringFx.ViewControllers;

import de.judgeman.H2SpringFx.Setting.Model.DatabaseConnection;
import de.judgeman.H2SpringFx.Services.*;
import de.judgeman.H2SpringFx.ViewControllers.Abstract.BaseViewController;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

/**
 * Created by Paul Richter on Thu 03/09/2020
 */
@Component
public class MainViewController extends BaseViewController {

    private final Logger logger = LogService.getLogger(this.getClass());

    @Autowired
    private ViewService viewService;
    @Autowired
    private SettingService settingService;
    @Autowired
    private DataSourceService dataSourceService;

    @FXML
    private Pane contentPane;
    @FXML
    private Pane glassPane;
    @FXML
    private Pane dialogOverLayer;

    private String lastViewPath;

    public Pane getGlassPane() {
        return glassPane;
    }

    public void setGlassPane(Pane glassPane) {
        this.glassPane = glassPane;
    }

    public Pane getContentPane() {
        return contentPane;
    }

    public void setContentPane(Pane contentPane) {
        this.contentPane = contentPane;
    }

    public Pane getDialogOverLayer() {
        return dialogOverLayer;
    }

    public void setDialogOverLayer(Pane dialogOverLayer) {
        this.dialogOverLayer = dialogOverLayer;
    }

    @FXML
    public void initialize() throws IOException {
        if (!checkForPrimaryDataSource()) {
            showDataSourceSelection();

            return;
        }

        initializeCurrentDataSource();
        dataSourceService.setCurrentDataSourceName(SettingService.NAME_PRIMARY_DATASOURCE);
        showTodoView();
    }

    private void initializeCurrentDataSource() {
        String currentDatabaseConnectionId = settingService.loadSetting(SettingService.CURRENT_PRIMARY_DATABASE_CONNECTION_KEY);
        List<DatabaseConnection> databaseConnections = settingService.getAllDatabaseConnections();

        DatabaseConnection databaseConnection = settingService.findCurrentDatabaseConnection(currentDatabaseConnectionId, databaseConnections);
        if (databaseConnection == null) {
            databaseConnection = databaseConnections.get(0);
            settingService.saveSetting(SettingService.CURRENT_PRIMARY_DATABASE_CONNECTION_KEY, databaseConnection.getId());
        }

        dataSourceService.initializePrimaryDataSource(databaseConnection);
    }

    private void showTodoView() throws IOException {
        loadAndShowView(ViewService.FILE_PATH_TODO_VIEW);
    }

    private void showDataSourceSelection() throws IOException {
        loadAndShowView(ViewService.FILE_PATH_DATASOURCE_SELECTION_VIEW);
    }

    public void loadAndShowView(String viewPath) throws IOException {
        Parent root = viewService.getRootElementFromFXML(viewPath);
        removeLastVisibleView();
        showNewView(root);
    }

    private void showNewView(Parent root) {
        contentPane.getChildren().add(root);
    }

    private void removeLastVisibleView() {
        contentPane.getChildren().clear();
    }

    private boolean checkForPrimaryDataSource() {
        return settingService.existAnyDatabaseConnections();
    }

    public void setLastViewPath(String lastViewPath) {
        this.lastViewPath = lastViewPath;
    }

    public void showViewBefore() throws IOException {
        if (lastViewPath == null) {
            logger.info("No last view registered");
            return;
        }

        loadAndShowView(lastViewPath);
        lastViewPath = null;
    }
}
