/*
 * Project Root Validator
 *
 * @link https://github.com/d4rken/rootvalidator
 * @license https://github.com/d4rken/rootvalidator/blob/master/LICENSE GPLv3
 */

package eu.thedarken.rootvalidator.tools;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import eu.thedarken.rootvalidator.R;

public class Helpers {

    public static void openLink(Context context, Uri link) {
        try {
            Intent i = new Intent(Intent.ACTION_VIEW, link);
            context.startActivity(i);
        } catch (Exception e) {
            Toast.makeText(context, context.getString(R.string.cant_open, link.toString()), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

}
