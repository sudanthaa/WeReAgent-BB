package smsks.wereagent;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.container.MainScreen;

public final class WRAScreen extends MainScreen {
	
	WRAServer server = null;
	ButtonField startButton = null;
	ButtonField stopButton = null;
	LabelField requestContentLabel = null;
	LabelField responseContentLabel = null;
	int requestSeed = 0;
	int responseSeed = 0;
	int statusSeed = 0;
	
    public WRAScreen() {        
        // Set the displayed title of the screen       
        setTitle("WeReAgent");
        
        startButton = new ButtonField("Start Server", ButtonField.CONSUME_CLICK | Field.FIELD_LEADING);
        stopButton = new ButtonField("Stop Server", ButtonField.CONSUME_CLICK | Field.FIELD_LEADING);
        
        startButton.setChangeListener(new FieldChangeListener() {
			public void fieldChanged(Field field, int context) {
				onPressStart();
			}
		});
        
        stopButton.setChangeListener(new FieldChangeListener() {
			public void fieldChanged(Field field, int context) {
				onPressStop();
			}
		});
        
        add(startButton);
        add(stopButton);
    }
    
    private void onPressStart() {
    	Dialog.alert("Button Pressed");
    	startServer();
    }
    
    private void onPressStop() {
    	Dialog.alert("Button Pressed");
    	stopServer();
    }
    
    private boolean startServer() {
    	if (server != null)
    		return true;
    
    	server = new WRAServer();
    	server.start();
    	
    	return true;
    }
    
    private boolean stopServer() {
    	if (server == null)
    		return true;
    	
    	server.halt();
    	server = null;
    	return true;
    }
}
