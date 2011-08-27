package net.fobel.android.remoteduino;

import java.io.IOException;

import net.fobel.android.remoteduino.devices.WebArduinoIRDevice;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;


public class RemoteDuinoActivity extends Activity {
	RemoteCommandManager cmd_manager;
	String label;
	private static final String SETTING_URL = "setting_url"; 
	private WebArduinoIRDevice mIRDevice;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		mIRDevice = initDevice();

        try {
        	/* Create RemoteCommandManager, loading from file if available. */
        	cmd_manager = new RemoteCommandManager(getApplicationContext());
    	} catch (IOException e) {
			Toast.makeText(this, "IOException", Toast.LENGTH_SHORT).show();
		} catch(Exception e) {
			Toast.makeText(this, "[onCreate] Unexpected exception: " + e, Toast.LENGTH_SHORT).show();
		}
        setContentView(R.layout.main);
    	/* Generate buttons in display corresponding to commands in
    	 * RemoteCommandManager. */
		update_codes_list();
    }
    
    private WebArduinoIRDevice initDevice() {
        SharedPreferences settings = getPreferences(0);
    	String url = settings.getString(SETTING_URL, "192.168.0.100");
		return new WebArduinoIRDevice(url);
	}

	void update_codes_list() {
    	/* Remove all command buttons from display, then generate buttons
    	 * corresponding to commands in RemoteCommandManager. */
		/* TODO: Change layout of buttons:
		 * 			-Grid?
		 * 			-Configurable (serializable) layout? XML?
		 */
    	LinearLayout ll = (LinearLayout)findViewById(R.id.llo__remote_buttons);
    	ll.removeAllViews();
    	final Context c = this.getApplicationContext();
    	for(RemoteCommand cmd : cmd_manager.get_commands()) {
	    	final RemoteButton temp = new RemoteButton(c, mIRDevice, cmd);
    		ll.addView(temp);
    	}
    }
    
    public void set_label(String label) {
    	/* This function sets the temporary label value to be used when
    	 * learning a remote command.
    	 * This may seem a little awkward, but is required since the
    	 * call-back needs somewhere to store state until the command response
    	 * has been received. */
    	this.label = label;
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
        case R.id.learn:
        	showLearnCode();
            return true;
        case R.id.change_ip:
        	showEnterIP();
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }
    
    private void showEnterIP() {
//		Toast.makeText(this, "Would be pretty sweet if this worked!", Toast.LENGTH_SHORT);
    	AlertDialog.Builder alert = new AlertDialog.Builder(this);
    	final EditText input = new EditText(this);
    	alert.setMessage("Current IP: " + mIRDevice.getUrl());
    	alert.setView(input);
		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				String newURL = input.getText().toString().trim();

				// Update the active device
				mIRDevice.setUrl(newURL);

				// Store the pref
				SharedPreferences settings = getPreferences(0);
				SharedPreferences.Editor editor = settings.edit();
				editor.putString(SETTING_URL, newURL);

				// Commit the edits!
				editor.commit();
			}
		});
    	
		alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				dialog.cancel();
			}
		});
		alert.show();
	}

	private void showLearnCode() {
    	/* This method creates an alert dialog with a textbox and OK/Cancel
    	 * buttons. When OK is pressed, a request is sent to the IR device to
    	 * learn a remote code.  Once a response is received, a RemoteCommand
    	 * instance is created (assigned the label from the textbox) and is
    	 * added to the cmd_manager RemoteCommandManager list. */
    	final AlertDialog.Builder alert = new AlertDialog.Builder(this);
    	final EditText input = new EditText(this);
    	/* 'here' acts as a reverse reference for the alert's call-back function
    	 * to maintain state while learning a command. */
    	/* TODO: This might be unnecessary - there may have been something else
    	 * wrong, that appeared to make this necessary.  Shouldn't hurt
    	 * anything for now though... */
    	final RemoteDuinoActivity here = this;
    	alert.setMessage("Button Name:");
    	alert.setView(input);
    	alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
    		public void onClick(DialogInterface dialog, int whichButton) {
    			/* TODO: (see above)
    			 * We may be able to simply use a local variable for 'label'.
    			 * I seem to remember having issues with that, but once again,
    			 * it may have been an unrelated issue that seemed to make this
    			 * necessary. */
    			here.set_label(input.getText().toString().trim());
    			RemoteCommand cmd;
    			try {
    				cmd = RemoteCommand.learn_command(label, mIRDevice);
    				cmd_manager.add(cmd);
    				Toast.makeText(here, "Added code to local list: " + cmd.code + ", " + cmd.protocol, Toast.LENGTH_SHORT).show();
    				here.update_codes_list();
    			} catch (Exception e) {
    				Toast.makeText(here, e.getMessage(), Toast.LENGTH_SHORT).show();
    				e.printStackTrace();
    			}
    		}
    	});
    	
    	alert.setNegativeButton("Cancel",
    			new DialogInterface.OnClickListener() {
    		public void onClick(DialogInterface dialog, int whichButton) {
    			dialog.cancel();
    		}
    	});
    	alert.show();
    }
}