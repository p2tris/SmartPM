package ut.ee.SmartPM.lib;

import android.content.Context;
import android.widget.TextView;

public interface LibInterface {
    public String useMyLib(Context context, TextView mAutoLabel);
    public String getName();
    public String getType();
}