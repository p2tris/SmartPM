package ut.ee.SmartPM.messageParse;

import ut.ee.SmartPM.lib.LibLoader;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

public class XmlGuiAutomatic extends LinearLayout {
	TextView label;
	TextView autoLabel;
	Context mContext;
	
	public XmlGuiAutomatic(Context context,String labelText, String lib) {
		super(context);
		
		this.mContext = context;
		label = new TextView(context);
		label.setText(labelText);
		autoLabel = new TextView(context);
		autoLabel.setText("");
		
		
		
		Log.d("APP", "before libloader");
		
		LibLoader libLoader = new LibLoader(mContext.getApplicationContext(), lib);  
		
		Log.d("APP", "after libloader");
		
		this.addView(label);
		this.addView(autoLabel);
	}

	public XmlGuiAutomatic(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
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