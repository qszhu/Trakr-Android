
package qszhu.trakr.target;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;

import com.parse.ParseException;
import com.parse.SaveCallback;
import com.testflightapp.lib.TestFlight;

import qszhu.trakr.R;
import qszhu.trakr.Utils;

public class CreateTargetFragment extends Fragment implements OnClickListener {

    private EditText mName, mDescription;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getActivity().setTitle(R.string.title_create_target);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.create_target, container, false);

        mName = (EditText) v.findViewById(R.id.name);
        mDescription = (EditText) v.findViewById(R.id.description);

        v.findViewById(R.id.action_create).setOnClickListener(this);

        return v;
    }

    @Override
    public void onResume() {
        TestFlight.passCheckpoint("create target resume");

        super.onResume();
    }

    @Override
    public void onClick(View v) {
        TestFlight.passCheckpoint("click create");

        final Activity activity = getActivity();

        final Target target = new Target()
                .setName(String.valueOf(mName.getText()))
                .setDescription(String.valueOf(mDescription.getText()));

        int error = target.getValidationError();
        if (error != 0) {
            Utils.showErrorDialog(activity, error);
            return;
        }

        final ProgressDialog progress = ProgressDialog.show(
                activity, null, activity.getString(R.string.create_target_progress), true);
        target.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                progress.dismiss();
                if (e != null) {
                    Utils.showErrorDialog(activity, e.getMessage());
                    return;
                }
                SelectTargetActivity.targetSelected(getActivity(), target);
            }
        });
    }
}
