package com.easyroute.utility;

import android.app.Activity;
import android.content.Context;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

/**
 * This class has show and hide keyboard methods
 * 
 */
public class Keyboard {
	/**
	 * Shows keyboard
	 * 
	 * @param context
	 */
	public static void show(Context context) {
		try {
			InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
		} catch (Exception e) {}
	}

	public static void show(final Context context, final EditText editText) {
		editText.requestFocus();
		show(context);
	}

	/**
	 * Hides keyboard.
	 * 
	 * @param context
	 */
	public static void hide(Context context) {
		try {
			InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(((Activity) context).getWindow().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		} catch (Exception e) {}
	}
}
