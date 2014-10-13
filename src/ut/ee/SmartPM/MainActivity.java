package ut.ee.SmartPM;

import java.util.HashMap;
import java.util.Map;

import ut.ee.SmartPM.messageParse.DoInBackground;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
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
			lblMessage.setText("No tasks!");
			executeBtn.setClickable(false);
			executeBtn.setBackgroundColor(Color.GRAY);
		}
				
	}		
	
	// Create a broadcast receiver to get message and show on screen 
	private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			
			String newMessage = intent.getExtras().getString(Config.EXTRA_MESSAGE);
			Map<String, String> messageMap = new HashMap<String, String>();
			// Splits the string and makes it to object that we can work with 
		    for(String loc : newMessage.split(";")){
		    	String[] elem = loc.split("\\|");
		    	messageMap.put(elem[0], elem[1]);
		    }
						
			// Waking up mobile if it is sleeping
			aController.acquireWakeLock(getApplicationContext());
			if(messageMap.containsKey("taskName")){
				// Display message on the screen
				lblMessage.setText("New TASK:\n" + messageMap.get("taskName"));
			} else {
				// Display message on the screen
				lblMessage.setText("New TASK!");
			}
			Toast.makeText(getApplicationContext(), "Got Message: " + newMessage, Toast.LENGTH_LONG).show();
			
			if(messageMap.containsKey("URL")){
				LinearLayout ll = (LinearLayout) findViewById(R.id.ll);
				new DoInBackground(getApplicationContext(), ll, executeBtn, lblMessage).execute(messageMap.get("URL"));
			} else {
				lblMessage.setText("No task URL");
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
		
		// TODO: Notify server about application being closed, possibly task stopped.
		
		super.onDestroy();
	}

}
