
package qszhu.trakr;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseACL;

public class TrakrApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        parseInit();
    }

    private void parseInit() {
        Parse.initialize(this, getString(R.string.parse_application_id),
                getString(R.string.parse_client_key));

        ParseACL defaultACL = new ParseACL();
        defaultACL.setPublicReadAccess(true);
        ParseACL.setDefaultACL(defaultACL, true);
    }

}
