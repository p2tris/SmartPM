package ut.ee.SmartPM.messageParse;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

public class XmlGuiAutomatic extends LinearLayout {
	TextView label;
	TextView autoLabel;
	
	public XmlGuiAutomatic(Context context,String labelText,String initialText) {
		super(context);
		label = new TextView(context);
		label.setText(labelText);
		autoLabel = new TextView(context);
		autoLabel.setText(initialText);
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