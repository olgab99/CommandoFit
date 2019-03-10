package com.olmatech.fitness.ui;

import android.view.GestureDetector;
import android.view.MotionEvent;

public class MainGestureDetector extends GestureDetector.SimpleOnGestureListener{
	private static final int SWIPE_MIN_DISTANCE = 100; 
   // private static final int SWIPE_MAX_OFF_PATH = 250; 
    private static final int SWIPE_THRESHOLD_VELOCITY = 100;  
    
    private int mSwipeDistance;
    private int mSwipeThresholdVelocity;
    
    private IDetectGestures gestDetector;
    
   public enum SwipeDirection
   {
	   LEFT_SWIPE,
	   RIGHT_SWIPE,
	   UP_SWIPE,
	   DOWN_SWIPE	   
	   
   }
    
    public MainGestureDetector(final IDetectGestures frm, final float scale)
    {
    	super();
    	gestDetector = frm;
    	mSwipeDistance = (int)(SWIPE_MIN_DISTANCE*scale/160.0f); // (int) (SWIPE_MIN_DISTANCE * scale + 0.5f);
    	mSwipeThresholdVelocity  =(int)(SWIPE_THRESHOLD_VELOCITY*scale/160.0f); //(int) (SWIPE_THRESHOLD_VELOCITY * scale + 0.5f);
    }
        
   
	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		
		try { 
//            if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH ||
//            		Math.abs(e1.getX() - e2.getX()) > SWIPE_MAX_OFF_PATH) 
//            {
//            	Log.i("Act", "Fling OTHER");
//            	return false; 
//            }
			
//			float diff = e1.getX() - e2.getX();
			
			
			//Log.d("GS","diff="+diff + "  mSwipeDistance=" + mSwipeDistance + "  velocityX="+velocityX +"  mSwipeThresholdVelocity=" +mSwipeThresholdVelocity);
                
            // right to left swipe 
            if(e1.getX() - e2.getX() > mSwipeDistance && Math.abs(velocityX) > mSwipeThresholdVelocity) {            	
            	gestDetector.onSwipe(SwipeDirection.LEFT_SWIPE);            	
                
            }  else if (e2.getX() - e1.getX() > mSwipeDistance && Math.abs(velocityX) > mSwipeThresholdVelocity) { 
            	gestDetector.onSwipe(SwipeDirection.RIGHT_SWIPE);            	           	
            } 
            else if(e1.getY() - e2.getY() > mSwipeDistance && Math.abs(velocityY) > mSwipeThresholdVelocity) { 
            	gestDetector.onSwipe(SwipeDirection.UP_SWIPE);              	            	          	
            }  else if (e2.getY() - e1.getY() > mSwipeDistance && Math.abs(velocityY) > mSwipeThresholdVelocity) { 
            	gestDetector.onSwipe(SwipeDirection.DOWN_SWIPE);            	            	
            }
//            else
//            {
//            	
//            }
        } catch (Exception e) { 
            // nothing 
        } 
        return false; 

	}	
	
	public interface IDetectGestures{
		public void onSwipe(SwipeDirection dir);
	}

}