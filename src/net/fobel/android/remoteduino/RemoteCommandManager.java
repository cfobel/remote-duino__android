package net.fobel.android.remoteduino;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import net.fobel.android.Serialization;
import android.content.Context;


public class RemoteCommandManager {
	/* This class stores a list of remote commands and handles the serialization
	 * and de-serialization of the list to the file "remote_codes.dat".
	 * NOTE: The file "remote_codes.dat" is currently stored in app-private
	 *		 storage, which is stored in system memory and is only accessible
	 *		 by this app. */
	ArrayList<RemoteCommand> commands;
	Context context;
	
	public RemoteCommandManager(Context c) throws IOException {
		context = c;
    	FileInputStream fis;
    	try {
	    	/* Load command list from file. */
    		fis = context.openFileInput("remote_codes.dat");
			byte[] cmd_bytes = Serialization.read_bytes(fis);
			commands = (ArrayList<RemoteCommand>) Serialization.deserializeObject(cmd_bytes); 
			fis.close();
    	} catch(FileNotFoundException e) {
	    	/* If file doesn't exist, create empty list and save it to file. */
			commands = new ArrayList<RemoteCommand>();
			save_remote_codes();
    	}
	}
	
	
	public void add(RemoteCommand cmd) {
    	/* Add command to list and save to file. */
		commands.add(cmd);
		try {
			save_remote_codes();
		} catch(IOException e) {
			// Ignore failed save...
		}
	}
	
	
	public Collection<RemoteCommand> get_commands() {
    	/* Return unmodifiable reference to command list.  This allows other
    	 * classes to easily traverse commands without permitting modification.
    	 */
		return Collections.unmodifiableList(commands);
	}
	
	
    void save_remote_codes() throws IOException {
    	/* Save command list to file. */
		byte[] cmd_bytes = Serialization.serializeObject(this.commands);
    	FileOutputStream fos;
		fos = context.openFileOutput("remote_codes.dat", Context.MODE_PRIVATE);
		fos.write(cmd_bytes);
		fos.close();
    }
}
