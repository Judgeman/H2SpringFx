package de.judgeman.H2SpringFx;

import de.judgeman.H2SpringFx.Services.LanguageService;
import de.judgeman.H2SpringFx.Services.ViewService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
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

    @Override
    public void init() throws Exception {
        springContext = SpringApplication.run(H2SpringFxApplication.class);

        languageService = springContext.getBean(LanguageService.class);
        viewService = springContext.getBean(ViewService.class);

        FXMLLoader fxmlLoader = new FXMLLoader(viewService.GetUrlForView(ViewService.FILE_PATH_ENTRY_POINT));
        languageService.registerFXMLLoader(fxmlLoader);
        languageService.restoreLastUsedOrDefaultResourceBundle();
        fxmlLoader.setControllerFactory(springContext::getBean);

        root = fxmlLoader.load();
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle(languageService.getLocalizationText("applicationTitle"));
        primaryStage.setScene(new Scene(root, ViewService.DEFAULT_WIDTH, ViewService.DEFAULT_HEIGHT));

        viewService.setDefaultStyleCss(primaryStage);
        viewService.restoreScenePositionAndSize(primaryStage);

        primaryStage.show();
    }

    @Override
    public void stop() {
        springContext.stop();
    }
}
