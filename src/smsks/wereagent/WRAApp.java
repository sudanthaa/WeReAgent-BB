package smsks.wereagent;

import net.rim.device.api.ui.UiApplication;

/**
 * This class extends the UiApplication class, providing a
 * graphical user interface.
 */
public class WRAApp extends UiApplication {
    /**
     * Entry point for application
     * @param args Command line arguments (not used)
     */ 
    public static void main(String[] args) {
        // Create a new instance of the application and make the currently
        // running thread the application's event dispatch thread.
        WRAApp theApp = new WRAApp();       
        theApp.enterEventDispatcher();
    }
    

    /**
     * Creates a new WRAApp object
     */
    public WRAApp() {        
        // Push a screen onto the UI stack for rendering.
        pushScreen(new WRAScreen());
    }    
}
