package dc24.iqos.breather;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class MagicAppRestart extends Activity {
    // Do not forget to add it to AndroidManifest.xml
    // <activity android:name="your.package.name.MagicAppRestart"/>
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.exit(0);
    }
    public static void doRestart(Activity anyActivity) {
        anyActivity.startActivity(new Intent(anyActivity.getApplicationContext(), MagicAppRestart.class));
    }
}