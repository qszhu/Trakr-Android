
package qszhu.trakr.target;

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

import com.parse.ParseQueryAdapter;
import com.parse.ParseQueryAdapter.OnQueryLoadListener;
import com.testflightapp.lib.TestFlight;

import qszhu.trakr.R;
import qszhu.trakr.Utils;

import java.util.List;

public class SelectTargetFragment extends ListFragment implements OnQueryLoadListener<Target> {

    private static final String TAG = SelectTargetFragment.class.getCanonicalName();

    private TargetAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getActivity().setTitle(R.string.title_select_target);

        mAdapter = new TargetAdapter(getActivity());
        mAdapter.addOnQueryLoadListener(this);

        setListAdapter(mAdapter);
        // setListAdapter() already calls setListShown(true)
        setListShown(false);
    }

    @Override
    public void onResume() {
        TestFlight.passCheckpoint("select target resume");
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

                getFragmentManager().beginTransaction()
                        .replace(android.R.id.content, new CreateTargetFragment())
                        .addToBackStack("create target")
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        TestFlight.passCheckpoint("click target");

        Target target = mAdapter.getItem(position);
        SelectTargetActivity.targetSelected(getActivity(), target);
    }

    @Override
    public void onLoading() {
        Log.d(TAG, "loading");
    }

    @Override
    public void onLoaded(List<Target> objects, Exception e) {
        Log.d(TAG, "loaded");
        if (isVisible()) {
            setListShown(true);
        }
        if (e != null) {
            Utils.showErrorDialog(getActivity(), e.getMessage());
        }
    }

    private static class TargetAdapter extends ParseQueryAdapter<Target> {

        public TargetAdapter(Context context) {
            super(context, Target.class);
        }

        @Override
        public View getItemView(Target target, View v, ViewGroup parent) {
            if (v == null) {
                v = LayoutInflater.from(getContext())
                        .inflate(R.layout.simple_list_item_1, parent, false);
            }

            TextView text1 = (TextView) v.findViewById(R.id.text1);
            String name = target.getName();
            text1.setText(name);

            return v;
        }

    }
}
