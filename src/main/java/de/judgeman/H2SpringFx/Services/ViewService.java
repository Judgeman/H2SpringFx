package de.judgeman.H2SpringFx.Services;

import de.judgeman.H2SpringFx.HelperClasses.CallBack;
import de.judgeman.H2SpringFx.HelperClasses.ViewRootAndControllerPair;
import de.judgeman.H2SpringFx.ViewControllers.Abstract.BaseDialogController;
import de.judgeman.H2SpringFx.ViewControllers.Abstract.BaseViewController;
import de.judgeman.H2SpringFx.ViewControllers.DialogControllers.ConfirmDialogController;
import de.judgeman.H2SpringFx.ViewControllers.DialogControllers.TextInputDialogController;
import de.judgeman.H2SpringFx.ViewControllers.MainViewController;
import javafx.animation.FadeTransition;
import javafx.animation.SequentialTransition;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.net.URL;

@Service
public class ViewService {

    public static final String FILE_PATH_DEFAULT_STYLE_CSS = "/h2SpringFxStyle.css";

    public static final String FILE_PATH_MAIN_VIEW = "/views/MainView.fxml";
    public static final String FILE_PATH_SPLASH_SCREEN = "/views/SplashScreen.fxml";

    public static final String FILE_PATH_DIALOG_INFORMATION = "/views/dialogViews/InformationDialog.fxml";
    public static final String FILE_PATH_DIALOG_CONFIRMATION = "/views/dialogViews/ConfirmDialog.fxml";
    public static final String FILE_PATH_DIALOG_TEXT_INPUT = "/views/dialogViews/TextInputDialog.fxml";

    public static final String FILE_PATH_DATASOURCE_SELECTION_VIEW = "/views/DataSourceSelectionView.fxml";
    public static final String FILE_PATH_SETTINGS_VIEW = "/views/SettingsView.fxml";
    public static final String FILE_PATH_TODO_VIEW = "/views/TodoView.fxml";

    public static final double DEFAULT_WIDTH = 800;
    public static final double DEFAULT_HEIGHT = 600;

    public static final double DEFAULT_WIDTH_SPLASH_SCREEN = 500;
    public static final double DEFAULT_HEIGHT_SPLASH_SCREEN = 200;

    private Stage primaryStage;

    private MainViewController mainViewController;

    @Autowired
    private ConfigurableApplicationContext springContext;

    @Autowired
    private SettingService settingService;

    @Autowired
    private LanguageService languageService;

    @Autowired
    private AnimationService animationService;

    public void registerMainViewController(MainViewController mainViewController) {
        this.mainViewController = mainViewController;
    }

    public URL GetUrlForView(String filePath) {
        return getClass().getResource(filePath);
    }

    public void restoreScenePositionAndSize(Stage stage) {
        // TODO: load last position of the stage
        // TODO: load last size of the stage
    }

    public void setDefaultStyleCss(Stage stage) {
        stage.getScene().getStylesheets().removeAll();
        stage.getScene().getStylesheets().add(getClass().getResource(FILE_PATH_DEFAULT_STYLE_CSS).toExternalForm());
    }

    public ViewRootAndControllerPair getRootAndViewControllerFromFXML(String fxmlPath) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(GetUrlForView(fxmlPath));
        fxmlLoader.setResources(languageService.getCurrentUsedResourceBundle());
        fxmlLoader.setControllerFactory(springContext::getBean);

        Parent root = fxmlLoader.load();
        BaseViewController viewController = fxmlLoader.getController();

        return new ViewRootAndControllerPair(root, viewController);
    }

    public Parent getRootElementFromFXML(String fxmlPath) throws IOException {
        return getRootAndViewControllerFromFXML(fxmlPath).getRoot();
    }

    public void registerPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public void showInformationDialog(String title, String information) throws IOException {
        showDialog(initDialog(title, information, FILE_PATH_DIALOG_INFORMATION).getRoot());
    }

    public void showConfirmationDialog(String title, String information, CallBack callBack) throws IOException {
        ViewRootAndControllerPair viewRootAndControllerPair = initDialog(title, information, FILE_PATH_DIALOG_CONFIRMATION);
        ((ConfirmDialogController) viewRootAndControllerPair.getViewController()).setCallBack(callBack);

        showDialog(viewRootAndControllerPair.getRoot());
    }

    public void showInputDialog(String title,
                                String information,
                                String initialInputText,
                                TextInputDialogController.InputValidation validation,
                                boolean initialValidation,
                                CallBack callBack) throws IOException {
        ViewRootAndControllerPair viewRootAndControllerPair = initDialog(title, information, FILE_PATH_DIALOG_TEXT_INPUT);

        TextInputDialogController inputController = (TextInputDialogController) viewRootAndControllerPair.getViewController();
        inputController.setInitialInputText(initialInputText);
        inputController.setValidationCheckForInput(validation);
        inputController.setCallBack(callBack);

        if (initialValidation) {
            inputController.validateInput();
        }

        showDialog(viewRootAndControllerPair.getRoot());
    }

    private ViewRootAndControllerPair initDialog(String title, String information, String dialogFilePath) throws IOException {
        ViewRootAndControllerPair viewRootAndControllerPair = getRootAndViewControllerFromFXML(dialogFilePath);
        BaseDialogController dialogController = ((BaseDialogController) viewRootAndControllerPair.getViewController());

        dialogController.setTitle(title);
        dialogController.setInformation(information);

        return viewRootAndControllerPair;
    }

    private void showDialog(Parent dialogRoot) {
        FadeTransition dialogBackgroundFadeInTransition = animationService.createFadeInTransition(mainViewController.getDialogOverLayer());
        FadeTransition dialogRootFadeInTransition = animationService.createFadeInTransition(mainViewController.getGlassPane());
        SequentialTransition bounceTransition = animationService.createBounceInTransition(dialogRoot);

        mainViewController.getDialogOverLayer().setVisible(true);
        mainViewController.getGlassPane().setVisible(true);

        mainViewController.getGlassPane().getChildren().add(dialogRoot);

        dialogBackgroundFadeInTransition.play();
        dialogRootFadeInTransition.play();
        bounceTransition.play();
    }

    public void dismissDialog() {
        dismissDialog(null);
    }

    public void dismissDialog(CallBack callBack) {
        dismissRootElementFromGlassPane();

        FadeTransition dialogBackgroundPaneFadeOutTransition = animationService.createFadeOutTransition(mainViewController.getDialogOverLayer());
        FadeTransition dialogRootFadeOutTransition = animationService.createFadeOutTransition(mainViewController.getGlassPane());

        dialogRootFadeOutTransition.setOnFinished(event -> {
            mainViewController.getGlassPane().setVisible(false);
            mainViewController.getDialogOverLayer().setVisible(false);
            mainViewController.getGlassPane().getChildren().clear();

            if (callBack != null) {
                callBack.execute();
            }
        });

        SequentialTransition sequentialTransition = new SequentialTransition();
        sequentialTransition.getChildren().addAll(dialogBackgroundPaneFadeOutTransition, dialogRootFadeOutTransition);
        sequentialTransition.play();
    }

    private void dismissRootElementFromGlassPane() {
        ObservableList<Node> elementsOnGlassPane = mainViewController.getGlassPane().getChildren();
        if (elementsOnGlassPane != null && elementsOnGlassPane.size() > 0) {
            animationService.createBounceOutTransition(elementsOnGlassPane.get(0)).play();
        }
    }

    public File getDirectoryFromUser(String title) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle(title);

        return directoryChooser.showDialog(primaryStage);
    }

    public MainViewController getMainViewController() {
        return mainViewController;
    }

    public void registerLastView(String filePathToView) {
        getMainViewController().setLastViewPath(filePathToView);
    }

    public void showNewView(String filePathToView) throws IOException {
        getMainViewController().loadAndShowView(filePathToView);
    }
}
