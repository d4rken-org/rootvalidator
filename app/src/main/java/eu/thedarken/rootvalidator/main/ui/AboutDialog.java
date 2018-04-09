/*
 * Project Root Validator
 *
 * @link https://github.com/d4rken/rootvalidator
 * @license https://github.com/d4rken/rootvalidator/blob/master/LICENSE GPLv3
 */

package eu.thedarken.rootvalidator.main.ui;

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

    @SuppressWarnings("HardCodedStringLiteral")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View dialogLayout = inflater.inflate(R.layout.fragment_dialog_about, container);
        TextView message = dialogLayout.findViewById(R.id.tv_message);
        message.setText(getString(R.string.about_version_message, getString(R.string.app_name), BuildConfig.VERSION_NAME, String.valueOf(BuildConfig.VERSION_CODE)));
        Button gplus = dialogLayout.findViewById(R.id.bt_gplus);
        gplus.setOnClickListener(v -> {
            Helpers.openLink(v.getContext(), Uri.parse("https://plus.google.com/116634499773478773276"));
            dismiss();
        });
        Button web = dialogLayout.findViewById(R.id.bt_www);
        web.setOnClickListener(v -> {
            Helpers.openLink(v.getContext(), Uri.parse("http://www.darken.eu"));
            dismiss();
        });
        Button twitter = dialogLayout.findViewById(R.id.bt_twitter);
        twitter.setOnClickListener(v -> {
            Helpers.openLink(v.getContext(), Uri.parse("http://www.twitter.com/d4rken"));
            dismiss();
        });
        return dialogLayout;
    }


}