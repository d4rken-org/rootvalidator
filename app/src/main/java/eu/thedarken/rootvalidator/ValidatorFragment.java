package eu.thedarken.rootvalidator;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
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

import com.melnykov.fab.FloatingActionButton;

import java.util.ArrayList;

import eu.thedarken.rootvalidator.tests.TestInfo;
import eu.thedarken.rootvalidator.tools.Logy;
import eu.thedarken.rootvalidator.tools.ShareHelper;
import eu.thedarken.rootvalidator.ui.AboutDialog;
import eu.thedarken.rootvalidator.ui.EmptyRecyclerView;
import eu.thedarken.rootvalidator.ui.ShareDialog;

/**
 * Created by darken on 26/01/15.
 */
public class ValidatorFragment extends Fragment implements LoaderManager.LoaderCallbacks<ArrayList<TestInfo>>, ShareDialog.ShareCallback {
    private static final String TAG = "RV:ValidatorFragment";
    private FloatingActionButton mFab;
    private EmptyRecyclerView mRecyclerView;
    private View mEmptyStartView;
    private View mEmptyWorkingView;
    private ViewGroup mListContainer;
    private RVAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey("data")) {
                ArrayList<TestInfo> theData = savedInstanceState.getParcelableArrayList("data");
                mAdapter = new RVAdapter(theData);

            }
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_validator_layout, container, false);
        mListContainer = (ViewGroup) layout.findViewById(R.id.ll_list_container);
        mFab = (FloatingActionButton) layout.findViewById(R.id.fab);
        mRecyclerView = (EmptyRecyclerView) layout.findViewById(R.id.recyclerview);
        mEmptyStartView = inflater.inflate(R.layout.view_empty_start, mListContainer, false);
        mEmptyWorkingView = inflater.inflate(R.layout.view_empty_working, mListContainer, false);
        return layout;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
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
        mRecyclerView.setLayoutAnimation(controller);

        mFab.attachToRecyclerView(mRecyclerView);
        mFab.setVisibility(View.INVISIBLE);
        mEmptyStartView.setVisibility(View.GONE);
        mEmptyWorkingView.setVisibility(View.GONE);
        mListContainer.addView(mEmptyStartView);
        mListContainer.addView(mEmptyWorkingView);
        mRecyclerView.setEmptyView(mEmptyStartView);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
        setFABShare(mAdapter != null);
        animateFAB(false);
        if (mAdapter != null) {
            mRecyclerView.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();
        }
//        getView().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                mFab.performClick();
//            }
//        }, 3000);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mAdapter != null && !mAdapter.getData().isEmpty()) {
            outState.putParcelableArrayList("data", mAdapter.getData());
        }
        super.onSaveInstanceState(outState);
    }

    private void animateFAB(boolean out) {
        Animation animation;
        if (out) {
            animation = new TranslateAnimation(
                    Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0,
                    Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 1.0f);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    mFab.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        } else {
            animation = new TranslateAnimation(
                    Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0,
                    Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 0);

            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    mFab.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationEnd(Animation animation) {

                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        }
        animation.setDuration(600);
        animation.setInterpolator(new AnticipateOvershootInterpolator(1.2f));
        mFab.startAnimation(animation);
    }

    private void setFABShare(boolean share) {
        if (share) {
            mFab.setImageDrawable(getResources().getDrawable(R.drawable.ic_share_white_24dp));
            mFab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ShareDialog dialog = ShareDialog.instantiate(ValidatorFragment.this);
                    dialog.showDialog(getActivity());
                }
            });
        } else {
            mFab.setImageDrawable(getResources().getDrawable(R.drawable.ic_root));
            mFab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mRecyclerView.setEmptyView(mEmptyWorkingView);
                    getLoaderManager().restartLoader(RVLoader.ID, null, ValidatorFragment.this);
                    animateFAB(true);
                }
            });
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.validator_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public Loader<ArrayList<TestInfo>> onCreateLoader(int id, Bundle args) {
        Logy.d(TAG, "onCreateLoader()");
        return new RVLoader(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<TestInfo>> loader, ArrayList<TestInfo> data) {
        Logy.d(TAG, "onLoadFinished(" + data.size() + ")");
        mAdapter = new RVAdapter(data);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();

        setFABShare(mAdapter != null);
        animateFAB(false);
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<TestInfo>> loader) {
        Logy.d(TAG, "onLoaderReset()");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.about) {
            AboutDialog dialog = AboutDialog.instantiate();
            dialog.showDialog(getActivity());
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onShare(boolean extended) {
        ArrayList<String> toShare = new ArrayList<>();
        for (TestInfo testInfo : mAdapter.getData()) {
            toShare.addAll(testInfo.getDetails(getActivity()));
            if (extended) {
                toShare.add("RAW:");
                toShare.addAll(testInfo.getRaw());
            }
            toShare.add("##### " + testInfo.getTitle() +" #####\n");
        }
        ShareHelper.share(getActivity(), "RootValidator Results", toShare);
    }

}
