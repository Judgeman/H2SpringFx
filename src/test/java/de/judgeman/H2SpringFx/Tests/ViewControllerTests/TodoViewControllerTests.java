package de.judgeman.H2SpringFx.Tests.ViewControllerTests;

import de.judgeman.H2SpringFx.Services.LanguageService;
import de.judgeman.H2SpringFx.Services.SettingService;
import de.judgeman.H2SpringFx.Services.ViewService;
import de.judgeman.H2SpringFx.Tests.HelperClasses.UITestFxApp;
import org.junit.jupiter.api.BeforeAll;
import org.loadui.testfx.utils.FXTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(locations="classpath:test.properties")
public class TodoViewControllerTests {

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
}
