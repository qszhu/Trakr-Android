
package qszhu.trakr.progress;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

import qszhu.trakr.R;
import qszhu.trakr.plan.Plan;
import qszhu.trakr.task.Completion;

import java.util.Date;
import java.util.List;

@ParseClassName("Progress")
public class Progress extends ParseObject {

    public static final String COL_PLAN = "plan";
    public static final String COL_START_DATE = "startDate";
    public static final String COL_COMPLETIONS = "completions";
    public static final String COL_CREATOR = "creator";

    public Plan getPlan() {
        return (Plan) getParseObject(COL_PLAN);
    }

    public Progress setPlan(Plan plan) {
        put(COL_PLAN, plan);
        return this;
    }

    public Progress setPlan(String planId) {
        put(COL_PLAN, ParseObject.createWithoutData(Plan.class, planId));
        return this;
    }

    public Date getStartDate() {
        return getDate(COL_START_DATE);
    }

    public Progress setStartDate(Date startDate) {
        put(COL_START_DATE, startDate);
        return this;
    }

    public List<Completion> getCompletions() {
        return getList(COL_COMPLETIONS);
    }

    public Progress setCompletions(List<Completion> completions) {
        put(COL_COMPLETIONS, completions);
        return this;
    }

    public ParseUser getCreator() {
        return getParseUser(COL_CREATOR);
    }

    private void setCreator() {
        put(COL_CREATOR, ParseUser.getCurrentUser());
    }

    public int getValidationError() {
        if (getPlan() == null) {
            return R.string.error_missing_plan;
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
