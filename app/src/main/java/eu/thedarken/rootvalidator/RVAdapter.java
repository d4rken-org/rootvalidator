package eu.thedarken.rootvalidator;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import eu.thedarken.rootvalidator.tests.BP;
import eu.thedarken.rootvalidator.tests.TestInfo;

/**
 * Created by darken on 13.02.2015.
 */
public class RVAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final ArrayList<TestInfo> mData;
    private static final int NORMAL_TYPE = 0;

    public RVAdapter(ArrayList<TestInfo> data) {
        mData = data;
    }

    public ArrayList<TestInfo> getData() {
        return mData;
    }

    @Override
    public int getItemViewType(int position) {
        return NORMAL_TYPE;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case NORMAL_TYPE: {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_info_line, parent, false);
                return new RVInfoHolder(v);
            }
            default: {
                return null;
            }
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == NORMAL_TYPE) {
            TestInfo info = mData.get(position);
            RVInfoHolder infoHolder = (RVInfoHolder) holder;
            infoHolder.inject(info);
        }
    }

    class RVInfoHolder extends RecyclerView.ViewHolder {
        private CircleImageView mCircularImageView;
        private TextView mTitle;
        private LinearLayout mPointsContainer;
        private ImageView mExpandIcon;

        public RVInfoHolder(View itemView) {
            super(itemView);
            mCircularImageView = (CircleImageView) itemView.findViewById(R.id.civ_icon);
            mTitle = (TextView) itemView.findViewById(R.id.tv_title);
            mPointsContainer = (LinearLayout) itemView.findViewById(R.id.ll_points);
            mExpandIcon = (ImageView) itemView.findViewById(R.id.iv_expand_button);
        }

        private Context getContext() {
            return itemView.getContext();
        }

        public void inject(TestInfo info) {
            mCircularImageView.setImageDrawable(info.getIcon(getContext()));
            mTitle.setText(info.getPrimaryInfo(getContext()));
            if (info.getCriterias(getContext()).isEmpty()) {
                mExpandIcon.setVisibility(View.GONE);
                mPointsContainer.setVisibility(View.GONE);
                itemView.setOnClickListener(null);
            } else {
                mExpandIcon.setVisibility(View.VISIBLE);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mPointsContainer.setVisibility(mPointsContainer.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
                        mExpandIcon.setImageResource(mPointsContainer.getVisibility() == View.VISIBLE ? R.drawable.ic_expand_less_white_24dp : R.drawable.ic_expand_more_white_24dp);
                    }
                });
                mPointsContainer.removeAllViews();
                for (BP bp : info.getCriterias(getContext())) {
                    View bpLine = LayoutInflater.from(getContext()).inflate(R.layout.view_bp, mPointsContainer, false);
                    TextView text = (TextView) bpLine.findViewById(R.id.tv_bp_text);
                    CircleImageView point = (CircleImageView) bpLine.findViewById(R.id.civ_bp_icon);
                    text.setText(bp.getText());
                    point.setImageDrawable(bp.getPoint());
                    mPointsContainer.addView(bpLine);
                }
            }
        }
    }
}
