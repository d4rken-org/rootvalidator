package eu.thedarken.rootvalidator.tools;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import eu.thedarken.rootvalidator.R;

public class Helpers {

    public static void openLink(Activity activity, Uri link) {
        try {
            Intent i = new Intent(Intent.ACTION_VIEW, link);
            activity.startActivity(i);
            Toast.makeText(activity, activity.getString(R.string.cant_open, link.toString()), Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
