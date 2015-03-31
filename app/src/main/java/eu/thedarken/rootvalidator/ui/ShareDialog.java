package eu.thedarken.rootvalidator.ui;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import eu.thedarken.rootvalidator.R;


/**
 * Created by darken on 22.01.2015.
 */
public class ShareDialog extends DialogFragment {
    private Button mSimple, mExtended;
    private TextView mTitle, mMessage;

    public static ShareDialog instantiate(Fragment parent) {
        ShareDialog dialog = new ShareDialog();
        dialog.setTargetFragment(parent, 0);
        return dialog;
    }

    public void showDialog(FragmentActivity activity) {
        show(activity.getSupportFragmentManager(), ShareDialog.class.getName());
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View dialogLayout = inflater.inflate(R.layout.fragment_dialog_share, container);
        mTitle = (TextView) dialogLayout.findViewById(R.id.tv_title);
        mMessage = (TextView) dialogLayout.findViewById(R.id.tv_message);
        mSimple = (Button) dialogLayout.findViewById(R.id.bt_simple);
        mSimple.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getTargetFragment() instanceof ShareCallback) {
                    ((ShareCallback) getTargetFragment()).onShare(false);
                }
            }
        });
        mExtended = (Button) dialogLayout.findViewById(R.id.bt_extended);
        mExtended.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getTargetFragment() instanceof ShareCallback) {
                    ((ShareCallback) getTargetFragment()).onShare(true);
                }
            }
        });
        return dialogLayout;
    }

    public interface ShareCallback {
        public void onShare(boolean extended);
    }
}