package com.olmatech.fitness.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.olmatech.fitness.R;
import com.olmatech.fitness.main.Common;

public class HelpActivity extends BaseFragmentActivity{
	
	private int callerId;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		
		if(savedInstanceState != null)
		{
			if(savedInstanceState.containsKey("caller")) callerId = savedInstanceState.getInt("caller");
			else callerId = Common.ACT_FirstActivityPhone;
		}
		else
		{
			Intent intent = this.getIntent();
			callerId = intent.getIntExtra("caller", Common.ACT_FirstActivityPhone);
		}
		
		setContentView(R.layout.dlg_help);
		
		WebView wv = (WebView) this.findViewById(R.id.help_wv);
		WebSettings st=wv.getSettings();
		st.setJavaScriptEnabled(true);
		st.setDomStorageEnabled(true);
		
		wv.setWebViewClient(new WebViewClient(){

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				if(url.contains("http"))
				{
					processDisplayWeb(url);
				}
				else
				{
					processString(url);
				}
				return true;
			}
			
		});
		
//		wv.setWebChromeClient(new WebChromeClient(){
//
//			@Override
//			public boolean onJsAlert(WebView view, String url, String message,
//					JsResult result) {
//				// TODO Auto-generated method stub
//			
//				return super.onJsAlert(view, url, message, result);
//			}
//			
//		});				
		
		String page="file:///android_asset/help/";
		switch(callerId)
		{
		case Common.ACT_FirstActivityPhone:
			page =page+"help1.html";
			break;
		case Common.ACT_MainActivityPhone:
			page =page+"workouts_list.html";
			break;
		case Common.ACT_InterviewActivity:
			page =page+"interview.html";
			break;
		case Common.ACT_CalcMenuActivity:
			page =page+"help1.html";
			break;		
//		case Common.ACT_ExListActivity:  
//			page ="file:///android_asset/help/help1.html"; 
//			break;
	    case Common.ACT_ExerciseActivityPhone:  
	    	page =page+"workout.html"; 
	    	break;
	    case Common.ACT_MenuActivity:  
	    	page =page+"menu.html"; 
	    	break; 
	   // case Common.ACT_HELP:  page ="file:///android_asset/help/help1.html"; break;
	    case Common.ACT_HrCalculatorActivity :  
	    	page =page+"heartrate.html"; 
	    	break;
	    case Common.ACT_MaxrepActivity:  
	    	page =page+"onemaxrep.html"; 
	    	break;
	    case Common.ACT_Settings:  
	    	page =page+"settings.html"; 
	    	break;
	    //case Common.ACT_LogListDlgActivity :  page ="file:///android_asset/help/help1.html"; break;
	    //case Common.ACT_CardioZonesActivity:  page ="file:///android_asset/help/help1.html"; break;
	    case Common.ACT_TackticalActivity:  
	    	page =page+"tactical.html"; 
	    	break;
	    //case Common.ACT_ExImageViewActivity:  page ="file:///android_asset/help/help1.html"; break;
	    case Common.ACT_LogCalendarActivity:  
	    	page =page+"logcalendar.html"; 
	    	break;
	    //case Common.ACT_LogViewActivity:  page ="file:///android_asset/help/help1.html"; break;
	    //case Common.ACT_PopupDlgActivity:  page ="file:///android_asset/help/help1.html"; break;
	    //case Common.ACT_FacebookActivity:  page ="file:///android_asset/help/help1.html"; break;
		default:
				page =page+"help1.html";
				break;				
		}
		
		wv.loadUrl(page);
	}
	
	private void processDisplayWeb(String url)
	{
		try
		 {			 
			 Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
			 startActivity(browserIntent);	
		 }
		 catch(Exception e)
		 {
			 e.printStackTrace();
		 }			
		
	}
	
	private void processString(String s)
	{
		WebView wv = (WebView) this.findViewById(R.id.help_wv);
		if(s.contains("file:///android_asset/help/")) wv.loadUrl(s);
		else wv.loadUrl("file:///android_asset/help/" + s);
	}
	
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putInt("caller", callerId);
		super.onSaveInstanceState(outState);
	}



	public void onButtonClick(View view)
	{
		switch(view.getId())
		{
		case R.id.help_but_close:
			setResult(Common.RESULT_OK);
			finish();
			break;
		case R.id.help_email:
			Common.sendMail(this, getString(R.string.support_email), 
					"Email from " + getString(R.string.app_name), null,null);
			break;
		case R.id.help_logo:			
			WebView wv = (WebView) this.findViewById(R.id.help_wv);
			wv.loadUrl("file:///android_asset/help/about.html");
			break;
		default:
			super.onButtonClick(view);
			break;
		}
		
	}

	@Override
	public int getId() {
		// TODO Auto-generated method stub
		return Common.ACT_HELP;
	}

}
