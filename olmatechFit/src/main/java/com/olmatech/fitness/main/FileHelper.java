package com.olmatech.fitness.main;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import android.content.res.AssetManager;

public final class FileHelper {
	
	//unzip images to HD
	 public static boolean unzip(final AssetManager assetManager, final String outputDir)			
		{	
	    	 InputStream in = null;	
			 
			 try
			 {
				 in = assetManager.open("exam_images.zip");
			 }
			 catch(Exception e)
			 {
				 //no images
				 return true;			 
			 }
			 
			 
			 if(in == null)
			 {
				return true;
			 }
			 File dirData = new File(outputDir);
			if(dirData.exists() == false)
			{			
				if(dirData.mkdir() == false)
				{
					return false;
				}			
			}		

			FileOutputStream fos = null;
			BufferedOutputStream dest = null;
				
			ZipInputStream zin = new ZipInputStream(new BufferedInputStream(in));		
			ZipEntry entry = null;	
			
			try {
				entry = zin.getNextEntry();
			} catch (IOException e1) {			
				e1.printStackTrace();			
				return false;
			}	
			
			if(entry == null)
			{
				return false;
			}
			
			try
			{
				
				final int BUFFER = 2048;			
							
				while(entry != null) {			
				   // extract data
				   // open output streams
					int count;
		            byte data[] = new byte[BUFFER];
		            // write the files to the disk	            
		            
		            File f = new File(dirData, entry.getName());      
		           
		            if (entry.isDirectory()) 
		            {
		            	if(!f.exists())
		            	{
		            		f.mkdirs();	   
		            	}	            	         		
		            	entry = zin.getNextEntry();	
		            	continue;
		            } 
		            else if (!f.getParentFile().exists()) {
		                  f.getParentFile().mkdirs();	                
		            }
		                        
			            fos = new FileOutputStream(f);			            
			            dest = new BufferedOutputStream(fos, BUFFER);
			            while ((count = zin.read(data, 0, BUFFER)) != -1) {
			               dest.write(data, 0, count);
			            }
			         
			            dest.flush();
			            dest.close();
		            
		            
		            entry = zin.getNextEntry();	
				}//while
			}
			catch(FileNotFoundException  e)
			{
				e.printStackTrace();			
				return false;
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
				if(fos != null)
				{
					try {
						fos.close();
					} catch (IOException e) {					
						
					}
					
				}
				if(dest != null)
				{
					try {
						dest.close();
					} catch (IOException e) {					
						
					}
				}
				return false;
			}
			finally
			{
				try {
					zin.close();
				} catch (IOException e) {
					e.printStackTrace();
				}	
			}		
			return true;
		}

}
