
package qszhu.trakr.target;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;

public class SelectTargetActivity extends Activity {

    public static final String EXTRA_TARGET_ID = "extra_target_id";
    public static final String EXTRA_TARGET_NAME = "extra_target_name";

    public static void targetSelected(Activity activity, Target target) {
        Intent data = new Intent();
        data.putExtra(EXTRA_TARGET_ID, target.getObjectId());
        data.putExtra(EXTRA_TARGET_NAME, target.getName());

        activity.setResult(Activity.RESULT_OK, data);
        activity.finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SelectTargetFragment())
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();
    }

}
