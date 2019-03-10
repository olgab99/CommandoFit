package com.olmatech.fitness.interfaces;

public interface IShowDialogs {
	public void showMsgDlg(final int dlgMessage,final int reason);
	public void showErrorDlg(final int msgResourceId, final int reason);
	public void showYesNoDlg(final int dlgMessage, final int okMsg, final int cancelMsg, final int reason);
	public void showYesNoCancelDlg(final int dlgMessage, final int okMsg, final int cancelMsg, 
			 final int doNothingMsg, final int reason);
	public void processDlgNo(final int reason);
	public void processDlgYes(final int reason);
	public void processErrDlgClose(final int reason);
	public void processMsgDlgClosed(final int reason);
	//public void resetDlgReason();
	

}
