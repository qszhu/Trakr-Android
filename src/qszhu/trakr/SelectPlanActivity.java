
package qszhu.trakr;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;

public class SelectPlanActivity extends Activity {

    public static final String EXTRA_PLAN_ID = "extra_plan_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle(R.string.title_select_plan);

        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SelectPlanFragment())
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();
    }

}
