
package qszhu.trakr;

import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ProgressDetailFragment extends ListFragment {

    public static ProgressDetailFragment newInstance(String progressId) {
        ProgressDetailFragment f = new ProgressDetailFragment();
        Bundle args = new Bundle();
        args.putString("objectId", progressId);
        f.setArguments(args);
        return f;
    }

    private TaskAdapter mAdapter;
    private List<ParseObject> mTasks;
    private ParseObject mProgress;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Progress");
        query.whereEqualTo("objectId", getArguments().getString("objectId"));
        query.include("plan.tasks");
        query.findInBackground(new FindCallback<ParseObject>() {

            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e != null) {
                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                    return;
                }
                mProgress = objects.get(0);
                ParseObject plan = mProgress.getParseObject("plan");
                mTasks = plan.getList("tasks");

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

            ParseObject task = mTasks.get(position);
            String name = task.getString("name");

            int offset = task.getInt("offset");
            Calendar c = Calendar.getInstance();
            c.setTime(mProgress.getDate("startDate"));
            c.add(Calendar.DATE, offset);
            Date taskDate = c.getTime();

            text1.setText(name);
            text2.setText(DateFormat.getDateInstance(DateFormat.SHORT, Locale.US).format(taskDate));
            return v;
        }

    }

}
