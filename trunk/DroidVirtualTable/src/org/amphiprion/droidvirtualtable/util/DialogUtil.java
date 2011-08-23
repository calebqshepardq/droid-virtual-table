package org.amphiprion.droidvirtualtable.util;

import org.amphiprion.droidvirtualtable.R;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class DialogUtil {
	public static void showConfirmDialog(Context context, String message) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setMessage(message).setCancelable(false).setPositiveButton(context.getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				dialog.dismiss();
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	}

	public static void showErrorDialog(Context context, String message, Exception e) {
		String str = message;
		Throwable t = e;
		while (t != null) {
			str += "Cause-> " + t.getMessage() + " ";
			StackTraceElement[] sts = t.getStackTrace();
			for (StackTraceElement ste : sts) {
				str += ste.getClassName() + "." + ste.getMethodName() + ": line " + ste.getLineNumber();
			}
			t = t.getCause();
		}
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setMessage(str).setCancelable(false).setPositiveButton(context.getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				dialog.dismiss();
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	}

}
