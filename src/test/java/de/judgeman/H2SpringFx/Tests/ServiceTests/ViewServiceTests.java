package de.judgeman.H2SpringFx.Tests.ServiceTests;

import de.judgeman.H2SpringFx.Services.ViewService;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
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

@SpringBootTest
@TestPropertySource(locations="classpath:test.properties")
@Category(TestFX.class)
public class ViewServiceTests extends GuiTest {

    public static final String pathToTestXML = "/TestFXML.fxml";

    @BeforeAll
    public static void setupTestGUI() {
        // avoid AWT headless exception
        System.setProperty("java.awt.headless", "false");
        FXTestUtils.launchApp(TestFxApp.class);
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

    @Override
    protected Parent getRootNode() {
        return null;
    }

    public static class TestFxApp extends Application
    {
        private Scene scene = null;
        private Stage primaryStage = null;

        @Override
        public void start(Stage primaryStage)
        {
            this.primaryStage = primaryStage;
        }

        public void setRoot(Parent rootNode)
        {
            scene.setRoot( rootNode );
        }
    }
}
