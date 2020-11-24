package de.judgeman.H2SpringFx;

import de.judgeman.H2SpringFx.Services.AlertService;
import de.judgeman.H2SpringFx.ViewControllers.MainViewController;
import de.judgeman.H2SpringFx.Services.LanguageService;
import de.judgeman.H2SpringFx.Services.ViewService;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * Created by Paul Richter on Mon 30/03/2020
 */
@SpringBootApplication
public class H2SpringFxApplication extends Application {

    private ConfigurableApplicationContext springContext;
    private Parent root;

    private LanguageService languageService;
    private ViewService viewService;

    private Exception exceptionOnStartup;

    @Override
    public void init() {
        try {
            springContext = SpringApplication.run(H2SpringFxApplication.class);

            languageService = springContext.getBean(LanguageService.class);
            viewService = springContext.getBean(ViewService.class);

            root = viewService.getRootElementFromFXML(ViewService.FILE_PATH_MAIN_VIEW);
            viewService.registerMainViewController(springContext.getBean(MainViewController.class));
        } catch (Exception ex) {
            ex.printStackTrace();
            exceptionOnStartup = ex;
        }
    }

    @Override
    public void start(Stage primaryStage) {
        if (exceptionOnStartup != null) {
            AlertService.showAlert(exceptionOnStartup);
            return;
        }

        primaryStage.setTitle(languageService.getLocalizationText("applicationTitle"));
        primaryStage.setScene(new Scene(root, ViewService.DEFAULT_WIDTH, ViewService.DEFAULT_HEIGHT));

        viewService.registerPrimaryStage(primaryStage);
        viewService.setDefaultStyleCss(primaryStage);
        viewService.restoreScenePositionAndSize(primaryStage);

        primaryStage.show();
    }

    @Override
    public void stop() {
        if (springContext != null) {
            springContext.stop();
        }
    }
}
