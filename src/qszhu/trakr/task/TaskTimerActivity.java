
package qszhu.trakr.task;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.SaveCallback;
import com.testflightapp.lib.TestFlight;

import qszhu.trakr.R;
import qszhu.trakr.Utils;
import qszhu.trakr.progress.Progress;

public class TaskTimerActivity extends Activity implements OnClickListener {

    public static final String EXTRA_TARGET_NAME = "extra_target_name";
    public static final String EXTRA_TASK_NAME = "extra_task_name";
    public static final String EXTRA_TASK_ID = "extra_task_id";
    public static final String EXTRA_PROGRESS_ID = "extra_progress_id";

    private static final int ONE_SECOND = 1000;

    private static final String PREF_TIMER = "timer";
    private static final String PREF_PROGRESS_ID = "pref_progress_id";
    private static final String PREF_TASK_ID = "pref_task_id";
    private static final String PREF_SECONDS = "pref_seconds";

    private Button mTimer, mFinish, mCancel;
    private TextView mTimerText;
    private int mSeconds;
    private boolean mRunning;
    private String mTaskId, mProgressId;
    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.task_timer);

        Intent intent = getIntent();
        mTaskId = intent.getStringExtra(EXTRA_TASK_ID);
        mProgressId = intent.getStringExtra(EXTRA_PROGRESS_ID);
        final String targetName = intent.getStringExtra(EXTRA_TARGET_NAME);
        final String taskName = intent.getStringExtra(EXTRA_TASK_NAME);
        loadTimer();

        mTimer = (Button) findViewById(R.id.action_timer);
        mFinish = (Button) findViewById(R.id.action_finish);
        mCancel = (Button) findViewById(R.id.action_cancel);
        mTimerText = (TextView) findViewById(R.id.timer_text);

        ((TextView) findViewById(R.id.target_name)).setText(targetName);
        ((TextView) findViewById(R.id.task_name)).setText(taskName);
        updateTimerText();

        mTimer.setOnClickListener(this);
        mFinish.setOnClickListener(this);
        mCancel.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        TestFlight.passCheckpoint("task timer resume");

        super.onResume();
    }

    @Override
    public void onClick(View view) {
        if (view == mTimer) {
            TestFlight.passCheckpoint("click timer");
            switchTimer();
            return;
        }
        if (view == mFinish) {
            TestFlight.passCheckpoint("click finish");
            finishTimer();
            return;
        }
        if (view == mCancel) {
            TestFlight.passCheckpoint("click cancel");
            cancelTimer();
            return;
        }
    }

    @Override
    public void onBackPressed() {
        TestFlight.passCheckpoint("back pressed");

        pauseTimer();
        super.onBackPressed();
    }

    private void updateTimerText() {
        int hour = mSeconds / 3600;
        int minute = mSeconds % 3600 / 60;
        int second = mSeconds % 60;
        mTimerText.setText(String.format("%02d:%02d:%02d", hour, minute, second));
    }

    private void switchTimer() {
        mRunning = !mRunning;
        mTimer.setText(mRunning ? R.string.action_stop : R.string.action_start);
        if (mRunning) {
            startTimer();
        } else {
            stopTimer();
        }
    }

    private void startTimer() {
        mHandler.postDelayed(mTimerRunnable, ONE_SECOND);
    }

    private void stopTimer() {
        mRunning = false;
        mHandler.removeCallbacks(mTimerRunnable);
    }

    private void finishTimer() {
        stopTimer();
        Completion completion = new Completion()
                .setCost(mSeconds)
                .setTask(mTaskId);
        int error = completion.getValidationError();
        if (error != 0) {
            Utils.showErrorDialog(this, error);
            return;
        }

        final ProgressDialog dialog = ProgressDialog.show(
                this, null, getString(R.string.complete_task_progress), true);
        Progress progress = ParseObject.createWithoutData(Progress.class, mProgressId);
        progress.addCompletion(completion);
        progress.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                dialog.dismiss();
                if (e != null) {
                    Utils.showErrorDialog(getApplicationContext(), e.getMessage());
                    return;
                }
                finish();
            }
        });
    }

    private void cancelTimer() {
        stopTimer();
        clearTimer();
        finish();
    }

    private void saveTimer() {
        getSharedPreferences(PREF_TIMER, MODE_PRIVATE).edit()
                .putString(PREF_PROGRESS_ID, mProgressId)
                .putString(PREF_TASK_ID, mTaskId)
                .putInt(PREF_SECONDS, mSeconds)
                .commit();
    }

    private void loadTimer() {
        SharedPreferences pref = getSharedPreferences(PREF_TIMER, MODE_PRIVATE);
        if (!pref.getString(PREF_PROGRESS_ID, "").equals(mProgressId)) {
            return;
        }
        if (!pref.getString(PREF_TASK_ID, "").equals(mTaskId)) {
            return;
        }
        mSeconds = pref.getInt(PREF_SECONDS, 0);
    }

    private void clearTimer() {
        getSharedPreferences(PREF_TIMER, MODE_PRIVATE).edit()
                .putString(PREF_PROGRESS_ID, "")
                .putString(PREF_TASK_ID, "")
                .putInt(PREF_SECONDS, 0)
                .commit();
    }

    private void pauseTimer() {
        stopTimer();
        saveTimer();
    }

    private Runnable mTimerRunnable = new Runnable() {

        @Override
        public void run() {
            if (!mRunning) {
                return;
            }
            mSeconds += 1;
            updateTimerText();
            mHandler.postDelayed(this, ONE_SECOND);
        }

    };

}
