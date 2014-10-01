package ut.ee.SmartPM;

import ut.ee.SmartPM.lib.LibLoader;
import ut.ee.SmartPM.messageParse.DoInBackground;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gcm.GCMRegistrar;

public class MainActivity extends Activity {
	// label to display gcm messages
	TextView lblMessage;
	Controller aController;
	public LinearLayout ll;
	Button execute;
	
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
		execute = (Button)findViewById(R.id.StartStop);
		
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
				
//				final Context context = this;
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
			execute.setClickable(false);
			execute.setBackgroundColor(Color.GRAY);
		}
		
//		new DoInBackground(getApplicationContext(), ll).execute("http://halapuu.host56.com/pn/xmlgui1.xml");
		
	}		
	
	public void executeTask(View v)
	{
				
		if (execute.getText() == "Start") {
			execute.setText("Stop");
			execute.setBackgroundColor(Color.RED);
			execute.setClickable(true);
			
			// At the moment we start loading libraries etc. when task is started (because in case of interruption certain lib might
			// not be needed anymore).
			Log.d("APP", "before libloader");
			
			LibLoader libLoader = new LibLoader(getApplicationContext());  
			
			Log.d("APP", "after libloader");
			
			// TODO: Notify server about task being started
			
		} else {
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
			// set title
			alertDialogBuilder.setTitle("Confirm");
 
			// set dialog message
			alertDialogBuilder
				.setMessage("Are you sure it is done?!")
				.setCancelable(false)
				.setPositiveButton("Yes",new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int id) {
						execute.setText("Done");
						execute.setBackgroundColor(Color.GRAY);
						execute.setClickable(false);
						lblMessage.append("\n Done, no tasks for you!");
					}
				  })
				.setNegativeButton("No",new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int id) {
						// if this button is clicked, just close
						// the dialog box and do nothing
						dialog.cancel();
					}
				});
 
				// create alert dialog
				AlertDialog alertDialog = alertDialogBuilder.create();
 
				// show it
				alertDialog.show();
			}
			
			
			// TODO: Notify server about task being done
			
		
		
	}

	// Create a broadcast receiver to get message and show on screen 
	private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			
			String newMessage = intent.getExtras().getString(Config.EXTRA_MESSAGE);
						
			// Waking up mobile if it is sleeping
			aController.acquireWakeLock(getApplicationContext());
			
			// Display message on the screen
			lblMessage.setText(newMessage + "\n");		
			
			Toast.makeText(getApplicationContext(), "Got Message: " + newMessage, Toast.LENGTH_LONG).show();
			
			LinearLayout ll = (LinearLayout) findViewById(R.id.ll);
			new DoInBackground(getApplicationContext(), ll).execute("http://halapuu.host56.com/pn/xmlgui1.xml");
									
			// Set execution available
			execute.setText("Start");
			execute.setBackgroundColor(Color.GREEN);
			execute.setClickable(true);
			
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
