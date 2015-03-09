/*
 * XmlGui application.
 * Written by Frank Ableson for IBM Developerworks
 * June 2010
 * Use the code as you wish no warranty of fitness, etc, etc.
 */

/**
 * @author P�tris Halapuu (p2tris@gmail.com)
 * 
 * Added customized fields
 */


package ut.ee.SmartPM.messageParse;

import ut.ee.SmartPM.R;
import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.EditText;
import android.text.method.DigitsKeyListener;

public class XmlGuiEditBox extends LinearLayout {
	TextView label;
	EditText txtBox;
	
	public XmlGuiEditBox(Context context,String labelText,String initialText) {
		super(context);
		ImageView pin = new ImageView(context);
		pin.setImageResource(R.drawable.pin);
		label = new TextView(context);
		label.setText(labelText);
		txtBox = new EditText(context);
		txtBox.setText(initialText);
		txtBox.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT));
		this.addView(pin);
		this.addView(label);
		this.addView(txtBox);
	}

	public XmlGuiEditBox(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public void makeNumeric()
	{
		DigitsKeyListener dkl = new DigitsKeyListener(true,true);
		txtBox.setKeyListener(dkl);
	}
	public String getValue()
	{
		return txtBox.getText().toString();
	}
	
	public void setValue(String v)
	{
		txtBox.setText(v);
	}

}
