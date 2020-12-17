package de.judgeman.H2SpringFx.Tests.ServiceTests;

import de.judgeman.H2SpringFx.HelperClasses.ViewRootAndControllerPair;
import de.judgeman.H2SpringFx.Services.ViewService;
import de.judgeman.H2SpringFx.Tests.HelperClasses.UITestFxApp;
import de.judgeman.H2SpringFx.Tests.HelperClasses.UITestingService;
import de.judgeman.H2SpringFx.Tests.ViewControllerTests.EntryPointViewControllerTests;
import de.judgeman.H2SpringFx.ViewControllers.DialogControllers.InformationDialogController;
import de.judgeman.H2SpringFx.ViewControllers.MainViewController;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.junit.Assert;
import org.junit.experimental.categories.Category;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.loadui.testfx.GuiTest;
import org.loadui.testfx.categories.TestFX;
import org.loadui.testfx.utils.FXTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.TestPropertySource;

import java.io.IOException;
import java.net.URL;
import java.util.concurrent.atomic.AtomicBoolean;

@SpringBootTest
@TestPropertySource(locations="classpath:test.properties")
@Category(TestFX.class)
public class ViewServiceTests extends GuiTest {

    public static final String pathToTestXML = "/TestFXML.fxml";

    @BeforeAll
    public static void setupTestGUI() {
        // avoid AWT headless exception
        System.setProperty("java.awt.headless", "false");
        if (!UITestFxApp.isAppRunning) {
            FXTestUtils.launchApp(UITestFxApp.class);
        }
    }

    @Autowired
    private ConfigurableApplicationContext springContext;

    @Autowired
    private ViewService viewService;

    @Test
    public void loadTestFXMLFileTest() {
        URL url = viewService.GetUrlForView("/TestFXML.fxml");
        Assert.assertNotNull(url);
    }

    @Test
    public void setStyleToStageTest() throws Exception {
        FXTestUtils.invokeAndWait(() -> {
            Stage stage = new Stage();
            Scene scene = null;
            try {
                scene = new Scene(viewService.getRootElementFromFXML(pathToTestXML));
            } catch (IOException e) {
                e.printStackTrace();
                Assert.fail(e.getMessage());
            }
            stage.setScene(scene);

            Assert.assertEquals(0, stage.getScene().getStylesheets().size());
            viewService.setDefaultStyleCss(stage);

            Assert.assertEquals(1, stage.getScene().getStylesheets().size());
            URL resourceUrl = getClass().getResource(ViewService.FILE_PATH_DEFAULT_STYLE_CSS);
            Assert.assertEquals(resourceUrl.toString(), stage.getScene().getStylesheets().get(0));
        }, 10 );
    }

    @Test
    public void registerPrimaryStageTest() throws Exception {
        FXTestUtils.invokeAndWait(() -> {
            Stage stage = new Stage();
            viewService.registerPrimaryStage(stage);

            Assert.assertEquals(stage, viewService.getPrimaryStage());
        }, 10 );
    }

    @Test
    public void restoreLastScenePositionAndSizeTest() throws Exception {
        FXTestUtils.invokeAndWait(() -> {
            // TODO: save last position

            Stage stage = new Stage();
            viewService.restoreScenePositionAndSize(stage);

            // TODO: assert saved last position on the stage object
        }, 10 );
    }

    @Test
    public void showInformationDialogTest() throws Exception {
        AtomicBoolean callBackCalled = new AtomicBoolean(false);
        FXTestUtils.invokeAndWait(() -> {

            MainViewController mainViewController = UITestingService.getNewMainController(viewService);
            Pane glassPane = mainViewController.getGlassPane();

            Assert.assertEquals(0, glassPane.getChildren().size());

            viewService.registerMainViewController(mainViewController);
            try {
                viewService.showInformationDialog("Test", "Test information");
            } catch (IOException e) {
                e.printStackTrace();
                Assert.fail();
            }
            Assert.assertEquals(1, glassPane.getChildren().size());

            viewService.dismissDialog(e -> {
                Assert.assertEquals(0, glassPane.getChildren().size());
                callBackCalled.set(true);
            });

            UITestingService.waitForAnimationFinished(5, callBackCalled);
        }, 10 );

        Assert.assertTrue(callBackCalled.get());
    }

    @Test
    public void showInformationDialogAndDismissWithOkButtonTest() throws Exception {
        AtomicBoolean callBackCalled = new AtomicBoolean(false);
        FXTestUtils.invokeAndWait(() -> {
            ViewRootAndControllerPair pair = null;
            MainViewController mainViewController = UITestingService.getNewMainController(viewService);
            Pane glassPane = mainViewController.getGlassPane();

            viewService.registerMainViewController(mainViewController);
            ViewRootAndControllerPair dialogPair = null;
            try {
                dialogPair = viewService.showInformationDialog("Test", "Test information");
            } catch (IOException e) {
                e.printStackTrace();
                Assert.fail();
            }

            Assert.assertEquals(1, glassPane.getChildren().size());
            Assert.assertTrue(dialogPair.getViewController() instanceof InformationDialogController);

            ((InformationDialogController) dialogPair.getViewController()).setCallBack(event -> {
                Assert.assertEquals(0, glassPane.getChildren().size());
                callBackCalled.set(true);
            });

            ((InformationDialogController) dialogPair.getViewController()).okButtonClicked();
            UITestingService.waitForAnimationFinished(5, callBackCalled);

        }, 10 );

        Assert.assertTrue(callBackCalled.get());
    }

    @Test
    public void tryDismissDialogWithoutElementsOnGlassPane() {
        AtomicBoolean callBackCalled = new AtomicBoolean(false);

        MainViewController mainViewController = UITestingService.getNewMainController(viewService);
        Pane glassPane = mainViewController.getGlassPane();

        Assert.assertEquals(0, glassPane.getChildren().size());

        viewService.registerMainViewController(mainViewController);
        viewService.dismissDialog(e -> {
            Assert.assertEquals(0, glassPane.getChildren().size());
            callBackCalled.set(true);
        });

        UITestingService.waitForAnimationFinished(5, callBackCalled);
        Assert.assertTrue(callBackCalled.get());
    }

    @Test
    public void tryDismissDialogWithoutCallback() {
        MainViewController mainViewController = new MainViewController();
        Pane glassPane = new Pane();
        Pane overLayerPane = new Pane();
        mainViewController.setGlassPane(glassPane);
        mainViewController.setDialogOverLayer(overLayerPane);
        viewService.registerMainViewController(mainViewController);

        try {
            viewService.showInformationDialog("Test", "Test information");
        } catch (IOException e) {
            e.printStackTrace();
            Assert.fail();
        }
        Assert.assertEquals(1, glassPane.getChildren().size());

        viewService.dismissDialog(null);
        UITestingService.waitForAnimationFinished(1);
        Assert.assertEquals(0, glassPane.getChildren().size());
    }

    @Override
    protected Parent getRootNode() {
        return null;
    }
}
