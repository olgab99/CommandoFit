package com.olmatech.fitness.view;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.olmatech.fitness.R;

public class DescriptionFragment extends BaseFragment{
	private WebView web_view;
	private TextView txtTitle;
	
	//for web view
	private final static String encoding = "UTF-8";
	private final static String mimeType = "text/html";
	private final static String HTML_HEAD = "<!DOCTYPE html><html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" /></head><body style=\"backgroind:#2A2B2B;color:#FFF\">";
	private final static String HTML_END = "</body></html>";
	
	private IDescriptionListener mListener;
	
	private View butClose;
	
 private String description_text;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.fragment_description, container, false);
		txtTitle = (TextView)view.findViewById(R.id.txt_ex_name);
		web_view = (WebView)view.findViewById(R.id.txt_descrip);
		web_view.setBackgroundColor(0x00000000); 
		web_view.setWebViewClient(new WebViewClient(){

			
			@Override
			public void onPageFinished(WebView view, String url) {	
				
				super.onPageFinished(view, url);
				web_view.setBackgroundColor(0x00000000);
				setLayerType();
				
			}
			
		});
		WebSettings sets = web_view.getSettings();
		sets.setSupportZoom(true);
		sets.setBuiltInZoomControls(false);
		sets.setDefaultFixedFontSize(16); 
		sets.setDefaultFontSize(16);
		
		butClose = view.findViewById(R.id.but_description_close);
		butClose.setOnTouchListener(new OnTouchListener(){
			@Override
			public boolean onTouch(View v, MotionEvent me) {
				if(me.getAction() == MotionEvent.ACTION_UP)
				{
					if(mListener != null)
					{
						mListener.onDescriptionClose();
					}
					return true;
				}
				return false;
			}
			
		});
		
		if(savedInstanceState== null)
		{
			description_text=null;
		}
		else
		{
			if(savedInstanceState.containsKey("description_text"))
			{				
				setDescription(savedInstanceState.getString("description_text"));
			}
			if(savedInstanceState.containsKey("title"))
			{
				setTitle(savedInstanceState.getString("title"));
			}
		}

		return view;
	}
	
	
	

	@Override
	public void onSaveInstanceState(Bundle outState) {

		if(description_text != null) outState.putString("description_text", description_text);
		if(txtTitle != null) outState.putString("title", (String) txtTitle.getText());
		
		super.onSaveInstanceState(outState);
	}




	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@SuppressLint("NewApi")	
	private void setLayerType()
	{
		if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) web_view.setLayerType(WebView.LAYER_TYPE_SOFTWARE, null);
	}
	
	public void setDescription(final String txt)
	{	
		description_text = txt;
		if(description_text != null)
		{
			web_view.loadDataWithBaseURL(null, HTML_HEAD + description_text + HTML_END, mimeType, encoding, "");
		}
		else
		{
			web_view.loadDataWithBaseURL(null, HTML_HEAD + "" + HTML_END, mimeType, encoding, "");
		}
			
		web_view.setBackgroundColor(0x00000000); 		
	}
	
	public void setTitle(final String s)
	{
		if(txtTitle != null && s != null) txtTitle.setText(s);
	}
	
	public void showCloseButton(final boolean show)
	{
		if(show)
		{
			if(butClose.getVisibility() != View.VISIBLE)
			{
				butClose.setVisibility(View.VISIBLE);
			}
			butClose.setEnabled(true);
		}
		else
		{
			if(butClose.getVisibility() == View.VISIBLE)
			{
				butClose.setVisibility(View.GONE);
			}
			butClose.setEnabled(false);
		}
	}
	
	@Override
	public void onAttach(Activity activity) {		
		super.onAttach(activity);
		
		try {
			mListener = (IDescriptionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement IDescriptionListener");
        }       
	}
	
	
	public interface IDescriptionListener
	{
		public void onDescriptionClose();
	}

}
