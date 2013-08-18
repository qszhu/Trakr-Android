
package qszhu.trakr.task;

import android.app.ListFragment;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.testflightapp.lib.TestFlight;

import qszhu.trakr.R;
import qszhu.trakr.Utils;
import qszhu.trakr.plan.Plan;
import qszhu.trakr.progress.Progress;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class TaskListFragment extends ListFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().setTitle(R.string.title_my_tasks);
    }

    @Override
    public void onResume() {
        TestFlight.passCheckpoint("task list resume");

        super.onResume();
        refresh();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.refresh, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                refresh();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        TestFlight.passCheckpoint("click todo");

        Todo todo = (Todo) l.getAdapter().getItem(position);
        Intent intent = new Intent(getActivity(), TaskTimerActivity.class);
        intent.putExtra(TaskTimerActivity.EXTRA_TARGET_NAME,
                todo.progress.getPlan().getTarget().getName());
        intent.putExtra(TaskTimerActivity.EXTRA_TASK_NAME, todo.task.getName());
        intent.putExtra(TaskTimerActivity.EXTRA_TASK_ID, todo.task.getObjectId());
        intent.putExtra(TaskTimerActivity.EXTRA_PROGRESS_ID, todo.progress.getObjectId());
        startActivity(intent);
    }

    private void refresh() {
        ParseUser user = ParseUser.getCurrentUser();
        if (user == null) {
            return;
        }

        setListShown(false);

        ParseQuery<Progress> query = ParseQuery.getQuery(Progress.class);
        query.whereEqualTo(Progress.COL_CREATOR, user);
        query.include(Progress.COL_PLAN);
        query.include(Progress.COL_PLAN + "." + Plan.COL_TARGET);
        query.include(Progress.COL_PLAN + "." + Plan.COL_TASKS);
        query.include(Progress.COL_COMPLETIONS);

        query.findInBackground(new FindCallback<Progress>() {

            @Override
            public void done(List<Progress> progresses, ParseException e) {
                if (e != null) {
                    setListShown(true);
                    Utils.showErrorDialog(getActivity(), e.getMessage());
                    return;
                }
                new LoadTodoTask(progresses).execute();
            }

        });
    }

    private class LoadTodoTask extends AsyncTask<Void, Void, List<Todo>> {

        private List<Progress> mProgresses;

        public LoadTodoTask(List<Progress> progresses) {
            mProgresses = progresses;
        }

        @Override
        protected List<Todo> doInBackground(Void... args) {
            ArrayList<Todo> todos = new ArrayList<Todo>();
            for (Progress progress : mProgresses) {
                List<Task> tasks = progress.getPlan().getTasks();
                if (tasks == null) {
                    continue;
                }
                for (Task task : tasks) {
                    Todo todo = new Todo();
                    todo.progress = progress;
                    todo.task = task;
                    List<Completion> completions = progress.getCompletions();
                    if (completions != null) {
                        for (Completion completion : completions) {
                            if (completion.getTask().getObjectId().equals(task.getObjectId())) {
                                todo.completion = completion;
                                break;
                            }
                        }
                    }
                    todos.add(todo);
                }
            }
            Collections.sort(todos, new Comparator<Todo>() {
                @Override
                public int compare(Todo todo1, Todo todo2) {
                    Date todoDate1 = todo1.task.getDate(todo1.progress.getStartDate());
                    Date todoDate2 = todo2.task.getDate(todo2.progress.getStartDate());
                    return todoDate1.compareTo(todoDate2);
                }
            });
            return todos;
        }

        @Override
        protected void onPostExecute(List<Todo> result) {
            if (isVisible()) {
                setListShown(true);
            }
            setListAdapter(new TodoAdapter(result));
        }

    }

    private class TodoAdapter extends BaseAdapter {

        private List<Todo> mTodos;

        public TodoAdapter(List<Todo> todos) {
            mTodos = todos;
        }

        @Override
        public int getCount() {
            return mTodos.size();
        }

        @Override
        public Object getItem(int position) {
            return mTodos.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public boolean isEnabled(int position) {
            return !mTodos.get(position).isCompleted();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                view = LayoutInflater.from(getActivity())
                        .inflate(R.layout.simple_list_item_2, parent, false);
            }

            int textColor = getActivity().getResources()
                    .getColor(android.R.color.primary_text_light);
            TextView text1 = (TextView) view.findViewById(R.id.text1);
            text1.setTextColor(textColor);
            TextView text2 = (TextView) view.findViewById(R.id.text2);
            text2.setTextColor(textColor);

            text1.setPaintFlags(text1.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            text2.setPaintFlags(text2.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));

            Todo todo = mTodos.get(position);
            text1.setText(todo.progress.getPlan().getTarget().getName());
            if (todo.isCompleted()) {
                text1.setPaintFlags(text1.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            }
            text2.setText(String.format("%s: %s", todo.task.getName(),
                    Utils.formatDate(todo.getDate())));
            if (todo.isCompleted()) {
                text2.setPaintFlags(text2.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            }

            int color = Color.TRANSPARENT;
            switch (todo.getType()) {
                case LATE:
                    color = Color.RED;
                    break;
                case TODAY:
                    color = Color.DKGRAY;
                    break;
                case TOMORROW:
                    color = Color.GRAY;
                    break;
                case FUTURE:
                    color = Color.LTGRAY;
                    break;
            }
            if (!todo.isCompleted()) {
                text1.setTextColor(color);
            }

            return view;
        }
    }

}
