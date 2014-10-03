package ut.ee.SmartPM.lib;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import org.apache.http.util.ByteArrayBuffer;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import dalvik.system.DexClassLoader;

public class LibLoader {
	Context mContext;
	
	public LibLoader(Context context, String lib) {
		this.mContext = context;
		Log.d("LIB", "In libloader");
		String fileName = lib.substring( lib.lastIndexOf('/')+1, lib.length() );
		
		// Check if folder exists
        File folder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/SmartPM");
        if (!folder.exists()) {
            folder.mkdir();
            Log.d("DIRECTORY", "created");
        }
        
        try {
        	
        	// take files from following path
        	final String libPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/SmartPM/";
            final File tmpDir = mContext.getDir("dex", 0);
            
            File myFile = new File(libPath + fileName);
            if(myFile.exists()){
            	Log.d("LIB","Already there");
            	final DexClassLoader classloader = new DexClassLoader(libPath + fileName, tmpDir.getAbsolutePath(), null, this.getClass().getClassLoader());
                final Class<Object> classToLoad = (Class<Object>) classloader.loadClass("ut.ee.SmartPM.lib.MyClass");
                
                LibInterface obj = (LibInterface) classToLoad.newInstance();
                
                // execute the library/plug-in
               
                Log.d("LIB", "before lib");
                obj.useMyLib(mContext);
                Log.d("LIB", obj.useMyLib(mContext));
                obj.getName();
                Log.d("LIB", obj.getName());
                obj.getType();
                Log.d("LIB", obj.getType());
            } else {
            	try {
                    
                    File dir = new File (libPath);
                    
                    URL url = new URL(lib); //you can write here any link
                    File file = new File(dir, fileName);

                    long startTime = System.currentTimeMillis();
                    Log.d("DownloadManager", "download begining");
                    Log.d("DownloadManager", "download url:" + url);
                    Log.d("DownloadManager", "downloaded file name:" + fileName);

                    /* Open a connection to that URL. */
                    URLConnection ucon = url.openConnection();

                    /*
                     * Define InputStreams to read from the URLConnection.
                     */
                    InputStream is = ucon.getInputStream();
                    BufferedInputStream bis = new BufferedInputStream(is);

                    /*
                     * Read bytes to the Buffer until there is nothing more to read(-1).
                     */
                    ByteArrayBuffer baf = new ByteArrayBuffer(5000);
                    int current = 0;
                    while ((current = bis.read()) != -1) {
                       baf.append((byte) current);
                    }


                    /* Convert the Bytes read to a String. */
                    FileOutputStream fos = new FileOutputStream(file);
                    fos.write(baf.toByteArray());
                    fos.flush();
                    fos.close();
                    Log.d("DownloadManager", "download ready in" + ((System.currentTimeMillis() - startTime) / 1000) + " sec");
	                if (((System.currentTimeMillis() - startTime) / 1000) == 0){
	                	Log.d("DownloadManager", "Download READY");
	                }
            } catch (IOException e) {
                Log.d("DownloadManager", "Error: " + e);
            }
            }
            
//            // Go through each apk and load it
//            // TODO: load only apk's that are required for the task
//            File dir = new File(libPath);
//            File[] filelist = dir.listFiles();
//            for (File f : filelist)
//            {
//            	// TODO: To get class name dynamically from jar we would need something like that...
//            	// http://stackoverflow.com/questions/11453614/how-can-i-load-a-jar-file-dynamically-in-an-android-application-4-0-3
//            	
//            	if (f.getName().endsWith(".apk")) {
//            		
//            		Log.d("PlugIn file name", f.getName());
//
//	            		
//	            		
//	            		final DexClassLoader classloader = new DexClassLoader(libPath + f.getName(), tmpDir.getAbsolutePath(), null, this.getClass().getClassLoader());
//	                    final Class<Object> classToLoad = (Class<Object>) classloader.loadClass("ut.ee.SmartPM.lib.MyClass");
//	                    
//	                    LibInterface obj = (LibInterface) classToLoad.newInstance();
//	                    
//	                    // execute the library/plug-in
//	                   
//	                    Log.d("LIB", "before lib");
//	                    obj.useMyLib(context);
//	                    Log.d("LIB", obj.useMyLib(context));
//	                    obj.getName();
//	                    Log.d("LIB", obj.getName());
//	                    obj.getType();
//	                    Log.d("LIB", obj.getType());
//	                    
//	
//            	}
//            	
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
}
