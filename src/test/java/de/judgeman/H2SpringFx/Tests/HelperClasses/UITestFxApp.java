package de.judgeman.H2SpringFx.Tests.HelperClasses;

import javafx.application.Application;
import javafx.stage.Stage;

public class UITestFxApp extends Application
{
    public static boolean isAppRunning = false;

    @Override
    public void start(Stage primaryStage)
    {
        isAppRunning = true;
    }

    @Override
    public void stop() {
        isAppRunning = false;
    }
}