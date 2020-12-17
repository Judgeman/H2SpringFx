package de.judgeman.H2SpringFx.Tests.HelperClasses;

import de.judgeman.H2SpringFx.HelperClasses.ViewRootAndControllerPair;
import de.judgeman.H2SpringFx.Services.ViewService;
import de.judgeman.H2SpringFx.ViewControllers.Abstract.ViewController;
import de.judgeman.H2SpringFx.ViewControllers.MainViewController;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

public class UITestingService {

    public static MainViewController getNewMainController(ViewService viewService) {
        return (MainViewController) getViewControllerForFXML(viewService, ViewService.FILE_PATH_MAIN_VIEW);
    }

    public static ViewController getViewControllerForFXML(ViewService viewService, String fxmlPath) {
        ViewRootAndControllerPair pair = null;
        try {
            pair = viewService.getRootAndViewControllerFromFXML(fxmlPath);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return pair.getViewController();
    }

    public static void waitForAnimationFinished(int seconds) {
        waitForAnimationFinished(seconds, null);
    }

    public static void waitForAnimationFinished(int seconds, AtomicBoolean checkAtomicForEverySecond) {
        int secondsPassed = 0;
        try {
            while (secondsPassed < seconds && (checkAtomicForEverySecond == null || !checkAtomicForEverySecond.get())) {
                Thread.sleep(1000);
                secondsPassed++;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
