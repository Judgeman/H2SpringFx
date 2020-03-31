package de.judgeman.H2SpringFx.Controller;

import de.judgeman.H2SpringFx.Services.FileService;
import de.judgeman.H2SpringFx.Services.LogService;
import de.judgeman.H2SpringFx.Services.SettingService;
import javafx.fxml.FXML;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.layout.AnchorPane;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by Paul Richter on Sun 30/03/2020
 */
@Component
public class EntryPointViewController {

    private Logger logger = LogService.getLogger(this.getClass());

    @Autowired
    private SettingService settingService;

    @FXML
    public AnchorPane anchorPane;

    public Label settingLabel;

    public TextField valueInput;

    public void ShowContextMenuOnEmptySpace(ContextMenuEvent event) {
        ContextMenu contextMenu = new ContextMenu();
        contextMenu.setImpl_showRelativeToWindow(true);

        MenuItem item1 = new MenuItem("Menu Item 1");
        contextMenu.getItems().add(item1);

        contextMenu.show(anchorPane, event.getScreenX(), event.getScreenY());
    }

    public void saveValue() {
        String value = valueInput.getText();

        settingService.SaveSetting("TestSettingEntry", value);
        settingLabel.setText("Saved value: " + value);
    }

    public void loadSavedValue() {
        String testSetting = settingService.LoadSetting("TestSettingEntry");
        logger.info("TestSetting: " + testSetting);

        settingLabel.setText("Loaded value: " + testSetting);
    }
}
