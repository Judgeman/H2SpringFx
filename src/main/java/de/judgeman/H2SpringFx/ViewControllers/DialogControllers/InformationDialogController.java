package de.judgeman.H2SpringFx.ViewControllers.DialogControllers;

import de.judgeman.H2SpringFx.HelperClasses.CallBack;
import de.judgeman.H2SpringFx.Services.ViewService;
import de.judgeman.H2SpringFx.ViewControllers.Abstract.ViewController;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by Paul Richter on Fri 04/09/2020
 */
@Component
public class InformationDialogController extends ViewController {

    @Autowired
    private ViewService viewService;

    @FXML
    private Label titleLabel;
    @FXML
    private Label informationLabel;
    @FXML
    private Button okButton;

    private CallBack callBack;

    public void okButtonClicked() {
        viewService.dismissDialog(callBack);
    }

    public void setTitle(String title) {
        titleLabel.setText(title);
    }

    public void setInformation(String information) {
        informationLabel.setText(information);
    }

    public void setCallBack(CallBack callBack) {
        this.callBack = callBack;
    }
}