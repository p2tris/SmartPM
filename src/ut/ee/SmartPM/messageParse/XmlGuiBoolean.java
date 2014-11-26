/**
 * @author Pätris Halapuu 2014
 */

package ut.ee.SmartPM.messageParse;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

public class XmlGuiBoolean extends LinearLayout {
	TextView label;
	CheckBox chkBox;
	
	public XmlGuiBoolean(Context context,String labelText) {
		super(context);
		label = new TextView(context);
		label.setText(labelText);
		chkBox = new CheckBox(context);
		this.addView(label);
		this.addView(chkBox);
	}

	public XmlGuiBoolean(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public String getValue()
	{
		if(chkBox.isChecked()){
			return "true";
		} else{
			return "false";
		}
	}
}