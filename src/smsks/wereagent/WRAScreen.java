package smsks.wereagent;

import java.util.Timer;
import java.util.TimerTask;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.FontFamily;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.container.MainScreen;

public final class WRAScreen extends MainScreen {
	
	WRAServer server = null;
	ButtonField startButton = null;
	ButtonField stopButton = null;
	LabelField statsusLabel = null;
	LabelField requestContentLabel = null;
	LabelField responseContentLabel = null;
	int requestSeed = 0;
	int responseSeed = 0;
	int statusSeed = 0;
	private static final String uuid = "8518F3E097C611E29E960800200C9A66";
	
    public WRAScreen() {        
        // Set the displayed title of the screen       
        setTitle("WeReAgent");
        
        Font smallFont = null;
        try {
            FontFamily fontFam = FontFamily.forName("BBMillbank");
            smallFont = fontFam.getFont(Font.PLAIN, 8);
        } catch (ClassNotFoundException e) {
            System.out.println("The specified font family was not found.");
        }
        
        statsusLabel = new LabelField("Hello Therer How are you", LabelField.ELLIPSIS | Field.FIELD_HCENTER);
        startButton = new ButtonField("Start Server", ButtonField.CONSUME_CLICK | Field.FIELD_HCENTER);
        stopButton = new ButtonField("Stop Server", ButtonField.CONSUME_CLICK | Field.FIELD_HCENTER);
        startButton.setMargin(5, 5, 5, 5);
        stopButton.setMargin(5, 5, 5, 5);
        
        requestContentLabel = new LabelField("Request: ", LabelField.ELLIPSIS | Field.FIELD_HCENTER);
        responseContentLabel = new LabelField("Response", LabelField.ELLIPSIS | Field.FIELD_HCENTER);
        requestContentLabel.setMargin(5, 5, 5, 5);
        responseContentLabel.setMargin(5, 5, 5, 5);
        if (smallFont != null) {
        	requestContentLabel.setFont(smallFont);
        	responseContentLabel.setFont(smallFont);
        }
        
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
        
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
			public void run() {
				onTimerThreadTimerEvent();
			}
		}, 1000, 1000);    
        
        add(statsusLabel);
        add(startButton);
        add(stopButton);
    }
    
    private void onTimerThreadTimerEvent() {
    	UiApplication.getUiApplication().invokeLater(new Runnable() {
            public void run() {
            	 onUiThreadTimerEvent();
            }
        });
    }
    
    private void onUiThreadTimerEvent() {
    	if (server == null)
    		return;
    	
    	int statsuSeed = server.getStatusSeed();
    	if (this.statusSeed != statsuSeed) {
    		server.getStatus();
    		this.statusSeed = statsuSeed;
    	}
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
    
    	server = new WRAServer(uuid);
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
