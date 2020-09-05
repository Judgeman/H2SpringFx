package de.judgeman.H2SpringFx.Services;

import javafx.fxml.FXMLLoader;
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

    public static final String FILE_PATH_SPLASH_SCREEN = "/SplashScreen.fxml";
    public static final String FILE_PATH_ENTRY_POINT = "/EntryPointView.fxml";

    public static final double DEFAULT_WIDTH = 800;
    public static final double DEFAULT_HEIGHT = 600;

    public static final double DEFAULT_WIDTH_SPLASH_SCREEN = 500;
    public static final double DEFAULT_HEIGHT_SPLASH_SCREEN = 200;

    private Stage primaryStage;

    @Autowired
    private ConfigurableApplicationContext springContext;

    @Autowired
    private SettingService settingService;

    @Autowired
    private LanguageService languageService;

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

    public Parent getRootElementFromFXML(String fxmlPath) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(GetUrlForView(fxmlPath));
        fxmlLoader.setResources(languageService.getCurrentUsedResourceBundle());
        fxmlLoader.setControllerFactory(springContext::getBean);

        return fxmlLoader.load();
    }

    public void registerPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

}
