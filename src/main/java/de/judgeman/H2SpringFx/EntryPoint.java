package de.judgeman.H2SpringFx;

import com.sun.javafx.application.LauncherImpl;
import de.judgeman.H2SpringFx.Services.LogService;
import org.slf4j.Logger;

/**
 * Created by Paul Richter on Mon 30/03/2020
 */
public class EntryPoint {

    private static Logger logger = LogService.getLogger(EntryPoint.class);

    public static void main(String[] args) {

        // tie the console out print to a file
        LogService.tieSystemOutAndErrToFileLogging();

        // Start as Spring Boot application with JavaFx
        LauncherImpl.launchApplication(H2SpringFxApplication.class, SplashScreen.class, args);
    }
}
