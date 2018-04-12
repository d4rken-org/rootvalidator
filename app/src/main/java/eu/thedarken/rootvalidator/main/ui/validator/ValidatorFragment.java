/*
 * Project Root Validator
 *
 * @link https://github.com/d4rken/rootvalidator
 * @license https://github.com/d4rken/rootvalidator/blob/master/LICENSE GPLv3
 */

package eu.thedarken.rootvalidator.main.ui.validator;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.LayoutAnimationController;
import android.view.animation.TranslateAnimation;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import eu.darken.mvpbakery.base.MVPBakery;
import eu.darken.mvpbakery.base.ViewModelRetainer;
import eu.darken.mvpbakery.injection.InjectedPresenter;
import eu.darken.mvpbakery.injection.PresenterInjectionCallback;
import eu.thedarken.rootvalidator.R;
import eu.thedarken.rootvalidator.main.core.TestResult;
import eu.thedarken.rootvalidator.main.ui.AboutDialog;
import eu.thedarken.rootvalidator.tools.ShareHelper;


public class ValidatorFragment extends Fragment implements ValidatorPresenter.View {

    @BindView(R.id.fab) FloatingActionButton fab;
    @BindView(R.id.recyclerview) RecyclerView recyclerView;
    @BindView(R.id.box_intro) View introBox;
    @BindView(R.id.box_working) View workingBox;

    @Inject ValidatorPresenter presenter;

    private final TestResultAdapter adapter = new TestResultAdapter();
    private Unbinder unbinder;
    private Snackbar upgradeBar;
    private boolean showDonate;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_validator_layout, container, false);
        unbinder = ButterKnife.bind(this, layout);
        return layout;
    }

    @Override
    public void onDestroyView() {
        unbinder.unbind();
        super.onDestroyView();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        AnimationSet set = new AnimationSet(true);
        Animation fadeIn = new AlphaAnimation(0.0f, 1.0f);
        fadeIn.setDuration(350);
        set.addAnimation(fadeIn);
        Animation dropDown = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, -1.0f, Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f
        );
        dropDown.setDuration(400);
        set.addAnimation(dropDown);
        LayoutAnimationController controller = new LayoutAnimationController(set, 0.2f);
        recyclerView.setLayoutAnimation(controller);

        recyclerView.setAdapter(adapter);

        fab.setVisibility(View.INVISIBLE);
        introBox.setVisibility(View.VISIBLE);
        workingBox.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        MVPBakery.<ValidatorPresenter.View, ValidatorPresenter>builder()
                .presenterFactory(new InjectedPresenter<>(this))
                .presenterRetainer(new ViewModelRetainer<>(this))
                .addPresenterCallback(new PresenterInjectionCallback<>(this))
                .attach(this);
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);

        animateFAB(false);
        fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_root));
        fab.setOnClickListener(v -> presenter.onTestAll());
    }

    private void animateFAB(boolean out) {
        Animation animation;
        if (out) {
            animation = new TranslateAnimation(
                    Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0,
                    Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 1.0f);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) { }

                @Override
                public void onAnimationEnd(Animation animation) {
                    fab.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) { }
            });
        } else {
            animation = new TranslateAnimation(
                    Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0,
                    Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 0);

            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    fab.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationEnd(Animation animation) { }

                @Override
                public void onAnimationRepeat(Animation animation) { }
            });
        }
        animation.setDuration(600);
        animation.setInterpolator(new AnticipateOvershootInterpolator(1.2f));
        fab.startAnimation(animation);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.validator_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.donate).setVisible(showDonate);
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.about) {
            AboutDialog dialog = AboutDialog.instantiate();
            dialog.showDialog(getActivity());
            return true;
        } else if (item.getItemId() == R.id.donate) {
            presenter.onDonateClicked(getActivity());
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void display(List<TestResult> testData) {
        adapter.setData(testData);
        adapter.notifyDataSetChanged();

        introBox.setVisibility(View.GONE);
        workingBox.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);

        fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_share_white_24dp));
        fab.setOnClickListener(v -> presenter.onShare());
        animateFAB(false);
    }

    @Override
    public void share(List<String> shareData) {
        ShareHelper.share(getActivity(), getString(R.string.label_export_title), shareData);
    }

    @Override
    public void showWorking() {
        animateFAB(true);
        introBox.setVisibility(View.GONE);
        workingBox.setVisibility(View.VISIBLE);
    }

    @Override
    public void showNagBar(boolean show) {
        if (show) {
            if (getView() == null) return;
            upgradeBar = Snackbar.make(getView(), R.string.donate_description, Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.action_donate, v -> presenter.onDonateClicked(getActivity()))
                    .addCallback(new Snackbar.Callback() {
                        @Override
                        public void onShown(Snackbar sb) {
                            ValidatorFragment.this.upgradeBar = sb;
                            super.onShown(sb);
                        }

                        @Override
                        public void onDismissed(Snackbar transientBottomBar, int event) {
                            upgradeBar = null;
                            super.onDismissed(transientBottomBar, event);
                        }
                    });
            upgradeBar.show();
        } else {
            if (upgradeBar != null) upgradeBar.dismiss();
        }
    }

    @Override
    public void showDonate(boolean showDonate) {
        this.showDonate = showDonate;
        if (getActivity() == null) return;
        getActivity().invalidateOptionsMenu();
    }
}
