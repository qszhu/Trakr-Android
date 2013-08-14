
package qszhu.trakr;

import android.content.Context;

import qszhu.trakr.plan.Plan;
import qszhu.trakr.task.Task;

import java.util.ArrayList;
import java.util.List;

public class AutoTask {

    private int mNumTasks;
    private Repeat mRepeat;

    public int getNumTasks() {
        return mNumTasks;
    }

    public AutoTask setNumTasks(int numTasks) {
        mNumTasks = numTasks;
        return this;
    }

    public Repeat getRepeat() {
        return mRepeat;
    }

    public AutoTask setRepeat(Repeat repeat) {
        mRepeat = repeat;
        return this;
    }

    public int getValidationError() {
        if (mNumTasks <= 0) {
            return R.string.error_non_positive_num_tasks;
        }
        if (mRepeat == null) {
            return R.string.error_invalid_repeat;
        }
        return 0;
    }

    private int getOffset(int n) {
        switch (mRepeat) {
            case EVERY_WEEK:
                return n * 7;
            case EVERY_MONTH:
                return n * 30;
            default:
                return n;
        }
    }

    public List<Task> getTasks(Context context, Plan plan) {
        ArrayList<Task> tasks = new ArrayList<Task>();
        double step = plan.getTotal() / (double) mNumTasks;
        for (int i = 0; i < mNumTasks; i++) {
            Task task = new Task();

            int start = (int) Math.round(i * step + 1);
            int end = (int) Math.round((i + 1) * step);
            task.setOffset(getOffset(i));
            task.setStep(end - start + 1);

            String unit = plan.getUnit().getName(context);
            String taskName = start == end ? String.format("%s %d", unit, start) :
                    String.format("%s %d - %s %d", unit, start, unit, end);
            task.setName(taskName);
            tasks.add(task);
        }
        return tasks;
    }

}
