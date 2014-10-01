package ut.ee.SmartPM.messageParse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;

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
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;

public class DoInBackground extends AsyncTask<String, Void, Void> {

    Context mContext;
    LinearLayout mLl;
    
    String tag = "DynamicFormXML";
	XmlGuiForm theForm;

    public DoInBackground(Context context, LinearLayout ll) {

        this.mContext = context;
        this.mLl = ll;
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


    private void getFormData(String string) {
    	String url = "http://halapuu.host56.com/pn/xmlgui1.xml";
    	
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
	        
	        // walk thru our form elements and dynamically create them, leveraging our mini library of tools.
	        int i;
	        for (i=0;i<theForm.fields.size();i++) {
	        	if (theForm.fields.elementAt(i).getType().equals("text")) {
	        		theForm.fields.elementAt(i).obj = new XmlGuiEditBox(mContext,(theForm.fields.elementAt(i).isRequired() ? "*" : "") + theForm.fields.elementAt(i).getLabel(),"");
	        		mLl.addView((View) theForm.fields.elementAt(i).obj);
	        	}
	        	if (theForm.fields.elementAt(i).getType().equals("numeric")) {
	        		theForm.fields.elementAt(i).obj = new XmlGuiEditBox(mContext,(theForm.fields.elementAt(i).isRequired() ? "*" : "") + theForm.fields.elementAt(i).getLabel(),"");
	        		((XmlGuiEditBox)theForm.fields.elementAt(i).obj).makeNumeric();
	        		mLl.addView((View) theForm.fields.elementAt(i).obj);
	        	}
	        	if (theForm.fields.elementAt(i).getType().equals("choice")) {
	        		theForm.fields.elementAt(i).obj = new XmlGuiPickOne(mContext,(theForm.fields.elementAt(i).isRequired() ? "*" : "") + theForm.fields.elementAt(i).getLabel(),theForm.fields.elementAt(i).getOptions());
	        		mLl.addView((View) theForm.fields.elementAt(i).obj);
	        	}
	        	if (theForm.fields.elementAt(i).getType().equals("boolean")) {
	        		theForm.fields.elementAt(i).obj = new XmlGuiBoolean(mContext,(theForm.fields.elementAt(i).isRequired() ? "*" : "") + theForm.fields.elementAt(i).getLabel());
	        		mLl.addView((View) theForm.fields.elementAt(i).obj);
	        	}
	        }
	        
	        
	        Button btn = new Button(mContext);
	        btn.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT));
	        
	        mLl.addView(btn);
	        
	        btn.setText("Submit");
	        btn.setOnClickListener(new Button.OnClickListener() {
	        	public void onClick(View v) {
	        		// check if this form is Valid
	        		if (!CheckForm())
	        		{
	        			AlertDialog.Builder bd = new AlertDialog.Builder(mLl.getContext());
	            		AlertDialog ad = bd.create();
	            		ad.setTitle("Error");
	            		ad.setMessage("Please enter all required (*) fields");
	            		ad.show();
	            		return;

	        		}
	        		if (theForm.getSubmitTo().equals("loopback")) {
	        			// just display the results to the screen
	        			String formResults = theForm.getFormattedResults();
	        			Log.i(tag,formResults);
	        			AlertDialog.Builder bd = new AlertDialog.Builder(mLl.getContext());
	            		AlertDialog ad = bd.create();
	            		ad.setTitle("Results");
	            		ad.setMessage(formResults);
	            		ad.show();
	            		return;
	        			
	        		} else {
	        			if (!SubmitForm()) {
		        			AlertDialog.Builder bd = new AlertDialog.Builder(mLl.getContext());
		            		AlertDialog ad = bd.create();
		            		ad.setTitle("Error");
		            		ad.setMessage("Error submitting form");
		            		ad.show();
		            		return;
	        			}
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
					if (theForm.fields.elementAt(i).getType().equals("boolean")) {
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