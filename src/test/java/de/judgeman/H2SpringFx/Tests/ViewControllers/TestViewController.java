package de.judgeman.H2SpringFx.Tests.ViewControllers;

import de.judgeman.H2SpringFx.ViewControllers.Abstract.BaseViewController;
import org.springframework.stereotype.Controller;

@Controller
public class TestViewController extends BaseViewController {
    @Override
    public void afterViewIsInitialized() {
        // do nothing
    }
}
