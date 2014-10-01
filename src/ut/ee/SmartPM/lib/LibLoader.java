package ut.ee.SmartPM.lib;

import java.io.File;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import dalvik.system.DexClassLoader;

public class LibLoader {
	
	public LibLoader(Context context) {
		
		
		// Check if folder exists
        File folder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/SmartPM");
        if (!folder.exists()) {
            folder.mkdir();
            Log.d("DIRECTORY", "created");
        }
        
        try {
        	
        	// take files from following path
        	final String libPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/SmartPM/";
            final File tmpDir = context.getDir("dex", 0);
            
            // Go through each apk and load it
            // TODO: load only apk's that are required for the task
            File dir = new File(libPath);
            File[] filelist = dir.listFiles();
            for (File f : filelist)
            {
            	// TODO: To get class name dynamically from jar we would need something like that...
            	// http://stackoverflow.com/questions/11453614/how-can-i-load-a-jar-file-dynamically-in-an-android-application-4-0-3
            	
            	if (f.getName().endsWith(".apk")) {
            		
            		Log.d("PlugIn file name", f.getName());

	            		
	            		
	            		final DexClassLoader classloader = new DexClassLoader(libPath + f.getName(), tmpDir.getAbsolutePath(), null, this.getClass().getClassLoader());
	                    final Class<Object> classToLoad = (Class<Object>) classloader.loadClass("ut.ee.SmartPM.lib.MyClass");
	                    
	                    LibInterface obj = (LibInterface) classToLoad.newInstance();
	                    
	                    // execute the library/plug-in
	                   
	                    Log.d("LIB", "before lib");
	                    obj.useMyLib(context);
	                    Log.d("LIB", obj.useMyLib(context));
	                    obj.getName();
	                    Log.d("LIB", obj.getName());
	                    obj.getType();
	                    Log.d("LIB", obj.getType());
	                    
	
            	}
            	
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
}
