
package qszhu.trakr.task;

import org.joda.time.DateTime;
import org.joda.time.Days;

import qszhu.trakr.progress.Progress;

import java.util.Date;

public class Todo {
    public enum TodoType {
        LATE, TODAY, TOMORROW, FUTURE
    }

    public Progress progress;
    public Task task;
    public Completion completion;

    public Date getDate() {
        return task.getDate(progress.getStartDate());
    }

    public boolean isCompleted() {
        return completion != null;
    }

    public TodoType getType() {
        int days = Days.daysBetween(new DateTime(), new DateTime(getDate())).getDays();
        if (days < 0) {
            return TodoType.LATE;
        }
        if (days == 0) {
            return TodoType.TODAY;
        }
        if (days == 1) {
            return TodoType.TOMORROW;
        }
        return TodoType.FUTURE;
    }
}
