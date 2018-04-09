/*
 * Project Root Validator
 *
 * @link https://github.com/d4rken/rootvalidator
 * @license https://github.com/d4rken/rootvalidator/blob/master/LICENSE GPLv3
 */

package eu.thedarken.rootvalidator.main.core;

import android.content.Context;

import java.util.List;

public interface TestResult extends Result {

    String getLabel(Context context);

    List<Criterion> getCriteria(Context context);
}
