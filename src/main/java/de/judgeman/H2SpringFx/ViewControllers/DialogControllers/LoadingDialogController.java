package de.judgeman.H2SpringFx.ViewControllers.DialogControllers;

import de.judgeman.H2SpringFx.HelperClasses.CallBack;
import de.judgeman.H2SpringFx.Services.ViewService;
import de.judgeman.H2SpringFx.ViewControllers.Abstract.BaseViewController;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 * Created by Paul Richter on Tue 01/12/2020
 */
@Controller
public class LoadingDialogController extends BaseViewController {

    @Autowired
    private ViewService viewService;

    @FXML
    private Label loadingTextLabel;
    @FXML
    private Button cancelButton;

    private CallBack cancelCallBack;

    public LoadingDialogController() {

    }

    public void setLoadingText(String text) {
        loadingTextLabel.setText(text);
    }

    public void enableCancelButton(boolean withCancelButton, CallBack cancelCallBack) {
        cancelButton.setVisible(withCancelButton);
        this.cancelCallBack = cancelCallBack;
    }

    public void cancelButtonClicked() {
        if (cancelCallBack == null) {
            viewService.dismissDialog();
            return;
        }

        cancelCallBack.execute();
    }

    @Override
    public void afterViewIsInitialized() {

    }
}
