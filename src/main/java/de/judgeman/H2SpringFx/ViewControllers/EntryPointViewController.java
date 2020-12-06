package de.judgeman.H2SpringFx.ViewControllers;

import de.judgeman.H2SpringFx.Services.LanguageService;
import de.judgeman.H2SpringFx.Services.LogService;
import de.judgeman.H2SpringFx.Services.SettingService;
import de.judgeman.H2SpringFx.Services.ViewService;
import de.judgeman.H2SpringFx.ViewControllers.Abstract.ViewController;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.layout.AnchorPane;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Locale;

/**
 * Created by Paul Richter on Sun 30/03/2020
 */
@Component
public class EntryPointViewController extends ViewController {

    private final Logger logger = LogService.getLogger(this.getClass());

    @Autowired
    private SettingService settingService;

    @Autowired
    private LanguageService languageService;

    @Autowired
    private ViewService viewService;

    @FXML
    private AnchorPane anchorPane;
    @FXML
    private ComboBox<Locale> languageComboBox;
    @FXML
    private Label titleLabel;
    @FXML
    private Label settingLabel;
    @FXML
    private Label setLanguageLabel;
    @FXML
    private TextField valueInput;
    @FXML
    private Button saveButton;
    @FXML
    private Button loadButton;
    @FXML
    private CheckBox dialogFeedbackCheckbox;

    @FXML
    public void initialize() {
        initLanguageComboBox();
        selectLastUsedLanguage();
        restoreCheckBoxValueForUserFeedback();
    }

    private void restoreCheckBoxValueForUserFeedback() {
        String value = settingService.loadSetting(SettingService.USE_DIALOG_ENTRY_KEY);
        boolean isSelected = Boolean.parseBoolean(value);
        dialogFeedbackCheckbox.setSelected(isSelected);
    }

    private void selectLastUsedLanguage() {
        Locale lastUsedLanguage = languageService.getLastUsedOrDefaultLanguage();
        if (languageComboBox.getItems().contains(lastUsedLanguage)) {
            languageComboBox.setValue(lastUsedLanguage);
        } else {
            languageComboBox.setValue(null);
        }
    }

    private void initLanguageComboBox() {
        languageComboBox.setItems(FXCollections.observableArrayList(languageService.getAvailableLanguages()));
    }

    public void saveValue() {
        String value = valueInput.getText();

        settingService.saveSetting("TestSettingEntry", value);
        showUserFeedback(String.format(languageService.getLocalizationText("savedValue"), value));

        logger.info(String.format("Saved value: %s", value));
    }

    public void loadSavedValue() {
        String testSettingValue = settingService.loadSetting("TestSettingEntry");
        logger.info(String.format("Loaded value: %s", testSettingValue));

        showUserFeedback(String.format(languageService.getLocalizationText("loadedValue"), testSettingValue));
        valueInput.setText(testSettingValue);
    }

    public void setNewLanguage() {
        Locale newLanguage = languageComboBox.getValue();

        languageService.setNewLanguage(newLanguage);
        logger.info(String.format("Set new language: %s", newLanguage));
        setNewTextValuesForAllLabels();

        languageService.saveNewLanguageSetting(newLanguage);

        showUserFeedback(String.format(languageService.getLocalizationText("setNewLanguage"), newLanguage));
    }

    private void showUserFeedback(String informationText) {
        showUserFeedback(languageService.getLocalizationText("defaultTitleForInformationDialog"), informationText);
    }

    private void showUserFeedback(String dialogTitle, String informationText) {
        if (dialogFeedbackCheckbox.isSelected()) {
            try {
                viewService.showInformationDialog(dialogTitle, informationText);
            } catch (Exception ex) {
                ex.printStackTrace();
                // log and ignore
            }
        } else {
            settingLabel.setText(informationText);
        }
    }

    public void dialogFeedbackCheckBoxChange() {
        if (dialogFeedbackCheckbox.isSelected()) {
            showUserFeedback(languageService.getLocalizationText("changedSettingUseDialogForFeedbackTitle"), languageService.getLocalizationText("changedSettingUseDialogForFeedback"));
            resetSettingLabel();
        }
        else {
            showUserFeedback(languageService.getLocalizationText("changedSettingDontUseDialogForFeedback"));
        }

        saveUserFeedbackCheckBoxValue();
    }

    private void saveUserFeedbackCheckBoxValue() {
        settingService.saveSetting(SettingService.USE_DIALOG_ENTRY_KEY, String.format("%s", dialogFeedbackCheckbox.isSelected()));
    }

    private void resetSettingLabel() {
        settingLabel.setText("");
    }

    private void setNewTextValuesForAllLabels() {
        titleLabel.setText(languageService.getLocalizationText("overviewTitle"));
        saveButton.setText(languageService.getLocalizationText("saveValue"));
        loadButton.setText(languageService.getLocalizationText("loadSavedValue"));
        setLanguageLabel.setText(languageService.getLocalizationText("setLanguage"));

        logger.info("Updated text properties with new localization");
    }
}
