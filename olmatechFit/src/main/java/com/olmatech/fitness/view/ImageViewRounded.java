package com.olmatech.fitness.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

public class ImageViewRounded extends ImageView{
	
	private static Paint pnt;
	private RectF clipRect;
	//private RectF clipRectInset;
	private final static float radius =4.0f;
	private final static float padding = radius / 2;
	private Path clipPath;

	public ImageViewRounded(Context context) {
		super(context);
		setUpView();		
	}
	
	public ImageViewRounded(Context context, AttributeSet attrs) {
		super(context, attrs);
		setUpView();		
	}
	
	public ImageViewRounded(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setUpView();
		
	}
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setUpView()
	{
		if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)  this.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
	}
	
//	private void initRects()
//	{
//		clipRect = new RectF();
//		clipRectInset = new RectF();
//		clipPath= new Path();
//	}
	
	private void setColors()
	{		
		pnt = new Paint();
		pnt.setColor(Color.rgb(53,53,53));  //53,53,53
		pnt.setAntiAlias(true);	
		pnt.setStyle(Paint.Style.STROKE);
		pnt.setStrokeWidth(2.0f);
	}
	
	

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {	
		//square view
		int size = 0;		
		int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
		 if (width > height) {
		       size = height;
		 } else {
		   size = width;
		 }
		 
		  setMeasuredDimension(size, size);	
		  super.onMeasure(
	                MeasureSpec.makeMeasureSpec(size, MeasureSpec.EXACTLY),
	                MeasureSpec.makeMeasureSpec(size, MeasureSpec.EXACTLY)
	        );
	}
	

//	@Override
//	protected void onLayout(boolean changed, int left, int top, int right,	int bottom) {
//		RelativeLayout.LayoutParams params =(RelativeLayout.LayoutParams)this.getLayoutParams();
//		int w = right-left;
//		int h = bottom-top;
//		
//		//Log.d("IMG", "onLayout w=" + w + "  h=" + h);
//		if(w > h) w = h;
//		Log.d("IMG", "onLayout new w=" + w );
//		
//		params.width = w;
//		params.height = w;
//		this.setLayoutParams(params);
//		this.setMeasuredDimension(w, w);
//		
//		if(clipRect == null || clipRectInset==null)
//		{
//			initRects();
//		}
//		clipRect.set(padding, padding, w - padding, w - padding);	
//		
//		final float inx = padding+2;
//		final float inw =  w - padding-2;
//		clipRectInset.set(inx, inx, inw, inw);
//		if(clipPath == null) clipPath = new Path();
//		clipPath.reset();		
//		clipPath.addRoundRect(clipRect, radius, radius, Path.Direction.CW);
//		super.onLayout(changed, left, top, left+w, top+w);
//	}

	@Override
	protected void onDraw(Canvas canvas) {		
		
	    int w = this.getWidth();
	   // int h = this.getHeight();	   
	    if(clipRect == null)
	    {
	    	clipRect = new RectF();
	    	clipRect.set(padding, padding, w - padding, w - padding);	
	    }
//	    if(clipRectInset == null)
//	    {
//	    	clipRectInset = new RectF();
//			final float inx = padding+2;
//			final float inw =  w - padding-2;
//			clipRectInset.set(inx, inx, inw, inw);
//	    }
	    
	    if(clipPath == null)
		{
			clipPath = new Path();
			clipPath.reset();
			clipPath.addRoundRect(clipRect, radius, radius, Path.Direction.CW); //clipRectInset
		}		  
	   
	  	final int count = canvas.save();
	  	
	    canvas.clipPath(clipPath);
		super.onDraw(canvas);	
		
		//border	
	  	if(pnt == null) setColors();
	  	canvas.drawRoundRect(clipRect, radius, radius, pnt);	  	
	  	
		canvas.restoreToCount(count);
		
				
	}
	
	

}
