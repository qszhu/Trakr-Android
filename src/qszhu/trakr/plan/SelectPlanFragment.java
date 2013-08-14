
package qszhu.trakr.plan;

import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.content.Context;
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

import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseQueryAdapter.OnQueryLoadListener;

import qszhu.trakr.R;
import qszhu.trakr.Utils;
import qszhu.trakr.task.Task;

import java.util.List;

public class SelectPlanFragment extends ListFragment implements OnQueryLoadListener<Plan> {

    private static final String TAG = SelectPlanFragment.class.getCanonicalName();

    private PlanAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getActivity().setTitle(R.string.title_select_plan);

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
                getFragmentManager().beginTransaction()
                        .replace(android.R.id.content, new CreatePlanFragment())
                        .addToBackStack("create plan")
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Plan plan = mAdapter.getItem(position);
        SelectPlanActivity.planSelected(getActivity(), plan);
    }

    @Override
    public void onLoading() {
        Log.d(TAG, "loading");
    }

    @Override
    public void onLoaded(List<Plan> objects, Exception e) {
        Log.d(TAG, "loaded");
        if (isVisible()) {
            setListShown(true);
        }
        if (e != null) {
            Utils.showErrorDialog(getActivity(), e.getMessage());
        }
    }

    private static class PlanAdapter extends ParseQueryAdapter<Plan> {

        private static final QueryFactory<Plan> QUERY_FACTORY = new QueryFactory<Plan>() {

            @Override
            public ParseQuery<Plan> create() {
                ParseQuery<Plan> query = ParseQuery.getQuery(Plan.class);
                query.include(Plan.COL_TARGET);
                return query;
            }

        };

        public PlanAdapter(Context context) {
            super(context, QUERY_FACTORY);
        }

        @Override
        public View getItemView(Plan plan, View v, ViewGroup parent) {
            if (v == null) {
                v = LayoutInflater.from(getContext())
                        .inflate(R.layout.simple_list_item_2, parent, false);
            }

            TextView text1 = (TextView) v.findViewById(R.id.text1);
            TextView text2 = (TextView) v.findViewById(R.id.text2);

            String name = plan.getTarget().getName();

            List<Task> tasks = plan.getTasks();
            int numTasks = tasks == null ? 0 : tasks.size();

            text1.setText(name);
            if (numTasks > 0) {
                text2.setText(String.format("%d tasks", numTasks));
            }
            return v;
        }

    }
}
