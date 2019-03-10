package kankan.wheel.widget.adapters;

import android.content.Context;

public class WeightLbWheelAdapter extends AbstractWheelTextAdapter{
	
	 /** The default max value */
    public static final int DEFAULT_MAX_VALUE = 500;

    /** The default min value */
    private static final int DEFAULT_MIN_VALUE = 0;
    
    private static int DELTA=5;  //weight increment  
    
    /**
     * From this index we are adding DELTA lb
     */
    private static int BREAK_VALUE=500;//15;   
    
    
 // Values
    private int minValue;
    private int maxValue;
    
    // format
    private String format;

    public WeightLbWheelAdapter(Context context, int minValue, int maxValue, String format) {
		 super(context);
		 this.minValue = minValue;
	     this.maxValue = maxValue;
	     this.format = format;
	}
    
    public WeightLbWheelAdapter(Context context)
    {
    	this(context, DEFAULT_MIN_VALUE, DEFAULT_MAX_VALUE);
    }
	
    public WeightLbWheelAdapter(Context context, int minValue, int maxValue)
    {
    	this(context, minValue, maxValue, null);
    }
    
    public static void setBreakValue(final int val)
    {
    	BREAK_VALUE= val;
    }
    
    public static int getBreakValue()
    {
    	return BREAK_VALUE;
    }
    
    public static void setDeltaValue(final int val)
    {
    	DELTA = val;
    }
    
    public static int getDeltaValue()
    {
    	return DELTA;
    }
	

	@Override
	public int getItemsCount() {
		return maxValue - minValue + 1;
	}

	@Override
	protected CharSequence getItemText(int index) {
		if (index >= 0 && index < getItemsCount()) 
		{
//			if(index ==0)
//			{
//				return "BW";
//			}
			if(index <= BREAK_VALUE)
			{
				return format != null ? String.format(format, index) : Integer.toString(index);				
			}
			else 
			{
				int val = (index - BREAK_VALUE)*DELTA + BREAK_VALUE;
				 return format != null ? String.format(format, val) : Integer.toString(val);
			}			
		}
		return null;
	}
	
	public static int getItemIndexFromValue(final int val)
	{
		if(val==0) return 0;
		if(val <= BREAK_VALUE) return val;
		
		final double breakVal = (double)BREAK_VALUE;
		final double deltaVal = (double)DELTA;
		
		double res = ((double)val - breakVal)/deltaVal + breakVal + 0.5;
		return (int)res;
	}
	
	public static int getItemValueFromIndex(final int index)
	{
		if(index==0)
		{
			return 0; //BW
		}
		
		if(index <= BREAK_VALUE)
		{
			return index;
		}
		return (index - BREAK_VALUE)*DELTA + BREAK_VALUE;
	}

}
