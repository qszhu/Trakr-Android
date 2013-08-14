
package qszhu.trakr.task;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import qszhu.trakr.R;

import java.util.Date;

@ParseClassName("Completion")
public class Completion extends ParseObject {

    public static final String COL_DATE = "date";
    public static final String COL_COST = "cost";
    public static final String COL_TASK = "task";

    public Date getDate() {
        return getDate(COL_DATE);
    }

    public Completion setDate(Date date) {
        put(COL_DATE, date);
        return this;
    }

    public int getCost() {
        return getInt(COL_COST);
    }

    public Completion setCost(int cost) {
        put(COL_COST, cost);
        return this;
    }

    public Task getTask() {
        return (Task) getParseObject(COL_TASK);
    }

    public Completion setTask(Task task) {
        put(COL_TASK, task);
        return this;
    }

    public Completion setTask(String taskId) {
        put(COL_TASK, ParseObject.createWithoutData(Task.class, taskId));
        return this;
    }

    public int getValidationError() {
        if (getCost() < 0) {
            return R.string.error_negative_cost;
        }
        if (getDate() == null) {
            setDate(new Date());
        }
        return 0;
    }

}
