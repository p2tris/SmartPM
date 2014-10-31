/**
 * @author Pätris Halapuu 2014
 */

package ut.ee.SmartPM.lib;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import org.apache.http.util.ByteArrayBuffer;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import dalvik.system.DexClassLoader;

public class LibLoader {
	Context mContext;
	String fileName;
	String libPath;
	TextView mAutoLabel;
	String rules;
	
	public LibLoader(Context context, String lib, TextView autoLabel, String rules) {
		this.mContext = context;
		this.mAutoLabel = autoLabel;
		Log.d("LIB", "In libloader");
		this.fileName = lib.substring( lib.lastIndexOf('/')+1, lib.length() );
		this.libPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/SmartPM/";
		this.rules = rules;
		
		// Check if folder exists
        File folder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/SmartPM");
        if (!folder.exists()) {
            folder.mkdir();
            Log.d("DIRECTORY", "created");
        }
        
        try {
        	
            final File tmpDir = mContext.getDir("dex", 0);
            
            File myFile = new File(libPath + fileName);
            if(myFile.exists()){
            	Log.d("LIB","Already there");
            	final DexClassLoader classloader = new DexClassLoader(libPath + fileName, tmpDir.getAbsolutePath(), null, this.getClass().getClassLoader());
                final Class<Object> classToLoad = (Class<Object>) classloader.loadClass("ut.ee.SmartPM.lib.MyClass");
                
                LibInterface obj = (LibInterface) classToLoad.newInstance();
                
                // execute the library/plug-in
               
                Log.d("LIBloader", "before lib");
                obj.useMyLib(mContext, mAutoLabel, rules);
                obj.getName();
                Log.d("LIBloader", obj.getName());
                obj.getType();
                Log.d("LIBloader", obj.getType());
            } else {
            	new DownloadLibfromInternet().execute(lib);

            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
	
	// Async Task Class
    class DownloadLibfromInternet extends AsyncTask<String, String, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        // Download Music File from Internet
        @Override
        protected String doInBackground(String... f_url) {
            int count;
            try {
                URL url = new URL(f_url[0]);
                URLConnection conection = url.openConnection();
                conection.connect();
                // Get Music file length
                int lenghtOfFile = conection.getContentLength();
                // input stream to read file - with 8k buffer
                InputStream input = new BufferedInputStream(url.openStream(),10*1024);
                // Output stream to write file in SD card
                OutputStream output = new FileOutputStream(libPath+fileName);
                byte data[] = new byte[1024];
                long total = 0;
                while ((count = input.read(data)) != -1) {
                    total += count;
                    // Publish the progress which triggers onProgressUpdate method
                    publishProgress("" + (int) ((total * 100) / lenghtOfFile));

                    // Write data to file
                    output.write(data, 0, count);
                }
                // Flush output
                output.flush();
                // Close streams
                output.close();
                input.close();
            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(String file_url) {
        	final File tmpDir = mContext.getDir("dex", 0);
            
            File myFile = new File(libPath + fileName);
            if(myFile.exists()){
            	Log.d("LIB","Starting to load downloaded file class");
            	
				try {
					final DexClassLoader classloader = new DexClassLoader(libPath + fileName, tmpDir.getAbsolutePath(), null, this.getClass().getClassLoader());
	                final Class<Object> classToLoad = (Class<Object>) classloader.loadClass("ut.ee.SmartPM.lib.MyClass");
	                
	                LibInterface obj = (LibInterface) classToLoad.newInstance();
	                
	                // execute the library/plug-in
	               
	                Log.d("LIB", "before lib");
	                obj.useMyLib(mContext, mAutoLabel, rules);
	                Log.d("LIB", obj.useMyLib(mContext, mAutoLabel, rules));
	                obj.getName();
	                Log.d("LIB", obj.getName());
	                obj.getType();
	                Log.d("LIB", obj.getType());
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InstantiationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                
               
        } else {
        	Log.d("LIB FAIL", "Failed loading the lib after downloading the lib");
        }
    }
    
    }
}
