
package qszhu.trakr.progress;

import android.app.Activity;
import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import qszhu.trakr.R;
import qszhu.trakr.Utils;
import qszhu.trakr.task.Task;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ProgressDetailFragment extends ListFragment {

    private static final String ARG_OBJECT_ID = "objectId";

    public static ProgressDetailFragment newInstance(String progressId) {
        ProgressDetailFragment f = new ProgressDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_OBJECT_ID, progressId);
        f.setArguments(args);
        return f;
    }

    private TaskAdapter mAdapter;
    private List<Task> mTasks;
    private Progress mProgress;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final Activity activity = getActivity();
        activity.setTitle(R.string.title_progress_detail);

        ParseQuery<Progress> query = ParseQuery.getQuery(Progress.class);
        query.whereEqualTo("objectId", getArguments().getString(ARG_OBJECT_ID));
        query.include("plan.tasks");
        query.findInBackground(new FindCallback<Progress>() {

            @Override
            public void done(List<Progress> objects, ParseException e) {
                if (e != null) {
                    Utils.showErrorDialog(getActivity(), e.getMessage());
                    return;
                }
                mProgress = objects.get(0);
                mTasks = mProgress.getPlan().getTasks();

                mAdapter = new TaskAdapter();
                setListAdapter(mAdapter);
            }

        });
    }

    private class TaskAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mTasks.size();
        }

        @Override
        public Object getItem(int position) {
            return mTasks.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                v = LayoutInflater.from(getActivity())
                        .inflate(R.layout.simple_list_item_2, parent, false);
            }
            TextView text1 = (TextView) v.findViewById(R.id.text1);
            TextView text2 = (TextView) v.findViewById(R.id.text2);

            Task task = mTasks.get(position);
            String name = task.getName();

            int offset = task.getOffset();
            Calendar c = Calendar.getInstance();
            c.setTime(mProgress.getStartDate());
            c.add(Calendar.DATE, offset);
            Date taskDate = c.getTime();

            text1.setText(name);
            text2.setText(Utils.formatDate(taskDate));
            return v;
        }

    }

}
