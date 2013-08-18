
package qszhu.trakr.plan;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import com.parse.ParseException;
import com.parse.SaveCallback;
import com.testflightapp.lib.TestFlight;

import qszhu.trakr.AutoTask;
import qszhu.trakr.R;
import qszhu.trakr.Repeat;
import qszhu.trakr.Unit;
import qszhu.trakr.Utils;
import qszhu.trakr.target.SelectTargetActivity;

import java.util.Calendar;
import java.util.Date;

public class CreatePlanFragment extends Fragment implements OnClickListener, OnDateSetListener {

    private static final int REQ_SELECT_TARGET = 1000;

    private Button mSelectTarget, mSelectDate, mCreate;
    private EditText mTotal, mNumTasks;
    private Spinner mUnit, mRepeat;
    private Plan mPlan = new Plan();
    private AutoTask mAutoTask = new AutoTask();

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getActivity().setTitle(R.string.title_create_plan);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.create_plan, container, false);

        mSelectTarget = (Button) v.findViewById(R.id.action_select_target);
        mTotal = (EditText) v.findViewById(R.id.total);
        mUnit = (Spinner) v.findViewById(R.id.unit);
        mSelectDate = (Button) v.findViewById(R.id.action_select_date);
        mNumTasks = (EditText) v.findViewById(R.id.num_tasks);
        mRepeat = (Spinner) v.findViewById(R.id.repeat);
        mCreate = (Button) v.findViewById(R.id.action_create);

        if (mPlan.getStartDate() == null) {
            mPlan.setStartDate(new Date());
        }
        mSelectDate.setText(Utils.formatDate(mPlan.getStartDate()));

        mSelectTarget.setOnClickListener(this);
        mSelectDate.setOnClickListener(this);
        mCreate.setOnClickListener(this);

        return v;
    }

    @Override
    public void onResume() {
        TestFlight.passCheckpoint("create plan resume");

        super.onResume();
    }

    @Override
    public void onClick(View v) {
        if (v == mSelectTarget) {
            TestFlight.passCheckpoint("click target");

            selectTarget();
            return;
        }
        if (v == mSelectDate) {
            TestFlight.passCheckpoint("click date");

            showDatePicker();
            return;
        }
        if (v == mCreate) {
            TestFlight.passCheckpoint("click create");

            savePlan();
            return;
        }
    }

    private void showDatePicker() {
        Calendar c = Calendar.getInstance();
        c.setTime(mPlan.getStartDate());
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        new DatePickerDialog(getActivity(), this, year, month, day).show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        final Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, day);
        mPlan.setStartDate(c.getTime());
        mSelectDate.setText(Utils.formatDate(mPlan.getStartDate()));
    }

    private void savePlan() {
        final Activity activity = getActivity();

        if (!TextUtils.isEmpty(mNumTasks.getText())) {
            mAutoTask.setNumTasks(Integer.valueOf(mNumTasks.getText().toString()));
        }
        mAutoTask.setRepeat(Repeat.fromPosition(activity, mRepeat.getSelectedItemPosition()));

        int error = mAutoTask.getValidationError();
        if (error != 0) {
            Utils.showErrorDialog(activity, error);
            return;
        }

        if (!TextUtils.isEmpty(mTotal.getText().toString())) {
            mPlan.setTotal(Integer.valueOf(mTotal.getText().toString()));
        }
        mPlan.setUnit(Unit.fromPosition(activity, mUnit.getSelectedItemPosition()));

        error = mPlan.getValidationError();
        if (error != 0) {
            Utils.showErrorDialog(activity, error);
            return;
        }

        mPlan.setTasks(mAutoTask.getTasks(activity, mPlan));

        final ProgressDialog progress = ProgressDialog.show(
                activity, null, activity.getString(R.string.create_plan_progress), true);
        mPlan.saveInBackground(new SaveCallback() {

            @Override
            public void done(ParseException e) {
                progress.dismiss();
                if (e != null) {
                    Utils.showErrorDialog(activity, e.getMessage());
                    return;
                }
                SelectPlanActivity.planSelected(getActivity(), mPlan);
            }

        });
    }

    private void selectTarget() {
        Intent intent = new Intent(getActivity(), SelectTargetActivity.class);
        startActivityForResult(intent, REQ_SELECT_TARGET);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQ_SELECT_TARGET:
                if (resultCode != Activity.RESULT_OK) {
                    return;
                }
                String targetId = data.getStringExtra(SelectTargetActivity.EXTRA_TARGET_ID);
                String targetName = data.getStringExtra(SelectTargetActivity.EXTRA_TARGET_NAME);
                mSelectTarget.setText(targetName);
                mPlan.setTarget(targetId);
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

}
