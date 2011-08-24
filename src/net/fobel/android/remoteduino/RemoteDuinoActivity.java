package net.fobel.android.remoteduino;

import android.app.Activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.EditText;
import android.widget.LinearLayout;
import java.io.IOException;


public class RemoteDuinoActivity extends Activity {
	RemoteCommandManager cmd_manager;
	String label;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
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
	    	final RemoteButton temp = new RemoteButton(c, cmd);
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
    
    
    public void on__learn_code__handler(View view) {
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
					cmd = RemoteCommand.learn_command(label);
					cmd_manager.add(cmd);
					Toast.makeText(here, "Added code to local list: " + cmd.code + ", " + cmd.protocol, Toast.LENGTH_SHORT).show();
					here.update_codes_list();
				} catch (Exception e) {
					// TODO Auto-generated catch block
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