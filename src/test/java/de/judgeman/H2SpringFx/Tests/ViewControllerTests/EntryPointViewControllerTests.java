package de.judgeman.H2SpringFx.Tests.ViewControllerTests;

import de.judgeman.H2SpringFx.Services.LanguageService;
import de.judgeman.H2SpringFx.Services.SettingService;
import de.judgeman.H2SpringFx.Services.ViewService;
import de.judgeman.H2SpringFx.Tests.HelperClasses.UITestFxApp;
import de.judgeman.H2SpringFx.Tests.HelperClasses.UITestingService;
import de.judgeman.H2SpringFx.ViewControllers.EntryPointViewController;
import de.judgeman.H2SpringFx.ViewControllers.MainViewController;
import javafx.application.Application;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.loadui.testfx.utils.FXTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;

@SpringBootTest
@TestPropertySource(locations="classpath:test.properties")
public class EntryPointViewControllerTests {

    @Autowired
    private ViewService viewService;

    @Autowired
    private SettingService settingService;

    @Autowired
    private LanguageService languageService;

    @BeforeAll
    public static void setupTestGUI() {
        // avoid AWT headless exception
        System.setProperty("java.awt.headless", "false");

        if (!UITestFxApp.isAppRunning) {
            FXTestUtils.launchApp(UITestFxApp.class);
        }
    }

    @Test
    public void selectLastUsedLanguageTest() {
        languageService.saveNewLanguageSetting(Locale.CANADA);

        EntryPointViewController entryPointViewController = (EntryPointViewController) UITestingService.getViewControllerForFXML(viewService, ViewService.FILE_PATH_ENTRY_POINT);
        Assert.assertSame(null, entryPointViewController.getSelectedLanguage());
    }

    @Test
    public void setOtherLanguageTest() {
        EntryPointViewController entryPointViewController = (EntryPointViewController) UITestingService.getViewControllerForFXML(viewService, ViewService.FILE_PATH_ENTRY_POINT);

        entryPointViewController.setLanguageComboBoxValue(null);
        Assert.assertSame(null, entryPointViewController.getSelectedLanguage());

        entryPointViewController.setLanguageComboBoxValue(Locale.CANADA);
        Assert.assertSame(null, entryPointViewController.getSelectedLanguage());

        entryPointViewController.setLanguageComboBoxValue(Locale.US);
        Assert.assertSame(Locale.US, entryPointViewController.getSelectedLanguage());

        entryPointViewController.setNewLanguage();
        String savedLanguage = settingService.loadSetting(SettingService.LANGUAGE_ENTRY_KEY);
        Assert.assertSame(Locale.US.toLanguageTag(), savedLanguage);
    }

    @Test
    public void saveInputValueTest() {
        EntryPointViewController entryPointViewController = (EntryPointViewController) UITestingService.getViewControllerForFXML(viewService, ViewService.FILE_PATH_ENTRY_POINT);

        String testValue = "Test Bob";

        entryPointViewController.setInputValue(testValue);
        Assert.assertEquals(testValue, entryPointViewController.getInputValue());

        entryPointViewController.saveValue();

        String savedValue = settingService.loadSetting("TestSettingEntry");
        Assert.assertEquals(testValue, savedValue);

        entryPointViewController.setInputValue(null);
        entryPointViewController.loadSavedValue();

        Assert.assertEquals(testValue, entryPointViewController.getInputValue());
    }

    @Test
    public void changeDialogFeedbackTest() {
        viewService.registerMainViewController(null);
        EntryPointViewController entryPointViewController = (EntryPointViewController) UITestingService.getViewControllerForFXML(viewService, ViewService.FILE_PATH_ENTRY_POINT);
        entryPointViewController.setDialogFeedbackCheckBoxValue(true);
        entryPointViewController.dialogFeedbackCheckBoxChange();

        Assert.assertEquals("", entryPointViewController.getSettingLabelValue());

        MainViewController mainViewController = UITestingService.getNewMainController(viewService);
        viewService.registerMainViewController(mainViewController);
        Pane glassPane = mainViewController.getGlassPane();

        Assert.assertSame(0, glassPane.getChildren().size());

        entryPointViewController.setDialogFeedbackCheckBoxValue(true);
        entryPointViewController.dialogFeedbackCheckBoxChange();

        Assert.assertSame(1, glassPane.getChildren().size());

        AtomicBoolean callBackCalled = new AtomicBoolean(false);
        viewService.dismissDialog(event -> {
            callBackCalled.set(true);
        });
        UITestingService.waitForAnimationFinished(5, callBackCalled);
        Assert.assertSame(0, glassPane.getChildren().size());

        entryPointViewController.setDialogFeedbackCheckBoxValue(false);
        entryPointViewController.dialogFeedbackCheckBoxChange();

        Assert.assertEquals(languageService.getLocalizationText("changedSettingDontUseDialogForFeedback"), entryPointViewController.getSettingLabelValue());
    }
}
