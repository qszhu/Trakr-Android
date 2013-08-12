
package qszhu.trakr;

import android.app.Fragment;
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
import android.widget.Toast;

import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseQueryAdapter.OnQueryLoadListener;
import com.parse.ParseUser;

import java.util.List;

public class ProgressListFragment extends ListFragment implements OnQueryLoadListener<ParseObject> {

    private static final String TAG = ProgressListFragment.class.getCanonicalName();

    private ProgressAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mAdapter = new ProgressAdapter(getActivity());
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
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        ParseObject progress = mAdapter.getItem(position);
        String progressId = progress.getObjectId();
        Fragment frag = ProgressDetailFragment.newInstance(progressId);
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, frag)
                .addToBackStack("progress detail")
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
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

    private static class ProgressAdapter extends ParseQueryAdapter<ParseObject> {

        private static final QueryFactory<ParseObject> QUERY_FACTORY = new QueryFactory<ParseObject>() {

            @Override
            public ParseQuery<ParseObject> create() {
                ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Progress");
                ParseUser user = ParseUser.getCurrentUser();
                if (user != null) {
                    query.whereEqualTo("creator", user);
                }
                query.include("plan");
                query.include("plan.target");
                return query;
            }

        };

        public ProgressAdapter(Context context) {
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

            ParseObject plan = object.getParseObject("plan");
            ParseObject target = plan.getParseObject("target");
            String name = target.getString("name");

            List<Object> completions = object.getList("completions");
            int numCompletions = completions == null ? 0 : completions.size();

            List<Object> tasks = plan.getList("tasks");
            int numTasks = tasks == null ? 0 : tasks.size();

            text1.setText(name);
            text2.setText(String.format("%d/%d tasks", numCompletions, numTasks));

            return v;
        }

    }

}
