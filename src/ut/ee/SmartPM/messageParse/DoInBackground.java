/**
 * @author Pätris Halapuu 2014
 */

package ut.ee.SmartPM.messageParse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DoInBackground extends AsyncTask<String, Void, Void> {

    Context mContext;
    LinearLayout mLl;
    Button mBtn;
    TextView mLblMessage;
    
    String tag = "DynamicFormXML";
	XmlGuiForm theForm;
	
	public static final String PREFS_NAME = "MyPrefsFile";

    public DoInBackground(Context context, LinearLayout ll, Button btn, TextView lblMessage) {

        this.mContext = context;
        this.mLl = ll;
        this.mBtn = btn;
        this.mLblMessage = lblMessage;
    }
    
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }
    
    @Override
    protected Void doInBackground(String... params) {

    	getFormData(params[0]);
    		
		return null;
    }
    
	protected void onPostExecute(Void result) {
    	DisplayForm();
    }


    private void getFormData(String url) {
//    	String url = "http://halapuu.host56.com/pn/xmlgui1.xml";
    	
    	try {
			Log.i(tag,"ProcessForm");
			
			String xml = null;
			try {
				// defaultHttpClient
				DefaultHttpClient httpClient = new DefaultHttpClient();
				HttpPost httpPost = new HttpPost(url);

				HttpResponse httpResponse = httpClient.execute(httpPost);
				HttpEntity httpEntity = httpResponse.getEntity();
				xml = EntityUtils.toString(httpEntity);

			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			Document root = null;
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			try {

				DocumentBuilder db = dbf.newDocumentBuilder();

				InputSource is = new InputSource();
			        is.setCharacterStream(new StringReader(xml));
			        root = db.parse(is); 

				} catch (ParserConfigurationException e) {
					Log.e("Error: ", e.getMessage());
				} catch (SAXException e) {
					Log.e("Error: ", e.getMessage());
				} catch (IOException e) {
					Log.e("Error: ", e.getMessage());
			}
			
			NodeList forms = root.getElementsByTagName("form");
						
			if (forms.getLength() < 1) {
				// nothing here??
				Log.e(tag,"No form, let's bail");
			}
			Node form = forms.item(0);
			theForm = new XmlGuiForm();
			
			// process form level
			NamedNodeMap map = form.getAttributes();
			theForm.setFormNumber(map.getNamedItem("id").getNodeValue());
			theForm.setFormName(map.getNamedItem("name").getNodeValue());
			theForm.setActor(map.getNamedItem("actor").getNodeValue());
			Log.d("actor is set", theForm.getActor());
			if (map.getNamedItem("submitTo") != null)
				theForm.setSubmitTo(map.getNamedItem("submitTo").getNodeValue());
			else
				theForm.setSubmitTo("loopback");

			// now process the fields
			NodeList fields = root.getElementsByTagName("field");
			for (int i=0;i<fields.getLength();i++) {
				Node fieldNode = fields.item(i);
				NamedNodeMap attr = fieldNode.getAttributes();
				XmlGuiFormField tempField =  new XmlGuiFormField();
				tempField.setName(attr.getNamedItem("name").getNodeValue());
				tempField.setLabel(attr.getNamedItem("label").getNodeValue());
				tempField.setType(attr.getNamedItem("type").getNodeValue());
				tempField.setLib(attr.getNamedItem("autoLib").getNodeValue());
				tempField.setRules(attr.getNamedItem("rules").getNodeValue());
				if (attr.getNamedItem("required").getNodeValue().equals("Y"))
					tempField.setRequired(true);
				else
					tempField.setRequired(false);
				tempField.setOptions(attr.getNamedItem("options").getNodeValue());
				theForm.getFields().add(tempField);
			}
			
			Log.i(tag,theForm.toString());
		} catch (Exception e) {
			Log.e(tag,"Error occurred in ProcessForm:" + e.getMessage());
			e.printStackTrace();
		}
		
	}

	private void DisplayForm() {
		try
		{			
	        
	        mBtn.setText("Start");
	        mBtn.setClickable(true);
	        mLl.setVisibility(View.INVISIBLE);
	        mBtn.setOnClickListener(new Button.OnClickListener() {
	        	public void onClick(View v) {
	        		// check if this form is Valid
	        		if(mBtn.getText() == "Start"){
	        			
	        			SharedPreferences settings = mContext.getSharedPreferences(PREFS_NAME, 0);
	        			Log.d("sharedPref", settings.getBoolean("started", false)+"");
	        			if(!settings.getBoolean("started", false)){
		        			// Notify server about task being started
	        				Log.d("Sending","Sending");
		        			new sendReadyToStart(theForm).execute();
		        			mBtn.setClickable(false);
	        			} else if ((mBtn.getText()).equals("Paused")) {
		        			mBtn.setClickable(false);
						} else {
	        				mBtn.setClickable(true);
	        			}
	        			
	        			
	        			
	                    mLl.setVisibility(View.VISIBLE);
	        			
	        			// walk through  our form elements and dynamically create them, leveraging our mini library of tools.
	        	        int i;
	        	        for (i=0;i<theForm.fields.size();i++) {
	        	        	if (theForm.fields.elementAt(i).getType().equals("text")) {
	        	        		theForm.fields.elementAt(i).obj = new XmlGuiEditBox(mContext,(theForm.fields.elementAt(i).isRequired() ? "*" : "") + theForm.fields.elementAt(i).getLabel(),"");
	        	        		mLl.addView((View) theForm.fields.elementAt(i).obj);
	        	        	}
	        	        	else if (theForm.fields.elementAt(i).getType().equals("numeric")) {
	        	        		theForm.fields.elementAt(i).obj = new XmlGuiEditBox(mContext,(theForm.fields.elementAt(i).isRequired() ? "*" : "") + theForm.fields.elementAt(i).getLabel(),"");
	        	        		((XmlGuiEditBox)theForm.fields.elementAt(i).obj).makeNumeric();
	        	        		mLl.addView((View) theForm.fields.elementAt(i).obj);
	        	        	}
	        	        	else if (theForm.fields.elementAt(i).getType().equals("auto")) {
	        	        		theForm.fields.elementAt(i).obj = new XmlGuiAutomatic(mContext,(theForm.fields.elementAt(i).isRequired() ? "*" : "") + theForm.fields.elementAt(i).getLabel(),theForm.fields.elementAt(i).getLib(), theForm.fields.elementAt(i).getRules());
	        	        		mLl.addView((View) theForm.fields.elementAt(i).obj);
	        	        	}
	        	        	else if (theForm.fields.elementAt(i).getType().equals("choice")) {
	        	        		theForm.fields.elementAt(i).obj = new XmlGuiPickOne(mContext,(theForm.fields.elementAt(i).isRequired() ? "*" : "") + theForm.fields.elementAt(i).getLabel(),theForm.fields.elementAt(i).getOptions());
	        	        		mLl.addView((View) theForm.fields.elementAt(i).obj);
	        	        	}
	        	        	else if (theForm.fields.elementAt(i).getType().equals("boolean")) {
	        	        		theForm.fields.elementAt(i).obj = new XmlGuiBoolean(mContext,(theForm.fields.elementAt(i).isRequired() ? "*" : "") + theForm.fields.elementAt(i).getLabel());
	        	        		mLl.addView((View) theForm.fields.elementAt(i).obj);
	        	        	}
	        	        }
	        			
	        			
	        			mBtn.setText("Stop");
	        				        			
	        		} else if (mBtn.getText() == "Stop") {
		        		if (!CheckForm())
		        		{
		        			AlertDialog.Builder bd = new AlertDialog.Builder(mLl.getContext());
		            		AlertDialog ad = bd.create();
		            		ad.setTitle("Error");
		            		ad.setMessage("Please enter all required (*) fields");
		            		ad.show();
		            		return;
	
		        		}
		        		
	        			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mLl.getContext());
	        			// set title
	        			alertDialogBuilder.setTitle("Confirm");
	         
	        			// set dialog message
	        			alertDialogBuilder
	        				.setMessage("Are you sure it is done?!")
	        				.setCancelable(false)
	        				.setPositiveButton("Yes",new DialogInterface.OnClickListener() {
	        					public void onClick(DialogInterface dialog,int id) {
	        						if (!SubmitForm()) {
	        		        			AlertDialog.Builder bd = new AlertDialog.Builder(mLl.getContext());
	        		            		AlertDialog ad = bd.create();
	        		            		ad.setTitle("Error");
	        		            		ad.setMessage("Error submitting form");
	        		            		ad.show();
	        		            		return;
	        	        			}else {
	        	        				mBtn.setText("Done");
	        	        				mBtn.setClickable(false);
	        	        				mBtn.setVisibility(View.INVISIBLE);
	        	        				if(((LinearLayout) mLl).getChildCount() > 0) 
	        	        				    ((LinearLayout) mLl).removeAllViews(); 
	        	        				mLblMessage.setText("No tasks");
	        	        				SharedPreferences settings = mContext.getSharedPreferences(PREFS_NAME, 0);
	        	        				SharedPreferences.Editor editor = settings.edit();
	        	            		    
	        	        				Map<String,?> keys = settings.getAll();

	        	        				for(Map.Entry<String,?> entry : keys.entrySet()){
	        	        					if(!(entry.getKey()).equals("name")){
    	        								editor.remove(entry.getKey());
    	        								editor.commit();
	        	        					}          
	        	        				 }
	        	        			}
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
	        	}
		        } );
		} catch (Exception e) {
			Log.e(tag,"Error Displaying Form");
		}
	}

	private boolean SubmitForm()
	{
		try {
			
            Log.d(tag, "Submitting form");
            Thread workthread = new Thread(new TransmitFormData(theForm));

            workthread.start();

			return true;	
		} catch (Exception e) {
			Log.e(tag,"Error in SubmitForm()::" + e.getMessage());
			e.printStackTrace();
            // tell user we failed....
            Log.d(tag, "Error submitting form");


			return false;
		}
		
	}
	
	
	private boolean CheckForm()
	{
		try {
			int i;
			boolean good = true;
			
			
			for (i=0;i<theForm.fields.size();i++) {
				String fieldValue = (String) theForm.fields.elementAt(i).getData();
				Log.i(tag,theForm.fields.elementAt(i).getName() + " is [" + fieldValue + "]");
				if (theForm.fields.elementAt(i).isRequired()) {
					if (fieldValue == null) {
						good = false;
					} else {
						if (fieldValue.trim().length() == 0) {
							good = false;
						}
					}
					if (theForm.fields.elementAt(i).getType().equals("boolean") || theForm.fields.elementAt(i).getType().equals("auto")) {
						good = true;
					}
						
				}
			}
			return good;
		} catch(Exception e) {
			Log.e(tag,"Error in CheckForm()::" + e.getMessage());
			e.printStackTrace();
			return false;
		}
	}
	
	private class TransmitFormData implements Runnable
	{
        XmlGuiForm _form;
        TransmitFormData(XmlGuiForm form) {
            this._form = form;
        }

        public void run() {

            try { 
            	Log.d("SERVER", "Connecting to server");
            	String data = _form.getSubmitTo() + _form.getFormEncodedData();
                URL url = new URL(data);
                URLConnection conn = url.openConnection();
                Log.d(tag, "Data sent to server");
                // Get the response
                BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line = "";
                Boolean bSuccess = false;
                while ((line = rd.readLine()) != null) {
                    if (line.indexOf("SUCCESS") != -1) {
                        bSuccess = true;
                    }
                    // Process line...
                    Log.v(tag, line);
                }
                rd.close();

                if (bSuccess) {
                	Log.d(tag, "Form submitted sucessfully ");
                
                    return;

                }
            } catch (Exception e) {
                Log.d(tag, "Failed to send form data: " + e.getMessage());
            }
            
            
        }

	}
}