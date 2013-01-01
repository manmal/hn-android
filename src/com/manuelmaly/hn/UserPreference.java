package com.manuelmaly.hn;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.manuelmaly.hn.task.HNLoginTask;
import com.manuelmaly.hn.task.ITaskFinishedHandler;

public class UserPreference extends Preference implements ITaskFinishedHandler<Boolean> {

    private static final int TASKCODE_LOGIN = 10;

    private String mUsertoken;
    private String mUsername;

    private Activity mActivity;

    private EditText mUsernameField;
    private EditText mPasswordField;

    private AlertDialog mAlertDialog;

    public UserPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public UserPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public String getData() {
        if (mUsername == null || mUsertoken == null)
            return null;

        return mUsername.concat(Settings.USER_DATA_SEPARATOR).concat(mUsertoken);
    }

    public void setActivity(Activity activity) {
        mActivity = activity;
    }

    @Override
    protected void onClick() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View dialogContentView = inflater.inflate(R.layout.user_settings_dialog, null);
        mUsernameField = (EditText) dialogContentView.findViewById(R.id.user_settings_dialog_username);
        mPasswordField = (EditText) dialogContentView.findViewById(R.id.user_settings_dialog_password);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(dialogContentView).setMessage(R.string.credentials)
            .setPositiveButton(R.string.check_and_save, null).setNegativeButton(R.string.cancel, new OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    System.out.println("Cancel!");
                }
            });

        mAlertDialog = builder.create();
        mAlertDialog.setCanceledOnTouchOutside(true);
        mAlertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                final Button b = mAlertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                b.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        mAlertDialog.setCanceledOnTouchOutside(false);
                        b.setText(R.string.checking);
                        HNLoginTask.start(mUsernameField.getText().toString(), mPasswordField.getText().toString(),
                            mActivity, UserPreference.this, TASKCODE_LOGIN);
                        InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(
                            Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(mUsernameField.getWindowToken(), 0);
                    }
                });
            }
        });

        mAlertDialog.show();
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        if (restorePersistedValue) {
            String userData = getPersistedString("");
            mUsername = getUserNameFromUserdata(userData);
            mUsertoken = getUserTokenFromUserdata(userData);
        } else {
            mUsername = null;
            mUsertoken = null;
            persistString(getData());
        }
        super.onSetInitialValue(restorePersistedValue, defaultValue);
    }

    public static String getUserNameFromUserdata(String userData) {
        String[] splitData = userData.split(Settings.USER_DATA_SEPARATOR);
        if (splitData.length > 0)
            return splitData[0];
        return null;
    }

    public static String getUserTokenFromUserdata(String userData) {
        String[] splitData = userData.split(Settings.USER_DATA_SEPARATOR);
        if (splitData.length > 1)
            return splitData[1];
        return null;
    }

    @Override
    public void onTaskFinished(int taskCode, TaskResultCode code, Boolean result) {
        if (result != null && result) {
            if (mAlertDialog != null)
                mAlertDialog.dismiss();
        } else {
            int messageId;
            if (result != null && !result)
                messageId = R.string.error_login_failed;
            else
                messageId = code.equals(TaskResultCode.NoNetworkConnection) ? R.string.error_login_device_offline
                    : R.string.error_unknown_error;
            Toast.makeText(App.getInstance(), App.getInstance().getString(messageId), Toast.LENGTH_LONG).show();
            mAlertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setText(R.string.check_and_save);
        }

    }

}
