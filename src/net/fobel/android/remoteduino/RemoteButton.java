package net.fobel.android.remoteduino;

import java.io.IOException;

import net.fobel.android.AlertHelper;
import net.fobel.android.AlertHelper.AlertOnClickI;
import net.fobel.android.remoteduino.devices.WebArduinoIRDevice;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class RemoteButton extends Button {
    private final RemoteCommand mCommand;
    private final WebArduinoIRDevice mIrDevice;
    private final RemoteCommandManager mCommandManager;
    private final RemoteDuinoActivity mActivity;

    public RemoteButton(final RemoteDuinoActivity activity,
            RemoteCommandManager cmdManager, WebArduinoIRDevice irDevice,
            RemoteCommand cmd) {
        super(activity);
        mCommandManager = cmdManager;
        mIrDevice = irDevice;
        mCommand = cmd;
        mActivity = activity;

        this.setText(cmd.label);
        final RemoteButton thisButton = this;
        final String labelText = cmd.label;
        this.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                thisButton.send(mIrDevice);
            }
        });

        /*
         * On a long click, give the user some options for this button
         */
        this.setOnLongClickListener(new OnLongClickListener() {
            public boolean onLongClick(View v) {
                final CharSequence[] items = { "Rename", "Relearn", "Delete" };

                AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                builder.setTitle(labelText);
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        // Handle item selection
                        switch (item) {
                        case 0:
                            renameButton();
                            break;
                        case 1:
                            relearnButton();
                            break;
                        case 2:
                            deleteButton(thisButton, labelText);
                            break;
                        default:
                            Toast.makeText(mActivity, "Unknown command",
                                    Toast.LENGTH_SHORT).show();
                        }

                    }

                    private void renameButton() {
                        AlertHelper.showTextInputAlert(mActivity, "Rename: "
                                + mCommand.label, new AlertOnClickI() {
                            public void onClick(DialogInterface dialog,
                                    TextView input) {
                                String newButtonText = input.getText()
                                        .toString().trim();
                                String oldButtonText = mCommand.label;
                                mCommand.label = newButtonText;
                                try {
                                    mCommandManager.save_remote_codes();
                                } catch(IOException e) {
                                    // Ignore for now
                                    e.printStackTrace();
                                }
                                mActivity.update_codes_list();
                                Toast.makeText(
                                        mActivity,
                                        oldButtonText + " renamed to "
                                                + newButtonText,
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    private void relearnButton() {
                        Toast.makeText(mActivity, "Relearn doesn't work yet.",
                                Toast.LENGTH_SHORT).show();
                    }

                    private void deleteButton(final RemoteButton thisButton,
                            final String labelText) {
                        mCommandManager.remove(thisButton.mCommand);
                        mActivity.update_codes_list();
                        Toast.makeText(mActivity, labelText + " deleted",
                                Toast.LENGTH_SHORT).show();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
                return false;
            }
        });
    }

    public void send(WebArduinoIRDevice irDevice) {
        String result = mCommand.send(irDevice);
        /*
         * Toast.makeText(context,
         * String.format("CODE: c=%s, p=%s [%s]\nResult: %s", cmd.code,
         * cmd.protocol, cmd.label, result), Toast.LENGTH_SHORT).show();
         */
    }
}