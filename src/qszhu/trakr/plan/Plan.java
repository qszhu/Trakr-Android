
package qszhu.trakr.plan;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

import qszhu.trakr.R;
import qszhu.trakr.Unit;
import qszhu.trakr.target.Target;
import qszhu.trakr.task.Task;

import java.util.Date;
import java.util.List;

@ParseClassName("Plan")
public class Plan extends ParseObject {

    public static final String COL_TARGET = "target";
    public static final String COL_TOTAL = "total";
    public static final String COL_UNIT = "unit";
    public static final String COL_START_DATE = "startDate";
    public static final String COL_TASKS = "tasks";
    public static final String COL_CREATOR = "creator";

    public Target getTarget() {
        return (Target) getParseObject(COL_TARGET);
    }

    public Plan setTarget(Target target) {
        put(COL_TARGET, target);
        return this;
    }

    public Plan setTarget(String targetId) {
        put(COL_TARGET, ParseObject.createWithoutData(Target.class, targetId));
        return this;
    }

    public int getTotal() {
        return getInt(COL_TOTAL);
    }

    public Plan setTotal(int total) {
        put(COL_TOTAL, total);
        return this;
    }

    public Unit getUnit() {
        int unitValue = getInt(COL_UNIT);
        for (Unit unit : Unit.values()) {
            if (unit.getValue() == unitValue) {
                return unit;
            }
        }
        return null;
    }

    public Plan setUnit(Unit unit) {
        put(COL_UNIT, unit.getValue());
        return this;
    }

    public Date getStartDate() {
        return getDate(COL_START_DATE);
    }

    public Plan setStartDate(Date date) {
        put(COL_START_DATE, date);
        return this;
    }

    public List<Task> getTasks() {
        return getList(COL_TASKS);
    }

    public Plan setTasks(List<Task> tasks) {
        put(COL_TASKS, tasks);
        return this;
    }

    public ParseUser getCreator() {
        return getParseUser(COL_CREATOR);
    }

    private Plan setCreator() {
        put(COL_CREATOR, ParseUser.getCurrentUser());
        return this;
    }

    public int getValidationError() {
        if (getTarget() == null) {
            return R.string.error_missing_target;
        }
        if (getTotal() <= 0) {
            return R.string.error_non_positive_total;
        }
        if (getUnit() == null) {
            return R.string.error_invalid_unit;
        }
        if (getStartDate() == null) {
            setStartDate(new Date());
        }
        if (getCreator() == null) {
            setCreator();
        }
        return 0;
    }

}
