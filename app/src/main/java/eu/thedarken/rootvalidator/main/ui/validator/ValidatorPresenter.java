package eu.thedarken.rootvalidator.main.ui.validator;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import eu.darken.mvpbakery.base.Presenter;
import eu.darken.mvpbakery.injection.ComponentPresenter;
import eu.thedarken.rootvalidator.IAPHelper;
import eu.thedarken.rootvalidator.R;
import eu.thedarken.rootvalidator.main.core.Criterion;
import eu.thedarken.rootvalidator.main.core.Settings;
import eu.thedarken.rootvalidator.main.core.TestResult;
import eu.thedarken.rootvalidator.main.core.TestSuite;
import io.reactivex.Single;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.disposables.Disposables;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

@ValidatorComponent.Scope
public class ValidatorPresenter extends ComponentPresenter<ValidatorPresenter.View, ValidatorComponent> {

    private final Context context;
    private final List<TestSuite> tests;
    private List<TestResult> testData;
    private final IAPHelper iapHelper;
    private final Settings settings;
    private Disposable upgradeSub = Disposables.disposed();

    @Inject
    ValidatorPresenter(Context context, List<TestSuite> tests, IAPHelper iapHelper, Settings settings) {
        this.context = context;
        this.tests = tests;
        this.iapHelper = iapHelper;
        this.settings = settings;
    }

    @Override
    public void onBindChange(@Nullable View view) {
        super.onBindChange(view);
        iapHelper.check();
        if (testData != null) {
            onView(v -> v.display(testData));
        }
        if (getView() != null && upgradeSub.isDisposed()) {
            upgradeSub = iapHelper.isPremiumVersion()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(isProVersion -> {
                        onView(v -> v.showDonate(!isProVersion));
                        long delta = System.currentTimeMillis() - settings.getLastUpgradeNagTime();
                        if (delta > 3 * 60 * 1000) {
                            // Every 3 hours at most.
                            settings.setLastUpgradeNagTime(System.currentTimeMillis());
                            ValidatorPresenter.this.onView(v -> v.showNagBar(!isProVersion));
                        }
                    });
        } else {
            upgradeSub.dispose();
        }
    }

    void onTestAll() {
        onView(View::showWorking);
        Single.create((SingleOnSubscribe<List<TestResult>>) emitter -> {
            Timber.d("loadInBackground start...");
            long dur = System.currentTimeMillis();
            ArrayList<TestResult> results = new ArrayList<>();

            for (TestSuite suite : tests) {
                results.addAll(suite.test().blockingGet());
            }

            Timber.d("loadInBackground done: %dms", (System.currentTimeMillis() - dur));
            emitter.onSuccess(results);
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess(testInfos -> ValidatorPresenter.this.testData = testInfos)
                .subscribe(testInfos -> onView(v -> v.display(testInfos)));

    }

    void onShare() {
        if (testData == null) return;
        ArrayList<String> toShare = new ArrayList<>();
        for (TestResult testResult : testData) {
            toShare.addAll(getDetails(context, testResult));
            toShare.add("##########\n");
        }
        onView(v -> v.share(toShare));
    }

    static List<String> getDetails(Context context, TestResult testResult) {
        List<String> details = new ArrayList<>();
        details.add("##### " + testResult.getLabel(context) + " #####");
        details.add(context.getString(R.string.label_export_section_result));
        details.add("    " + testResult.getPrimaryInfo(context));

        final List<Criterion> criteria = testResult.getCriteria(context);
        if (criteria.size() > 0) details.add(context.getString(R.string.label_export_section_extras));
        for (Criterion criterion : criteria) {
            details.add("    " + criterion.getPrimaryInfo(context));
            if (criteria.indexOf(criterion) != criteria.size() - 1 && criteria.size() > 1) {
                details.add("    ---");
            }
        }
        return details;
    }

    void onDonateClicked(Activity activity) {
        iapHelper.donate(activity);
    }

    interface View extends Presenter.View {
        void share(List<String> shareData);

        void display(List<TestResult> testData);

        void showWorking();

        void showNagBar(boolean show);

        void showDonate(boolean showDonate);
    }
}
