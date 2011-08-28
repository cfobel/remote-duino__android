package net.fobel.android;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

public class AlertHelper {
    public static void showTextInputAlert(Context context, String message,
            AlertOnClickI positiveListener) {
        showTextInputAlert(context, message, "Ok", "Cancel", positiveListener,
                createCancelOnClickListener());
    }

    public static void showTextInputAlert(Context context, String message,
            String positiveButtonText, String negativeButtonText,
            final AlertOnClickI positiveListener,
            final AlertOnClickI negativeListener) {
        final AlertDialog.Builder alert = new AlertDialog.Builder(context);
        final TextView input = new EditText(context);

        alert.setMessage(message);
        alert.setView(input);
        alert.setPositiveButton(positiveButtonText, new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                positiveListener.onClick(dialog, input);
            }
        });

        alert.setNegativeButton(negativeButtonText, new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                negativeListener.onClick(dialog, input);
            }
        });
        alert.show();
    }

    private static AlertOnClickI createCancelOnClickListener() {
        return new AlertOnClickI() {
            public void onClick(DialogInterface dialog, TextView input) {
                dialog.cancel();
            }
        };
    }

    public interface AlertOnClickI {
        public void onClick(DialogInterface dialog, TextView input);
    }
}
