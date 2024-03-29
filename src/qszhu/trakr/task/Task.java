
package qszhu.trakr.task;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import java.util.Calendar;
import java.util.Date;

@ParseClassName("Task")
public class Task extends ParseObject {

    public static final String COL_NAME = "name";
    public static final String COL_OFFSET = "offset";
    public static final String COL_STEP = "step";

    public String getName() {
        return getString(COL_NAME);
    }

    public Task setName(String name) {
        put(COL_NAME, name);
        return this;
    }

    public int getOffset() {
        return getInt(COL_OFFSET);
    }

    public Task setOffset(int offset) {
        put(COL_OFFSET, offset);
        return this;
    }

    public Date getDate(Date startDate) {
        Calendar c = Calendar.getInstance();
        c.setTime(startDate);
        c.add(Calendar.DATE, getOffset());
        return c.getTime();
    }

    public int getStep() {
        return getInt(COL_STEP);
    }

    public Task setStep(int step) {
        put(COL_STEP, step);
        return this;
    }

}
