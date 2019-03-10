package com.olmatech.tools.licensing;

import android.app.Activity;
import android.content.ContentResolver;

public interface ILicensingParent {
	void setLicensed();
	void StartApp();
	void displayDlg(final int id);
	boolean isFinishing();
	boolean getStoredLicensed();
	ContentResolver getContentResolver();
	Activity getActivity();
	String getPackageName();
	void setProgressBarIndeterminateVisibility(final boolean val);
	String getPublicKey();
}
