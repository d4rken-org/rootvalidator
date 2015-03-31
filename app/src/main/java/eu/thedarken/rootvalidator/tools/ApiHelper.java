package eu.thedarken.rootvalidator.tools;

import android.os.Build;

public class ApiHelper {

	public static boolean hasJellyBeanMR1() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1;
	}

}
