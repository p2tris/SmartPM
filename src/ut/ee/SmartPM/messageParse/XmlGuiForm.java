/*
 * XmlGui application.
 * Written by Frank Ableson for IBM Developerworks
 * June 2010
 * Use the code as you wish no warranty of fitness, etc, etc.
 */

/**
 * @author Pätris Halapuu 2014
 * 
 * Added customized fields
 */



package ut.ee.SmartPM.messageParse;
import java.util.Vector;
import java.util.ListIterator;
import java.net.URLEncoder;

import android.util.Log;

public class XmlGuiForm {

	private String formNumber;
	private String formName;
	private String submitTo;
	private String actor;
	public Vector<XmlGuiFormField> fields;
	
	
	public XmlGuiForm()
	{
		this.fields = new Vector<XmlGuiFormField>();
		formNumber = "";
		formName = "";
		submitTo = "loopback"; // ie, do nothing but display the results
	}
	// getters & setters
	public String getFormNumber() {
		return formNumber;
	}

	public void setFormNumber(String formNumber) {
		this.formNumber = formNumber;
	}


	public String getFormName() {
		return formName;
	}
	public void setFormName(String formName) {
		this.formName = formName;
	}
	
	public String getSubmitTo() {
		return submitTo;
	}

	public void setSubmitTo(String submitTo) {
		this.submitTo = submitTo;
	}
	
	public String getActor() {
		return actor;
	}

	public void setActor(String actor) {
		this.actor = actor;
	}

	public Vector<XmlGuiFormField> getFields() {
		return fields;
	}

	public void setFields(Vector<XmlGuiFormField> fields) {
		this.fields = fields;
	}

	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("XmlGuiForm:\n");
		sb.append("Form Number: " + this.formNumber + "\n");
		sb.append("Form Name: " + this.formName + "\n");
		sb.append("Submit To: " + this.submitTo + "\n");
		sb.append("Actor: " + this.actor + "\n");
		if (this.fields == null) return sb.toString();
		ListIterator<XmlGuiFormField> li = this.fields.listIterator();
		while (li.hasNext()) {
			sb.append(li.next().toString());
		}
		
		return sb.toString();
	}
	
	public String getFormattedResults()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("Results:\n");
		if (this.fields == null) return sb.toString();
		ListIterator<XmlGuiFormField> li = this.fields.listIterator();
		while (li.hasNext()) {
			sb.append(li.next().getFormattedResult() + "\n");
		}
		
		return sb.toString();
	}
	
	public String getFormEncodedData()
	{
		try {
			int i = 0;
			StringBuilder sb = new StringBuilder();
			if (this.fields == null) return sb.toString();
			ListIterator<XmlGuiFormField> li = this.fields.listIterator();
			while (li.hasNext()) {
				if (i != 0)
				{
					sb.append("&");
				}
				else {
					sb.append("?");
				}
				XmlGuiFormField thisField = li.next();
				sb.append(thisField.name + "=");
				String encstring = URLEncoder.encode((String) thisField.getData(),"UTF-8");
				sb.append(encstring);
				i=1;
			}
			sb.append("&taskId=" + this.formNumber + "&taskName=" + this.formName + "&actName=" + this.actor);
			Log.d("readystring", sb.toString());
			return sb.toString();
		}
		catch (Exception e) {
			return "ErrorEncoding";
		}
	}
	
	
}
