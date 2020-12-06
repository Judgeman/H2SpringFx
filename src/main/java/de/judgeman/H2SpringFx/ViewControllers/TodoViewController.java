package de.judgeman.H2SpringFx.ViewControllers;

import de.judgeman.H2SpringFx.HelperClasses.CallBack;
import de.judgeman.H2SpringFx.HelperClasses.ValidationResult;
import de.judgeman.H2SpringFx.Setting.Model.DatabaseConnection;
import de.judgeman.H2SpringFx.Core.Model.Todo;
import de.judgeman.H2SpringFx.Services.*;
import de.judgeman.H2SpringFx.ViewControllers.Abstract.BaseViewController;
import de.judgeman.H2SpringFx.ViewControllers.DialogControllers.TextInputDialogController;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import liquibase.exception.LiquibaseException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Paul Richter on Sun 20/09/2020
 */
@Controller
public class TodoViewController extends BaseViewController {

    private static final double MAX_WIDTH = 1.7976931348623157E308;
    private final int MAX_LENGTH_NEW_TODO_INPUT = 256;

    @Autowired
    private TodoService todoService;
    @Autowired
    private ViewService viewService;
    @Autowired
    private SettingService settingService;
    @Autowired
    private LanguageService languageService;
    @Autowired
    private DataSourceService dataSourceService;

    @FXML
    private VBox todoListBox;
    @FXML
    private ComboBox<DatabaseConnection> dataSourceDropDown;
    @FXML
    private Label inputCounterLabel;
    @FXML
    private ProgressBar inputCounterProgressBar;
    @FXML
    private TextField newTodoTextField;
    @FXML
    private Label todoCounterLabel;

    private DatabaseConnection placeHolderForNewDatabaseConnection;
    private DatabaseConnection currentPrimaryDataConnection;

    @FXML
    private void initialize() {
        initializeDataSourceDropDown();
        updateInputCounter();
    }

    public void tryLoadingData() {
        showData(todoService.loadAllTodos());
        updateTodoCounter();
    }

    public void selectLastUsedDataSourceAndLoadData() throws IOException, LiquibaseException {
        String currentDatabaseConnectionId = settingService.loadSetting(SettingService.CURRENT_PRIMARY_DATABASE_CONNECTION_KEY);
        if (currentDatabaseConnectionId != null) {
            List<DatabaseConnection> databaseConnections = dataSourceDropDown.getItems();
            currentPrimaryDataConnection = settingService.findCurrentDatabaseConnection(currentDatabaseConnectionId, databaseConnections);
        }

        if (currentPrimaryDataConnection == null) {
            currentPrimaryDataConnection = dataSourceDropDown.getItems().get(0);
        }

        dataSourceDropDown.setValue(currentPrimaryDataConnection);
        initializeNewDatasource(currentPrimaryDataConnection);
    }

    private void initializeDataSourceDropDown() {
        ObservableList<DatabaseConnection> databaseConnections = FXCollections.observableList(settingService.getAllDatabaseConnections());

        placeHolderForNewDatabaseConnection = new DatabaseConnection();
        placeHolderForNewDatabaseConnection.setId(languageService.getLocalizationText("todoView.dataSourceDropDown.newEntry"));
        databaseConnections.add(placeHolderForNewDatabaseConnection);

        dataSourceDropDown.setItems(databaseConnections);
    }

    private void showData(ArrayList<Todo> todoList) {
        todoListBox.getChildren().clear();

        for (Todo todo: todoList) {
            addNewTodoOnTopOfTheList(todo);
        }
    }

    private void addNewTodoOnTopOfTheList(Todo todo) {
        CheckBox checkBox = new CheckBox();
        checkBox.setMaxWidth(MAX_WIDTH);
        checkBox.setText(todo.getText());
        checkBox.setSelected(todo.isDone());
        checkBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (todo.isDone() == newValue) {
                return;
            }

            try {
                todo.setDone(newValue);
                todoService.saveTodo(todo);
                updateTodoCounter();
            } catch (Exception ex) {
                todo.setDone(oldValue);
                checkBox.setSelected(oldValue);
                showError(ex);
            }
        });

        addContextMenuToTodoCheckBox(checkBox, todo);

        ArrayList<Node> oldList = new ArrayList<Node>(todoListBox.getChildren());

        todoListBox.getChildren().clear();
        todoListBox.getChildren().add(checkBox);
        todoListBox.getChildren().addAll(oldList);
    }

    private void removeTodoFromTheList(CheckBox checkBox) {
        todoListBox.getChildren().remove(checkBox);
    }

    @FXML
    private void newTodoTextFieldKeyTyped() {
        updateInputCounter();
    }

    private void updateInputCounter() {
        updateInputCounterLabel();
        updateInputCounterProgressBar();
    }

    private void updateInputCounterProgressBar() {
        double progress = ((double) newTodoTextField.getText().length()) / (double) MAX_LENGTH_NEW_TODO_INPUT;
        inputCounterProgressBar.setProgress(progress);
    }

    private void updateInputCounterLabel() {
        inputCounterLabel.setText(getCounterText(newTodoTextField.getText()));
    }

    private String getCounterText(String text) {
        if (text == null) {
            text = "";
        }

        return String.format("%s / %s", text.length(), MAX_LENGTH_NEW_TODO_INPUT);
    }

    private void updateTodoCounter() {
        int checkedTodos = countAllCheckedTodos();
        int allTodos = todoListBox.getChildren().size();
        todoCounterLabel.setText(String.format("%s / %s", checkedTodos, allTodos));
    }

    private int countAllCheckedTodos() {
        int counter = 0;
        for (Node node : todoListBox.getChildren()) {
            if (node instanceof CheckBox && ((CheckBox) node).isSelected()) {
                counter++;
            }
        }

        return counter;
    }

    @FXML
    private void dataSourceChanged() throws IOException {
        DatabaseConnection newSelectedDatabaseConnection = dataSourceDropDown.getValue();
        if (newSelectedDatabaseConnection == currentPrimaryDataConnection || newSelectedDatabaseConnection == null) {
            return;
        }

        if (newSelectedDatabaseConnection == placeHolderForNewDatabaseConnection) {
            currentPrimaryDataConnection = null;
            showDataSourceSelection();
            return;
        }

        initializeNewDatasource(newSelectedDatabaseConnection);
    }

    private void initializeNewDatasource(DatabaseConnection newSelectedDatabaseConnection) throws IOException {
        viewService.showLoadingDialog(languageService.getLocalizationText("mainView.loadingDialog.text"), attributes -> {
            try {
                dataSourceService.initializePrimaryDataSource(newSelectedDatabaseConnection);
                tryLoadingData();

                settingService.saveSetting(SettingService.CURRENT_PRIMARY_DATABASE_CONNECTION_KEY, newSelectedDatabaseConnection.getId());
                currentPrimaryDataConnection = newSelectedDatabaseConnection;

                Platform.runLater(() -> {
                    viewService.dismissDialog();
                });
            } catch (Exception ex) {
                ex.printStackTrace();

                try {
                    viewService.showInformationDialog(languageService.getLocalizationText("mainView.loadingData.error.title"),
                                                      String.format(languageService.getLocalizationText("mainView.loadingData.error.text"), ex.getMessage()));
                } catch (IOException iEx) {
                    iEx.printStackTrace();
                    AlertService.showAlert(iEx);
                    System.exit(1);
                }
            }
        });
    }

    private void showDataSourceSelection() throws IOException {
        viewService.registerLastView(ViewService.FILE_PATH_TODO_VIEW);
        viewService.showNewView(ViewService.FILE_PATH_DATASOURCE_SELECTION_VIEW);
    }

    @FXML
    private void deleteDataSource() throws IOException {
        DatabaseConnection selectedConnection = dataSourceDropDown.getValue();
        String title = languageService.getLocalizationText("todoView.dialog.removeDataSource.title");
        String text = String.format(languageService.getLocalizationText("todoView.dialog.removeDataSource.text"), selectedConnection.getId());
        viewService.showConfirmationDialog(title, text, attributes -> {
            settingService.deleteConnection(selectedConnection);
            dataSourceDropDown.getItems().remove(selectedConnection);

            if (currentPrimaryDataConnection == selectedConnection) {
                settingService.deleteSetting(SettingService.CURRENT_PRIMARY_DATABASE_CONNECTION_KEY);
                selectFirstDataSource();
            } else {
                dataSourceDropDown.setValue(currentPrimaryDataConnection);
            }
        });
    }

    private void selectFirstDataSource() {
        List<DatabaseConnection> databaseConnections = dataSourceDropDown.getItems();
        if (databaseConnections != null && databaseConnections.size() > 0) {
            dataSourceDropDown.setValue(databaseConnections.get(0));
        }
    }

    @FXML
    private void createTodo() throws IOException {
        if (isTodoTextValid(newTodoTextField.getText())) {
            try {
                Todo newTodo = todoService.saveNewTodo(newTodoTextField.getText());
                newTodoTextField.setText("");
                updateInputCounter();

                addNewTodoOnTopOfTheList(newTodo);
                updateTodoCounter();
            } catch (Exception ex) {
                ex.printStackTrace();
                viewService.showErrorDialog(ex);
            }
        } else {
            String dialogTitle = languageService.getLocalizationText("todoView.textIsNotRight.title");
            String dialogText = String.format(languageService.getLocalizationText("todoView.textIsNotRight.text"), MAX_LENGTH_NEW_TODO_INPUT);

            viewService.showInformationDialog(dialogTitle, dialogText);
        }
    }

    @FXML
    private void settingsButtonClicked() throws IOException {
        showSettingView();
    }

    private void showSettingView() throws IOException {
        viewService.registerLastView(ViewService.FILE_PATH_TODO_VIEW);
        viewService.showNewView(ViewService.FILE_PATH_SETTINGS_VIEW);
    }

    private boolean isTodoTextValid(String text) {
        return !StringUtils.isBlank(text) && text.length() <= MAX_LENGTH_NEW_TODO_INPUT;
    }

    @FXML
    private void addContextMenuToTodoCheckBox(CheckBox checkBox, Todo todo) {
        ContextMenu contextMenu = new ContextMenu();

        addEditMenuItem(contextMenu, checkBox, todo);
        addDeleteMenuItem(contextMenu, checkBox, todo);

        checkBox.setContextMenu(contextMenu);
    }

    private void addEditMenuItem(ContextMenu contextMenu, CheckBox checkBox, Todo todo) {
        MenuItem item = new MenuItem(languageService.getLocalizationText("todoView.contextMenu.edit"));
        contextMenu.getItems().add(item);

        item.setOnAction(event -> {
            try {
                String dialogTitle = languageService.getLocalizationText("todoView.dialog.editTodo.title");
                String dialogText = String.format(languageService.getLocalizationText("todoView.dialog.editTodo.text"), MAX_LENGTH_NEW_TODO_INPUT);

                TextInputDialogController.InputValidation inputValidation = createInputValidation();
                CallBack callBack = createInputCallBack(checkBox, todo);

                viewService.showInputDialog(dialogTitle, dialogText, todo.getText(), inputValidation, true, callBack);

            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private CallBack createInputCallBack(CheckBox checkBox, Todo todo) {
        return attributes -> {
            String newText = (String) attributes[0];

            checkBox.setText(newText);

            todo.setText(newText);
            try {
                todoService.saveTodo(todo);
            } catch (Exception ex) {
                showError(ex);
            }
        };
    }

    private void showError(Exception ex) {
        try {
            viewService.showErrorDialog(ex);
        } catch (IOException iEx) {
            iEx.printStackTrace();
            AlertService.showAlert(iEx);
        }
    }

    private TextInputDialogController.InputValidation createInputValidation() {
        return textInput -> {
            ValidationResult result = new ValidationResult();

            result.showInformationText = true;
            result.isValid = isTodoTextValid(textInput);
            if (result.isValid) {
                result.informationText = getCounterText(textInput);
            } else {
                result.informationText = String.format(languageService.getLocalizationText("todoView.textIsNotRight.text"), MAX_LENGTH_NEW_TODO_INPUT);
            }

            return result;
        };
    }

    private void addDeleteMenuItem(ContextMenu contextMenu, CheckBox checkBox, Todo todo) {
        MenuItem item = new MenuItem(languageService.getLocalizationText("todoView.contextMenu.delete"));
        contextMenu.getItems().add(item);

        item.setOnAction(event -> {
            try {
                String title = languageService.getLocalizationText("todoView.dialog.removeTodo.title");
                String text = languageService.getLocalizationText("todoView.dialog.removeTodo.text");
                viewService.showConfirmationDialog(title, text, attributes -> {
                    deleteTodo(todo, checkBox);
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void deleteTodo(Todo todo, CheckBox checkBox) {
        try {
            todoService.deleteTodo(todo);
            removeTodoFromTheList(checkBox);
            updateTodoCounter();
        } catch (Exception ex) {
            ex.printStackTrace();
            showError(ex);
        }
    }

    @Override
    public void afterViewIsInitialized() {
        Platform.runLater(() -> {
            try {
                selectLastUsedDataSourceAndLoadData();
            } catch (Exception ex) {
                ex.printStackTrace();
                AlertService.showAlert(ex);
                System.exit(1);
            }
        });
    }
}
