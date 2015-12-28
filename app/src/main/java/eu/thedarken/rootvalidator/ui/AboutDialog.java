/*
 * Project Root Validator
 *
 * @link https://github.com/d4rken/rootvalidator
 * @license https://github.com/d4rken/rootvalidator/blob/master/LICENSE GPLv3
 */

package eu.thedarken.rootvalidator.ui;

import android.app.Dialog;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import eu.thedarken.rootvalidator.BuildConfig;
import eu.thedarken.rootvalidator.R;
import eu.thedarken.rootvalidator.tools.Helpers;

public class AboutDialog extends DialogFragment {
    private Button mGPlus, mWeb, mTwitter;
    private TextView mMessage;

    public static AboutDialog instantiate() {
        return new AboutDialog();
    }

    public void showDialog(FragmentActivity activity) {
        show(activity.getSupportFragmentManager(), AboutDialog.class.getName());
    }

    @NonNull
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
        View dialogLayout = inflater.inflate(R.layout.fragment_dialog_about, container);
        mMessage = (TextView) dialogLayout.findViewById(R.id.tv_message);
        mMessage.setText(getString(R.string.about_version_message, getString(R.string.app_name), BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE));
        mGPlus = (Button) dialogLayout.findViewById(R.id.bt_gplus);
        mGPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Helpers.openLink(v.getContext(),Uri.parse("https://plus.google.com/116634499773478773276"));
                dismiss();
            }
        });
        mWeb = (Button) dialogLayout.findViewById(R.id.bt_www);
        mWeb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Helpers.openLink(v.getContext(),Uri.parse("http://www.darken.eu"));
                dismiss();
            }
        });
        mTwitter = (Button) dialogLayout.findViewById(R.id.bt_twitter);
        mTwitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Helpers.openLink(v.getContext(),Uri.parse("http://www.twitter.com/d4rken"));
                dismiss();
            }
        });
        return dialogLayout;
    }


}