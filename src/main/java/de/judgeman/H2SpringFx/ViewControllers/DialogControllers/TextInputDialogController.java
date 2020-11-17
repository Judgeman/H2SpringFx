package de.judgeman.H2SpringFx.ViewControllers.DialogControllers;

import de.judgeman.H2SpringFx.HelperClasses.CallBack;
import de.judgeman.H2SpringFx.HelperClasses.ValidationResult;
import de.judgeman.H2SpringFx.Services.ViewService;
import de.judgeman.H2SpringFx.ViewControllers.Abstract.BaseDialogController;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 * Created by Paul Richter on Fri 26/09/2020
 */
@Controller
public class TextInputDialogController extends BaseDialogController {

    @Autowired
    private ViewService viewService;

    private InputValidation inputValidation;

    private CallBack callBack;
    @FXML
    private Label validationInformationLabel;
    @FXML
    private TextField textInput;

    @Override
    public void okButtonClicked() {
        if (!validateInput()) {
            return;
        }

        viewService.dismissDialog(attributes -> {
            if (callBack != null) {
                callBack.execute(textInput.getText());
            }
        });
    }

    public boolean validateInput() {
        if (inputValidation == null) {
            okButton.setDisable(false);
            return true;
        }

        ValidationResult validationResult = inputValidation.validateInput(textInput.getText());
        if (validationResult.showInformationText) {
            validationInformationLabel.setText(validationResult.informationText);
        }

        okButton.setDisable(!validationResult.isValid);
        return validationResult.isValid;
    }

    public void setInitialInputText(String text) {
        textInput.setText(text);
    }

    public void setValidationCheckForInput(InputValidation inputValidation) {
        this.inputValidation = inputValidation;
    }

    public void setCallBack(CallBack callBack) {
        this.callBack = callBack;
    }

    public interface InputValidation
    {
        ValidationResult validateInput(String textInput);
    }
}
