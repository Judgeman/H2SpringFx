package de.judgeman.H2SpringFx.Services;

import javafx.scene.control.Alert;

/**
 * Created by Paul Richter on Tue 24/11/2020
 */
public class AlertService {

    private AlertService() {

    }

    public static void showAlert(Alert.AlertType alertType, String title, String headerText, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(message);

        alert.showAndWait();
    }

    public static void showAlert(Exception ex) {
        showAlert(Alert.AlertType.ERROR,
                 "Ups something went wrong",
                 "Error on loading",
                  ex.getMessage());
    }
}
