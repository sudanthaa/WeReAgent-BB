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
	ButtonField serverButton = null;
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
            smallFont = fontFam.getFont(Font.PLAIN, 10);
        } catch (ClassNotFoundException e) {
            System.out.println("The specified font family was not found.");
        }
        
        statsusLabel = new LabelField("Idle..", LabelField.ELLIPSIS | Field.FIELD_HCENTER);
        serverButton = new ButtonField(" Start Server ", ButtonField.CONSUME_CLICK | Field.FIELD_HCENTER);
        statsusLabel.setMargin(5, 5, 5, 5);
        serverButton.setMargin(5, 5, 5, 5);
        
        requestContentLabel = new LabelField("Request: ", LabelField.ELLIPSIS | Field.FIELD_LEFT);
        responseContentLabel = new LabelField("Response: ", LabelField.ELLIPSIS | Field.FIELD_LEFT);
        requestContentLabel.setMargin(5, 5, 5, 5);
        responseContentLabel.setMargin(5, 5, 5, 5);
        if (smallFont != null) {
        	requestContentLabel.setFont(smallFont);
        	responseContentLabel.setFont(smallFont);
        }
        
        serverButton.setChangeListener(new FieldChangeListener() {
			public void fieldChanged(Field field, int context) {
				onPressServerButton();
			}
		});
        
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
			public void run() {
				onTimerThreadTimerEvent();
			}
		}, 1000, 1000);    
        
        add(statsusLabel);
        add(serverButton);
        add(requestContentLabel);
        add(responseContentLabel);
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
    	
    	int statusSeed = server.getStatusSeed();
    	if (this.statusSeed != statusSeed) {
    		this.statusSeed = statusSeed;
    		statsusLabel.setText(server.getStatus(), 0,-1);
    	}
    	
    	int requestSeed = server.getReqestSeed();
    	if (this.requestSeed != requestSeed) {
    		this.requestSeed = requestSeed;
    		requestContentLabel.setText("Request:\n" + server.getRequest(), 0,-1);
    	}
    	
    	int responseSeed = server.getResponseSeed();
    	if (this.responseSeed != responseSeed) {
    		this.responseSeed = responseSeed;
    		responseContentLabel.setText("Response:\n" + server.getResponse(), 0,-1);
    	}
    }
    
    private void onPressServerButton() {    	
    	if (server == null)
    		startServer();
    	else 
    		stopServer();
    }
    
    private boolean startServer() {
    	if (server != null)
    		return true;
    
    	server = new WRAServer(uuid);
    	server.start();
    	serverButton.setLabel(" Stop Server ");
    	
    	return true;
    }
    
    private boolean stopServer() {
    	if (server == null)
    		return true;
    	
    	server.halt();
    	server = null;
    	serverButton.setLabel(" Start Server ");
    	return true;
    }
}
