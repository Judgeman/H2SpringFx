package de.judgeman.H2SpringFx.Services;

import de.judgeman.H2SpringFx.HelperClasses.ViewRootAndControllerPair;
import de.judgeman.H2SpringFx.ViewControllers.Abstract.ViewController;
import de.judgeman.H2SpringFx.ViewControllers.DialogControllers.InformationDialogController;
import de.judgeman.H2SpringFx.ViewControllers.MainViewController;
import javafx.animation.FadeTransition;
import javafx.animation.SequentialTransition;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URL;

@Service
public class ViewService {

    public static final String FILE_PATH_DEFAULT_STYLE_CSS = "/h2SpringFxStyle.css";

    public static final String FILE_PATH_MAIN_VIEW = "/views/MainView.fxml";
    public static final String FILE_PATH_SPLASH_SCREEN = "/SplashScreen.fxml";
    public static final String FILE_PATH_ENTRY_POINT = "/views/EntryPointView.fxml";

    public static final String FILE_DIALOG_INFORMATION = "/views/dialogViews/informationDialog.fxml";

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
        ViewController viewController = fxmlLoader.getController();

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
        ViewRootAndControllerPair viewRootAndControllerPair = getRootAndViewControllerFromFXML(FILE_DIALOG_INFORMATION);
        InformationDialogController informationDialogController = ((InformationDialogController) viewRootAndControllerPair.getViewController());

        informationDialogController.setTitle(title);
        informationDialogController.setInformation(information);

        showDialog(viewRootAndControllerPair.getRoot());
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
        dismissRootElementFromGlassPane();

        FadeTransition dialogBackgroundPaneFadeOutTransition = animationService.createFadeOutTransition(mainViewController.getDialogOverLayer());
        FadeTransition dialogRootFadeOutTransition = animationService.createFadeOutTransition(mainViewController.getGlassPane());

        dialogRootFadeOutTransition.setOnFinished(event -> {
            mainViewController.getGlassPane().setVisible(false);
            mainViewController.getDialogOverLayer().setVisible(false);
            mainViewController.getGlassPane().getChildren().clear();
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

}
