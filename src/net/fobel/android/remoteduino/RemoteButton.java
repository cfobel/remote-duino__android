package net.fobel.android.remoteduino;

import net.fobel.android.remoteduino.devices.WebArduinoIRDevice;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class RemoteButton extends Button {
	RemoteCommand cmd;
	Context context;
	private final WebArduinoIRDevice mIrDevice;

	public RemoteButton(Context context, WebArduinoIRDevice irDevice, RemoteCommand cmd) {
		super(context);
		mIrDevice = irDevice;
		this.cmd = cmd;
		this.context = context;
		this.setText(cmd.label);
		final RemoteButton here = this;
		this.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
				here.send(mIrDevice);
            }
        });
	}
	
	public void send(WebArduinoIRDevice irDevice) {
        String result = cmd.send(irDevice);
        /*
		Toast.makeText(context, String.format("CODE: c=%s, p=%s [%s]\nResult: %s",
				cmd.code, cmd.protocol, cmd.label, result), Toast.LENGTH_SHORT).show();
				*/
	}
}