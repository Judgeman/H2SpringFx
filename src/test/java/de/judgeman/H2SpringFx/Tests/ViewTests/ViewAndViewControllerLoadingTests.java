package de.judgeman.H2SpringFx.Tests.ViewTests;

import de.judgeman.H2SpringFx.HelperClasses.ViewRootAndControllerPair;
import de.judgeman.H2SpringFx.Services.ViewService;
import de.judgeman.H2SpringFx.Tests.HelperClasses.UITestFxApp;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.loadui.testfx.utils.FXTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.support.ResourcePatternResolver;

/**
 * Created by Paul Richter on Tue 05/01/2021
 */
@SpringBootTest
public class ViewAndViewControllerLoadingTests {

    @Autowired
    private ViewService viewService;

    @Autowired
    ResourcePatternResolver resourceResolver;

    @BeforeAll
    public static void setupTestGUI() {
        // avoid AWT headless exception
        System.setProperty("java.awt.headless", "false");

        if (!UITestFxApp.isAppRunning) {
            FXTestUtils.launchApp(UITestFxApp.class);
        }
    }

    @Test
    public void loadViewAndControllers() {
        loadViewAndControllerAndAssertIt(ViewService.FILE_PATH_SPLASH_SCREEN, false);

        loadViewAndControllerAndAssertIt(ViewService.FILE_PATH_MAIN_VIEW, true);
        loadViewAndControllerAndAssertIt(ViewService.FILE_PATH_TODO_VIEW, true);
        loadViewAndControllerAndAssertIt(ViewService.FILE_PATH_SETTINGS_VIEW, true);
        loadViewAndControllerAndAssertIt(ViewService.FILE_PATH_DATASOURCE_SELECTION_VIEW, true);

        loadViewAndControllerAndAssertIt(ViewService.FILE_PATH_DIALOG_CONFIRMATION, true);
        loadViewAndControllerAndAssertIt(ViewService.FILE_PATH_DIALOG_INFORMATION, true);
        loadViewAndControllerAndAssertIt(ViewService.FILE_PATH_DIALOG_LOADING, true);
        loadViewAndControllerAndAssertIt(ViewService.FILE_PATH_DIALOG_TEXT_INPUT, true);
    }

    private void loadViewAndControllerAndAssertIt(String filePath, boolean mustHaveAViewController) {
        ViewRootAndControllerPair pair = viewService.getRootAndViewControllerFromFXML(filePath);
        Assertions.assertNotNull(pair);
        Assertions.assertNotNull(pair.getRoot());
        if (mustHaveAViewController) {
            Assertions.assertNotNull(pair.getViewController());
        } else {
            Assertions.assertNull(pair.getViewController());
        }
    }
}
