package de.judgeman.H2SpringFx.HelperClasses;

import de.judgeman.H2SpringFx.ViewControllers.Abstract.BaseViewController;
import javafx.scene.Parent;

/**
 * Created by Paul Richter on Fri 04/09/2020
 */
public class ViewRootAndControllerPair {

    private Parent root;
    private BaseViewController viewController;

    public ViewRootAndControllerPair(Parent root, BaseViewController viewController) {
        this.root = root;
        this.viewController = viewController;
    }

    public Parent getRoot() {
        return root;
    }

    public void setRoot(Parent root) {
        this.root = root;
    }

    public BaseViewController getViewController() {
        return viewController;
    }

    public void setViewController(BaseViewController viewController) {
        this.viewController = viewController;
    }
}
