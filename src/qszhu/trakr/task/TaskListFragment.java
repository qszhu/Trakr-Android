
package qszhu.trakr.task;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;

import qszhu.trakr.R;

public class TaskListFragment extends Fragment {

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final Activity activity = getActivity();
        activity.setTitle(R.string.title_my_tasks);
    }

}
