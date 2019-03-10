package com.olmatech.fitness.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.olmatech.fitness.R;
import com.olmatech.fitness.db.DBAdapter;
import com.olmatech.fitness.main.Common;
import com.olmatech.fitness.main.LogData;

//list of logs - sets # + checkmark + delete button
//for weight-lifting logs only 
//accessed through Log Edit button in excersise view
public class LogListDlgActivity extends BaseFragmentActivity{
	
	private long logId=-1;
	
	private LinkedList<LogData> logData;

	@Override
	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_loglist);
		//we need log id for deleting log data
		if(savedInstanceState == null)
		{
			Intent intent = this.getIntent();
			logId = intent.getLongExtra("logid", -1);			
		}
		else
		{
			if(savedInstanceState.containsKey("logid"))
			{
				logId = savedInstanceState.getLong("logid");
			}
		}
		if(logId <=0){
			exitWithError();
			return;
		}
		//init log data - get saved from db
		new InitTask().execute();
	}
	
	private void processInitResult(final long res)
	{
		if(res >0)
		{
			populateList();
		}
		else exitWithError();
	}
	
	private boolean getLogDataFromDb()
	{
		DBAdapter dbAdapter = DBAdapter.getAdapter(this);
		logData = dbAdapter.getLogDataForWeight(logId);
		if(logData == null || logData.size() ==0) return false;
		return true;
	}
	
		
	private boolean populateList()
	{
		if(logData == null) return false;
		int cnt = logData.size();
		if(cnt <=0) return false;
		
		String[] from = new String[]{"number","weight", "reps"};
		int[]  to = new int[] {R.id.txt_set, R.id.txt_weight, R.id.txt_reps};
		List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();
		LogData data;
		//title
		
		HashMap<String, String> map = new HashMap<String, String>();   
		map.put("number", "#");
		map.put("weight", "WEIGHT");
		map.put("reps", "REPS");
		fillMaps.add(map);
		
		for(int i= 0; i < cnt; i++)
		{
			data = logData.get(i);
			map = new HashMap<String, String>();   
			map.put("number", Integer.toString(i+1));
			map.put("weight", Integer.toString(data.getWeight()));
			map.put("reps", Integer.toString(data.getReps()));
			fillMaps.add(map);
		}
		
		ListView lv = (ListView)this.findViewById(R.id.list_log);
		SimpleAdapter adapter = new SimpleAdapter(this, fillMaps, R.layout.list_item_loglist, 
						from, to);
    	lv.setAdapter(adapter);
    	return true;
		
	}
	
	private void exitWithError()
	{		
		this.showErrorDlg(R.string.err_reading_data, Common.REASON_FATAL_ERROR);
	}
	
		

	public void onButtonClick(View view)
	{
		switch(view.getId())
		{
		case R.id.but_delete_last:
			//delete last entry from the log
			deleteLastLogEntry();
			break;
		default:
			close();
			break;
		}	
	}
	
	private void close()
	{
		Intent intent = this.getIntent();
		if(logData == null) intent.putExtra("num_sets", 0);
		else intent.putExtra("num_sets", logData.size());
		
		this.setResult(Common.RESULT_OK, intent);
		finish();
	}
	
	private void deleteLastLogEntry()
	{
		if(logData == null) return;
		int cnt = logData.size();
		if(cnt <=0) return;
		
		LogData data = logData.get(cnt-1);
		
		DBAdapter adapter = DBAdapter.getAdapter(this);
		boolean res = adapter.deleteWeightLogEntry(logId, data.getSeqNum());
		if(!res)
		{
			this.runOnUiThread(new Runnable(){

				@Override
				public void run() {
					showErrorDlg(R.string.err_processing, Common.REASON_NOT_IMPORTANT);			
				}				
			});					
		}
		else
		{
			if(cnt ==1)
			{
				logData=null;
				this.showMsgDlg(R.string.logs_ex_deleted, Common.REASON_NOT_IMPORTANT);
			}
			else
			{
				logData.remove(cnt-1);
				this.runOnUiThread(new Runnable(){

					@Override
					public void run() {
						populateList();					
					}
					
				});			
			}			
		}
	}


	@Override
	public void processMsgDlgClosed(final int reason) {
		super.processMsgDlgClosed(reason);
		close();
	}

	@Override
	public int getId() {		
		return Common.ACT_LogListDlgActivity;
	}
	
	private void showProgress(final boolean show)
	{
		View progrView = this.findViewById(R.id.progrBar);
		if(progrView != null)
		{
			if(show) progrView.setVisibility(View.VISIBLE);
			else progrView.setVisibility(View.GONE);
		}
	}
	
	private class InitTask extends AsyncTask<String, Void, long[]>
	{
		// can use UI thread here
		protected void onPreExecute() { 
			showProgress(true);
		}  		

		@Override
		protected long[] doInBackground(String... arg0) {
			long[] res = new long[1];
			res[0] = (getLogDataFromDb())? 1 : 0;
			return res;
		}
		
		protected void onPostExecute(final long[] result) 
		{
			showProgress(false);
			LogListDlgActivity.this.processInitResult(result[0]);
		}
		
	}

}
