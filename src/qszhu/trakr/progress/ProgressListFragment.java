
package qszhu.trakr.progress;

import android.app.Activity;
import android.app.ListFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseQueryAdapter.OnQueryLoadListener;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.testflightapp.lib.TestFlight;

import qszhu.trakr.R;
import qszhu.trakr.Utils;
import qszhu.trakr.plan.Plan;
import qszhu.trakr.plan.SelectPlanActivity;
import qszhu.trakr.task.Completion;
import qszhu.trakr.task.Task;

import java.util.List;

public class ProgressListFragment extends ListFragment implements OnQueryLoadListener<Progress> {

    private static final String TAG = ProgressListFragment.class.getCanonicalName();
    private static final int REQ_SELECT_PLAN = 1000;

    private ProgressAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final Activity activity = getActivity();
        activity.setTitle(R.string.title_my_progress);

        mAdapter = new ProgressAdapter(activity);
        mAdapter.addOnQueryLoadListener(this);

        setListAdapter(mAdapter);
        // setListAdapter() already calls setListShown(true)
        setListShown(false);
    }

    @Override
    public void onResume() {
        TestFlight.passCheckpoint("progress list resume");

        super.onResume();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                TestFlight.passCheckpoint("click refresh");

                Log.d(TAG, "refresh");
                setListShown(false);
                mAdapter.loadObjects();
                return true;
            case R.id.action_add:
                TestFlight.passCheckpoint("click add");

                Intent intent = new Intent(getActivity(), SelectPlanActivity.class);
                startActivityForResult(intent, REQ_SELECT_PLAN);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        final Activity activity = getActivity();
        switch (requestCode) {
            case REQ_SELECT_PLAN:
                if (resultCode != Activity.RESULT_OK) {
                    return;
                }

                String planId = data.getStringExtra(SelectPlanActivity.EXTRA_PLAN_ID);
                Progress progress = new Progress().setPlan(planId);

                int error = progress.getValidationError();
                if (error != 0) {
                    Utils.showErrorDialog(activity, error);
                    return;
                }

                final ProgressDialog dialog = ProgressDialog.show(activity, null,
                        activity.getString(R.string.create_progress_progress), true);
                progress.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        dialog.dismiss();
                        if (e != null) {
                            Utils.showErrorDialog(activity, e.getMessage());
                            return;
                        }
                        setListShown(false);
                        mAdapter.loadObjects();
                    }
                });
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        TestFlight.passCheckpoint("click progress");

        Progress progress = mAdapter.getItem(position);
        startActivity(ProgressDetailActivity.getIntent(getActivity(), progress.getObjectId()));
    }

    @Override
    public void onLoading() {
        Log.d(TAG, "loading");
    }

    @Override
    public void onLoaded(List<Progress> objects, Exception e) {
        Log.d(TAG, "loaded");
        if (isVisible()) {
            setListShown(true);
        }
        if (e != null) {
            Utils.showErrorDialog(null, e.getMessage());
        }
    }

    private static class ProgressAdapter extends ParseQueryAdapter<Progress> {

        private static final QueryFactory<Progress> QUERY_FACTORY = new QueryFactory<Progress>() {

            @Override
            public ParseQuery<Progress> create() {
                ParseQuery<Progress> query = ParseQuery.getQuery(Progress.class);
                ParseUser user = ParseUser.getCurrentUser();
                if (user != null) {
                    query.whereEqualTo(Progress.COL_CREATOR, user);
                }
                query.include(Progress.COL_PLAN);
                query.include(Progress.COL_PLAN + "." + Plan.COL_TARGET);
                return query;
            }

        };

        public ProgressAdapter(Context context) {
            super(context, QUERY_FACTORY);
        }

        @Override
        public View getItemView(Progress progress, View v, ViewGroup parent) {
            if (v == null) {
                v = LayoutInflater.from(getContext())
                        .inflate(R.layout.simple_list_item_2, parent, false);
            }

            TextView text1 = (TextView) v.findViewById(R.id.text1);
            TextView text2 = (TextView) v.findViewById(R.id.text2);

            String name = progress.getPlan().getTarget().getName();

            List<Completion> completions = progress.getCompletions();
            int numCompletions = completions == null ? 0 : completions.size();

            List<Task> tasks = progress.getPlan().getTasks();
            int numTasks = tasks == null ? 0 : tasks.size();

            text1.setText(name);
            text2.setText(String.format("%d/%d tasks", numCompletions, numTasks));

            return v;
        }

    }

}
