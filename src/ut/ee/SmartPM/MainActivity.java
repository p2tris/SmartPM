/**
 * @author Pätris Halapuu 2014
 */

package ut.ee.SmartPM;

import java.util.HashMap;
import java.util.Map;

import ut.ee.SmartPM.messageParse.DoInBackground;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gcm.GCMRegistrar;

public class MainActivity extends Activity {
	// label to display gcm messages
	public ImageView imageView1;
	Controller aController;
	public LinearLayout ll;
	Button executeBtn;
	TextView statusbar;
	
	public static final String PREFS_NAME = "MyPrefsFile";
	
	public static String actorName;
	
	// Asyntask
	AsyncTask<Void, Void, Void> mRegisterTask;
	
	public static String name;
	public static String email;
	
	final Context context = this;

	@Override
	public void onCreate(Bundle savedInstanceState) { 
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		LinearLayout ll = (LinearLayout) findViewById(R.id.ll);
		
		// Restore preferences
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        actorName = settings.getString("name", "Anon");
        
        SharedPreferences.Editor editor = settings.edit();
	    		    
		Map<String,?> keys = settings.getAll();

		for(Map.Entry<String,?> entry : keys.entrySet()){
			if(!(entry.getKey()).equals("name")){
				if(!(entry.getKey()).equals("message")){
					if(!(entry.getKey()).equals("taskName")){
						if(!(entry.getKey()).equals("started")){
							editor.remove(entry.getKey());
							editor.commit();	
						}
					}
				}
			}          
		 }
        
		//Get Global Controller Class object (see application tag in AndroidManifest.xml)
		aController = (Controller) getApplicationContext();
		
		// Check if Internet present
		if (!aController.isConnectingToInternet()) {
			
			// Internet Connection is not present
			aController.showAlertDialog(MainActivity.this,
					"Internet Connection Error",
					"Please connect to Internet connection", false);
			// stop executing code by return
			return;
		}
		
		// Getting name, email from intent
		Intent i = getIntent();
		
		name = i.getStringExtra("name");
		email = i.getStringExtra("email");		
		statusbar = (TextView) findViewById(R.id.textView1);
		imageView1 = (ImageView) findViewById(R.id.imageView1);
		executeBtn = (Button)findViewById(R.id.StartStop);
		
		// Make sure the device has the proper dependencies.
		GCMRegistrar.checkDevice(this);

		// Make sure the manifest permissions was properly set 
		GCMRegistrar.checkManifest(this);
		
		// Register custom Broadcast receiver to show messages on activity
		registerReceiver(mHandleMessageReceiver, new IntentFilter(
				Config.DISPLAY_MESSAGE_ACTION));
		
		// Get GCM registration id
		final String regId = GCMRegistrar.getRegistrationId(this);

		// Check if regid already presents
		if (regId.equals("")) {
			
			// Register with GCM			
			GCMRegistrar.register(this, Config.GOOGLE_SENDER_ID);
			
		} else {
			
			// Device is already registered on GCM Server
			if (GCMRegistrar.isRegisteredOnServer(this)) {
				// skip registration			
			} else {
				
				// Try to register again, but not in the UI thread.
				// It's also necessary to cancel the thread onDestroy(),
				// hence the use of AsyncTask instead of a raw thread.
				
				mRegisterTask = new AsyncTask<Void, Void, Void>() {

					@Override
					protected Void doInBackground(Void... params) {
						
						// Register on our server
						// On server creates a new user
						aController.register(context, name, email, regId);
						
						return null;
					}

					@Override
					protected void onPostExecute(Void result) {
						mRegisterTask = null;
					}

				};
				
				// execute AsyncTask
				mRegisterTask.execute(null, null, null);
			}
		}
		
		executeBtn.setClickable(false);
		executeBtn.setVisibility(View.INVISIBLE);
		statusbar.setText(actorName);
			
		

	}		
	
	// Create a broadcast receiver to get message and show on screen 
	private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			
			String newMessage = intent.getExtras().getString(Config.EXTRA_MESSAGE);
			
			
			Map<String, String> messageMap = new HashMap<String, String>();
			// Splits the string and makes it to object that we can work with 
			if(newMessage != null){
				if(newMessage.contains(";")){
					for(String loc : newMessage.split(";")){
				    	String[] elem = loc.split("\\|");
				    	messageMap.put(elem[0], elem[1]);
				    }	
				}
			}
		    
						
			// Waking up mobile if it is sleeping
			aController.acquireWakeLock(getApplicationContext());
			if(messageMap.containsKey("taskName")){
				// Display message on the screen
				
				Log.d("TASKNAME", messageMap.get("taskName"));
				if(messageMap.get("taskName").equals("start")){
					imageView1.setImageResource(R.drawable.greentaskball2);

					SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
				    SharedPreferences.Editor editor = settings.edit();
				    editor.putString("taskName", newMessage);
				    editor.putBoolean("started", true);

				    // Commit the edits!
				    editor.commit();
					executeBtn.setClickable(true);
				} else if (messageMap.get("taskName").equals("pause")) {
					
					SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
				    SharedPreferences.Editor editor = settings.edit();
				    editor.putString("taskName", newMessage);

				    // Commit the edits!
				    editor.commit();
					imageView1.setImageResource(R.drawable.idletaskball);

				    
					executeBtn.setClickable(false);
					executeBtn.setText("Paused");
				} else if (messageMap.get("taskName").equals("resume")) {
					
					SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
				    SharedPreferences.Editor editor = settings.edit();
				    editor.putString("taskName", newMessage);
				    editor.putBoolean("started", true);
				    // Commit the edits!
				    editor.commit();
				    
					imageView1.setImageResource(R.drawable.greentaskball2);
					
					executeBtn.setClickable(true);
					executeBtn.setText("Stop");
				}
			} else {
				// Display message on the screen
				imageView1.setImageResource(R.drawable.greentaskball);
			}
			Toast.makeText(getApplicationContext(), "Got Message: " + newMessage, Toast.LENGTH_SHORT).show();
			
			if(messageMap.containsKey("URL")){
				
				SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
			    SharedPreferences.Editor editor = settings.edit();
			    editor.putString("message", newMessage);

			    // Commit the edits!
			    editor.commit();
			    
				imageView1.setImageResource(R.drawable.greentaskball2);
				executeBtn.setVisibility(View.VISIBLE);
				executeBtn.setText("Start");
				LinearLayout ll = (LinearLayout) findViewById(R.id.ll);
				new DoInBackground(context, ll, executeBtn, imageView1).execute(messageMap.get("URL"));
			} else {
			}
									
			// Releasing wake lock
			aController.releaseWakeLock();
			
		}
	};
	
	
	@Override
	protected void onDestroy() {
		// Cancel AsyncTask
		if (mRegisterTask != null) {
			mRegisterTask.cancel(true);
		}
		try {
			// Unregister Broadcast Receiver
			unregisterReceiver(mHandleMessageReceiver);
			
			//Clear internal resources.
			GCMRegistrar.onDestroy(this);
			
		} catch (Exception e) {
			Log.e("UnRegister Receiver Error", "> " + e.getMessage());
		}
		
		// TODO: Save task status to memory...
		removeMeFromDB();
		
		super.onDestroy();
	}
	
	@Override  
    public boolean onCreateOptionsMenu(Menu menu) {  
        // Inflate the menu; this adds items to the action bar if it is present.  
        getMenuInflater().inflate(R.menu.menu_main, menu);//Menu Resource, Menu  
        return true;  
    }  
      
    @Override  
    public boolean onOptionsItemSelected(MenuItem item) {  
        switch (item.getItemId()) {  
        	//log out selected
            case R.id.item1:  
            	            	
            	removeMeFromDB();
            	
            //map selected
            case R.id.item2:  
              return true;     
              
            // history selected
            case R.id.item3:  
              return true;     
  
              default:  
                return super.onOptionsItemSelected(item);  
        }  
    } 
    
    public void removeMeFromDB(){
    	// Try to register again, but not in the UI thread.
		// It's also necessary to cancel the thread onDestroy(),
		// hence the use of AsyncTask instead of a raw thread.
		
		mRegisterTask = new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {
				
				// Register on our server
				// On server creates a new user
				aController.unregister(context, name);
				SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
				SharedPreferences.Editor editor = settings.edit();
    		    
				Map<String,?> keys = settings.getAll();

				for(Map.Entry<String,?> entry : keys.entrySet()){
					if(!(entry.getKey()).equals("name")){
						editor.remove(entry.getKey());
						editor.commit();	
					}
				}
				
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				mRegisterTask = null;
			}

		};
		
		// execute AsyncTask
		mRegisterTask.execute(null, null, null);
		GCMRegistrar.unregister(context);
    	finish();            
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
    	
    	LinearLayout mLl = (LinearLayout) findViewById(R.id.ll);
    	if(((LinearLayout) mLl).getChildCount() > 0) 
		    ((LinearLayout) mLl).removeAllViews(); 
    	
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);

    	Intent intent = new Intent("my-event");
		// add data
		intent.putExtra("message", settings.getString("message", "niente"));
		mHandleMessageReceiver.onReceive(context, intent);
		
		Log.d("Intent1",settings.getString("message", "niente"));
		
		Intent intent2 = new Intent("my-event2");
		// add data
		intent2.putExtra("message", settings.getString("taskName", "niente"));
		mHandleMessageReceiver.onReceive(context, intent2);
		
		Log.d("Intent2",settings.getString("taskName", "niente"));
    }

}
