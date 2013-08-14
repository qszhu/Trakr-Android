
package qszhu.trakr.plan;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;

public class SelectPlanActivity extends Activity {

    public static final String EXTRA_PLAN_ID = "extra_plan_id";

    public static void planSelected(Activity activity, Plan plan) {
        String planId = plan.getObjectId();

        Intent data = new Intent();
        data.putExtra(SelectPlanActivity.EXTRA_PLAN_ID, planId);

        activity.setResult(Activity.RESULT_OK, data);
        activity.finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SelectPlanFragment())
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();
    }

}
