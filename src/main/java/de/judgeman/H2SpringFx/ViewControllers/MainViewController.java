package de.judgeman.H2SpringFx.ViewControllers;

import de.judgeman.H2SpringFx.Services.LogService;
import de.judgeman.H2SpringFx.Services.SettingService;
import de.judgeman.H2SpringFx.Services.ViewService;
import de.judgeman.H2SpringFx.ViewControllers.Abstract.ViewController;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 * Created by Paul Richter on Thu 03/09/2020
 */
@Controller
public class MainViewController extends ViewController {

    private Logger logger = LogService.getLogger(this.getClass());

    @Autowired
    private ViewService viewService;

    @Autowired
    private SettingService settingService;

    @FXML
    private Pane contentPane;
    @FXML
    private Pane glassPane;
    @FXML
    private Pane dialogOverLayer;

    public Pane getGlassPane() {
        return glassPane;
    }

    public void setGlassPane(Pane glassPane) {
        this.glassPane = glassPane;
    }

    public Pane getContentPane() {
        return contentPane;
    }

    public Pane getDialogOverLayer() {
        return dialogOverLayer;
    }

    public void setDialogOverLayer(Pane dialogOverLayer) {
        this.dialogOverLayer = dialogOverLayer;
    }

    @FXML
    public void initialize() {
        showEntryPointView();
    }

    private void showEntryPointView() {
        Parent root = viewService.getRootElementFromFXML(ViewService.FILE_PATH_ENTRY_POINT);
        removeLastVisibleView();
        showNewView(root);
    }

    private void showNewView(Parent root) {
        contentPane.getChildren().add(root);
    }

    private void removeLastVisibleView() {
        contentPane.getChildren().removeAll();
    }
}