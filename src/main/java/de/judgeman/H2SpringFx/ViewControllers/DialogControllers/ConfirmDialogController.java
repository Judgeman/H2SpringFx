package de.judgeman.H2SpringFx.ViewControllers.DialogControllers;

import de.judgeman.H2SpringFx.HelperClasses.CallBack;
import de.judgeman.H2SpringFx.Services.ViewService;
import de.judgeman.H2SpringFx.ViewControllers.Abstract.BaseDialogController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 * Created by Paul Richter on Fri 27/09/2020
 */
@Controller
public class ConfirmDialogController extends BaseDialogController {

    @Autowired
    private ViewService viewService;

    private CallBack callBack;

    @Override
    public void okButtonClicked() {
        viewService.dismissDialog(callBack);
    }

    public void setCallBack(CallBack callBack) {
        this.callBack = callBack;
    }
}
