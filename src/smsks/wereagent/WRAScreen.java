package smsks.wereagent;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.container.MainScreen;

/**
 * A class extending the MainScreen class, which provides default standard
 * behavior for BlackBerry GUI applications.
 */
public final class WRAScreen extends MainScreen {
    /**
     * Creates a new WRAScreen object
     */
	
	WRAServer server = null;
	ButtonField serviceButton = null;
	LabelField requestContentLabel = null;
	LabelField responseContentLabel = null;
	int requestSeed = 0;
	int responseSeed = 0;
	int statusSeed = 0;
	
    public WRAScreen() {        
        // Set the displayed title of the screen       
        setTitle("WeReAgent");
        
        serviceButton = new ButtonField("Start");
        serviceButton.setChangeListener(new FieldChangeListener() {
			public void fieldChanged(Field field, int context) {
				// TODO Auto-generated method stub
				onPressServiceButton();
			}
		});
    }
    
  
    private void onPressServiceButton() {
    	
    }
}
