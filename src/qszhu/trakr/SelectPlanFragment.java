
package qszhu.trakr;

import android.app.Activity;
import android.app.ListFragment;
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
import android.widget.Toast;

import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseQueryAdapter.OnQueryLoadListener;

import java.util.List;

public class SelectPlanFragment extends ListFragment implements OnQueryLoadListener<ParseObject> {

    private static final String TAG = SelectPlanFragment.class.getCanonicalName();

    private static final int REQ_CREATE_PLAN = 1000;

    private PlanAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mAdapter = new PlanAdapter(getActivity());
        mAdapter.addOnQueryLoadListener(this);

        setListAdapter(mAdapter);
        // setListAdapter() already calls setListShown(true)
        setListShown(false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                Log.d(TAG, "refresh");
                setListShown(false);
                mAdapter.loadObjects();
                return true;
            case R.id.action_add:
                // TODO: create plan activity
                // Intent intent = new Intent(getActivity(),
                // SelectPlanActivity.class);
                // startActivityForResult(intent, REQ_SELECT_TARGET);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case REQ_CREATE_PLAN:
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        ParseObject plan = mAdapter.getItem(position);
        String planId = plan.getObjectId();

        Intent data = new Intent();
        data.putExtra(SelectPlanActivity.EXTRA_PLAN_ID, planId);

        final Activity activity = getActivity();
        activity.setResult(Activity.RESULT_OK, data);
        activity.finish();
    }

    @Override
    public void onLoading() {
        Log.d(TAG, "loading");
    }

    @Override
    public void onLoaded(List<ParseObject> objects, Exception e) {
        Log.d(TAG, "loaded");
        setListShown(true);
        if (e != null) {
            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private static class PlanAdapter extends ParseQueryAdapter<ParseObject> {

        private static final QueryFactory<ParseObject> QUERY_FACTORY = new QueryFactory<ParseObject>() {

            @Override
            public ParseQuery<ParseObject> create() {
                ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Plan");
                query.include("target");
                return query;
            }

        };

        public PlanAdapter(Context context) {
            super(context, QUERY_FACTORY);
        }

        @Override
        public View getItemView(ParseObject object, View v, ViewGroup parent) {
            if (v == null) {
                v = LayoutInflater.from(getContext())
                        .inflate(R.layout.simple_list_item_2, parent, false);
            }

            TextView text1 = (TextView) v.findViewById(R.id.text1);
            TextView text2 = (TextView) v.findViewById(R.id.text2);

            ParseObject target = object.getParseObject("target");
            String name = target.getString("name");

            List<Object> tasks = object.getList("tasks");
            int numTasks = tasks == null ? 0 : tasks.size();

            text1.setText(name);
            if (numTasks > 0) {
                text2.setText(String.format("%d tasks", numTasks));
            }
            return v;
        }

    }
}
