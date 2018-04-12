package eu.thedarken.rootvalidator;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.Nullable;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.BehaviorSubject;
import timber.log.Timber;

@AppComponent.Scope
public class IAPHelper implements PurchasesUpdatedListener, BillingClientStateListener {
    static final String SKU_UPGRADE_DONATE = "upgrade.donate";
    private final BehaviorSubject<List<Upgrade>> upgradesPublisher = BehaviorSubject.create();
    private final BillingClient billingClient;

    public static class Upgrade {
        enum Type {
            DONATE, UNKNOWN
        }

        private final Purchase purchase;
        private final Type type;

        Upgrade(Purchase purchase) {
            this.purchase = purchase;
            if (purchase.getSku().endsWith(SKU_UPGRADE_DONATE)) type = Type.DONATE;
            else type = Type.UNKNOWN;
        }

        public Type getType() {
            return type;
        }
    }

    @Inject
    public IAPHelper(Context context) {
        billingClient = BillingClient.newBuilder(context).setListener(this).build();
        billingClient.startConnection(this);
    }

    @Override
    public void onBillingSetupFinished(int responseCode) {
        Timber.d("onBillingSetupFinished(responseCode=%d)", responseCode);
        if (BillingClient.BillingResponse.OK == responseCode) {
            final Purchase.PurchasesResult purchasesResult = billingClient.queryPurchases(BillingClient.SkuType.INAPP);
            Timber.d("queryPurchases(): code=%d, purchases=%s", purchasesResult.getResponseCode(), purchasesResult.getPurchasesList());
            onPurchasesUpdated(purchasesResult.getResponseCode(), purchasesResult.getPurchasesList());
        }
    }

    @Override
    public void onBillingServiceDisconnected() {
        Timber.d("onBillingServiceDisconnected()");
    }

    @Override
    public void onPurchasesUpdated(int responseCode, @Nullable List<Purchase> purchases) {
        Timber.d("onPurchasesUpdated(responseCode=%d, purchases=%s)", responseCode, purchases);
        if (purchases != null) notifyOfPurchases(purchases);
    }

    private void notifyOfPurchases(List<Purchase> purchases) {
        Timber.d("notifyOfPurchases(%s)", purchases);
        List<Upgrade> upgrades = new ArrayList<>();
        for (Purchase p : purchases) upgrades.add(new Upgrade(p));
        upgradesPublisher.onNext(upgrades);
    }

    public void check() {
        Single.create((SingleOnSubscribe<Purchase.PurchasesResult>) e -> e.onSuccess(billingClient.queryPurchases(BillingClient.SkuType.INAPP)))
                .subscribeOn(Schedulers.io())
                .filter(r -> r.getResponseCode() == 0 && r.getPurchasesList() != null)
                .map(Purchase.PurchasesResult::getPurchasesList)
                .subscribe(this::notifyOfPurchases, Timber::e);
    }

    public Observable<Boolean> isPremiumVersion() {
        if (BuildConfig.DEBUG) {
            return upgradesPublisher.map(egal -> !true);
        }
        return upgradesPublisher.map(upgrades -> {
            boolean proVersion = false;
            for (Upgrade upgrade : upgrades) {
                if (upgrade.getType().equals(Upgrade.Type.DONATE)) {
                    proVersion = true;
                    break;
                }
            }
            return proVersion;
        });
    }

    private BillingFlowParams buildSKUDonateUpgrade() {
        return BillingFlowParams.newBuilder()
                .setSku(SKU_UPGRADE_DONATE)
                .setType(BillingClient.SkuType.INAPP)
                .build();
    }

    public void donate(Activity activity) {
        billingClient.launchBillingFlow(activity, buildSKUDonateUpgrade());
    }
}
