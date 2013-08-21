
package qszhu.trakr.progress;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class ProgressDetailActivity extends Activity {

    public static final String EXTRA_PROGRESS_ID = "extra_progress_id";

    public static Intent getIntent(Context context, String progressId) {
        Intent intent = new Intent(context, ProgressDetailActivity.class);
        intent.putExtra(EXTRA_PROGRESS_ID, progressId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        String progressId = intent.getStringExtra(EXTRA_PROGRESS_ID);
        Fragment frag = ProgressDetailFragment.newInstance(progressId);
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, frag)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();
    }

}
