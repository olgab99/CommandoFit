package com.olmatech.fitness.ui;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.olmatech.fitness.R;
import com.olmatech.fitness.db.DBAdapter;
import com.olmatech.fitness.db.DataStore;
import com.olmatech.fitness.main.Common;
import com.olmatech.fitness.main.CurProgram;
import com.olmatech.fitness.main.DayData;
import com.olmatech.fitness.main.User;
import com.olmatech.fitness.main.Workout.WorkoutMode;
import com.olmatech.fitness.ui.MainGestureDetector.IDetectGestures;
import com.olmatech.fitness.ui.MainGestureDetector.SwipeDirection;

public class LogCalendarActivity extends BaseFragmentActivity implements IDetectGestures, OnTouchListener{
	
	//private final static String TAG = "LogCalendarActivity";
		
	private GregorianCalendar dateFirstOfMonth;
	private List<DayData> listDays;
	private int totalDaysInMonth=0;
	private int offset=0;
	
	private WorkoutMode selectMode = WorkoutMode.All;
	private boolean showScheduler = false;
	
	//strings
	private final static String ENDURANCE = "Endurance";
	private final static String STRENGTH = "Mass";
	private final static String TACKTICAL = "Tactical";
	private final static String CARDIO= "Cardio";
	private final static String WORKOUT = "Workout";
	
	private final int FUTURE_WORKOUT=-2;
	
	//for schedule
	private long progrStartDaytime=-1;
	private int progrStartDayOrder=-1;
	
	//to h-ligt today 
	private int day_today = -1;
	
	private int minH=-1;
	
	//gestures
	private GestureDetector gestureDetector;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.dlg_calendar);
		int curMonth=-1;
		int curYear=-1;

		if(savedInstanceState !=null && savedInstanceState.containsKey("month") &&
				savedInstanceState.containsKey("year"))
		{
			curMonth = savedInstanceState.getInt("month");
			curYear = savedInstanceState.getInt("year");
			if(savedInstanceState.containsKey("mode"))
			{
				selectMode = WorkoutMode.getModeFromId(savedInstanceState.getInt("mode"));
				if(selectMode == WorkoutMode.Unknown) selectMode = WorkoutMode.All;
			}
			showScheduler = savedInstanceState.getBoolean("future");
		}
		else{
			Calendar rightNow = GregorianCalendar.getInstance(); //today
			curMonth = rightNow.get(Calendar.MONTH);
			curYear = rightNow.get(Calendar.YEAR);
			
			showScheduler=true;
		}
		dateFirstOfMonth = new GregorianCalendar(curYear,curMonth, 1, 0, 0,0);
		
		GridView gridview = (GridView) findViewById(R.id.calendar);			    
		    
    	gridview.setOnItemClickListener(new OnItemClickListener() {
	        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
	        	Object tag = v.getTag();			        	
	        	processLogClick(tag);
	        	
	        }
	    });
    	
    	gridview.setOnTouchListener(this);
    	
    	showSchedulerCheckbox();
        	
    	Spinner spinner = (Spinner)this.findViewById(R.id.log_mode_select);
		 spinner.setOnItemSelectedListener(new OnItemSelectedListener(){

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int pos,long id) {
				
				WorkoutMode md=WorkoutMode.All;
				if(pos == 1) md= WorkoutMode.Weight;
				else if(pos ==2) md= WorkoutMode.Tactical;								
				//reload calendar
				if(md != selectMode)
				{
					selectMode = md;
					showSchedulerCheckbox();
					//reload
					new InitMonthTask().execute();					
				}
				
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
			 
		 })  ;	
		 
	     View mainCont = this.findViewById(R.id.main_cont);
	     mainCont.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
	            @Override
	            public void onGlobalLayout() {
	                //At this point the layout is complete and the 
	                //dimensions of mainCont and any child views are known.
	            	calcCalItemH();
	            }
	        });
	     
	     initGestures();
		
		new InitMonthTask().execute();		
	}	
	
	private void initGestures() {		
		// gestures
		float scale = this.getResources().getDisplayMetrics().densityDpi;
		gestureDetector = new GestureDetector(this, new MainGestureDetector(this, scale));
	}
	
	private void calcCalItemH()
	{
		if(minH < 0) //Common.isTabletDevice(this) &&
		{
			//minH = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 120, getResources().getDisplayMetrics());
			
			GridView calV = (GridView)this.findViewById(R.id.calendar);	
			int cnt = calV.getCount();
			if(cnt == 0) return;			
			
			View topV = this.findViewById(R.id.log_mode_select);
			int y1 = topV.getBottom();
			View botV = this.findViewById(R.id.bot_container);
			int y2 = botV.getTop();
			int d = y2 - y1-20;	
			
			int vh = d/7;
			View day = calV.getChildAt(8);
			if(vh > day.getHeight())
			{
				minH = vh;
				calV.invalidateViews();
			}
			else
			{
				minH =0;
			}
				
		}
	}
		
	private boolean initCalendar()
	{		
		totalDaysInMonth = dateFirstOfMonth.getActualMaximum(Calendar.DAY_OF_MONTH);
		int y = dateFirstOfMonth.get(Calendar.YEAR);
		int m = dateFirstOfMonth.get(Calendar.MONTH);
		
		Calendar today =  GregorianCalendar.getInstance();
		Calendar dateLastOfMonth = new GregorianCalendar(y,m,totalDaysInMonth, 0,0,0);
		
		if(!today.before(dateFirstOfMonth) && !today.after(dateLastOfMonth))
		{
			day_today = today.get(Calendar.DAY_OF_MONTH);
		}
		else day_today = -1;
		
		DBAdapter adapter = DBAdapter.getAdapter(this.getApplicationContext());
		final long startDate = Common.getDateTimeForDay(y,m,1);
		final long endDate = Common.getDateTimeForDay(y,m,totalDaysInMonth);
		
		Hashtable<Long, DayData> htDays; // datetime - DayInfo
		
		final int progrId = CurProgram.getProgram().getId();
		
		if(selectMode == WorkoutMode.All)
		{
			htDays = adapter.getLogsForAMonth(User.getUser().getId(),progrId, startDate, endDate);
		}
		else if(selectMode == WorkoutMode.Tactical)
		{
			htDays = adapter.getTackticalLogsForAMonth(User.getUser().getId(), startDate, endDate);
		}
		else //Weight
		{
			htDays = adapter.getModulesLogsForAMonth(User.getUser().getId(), startDate, endDate);
		}
		
		boolean haveLogs = (htDays == null || htDays.size()==0)? false : true;
		
		
		final long dtToday = Common.getDateTimeForToday();
		DataStore ds = new DataStore(this);
		int startDayOrderSet = ds.getCurDayOrder();	
		
		if(startDayOrderSet == Common.EMPTY_VALUE)
		{
			startDayOrderSet = 1;
			ds.setCurDayOrder(1);
		}		
		else if(startDayOrderSet == CurProgram.PROGR_COMPLETE_DAYORDER)
		{
			showScheduler = false;
		}
		
		if(showScheduler && (progrStartDaytime <=0 ||  progrStartDayOrder <=0))
		{
			long[] lastdayComplete = adapter.getLastCompletedDayorder(progrId);			
			if(lastdayComplete != null)
			{
				
				if(lastdayComplete[1] >= startDayOrderSet)
				{
					//we moved back
					//we will start program today
					progrStartDaytime= dtToday;
					progrStartDayOrder = startDayOrderSet;
				}
				else
				{
					//is completed today?
					
					if(dtToday ==lastdayComplete[0])
					{
						//today - progr starts next day
						Calendar today2 = GregorianCalendar.getInstance();
						today2.add(Calendar.DATE, 1); //next day
						progrStartDaytime = Common.getDateTimeForDate(today2);
						progrStartDayOrder = (int) (lastdayComplete[1] +1);					
					}
					else
					{
						//find day complteted, add any rest days and check with today						
						int restdays = (int) (startDayOrderSet - lastdayComplete[1]);
						if(restdays >1)
						{
							//Calendar cal = new GregorianCalendar();
							//cal.setTimeInMillis(lastdayComplete[0]); //when completed
							//cal.add(Calendar.DATE, restdays); // next day we can exercise
							final long dtCal =lastdayComplete[0] + restdays*MILLS_IN_DAY; // cal.getTimeInMillis();
							if(dtCal > dtToday)
							{
								progrStartDaytime = dtToday; // start with rest daycal.getTimeInMillis(); ///////////////////////////  MILLS_IN_DAY
								//figure out day
								int d = (int) ((dtCal - dtToday)/MILLS_IN_DAY);								
								progrStartDayOrder = startDayOrderSet-d;
							}
							else
							{
								progrStartDaytime = dtToday;
								progrStartDayOrder = startDayOrderSet;
							}							
							
						}
						else
						{
							//no breaks - set for today
							progrStartDaytime = dtToday;
							progrStartDayOrder = startDayOrderSet;
						}
					}
				}
				
				
				
			}
			else
			{
				//nothing completed
				progrStartDaytime = dtToday;
				progrStartDayOrder = startDayOrderSet;
			}
			
		}
		
			
		
		listDays = new LinkedList<DayData>();
		offset = getDayOfWeekOffset(dateFirstOfMonth.get(Calendar.DAY_OF_WEEK));
		int cnt = totalDaysInMonth + offset;	
		
		DayData dayData;
		//day of week titles
		for(int i=0; i < 7; i++)
		{			
			listDays.add(null);
		}
		//"empty" days
		for(int i=0; i < offset; i++)
		{
			listDays.add(null);
		}
		
		long dt;
		Long key;
		//for each day of month			
		
		List<DayData>  pogrDays=null;
		boolean haveScheduler = false;
	
		if(showScheduler && selectMode == WorkoutMode.All)
		{
			int total = adapter.getDaysInProgram(progrId);		
			Calendar firstDayOfProgr = new GregorianCalendar();
			firstDayOfProgr.setTimeInMillis(progrStartDaytime);
			Calendar lastDayOfProgram =new GregorianCalendar();
			lastDayOfProgram.setTimeInMillis(progrStartDaytime);
			
			int moreDays = total - progrStartDayOrder;  //total more days in progr
			lastDayOfProgram.add(Calendar.DATE, moreDays);  //last day
			
			Calendar lastFDayOfMonth = new GregorianCalendar(y,m,totalDaysInMonth,0,0,0);
						
			if(!firstDayOfProgr.after(lastFDayOfMonth) && !lastDayOfProgram.before(dateFirstOfMonth))
			{					
				
				int daysBtw = this.daysBetween(firstDayOfProgr, dateFirstOfMonth);	
				int startDay;
				if(daysBtw <=0)
				{
					//progr starts this month
					startDay = progrStartDayOrder;					
				}
				else
				{
					startDay = daysBtw + progrStartDayOrder;	
				}
				
				if(startDay <= total)
				{
					pogrDays = adapter.getProgrForSchedule(progrId,startDay,
							Math.min(total - startDay, totalDaysInMonth));
					if(pogrDays != null && pogrDays.size() >0)
					{
						haveScheduler = true;
					}						
				}								
			}				
		}
				
		int moreProgrDays;		
		if(haveScheduler)
		{
			moreProgrDays = pogrDays.size();
		}
		else moreProgrDays = -1;
		int ind =0;		
		if(haveLogs)
		{			
			for(int i=1; i<=totalDaysInMonth; i++ )
			{
				dt = Common.getDateTimeForDay(y, m, i);
				//if we have log for today - should be '>' not '>='
				boolean nolog = false;				
				if(haveScheduler)
				{
					if(dt > progrStartDaytime)  //HERE
					{
						nolog = true;
					}
					else if(dt == progrStartDaytime)
					{
						//check if we have log for today - special case
						key = Long.valueOf(dt);
						if(!htDays.containsKey(key))
						{
							nolog = true;
						}
					}
				}					
				
				if(nolog) //////////////////////////////////////////////////////
				{					
					if(ind < moreProgrDays)
					{
						dayData = pogrDays.get(ind);
						dayData.dayOfMonth=i;
						dayData.workoutId=FUTURE_WORKOUT;
						if(dayData.workoutMode == WorkoutMode.Rest || dayData.workoutMode == WorkoutMode.Unknown)
						{
							dayData.blockTxt = "REST";
							dayData.dayOrder=-1;
						}
						ind++;
						
						listDays.add(dayData);
					}
					else
					{
						listDays.add(null);
					}			
				}
				else  //log or date was completed
				{
					key = Long.valueOf(dt);
					if(htDays.containsKey(key))
					{
						//we have log for day
						dayData = htDays.get(key);
						dayData.dayOfMonth=i;
						//max_day_order = Math.max(max_day_order, dayData.dayOrder);
						
						listDays.add(dayData);
					}
					else
					{
						listDays.add(null);
					}
				}			
				
			}	
		}
		else // no logs
		{
			if(haveScheduler)
			{
				for(int i=1; i<=totalDaysInMonth; i++ )
				{
					dt = Common.getDateTimeForDay(y, m, i);
					if(dt >= progrStartDaytime)
					{
						if(ind < moreProgrDays)
						{
							dayData = pogrDays.get(ind);
							dayData.dayOfMonth=i;
							dayData.workoutId=FUTURE_WORKOUT;
							if(dayData.workoutMode == WorkoutMode.Rest || dayData.workoutMode == WorkoutMode.Unknown)
							{
								dayData.blockTxt = "REST";
								dayData.dayOrder=-1;
							}
							ind++;
							
							listDays.add(dayData);
						}
						else
						{
							listDays.add(null);
						}			
					}
					else
					{
						listDays.add(null);
					}
				}
			}
			else
			{
				this.feelWithNulls();
			}		
			
		}
		
		
		final int rem = cnt%7;
		if(rem >0)
		{
			for(int i=0; i < 7-rem; i++)
			{
				listDays.add(null);
			}
		}
		if(htDays != null)
		{
			htDays.clear();
			htDays=null;
		}
		
		return true;
	}
	
	private final static int MILLS_IN_DAY = 1000 * 60 * 60 * 24;
	 private int daysBetween(Calendar d1, Calendar d2){
		 //remove affect of daysaving - add 2 hours to bigger date
		 Calendar temp = (Calendar)d2.clone();
		 temp.add(Calendar.HOUR, 2);
		 
         return (int)( (temp.getTimeInMillis() - d1.getTimeInMillis()) / MILLS_IN_DAY);
	 }
	 
	 private void feelWithNulls()
	 {
		 for(int i=1; i<=totalDaysInMonth; i++ )
			{
				listDays.add(null);
			}
	 }
	
	private void processCalInitDone(final boolean res)
	{
		if(res)
		{
			GridView gridview = (GridView) findViewById(R.id.calendar);			
		    gridview.setAdapter(new LogAdapter(this));
		    
		    gridview.invalidateViews();
		    
		    int month = dateFirstOfMonth.get(Calendar.MONTH);
		    
		    TextView tvTitle = (TextView)findViewById(R.id.month_title);
		    tvTitle.setText(Common.getShortMonthName(month) + " " + dateFirstOfMonth.get(Calendar.YEAR));

		}
		else
		{
			
		}
	}
	
	private void processLogClick(Object tag)
	{
		if(tag == null || !(tag instanceof DayData)){
			return;
		}
		DayData dayData = (DayData)tag;
		
		if(dayData.workoutId == FUTURE_WORKOUT)
		{
			return;
		}
		
		if(dayData.numOfLogs<=0)
		{
			this.showMsgDlg(R.string.msg_no_logs, Common.REASON_NOT_IMPORTANT);
			return;
		}
		
		Intent intent = new Intent(this, LogViewActivity.class);
		//long dt = Common.getDateTimeForDay(dateFirstOfMonth.get(Calendar.YEAR), dateFirstOfMonth.get(Calendar.MONTH), dayData.dayOfMonth);
		//intent.putExtra("datetime", dt);
		intent.putExtra("year", dateFirstOfMonth.get(Calendar.YEAR));
		intent.putExtra("month",dateFirstOfMonth.get(Calendar.MONTH));
		intent.putExtra("day", dayData.dayOfMonth);
		intent.putExtra("workout_id", dayData.workoutId);
		intent.putExtra("week_id", dayData.weekId);
		
		intent.putExtra("formode", selectMode.getId());
		intent.putExtra("log_or_laps_count", dayData.numOfLogs);
		
		intent.putExtra("day_mode", dayData.workoutMode.getId());///////////////////////
		
		//type		
		switch(dayData.workoutMode)
		{
		case Endurance:
			intent.putExtra("wk_type",ENDURANCE);
			break;
		case Bodybuilding:
			intent.putExtra("wk_type",STRENGTH);
			break;
		case Tactical:
			intent.putExtra("wk_type",TACKTICAL);
			break;
		case Cardio:
			intent.putExtra("wk_type",CARDIO);
			break;
		default: break;
		}
		
		this.startActivityForResult(intent, Common.ACT_LogViewActivity);
	}
	
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch(requestCode)
		{
		case Common.ACT_LogViewActivity:
			
			return;
		case Common.ACT_DateSelectActivity:
			if(resultCode == Common.RESULT_OK && data != null)
			{
				int m = data.getIntExtra("month", -1);
				int yr = data.getIntExtra("year", -1);
				if(m >=0 && yr >0)
				{
					dateFirstOfMonth = new GregorianCalendar(yr,m, 1);					
					new InitMonthTask().execute();
				}
			}
			return;
		default: break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}


	private int getDayOfWeekOffset(final int d)
	{
		switch(d)
		{
		case Calendar.SUNDAY: return 0;
		case Calendar.MONDAY: return 1;
		case Calendar.TUESDAY: return 2;
		case Calendar.WEDNESDAY: return 3;
		case Calendar.THURSDAY: return 4;
		case Calendar.FRIDAY: return 5;
		case Calendar.SATURDAY: return 6;
		default: return 0; //error??
		}
	}
	
	private String getDayTitle(final int ind)
	{
		switch(ind)
		{
		case 0: return "SUN";
		case 1: return "MON";
		case 2: return "TUE";
		case 3: return "WED";
		case 4: return "THU";
		case 5: return "FRI";
		case 6: return "SAT";
		default: return "";
		}
	}
	
	private void showSchedulerCheckbox()
	{
		CheckBox cb = (CheckBox)this.findViewById(R.id.cb_scheduler);
		if(selectMode == WorkoutMode.All)
		{
			if(cb.getVisibility() != View.VISIBLE) cb.setVisibility(View.VISIBLE);
			cb.setEnabled(true);
			cb.setChecked(this.showScheduler);
		}
		else
		{
			if(cb.getVisibility() == View.VISIBLE) cb.setVisibility(View.INVISIBLE);
			cb.setEnabled(false);
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		if(dateFirstOfMonth != null)
		{
			outState.putInt("month", dateFirstOfMonth.get(Calendar.MONTH));
			outState.putInt("month", dateFirstOfMonth.get(Calendar.YEAR));
			outState.putInt("mode", selectMode.getId());
			outState.putBoolean("future", showScheduler);
		}		
		super.onSaveInstanceState(outState);
	}

	public void onButtonClick(View view) 
	{
		switch(view.getId())
		{
		case R.id.but_date_change:
			showChangeDate();
			break;		
		case R.id.but_close:
			finish();
			break;
		case R.id.cb_scheduler:
			showScheduler=!showScheduler;
			if(selectMode == WorkoutMode.All)
			{
				reloadCalendar();
			}			
			break;
		default: break;
		}
	}
	
	private void showChangeDate()
	{
		Common.showDateSelectActivity(this);
	}
	
	
	
	
	private void reloadCalendar()
	{
		if(selectMode == WorkoutMode.All){
			new InitMonthTask().execute();
		}
	}
	

	@Override
	public int getId() {
		return Common.ACT_LogCalendarActivity;
	}
	
	public class LogAdapter extends BaseAdapter{
		private Context context;
		public LogAdapter(Context cont)
		{
			context = cont;
		}

		@Override
		public int getCount() {
			return (listDays != null)? listDays.size() : 0;
		}

		@Override
		public Object getItem(int pos) {
			return (listDays != null)? listDays.get(pos) : null;
		}

		@Override
		public long getItemId(int pos) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		 	View gridView;
//			if (convertView == null) {  // if it's not recycled, initialize some attributes
//				gridView = inflater.inflate(R.layout.calendar_item, parent, false);
//			}
//			else
//			{
//				gridView = (View) convertView;
//			}
		 	
		 	if(position < 7)
		 	{		 		
		 		gridView = inflater.inflate(R.layout.calendar_item_top, parent, false);
		 		TextView tvName = (TextView)gridView.findViewById(R.id.cal_day_name);
		 		tvName.setText(getDayTitle(position));		 		
		 		return gridView;
		 	}
						
		 	gridView = inflater.inflate(R.layout.calendar_item, parent, false);
		 	TextView tvNum = (TextView)gridView.findViewById(R.id.day_number);
			TextView tvBlock = (TextView)gridView.findViewById(R.id.txt_block);
			TextView tvDay = (TextView)gridView.findViewById(R.id.txt_day);
			ImageView docIcon = (ImageView)gridView.findViewById(R.id.img_doc);
			View bgView = gridView.findViewById(R.id.cal_item_bg);		
			
			if(minH >0)
			{				
				LayoutParams params = bgView.getLayoutParams();
				params.height = minH;
				bgView.setLayoutParams(params);				
			}		

			if(listDays != null && position < listDays.size())
			{
				Object obj =  listDays.get(position);
				if(obj != null)
				{					
					DayData dayData =(DayData)obj;
					if(dayData.dayOfMonth >0){
						tvNum.setText(Integer.toString(dayData.dayOfMonth));
						tvNum.setVisibility(View.VISIBLE);
						if(day_today == dayData.dayOfMonth)
						{
							tvNum.setTextColor(Color.MAGENTA);
						}
					}
					else tvNum.setVisibility(View.INVISIBLE);
					//logs					
						
						if(selectMode == WorkoutMode.All)
						{
							if(dayData.numOfLogs >0)
							{
								bgView.setBackgroundResource(R.drawable.bg_calendar_day_active);
								docIcon.setVisibility(View.VISIBLE);
							}
							else if(showScheduler && dayData.workoutId==FUTURE_WORKOUT)
							{
								//future 								
								if(dayData.workoutMode == WorkoutMode.Rest || dayData.workoutMode == WorkoutMode.Unknown)
								{
									bgView.setBackgroundResource(R.drawable.bg_calendar_day);
									docIcon.setVisibility(View.GONE);
									
								}
								else if(dayData.workoutMode == WorkoutMode.Endurance)
								{
									bgView.setBackgroundResource(R.drawable.bg_calendar_day_future);
									docIcon.setVisibility(View.VISIBLE);
									docIcon.setImageResource(R.drawable.endurance_r);
								}
								else if(dayData.workoutMode == WorkoutMode.Tactical)
								{
									bgView.setBackgroundResource(R.drawable.bg_calendar_day_future);
									docIcon.setVisibility(View.VISIBLE);
									docIcon.setImageResource(R.drawable.tactical);
								}
								else if(dayData.workoutMode == WorkoutMode.Cardio)
								{
									bgView.setBackgroundResource(R.drawable.bg_calendar_day_future);
									docIcon.setVisibility(View.VISIBLE);
									docIcon.setImageResource(R.drawable.cardio);
								}
								else
								{
									bgView.setBackgroundResource(R.drawable.bg_calendar_day_future);
									docIcon.setVisibility(View.VISIBLE);
									docIcon.setImageResource(R.drawable.weight_r);
								}								
							}
							else
							{
								bgView.setBackgroundResource(R.drawable.bg_calendar_day_nolog);
								docIcon.setVisibility(View.VISIBLE);								
								docIcon.setImageResource(R.drawable.check_small);
							}
							
							gridView.setTag(dayData);
							
							if(dayData.blockTxt == null)
							{
								tvBlock.setVisibility(View.INVISIBLE);
							}
							else
							{
								tvBlock.setText(dayData.blockTxt);
								tvBlock.setVisibility(View.VISIBLE);
							}
							
							if(dayData.dayOrder <=0)
							{
								tvDay.setVisibility(View.INVISIBLE);
							}
							else
							{
								int dayNum = dayData.dayOrder%7;
								if(dayNum==0) dayNum = 7;
								tvDay.setText("Day " + dayNum); ////////////////////////////////
								tvDay.setVisibility(View.VISIBLE);
							}
						}
						else if(selectMode == WorkoutMode.Tactical)
						{
							if(dayData.numOfLogs >0)
							{
								bgView.setBackgroundResource(R.drawable.bg_calendar_day_active);
								docIcon.setVisibility(View.VISIBLE);
								gridView.setTag(dayData);							
								tvBlock.setText(TACKTICAL);
								tvBlock.setVisibility(View.VISIBLE);
								tvDay.setVisibility(View.INVISIBLE);
							}
							else
							{
								//we have day with 0 logs
								docIcon.setVisibility(View.GONE);
								bgView.setBackgroundResource(R.drawable.bg_calendar_day);
								tvBlock.setVisibility(View.INVISIBLE);
								tvDay.setVisibility(View.INVISIBLE);
							}							
						}
						else
						{
							if(dayData.numOfLogs >0)
							{
								bgView.setBackgroundResource(R.drawable.bg_calendar_day_active);
								docIcon.setVisibility(View.VISIBLE);
								gridView.setTag(dayData);	
								//here we can have dif. workouts in the same day - we didn't bring mode
//								tvBlock.setText(WORKOUT);								
								if(dayData.workoutMode == WorkoutMode.Endurance)
								{
									tvBlock.setText(ENDURANCE);
								}
								else if(dayData.workoutMode == WorkoutMode.Bodybuilding)
								{
									tvBlock.setText(STRENGTH);
								}
								else
								{
									tvBlock.setText(WORKOUT);
								}							
								tvBlock.setVisibility(View.VISIBLE);
								tvDay.setVisibility(View.INVISIBLE);
							}
							else
							{
								//we have day with 0 logs
								docIcon.setVisibility(View.GONE);
								bgView.setBackgroundResource(R.drawable.bg_calendar_day);
								tvBlock.setVisibility(View.INVISIBLE);
								tvDay.setVisibility(View.INVISIBLE);
							}
							
						}						
					
					
				}
				else
				{					
					//see if we are on a day without workout
					int off = offset+7;
					if((position < off) || (position >= off+totalDaysInMonth))
					{
						//empty days
						tvNum.setVisibility(View.INVISIBLE);						
					}
					else 
					{
						int num = position-off+1;
						tvNum.setVisibility(View.VISIBLE);
						tvNum.setText(Integer.toString(num));
						if(day_today == num)
						{
							tvNum.setTextColor(Color.MAGENTA);
						}
					}					
					bgView.setBackgroundResource(R.drawable.bg_calendar_day);
					tvBlock.setVisibility(View.INVISIBLE);
					tvDay.setVisibility(View.INVISIBLE);
					docIcon.setVisibility(View.GONE);
				}				
			}
			else
			{
				gridView = inflater.inflate(R.layout.calendar_item_empty, parent, false); /////////////////
			}
			return gridView;
		}
		
	}
		
	
	/////////////////////// TASK ////////////////////////
	protected class InitMonthTask extends AsyncTask<String, Void, long[]>
	{
		private ProgressDialog dialog = new ProgressDialog(LogCalendarActivity.this);  
		// can use UI thread here
		 protected void onPreExecute() { 
			 this.dialog.setMessage(getString(R.string.processing));  
 			 this.dialog.show();  
		  }  

		@Override
		protected long[] doInBackground(String... arg0) {
			boolean ok = initCalendar();
			long[] res = new long[1];
			res[0] =(ok)? 1 : 0;
			return res;
		}
		
		protected void onPostExecute(final long[] result) 
		{
			try
			{
				if(dialog != null)
				{
					dialog.dismiss();
					dialog = null;
				}
			}
			catch(Exception e)
			{
				//nothing
			}	         
			LogCalendarActivity.this.processCalInitDone((result[0]==1)? true : false);
		}
		
	}

///////////////   GESTURES /////////////////////////////	
	
	@Override
	public void onSwipe(SwipeDirection dir) {
		if(dir == SwipeDirection.LEFT_SWIPE)
		{
			//next
			dateFirstOfMonth.add(Calendar.MONTH, 1);
			new InitMonthTask().execute();
		}
		else if(dir == SwipeDirection.RIGHT_SWIPE)
		{
			//prev
			dateFirstOfMonth.add(Calendar.MONTH, -1);
			new InitMonthTask().execute();
		}
		
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return processOnTouch(event);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		return processOnTouch(event);
	}
	
	private boolean processOnTouch(MotionEvent event)
	{
		if (gestureDetector != null && gestureDetector.onTouchEvent(event))
		{				
			return true;
		}
		else
		{
			return false;
		}		
	}

}
