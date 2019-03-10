package com.olmatech.fitness.controls;

import android.content.Context;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PaintDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.StateListDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.util.AttributeSet;
import android.widget.ImageView;

public class ImageViewCardioEquipment extends ImageView{
	
	private int id=-1;
	private boolean isSelected = false;

	public ImageViewCardioEquipment(Context context) {
		super(context);	
	}
	
	public ImageViewCardioEquipment(Context context, AttributeSet attrs) {
		super(context, attrs);
		
	}
	
	public ImageViewCardioEquipment(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		
	}
	
//	@Override
//	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//		int sz;
//		boolean isTablet = Common.getIsTablet();
//		if(isTablet)
//		{
//			sz = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 80, this.getContext().getResources().getDisplayMetrics());
//		}
//		else sz = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, getContext().getResources().getDisplayMetrics());
//		
//		setMeasuredDimension(sz,sz); 
//	}
	
	public void setId(final int val){ id = val; }
	public int getId(){ return id; }
	
	//Buttons backgrounds
		private static ShapeDrawable.ShaderFactory bgMenuButShaderOn= new ShapeDrawable.ShaderFactory() {
		    @Override
		    public Shader resize(int width, int height) {
		        LinearGradient lg = new LinearGradient(0, 0, 0, height,
		            new int[] { 
		        		Color.argb(175,0,153,255), 	        		
		        		Color.argb(175, 98,192,255),			        		
		        		Color.argb(175, 0,153,255) }, 
		            new float[] {
		                0, 0.5f, 1 },
		            Shader.TileMode.REPEAT);
		         return lg;	        
		    }
		};
		
//		private static ShapeDrawable.ShaderFactory bgMenuBgShader = new ShapeDrawable.ShaderFactory() {
//
//			@Override
//		    public Shader resize(int width, int height) {
//		        LinearGradient lg = new LinearGradient(0, 0, 0, height,
//		            new int[] { 
//		        		Color.argb(255, 46,46,46), 	        		
//		        		Color.argb(255, 21,21,21),	
//		        		Color.argb(255, 0,0,0),
//		        		Color.argb(255, 0,0,0) }, 
//		            new float[] {
//		                0, 0.5f, 0.5f, 1 },
//		            Shader.TileMode.REPEAT);
//		         return lg;	        
//		    }
//			
//		};
		
		public void setButBackground()
		{		
			PaintDrawable pOff = new PaintDrawable(Color.TRANSPARENT);		
			
			int cornerRadius = 10;
			float[] outerR = new float[]{cornerRadius, cornerRadius, cornerRadius, cornerRadius, cornerRadius, cornerRadius, cornerRadius, cornerRadius};
//				RectF inset = new RectF(2, 2, 2, 2);
//				float[] innerR = new float[]{cornerRadius, cornerRadius, cornerRadius, cornerRadius, cornerRadius, cornerRadius, cornerRadius, cornerRadius};
			RoundRectShape rect = new RoundRectShape(outerR, null, null);  
	                
	        StateListDrawable states = new StateListDrawable();       
	        
	        PaintDrawable pOn = new PaintDrawable();
	        pOn.setShape(rect);
	        pOn.setShaderFactory(bgMenuButShaderOn);
	        
	        pOff.setShape(rect);
	        
	        states.addState(new int[] {android.R.attr.state_focused},(Drawable)pOn);
	        states.addState(new int[] {android.R.attr.state_pressed}, (Drawable)pOn);        
	        states.addState(new int[] {android.R.attr.state_enabled}, (Drawable)pOff);        
	        states.addState(new int[] { },(Drawable)pOff);    ///////////////////////// 
	        
	        this.setBackgroundDrawable(states);
	        isSelected = false;
	        
//		        int sdk = android.os.Build.VERSION.SDK_INT;
//		        if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
//		        	bgView.setBackgroundDrawable(states);
//		        } else {
//		        	bgView.setBackground(states);
//		        }
		}
		
		//set on backgr for menu button
		public void setButBackgroundOn()
		{			
			int cornerRadius = 10;
			float[] outerR = new float[]{cornerRadius, cornerRadius, cornerRadius, cornerRadius, cornerRadius, cornerRadius, cornerRadius, cornerRadius};

			RoundRectShape rect = new RoundRectShape(outerR, null, null);       
	        PaintDrawable pOn = new PaintDrawable();
	        pOn.setShape(rect);
	        pOn.setShaderFactory(bgMenuButShaderOn);        
	        
	        this.setBackgroundDrawable((Drawable)pOn);
	        isSelected = true;
		}
		
		public boolean isSelected(){ return isSelected; }
		//public void setSelected(final boolean val){ isSelected = val; }
		
		

}
