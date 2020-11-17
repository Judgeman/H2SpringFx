package de.judgeman.H2SpringFx.ViewControllers.Abstract;

import de.judgeman.H2SpringFx.Services.ViewService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 * Created by Paul Richter on Sat 26/09/2020
 */
@Controller
public class BaseDialogController extends BaseViewController {

    @Autowired
    private ViewService viewService;

    @FXML
    protected Label titleLabel;
    @FXML
    protected Label informationLabel;
    @FXML
    protected Button okButton;

    public void initialize() {
        Platform.runLater(() -> okButton.requestFocus());
    }

    public void okButtonClicked() {
        viewService.dismissDialog();
    }

    public void cancelButtonClicked() {
        viewService.dismissDialog();
    }

    public void setTitle(String title) {
        titleLabel.setText(title);
    }

    public void setInformation(String information) {
        informationLabel.setText(information);
    }
}
