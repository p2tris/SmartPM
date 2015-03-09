/**
 * @author Pätris Halapuu 2014
 */

package ut.ee.SmartPM.messageParse;

import ut.ee.SmartPM.R;
import ut.ee.SmartPM.lib.LibLoader;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class XmlGuiAutomatic extends LinearLayout {
	TextView label;
	TextView autoLabel;
	Context mContext;
	String rules;
	
	public XmlGuiAutomatic(Context context,String labelText, String lib, String rules) {
		super(context);
		this.rules = rules;
		
		this.mContext = context;
		
		ImageView pin = new ImageView(context);
		pin.setImageResource(R.drawable.pin);
		label = new TextView(context);
		label.setText(labelText);
		autoLabel = new TextView(context);
		autoLabel.setText("");
		
		Log.d("APP", "before libloader");
		
		LibLoader libLoader = new LibLoader(mContext.getApplicationContext(), lib, autoLabel, rules);  
		
		Log.d("APP", "after libloader");
		
		this.addView(pin);
		this.addView(label);
		this.addView(autoLabel);
	}

	public XmlGuiAutomatic(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public String getValue()
	{
		return autoLabel.getText().toString();
	}
	
	public void setValue(String v)
	{
		autoLabel.setText(v);
	}

}