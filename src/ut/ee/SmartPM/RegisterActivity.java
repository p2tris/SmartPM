package ut.ee.SmartPM;

import com.google.android.gcm.GCMRegistrar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class RegisterActivity extends Activity {
	
	// UI elements
	EditText txtName; 
	EditText txtEmail;
	
	// Register button
	Button btnRegister;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		
		//Get Global Controller Class object (see application tag in AndroidManifest.xml)
		final Controller aController = (Controller) getApplicationContext();
		
		// Check if Internet Connection present
		if (!aController.isConnectingToInternet()) {
			
			// Internet Connection is not present
			aController.showAlertDialog(RegisterActivity.this,
					"Internet Connection Error",
					"Please connect to working Internet connection", false);
			
			// stop executing code by return
			return;
		}

		// Check if GCM configuration is set
		if (Config.YOUR_SERVER_URL == null || Config.GOOGLE_SENDER_ID == null || Config.YOUR_SERVER_URL.length() == 0
				|| Config.GOOGLE_SENDER_ID.length() == 0) {
			
			// GCM sernder id / server url is missing
			aController.showAlertDialog(RegisterActivity.this, "Configuration Error!",
					"Please set your Server URL and GCM Sender ID", false);
			
			// stop executing code by return
			 return;
		}
		
		
		
		
		// Check if the user is already registered
		// Make sure the device has the proper dependencies.
		GCMRegistrar.checkDevice(this);
		// Make sure the manifest permissions was properly set 
		GCMRegistrar.checkManifest(this);
		if (GCMRegistrar.isRegisteredOnServer(this)) {
			
			// Skips registration.				
			Toast.makeText(getApplicationContext(), "Hi again!", Toast.LENGTH_LONG).show();
			Intent i = new Intent(getApplicationContext(), MainActivity.class);
			startActivity(i);
		}

		
		
		

		
		txtName = (EditText) findViewById(R.id.txtName);
		txtEmail = (EditText) findViewById(R.id.txtEmail);
		btnRegister = (Button) findViewById(R.id.btnRegister);
		
		// Click event on Register button
		btnRegister.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {  
				// Get data from EditText 
				String name = txtName.getText().toString(); 
				String email = txtEmail.getText().toString();
				
				// Check if user filled the form
				if(name.trim().length() > 0 && email.trim().length() > 0){
					
					// Launch Main Activity
					Intent i = new Intent(getApplicationContext(), MainActivity.class);
					
					// Registering user on our server					
					// Sending registration details to MainActivity
					i.putExtra("name", name);
					i.putExtra("email", email);
					startActivity(i);
					finish();
					
				}else{
					
					// User didn't fill that data
					aController.showAlertDialog(RegisterActivity.this, "Registration Error!", "Please enter your details", false);
				}
			}
		});
	}

}
