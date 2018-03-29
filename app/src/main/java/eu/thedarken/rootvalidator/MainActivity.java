/*
 * Project Root Validator
 *
 * @link https://github.com/d4rken/rootvalidator
 * @license https://github.com/d4rken/rootvalidator/blob/master/LICENSE GPLv3
 */

package eu.thedarken.rootvalidator;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private Fragment mFragment;

    public String getFragmentClass() {
        return ValidatorFragment.class.getName();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainactivity_layout);
        mFragment = getSupportFragmentManager().findFragmentByTag(getFragmentClass());
        if (mFragment == null) {
            mFragment = Fragment.instantiate(this, getFragmentClass());
            getSupportFragmentManager().beginTransaction().replace(R.id.content, mFragment, getFragmentClass()).commit();
        }

    }

}