/**
 * @author P�tris Halapuu 2014
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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gcm.GCMRegistrar;

public class MainActivity extends Activity {
	// label to display gcm messages
	TextView lblMessage;
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
		
		lblMessage = (TextView) findViewById(R.id.lblMessage);
		executeBtn = (Button)findViewById(R.id.StartStop);
		statusbar = (TextView) findViewById(R.id.textView1);
		
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
		
		if (lblMessage.getText() == "") {
			lblMessage.setText("No tasks");
			executeBtn.setClickable(false);
			executeBtn.setVisibility(View.INVISIBLE);
			statusbar.setText("Actor: " + actorName);
			
		}
				
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
					executeBtn.setClickable(true);
					statusbar.setText("Actor: " + actorName + " Status: Start");
				} else if (messageMap.get("taskName").equals("pause")) {
					executeBtn.setClickable(false);
					statusbar.setText("Actor: " + actorName + " Status: Adaptation in process");
				} else if (messageMap.get("taskName").equals("resume")) {
					executeBtn.setClickable(true);
					statusbar.setText("Actor: " + actorName + " Status: Resumed from adaptation");
				}
			} else {
				// Display message on the screen
				lblMessage.setText("New TASK");
			}
			Toast.makeText(getApplicationContext(), "Got Message: " + newMessage, Toast.LENGTH_SHORT).show();
			
			if(messageMap.containsKey("URL")){
				statusbar.setText("Actor: " + actorName + " Status: New task");
				lblMessage.setText(messageMap.get("taskName"));
				executeBtn.setVisibility(View.VISIBLE);
				LinearLayout ll = (LinearLayout) findViewById(R.id.ll);
				new DoInBackground(context, ll, executeBtn, lblMessage).execute(messageMap.get("URL"));
			} else {
				lblMessage.setText("No task");
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

}
