/*
 * Project Root Validator
 *
 * @link https://github.com/d4rken/rootvalidator
 * @license https://github.com/d4rken/rootvalidator/blob/master/LICENSE GPLv3
 */

package eu.thedarken.rootvalidator;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import java.util.ArrayList;

import eu.thedarken.rootvalidator.tests.TestInfo;
import eu.thedarken.rootvalidator.tests.busybox.BusyBoxInfo;
import eu.thedarken.rootvalidator.tests.busybox.BusyBoxTest;
import eu.thedarken.rootvalidator.tests.root.RootInfo;
import eu.thedarken.rootvalidator.tests.root.RootTest;
import eu.thedarken.rootvalidator.tests.selinux.SELinuxInfo;
import eu.thedarken.rootvalidator.tests.selinux.SELinuxTest;
import eu.thedarken.rootvalidator.tests.suapp.SuAppInfo;
import eu.thedarken.rootvalidator.tests.suapp.SuperUserAppTest;
import eu.thedarken.rootvalidator.tests.subinary.SuBinaryInfo;
import eu.thedarken.rootvalidator.tests.subinary.SuBinaryTest;
import eu.thedarken.rootvalidator.tools.ApiHelper;
import eu.thedarken.rootvalidator.tools.Logy;

public class RVLoader extends AsyncTaskLoader<ArrayList<TestInfo>> {
    public static final int ID = 5;
    private static final String TAG = "RV:RVLoader";
    private ArrayList<TestInfo> mData;

    public RVLoader(Context context) {
        super(context);
    }

    @Override
    public void deliverResult(ArrayList<TestInfo> data) {

        if (isReset()) {
            if (mData != null)
                onReleaseResources(mData);
        }
        ArrayList<TestInfo> oldData = mData;
        mData = data;

        if (isStarted()) {
            super.deliverResult(data);
        }

        if (oldData != null)
            onReleaseResources(oldData);
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    protected void onStopLoading() {
        cancelLoad();
    }

    @Override
    public void onCanceled(ArrayList<TestInfo> data) {
        onReleaseResources(data);
    }

    @Override
    protected void onReset() {
        onStopLoading();

        if (mData != null) {
            onReleaseResources(mData);
            mData = null;
        }
    }

    protected void onReleaseResources(ArrayList<TestInfo> data) {
    }

    @Override
    public ArrayList<TestInfo> loadInBackground() {
        Logy.d(TAG, "loadInBackground start...");
        long dur = System.currentTimeMillis();
        ArrayList<TestInfo> result = new ArrayList<>();

        RootInfo resultRoot = new RootTest(getContext()).test();
        result.add(resultRoot);

        SuBinaryInfo resultSuBinaryTest = new SuBinaryTest(getContext()).test();
        result.add(resultSuBinaryTest);

        SuAppInfo resultSuAppTest = new SuperUserAppTest(getContext(), resultSuBinaryTest).test();
        result.add(resultSuAppTest);

        BusyBoxInfo resultBusyBoxTest = new BusyBoxTest(getContext()).test();
        result.add(resultBusyBoxTest);

        SELinuxInfo resultSeLinuxTest = new SELinuxTest(getContext()).test();
        if (ApiHelper.hasJellyBeanMR1())
            result.add(resultSeLinuxTest);

        Logy.d(TAG, "loadInBackground done:" + (System.currentTimeMillis() - dur));
        return result;
    }
}