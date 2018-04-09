/*
 * Project Root Validator
 *
 * @link https://github.com/d4rken/rootvalidator
 * @license https://github.com/d4rken/rootvalidator/blob/master/LICENSE GPLv3
 */

package eu.thedarken.rootvalidator.main.ui.validator;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.thedarken.rootvalidator.R;
import eu.thedarken.rootvalidator.main.core.Criterion;
import eu.thedarken.rootvalidator.main.core.Result;
import eu.thedarken.rootvalidator.main.core.TestResult;


public class TestResultAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final List<TestResult> data = new ArrayList<>();
    private static final int NORMAL_TYPE = 0;

    @Override
    public int getItemViewType(int position) {
        return NORMAL_TYPE;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case NORMAL_TYPE:
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_info_line, parent, false);
                return new RVInfoHolder(v);
            default:
                return null;
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == NORMAL_TYPE) {
            TestResult info = data.get(position);
            RVInfoHolder infoHolder = (RVInfoHolder) holder;
            infoHolder.bind(info);
        }
    }

    public void setData(List<TestResult> data) {
        this.data.clear();
        if (data != null) this.data.addAll(data);
    }

    class RVInfoHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.icon) ImageView colcircl;
        @BindView(R.id.test_title) TextView title;
        @BindView(R.id.point_container) LinearLayout container;
        @BindView(R.id.expand_button) ImageView expand;

        RVInfoHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        private Context getContext() {
            return itemView.getContext();
        }

        public void bind(TestResult info) {
            colcircl.setImageResource(getOutcomeDrawable(info));
            title.setText(info.getPrimaryInfo(getContext()));
            final List<Criterion> criteria = info.getCriteria(getContext());
            if (criteria.isEmpty()) {
                expand.setVisibility(View.GONE);
                container.setVisibility(View.GONE);
                itemView.setOnClickListener(null);
            } else {
                expand.setVisibility(View.VISIBLE);
                itemView.setOnClickListener(v -> {
                    container.setVisibility(container.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
                    expand.setImageResource(container.getVisibility() == View.VISIBLE ? R.drawable.ic_expand_less_white_24dp : R.drawable.ic_expand_more_white_24dp);
                });
                container.removeAllViews();
                for (Criterion criterion : criteria) {
                    View bpLine = LayoutInflater.from(getContext()).inflate(R.layout.view_testpoint, container, false);
                    ImageView point = bpLine.findViewById(R.id.point_icon);
                    TextView text = bpLine.findViewById(R.id.point_text);
                    text.setText(criterion.getPrimaryInfo(getContext()));
                    point.setImageResource(getOutcomeDrawable(criterion));
                    container.addView(bpLine);
                }
            }
        }

        @DrawableRes
        int getOutcomeDrawable(Result result) {
            switch (result.getOutcome()) {
                case POSITIVE:
                    return R.drawable.ic_indicator_positive;
                case NEUTRAL:
                    return R.drawable.ic_indicator_neutral;
                case NEGATIVE:
                    return R.drawable.ic_indicator_negative;
                default:
                    return R.drawable.ic_indicator_negative;
            }
        }
    }
}
