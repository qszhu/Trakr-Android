
package qszhu.trakr;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseObject;
import com.testflightapp.lib.TestFlight;

import qszhu.trakr.plan.Plan;
import qszhu.trakr.progress.Progress;
import qszhu.trakr.target.Target;
import qszhu.trakr.task.Completion;
import qszhu.trakr.task.Task;

public class TrakrApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        TestFlight.takeOff(this, "4625c43a-98e2-407f-9a71-018a8024d128");

        parseInit();
    }

    private void parseInit() {
        ParseObject.registerSubclass(Target.class);
        ParseObject.registerSubclass(Task.class);
        ParseObject.registerSubclass(Plan.class);
        ParseObject.registerSubclass(Completion.class);
        ParseObject.registerSubclass(Progress.class);

        Parse.initialize(this, getString(R.string.parse_application_id),
                getString(R.string.parse_client_key));

        ParseACL defaultACL = new ParseACL();
        defaultACL.setPublicReadAccess(true);
        ParseACL.setDefaultACL(defaultACL, true);
    }

}
