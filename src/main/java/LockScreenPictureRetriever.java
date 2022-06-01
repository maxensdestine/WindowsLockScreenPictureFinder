import controller.ConfigurationsController;
import controller.ErrorLogController;
import model.Configurations;
import view.MainFrame;
import com.formdev.flatlaf.FlatLightLaf;

public class LockScreenPictureRetriever {

    public static void main(String[]args){
        ConfigurationsController.loadLocationInformation(null);
        ConfigurationsController.generateLockScreenPath(null);
        FlatLightLaf.setup();
        System.out.println(Configurations.getSaveDir());
        MainFrame frame = new MainFrame();
        ErrorLogController.setView(frame);

    }
}
