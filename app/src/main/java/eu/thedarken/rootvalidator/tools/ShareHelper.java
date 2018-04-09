/*
 * Project Root Validator
 *
 * @link https://github.com/d4rken/rootvalidator
 * @license https://github.com/d4rken/rootvalidator/blob/master/LICENSE GPLv3
 */

package eu.thedarken.rootvalidator.tools;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

import java.util.List;

import eu.thedarken.rootvalidator.R;


public class ShareHelper {

    public static void share(Activity activity, String subject, List<String> out) {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, subject);
        StringBuilder _printOut = new StringBuilder();
        for (String s : out) _printOut.append(s + "\n");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, _printOut.toString());
        try {
            activity.startActivity(Intent.createChooser(sharingIntent, activity.getResources().getString(R.string.share)));
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(activity, R.string.msg_error_no_app_share, Toast.LENGTH_SHORT).show();
        }
    }
}
