package ut.ee.SmartPM.messageParse;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.os.AsyncTask;
import android.util.Log;

public class sendReadyToStart extends AsyncTask<String, Void, Void>{
	
	XmlGuiForm theForm;
		
	public sendReadyToStart(XmlGuiForm theForm) {
		this.theForm = theForm;
	}
	
	@Override
    protected void onPreExecute() {
        super.onPreExecute();
    }
    
    @Override
    protected Void doInBackground(String... params) {

    	// send readyToStart to server
        HttpPost httppost;
        HttpClient httpclient;
        List<NameValuePair> nameValuePairs;

        try {
                httpclient = new DefaultHttpClient();
                // Server of Pätris
                // httppost = new HttpPost("http://smartpm.cloudapp.net/replyToServer.php");
                // DIAG server
                httppost = new HttpPost("http://www.dis.uniroma1.it/~smartpm/webtool/replyToServer.php");
                // Add your data
                nameValuePairs = new ArrayList<NameValuePair>(2);
                nameValuePairs.add(new BasicNameValuePair("actName", theForm.getActor().trim()));
                nameValuePairs.add(new BasicNameValuePair("taskId", theForm.getFormNumber().trim()));
                nameValuePairs.add(new BasicNameValuePair("taskName", theForm.getFormName().trim()));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                // Execute HTTP Post Request
                httpclient.execute(httppost);
        }

        catch (Exception e) {
                Log.d("readyToStartERROR","error" + e.toString());

        }
    		
		return null;
    }
    
	protected void onPostExecute(Void result) {
    }
}
