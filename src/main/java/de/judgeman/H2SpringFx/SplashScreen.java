package de.judgeman.H2SpringFx;

import javafx.application.Preloader;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.net.URL;

/**
 * Created by Paul Richter on Mon 30/03/2020
 */
public class SplashScreen extends Preloader {

    private Stage stage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.stage = primaryStage;
        stage.initStyle(StageStyle.UNDECORATED);

        URL url = getClass().getResource("/SplashScreen.fxml");
        FXMLLoader fxmlLoader = new FXMLLoader(url);
        Parent root = fxmlLoader.load();

        Scene scene = new Scene(root, 500, 200);
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void handleStateChangeNotification(StateChangeNotification stateChangeNotification) {
        if (stateChangeNotification.getType() == StateChangeNotification.Type.BEFORE_START) {
            stage.hide();
        }
    }
}
