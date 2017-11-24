package com.easyroute.ui.view;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.easyroute.R;

public class StickyTab extends FrameLayout {

    public interface OnStickyTabItemSelectListener {

        void onStickyTabItemSelect(int pos);
    }

    private final int NORMAL_TEXT_COLOR = ContextCompat.getColor(getContext(), R.color.primary_dark_text);
    private final int SELECTED_TEXT_COLOR = ContextCompat.getColor(getContext(), R.color.white);
    private final int BACKGROUND_RESOURCE = R.drawable.bg_sticky_tab;
    private final int INDICATOR_RESOURCE = R.drawable.bg_sticky_tab_indicator;

    private View indicator;
    private TextView tv1;
    private TextView tv2;
    private View selectedText;

    private OnStickyTabItemSelectListener mListener;
    private int mSelectedIndex = 0;

    public StickyTab(Context context) {
        super(context);
        init();
    }

    public StickyTab(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public void setOnItemSelectListener(OnStickyTabItemSelectListener listener) {
        mListener = listener;
    }

    public void setTexts(int first, int second) {
        tv1.setText(first);
        tv2.setText(second);
    }

    private void init() {
        setBackgroundResource(BACKGROUND_RESOURCE);
        indicator = new View(getContext());
        indicator.setBackgroundResource(INDICATOR_RESOURCE);
        indicator.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT));
        addView(indicator);
        LinearLayout container = new LinearLayout(getContext());
        container.setOrientation(LinearLayout.HORIZONTAL);
        container.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        addView(container);
        tv1 = new TextView(getContext());
        tv2 = new TextView(getContext());
        tv1.setGravity(Gravity.CENTER);
        tv2.setGravity(Gravity.CENTER);
        tv1.setLayoutParams(new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, 1));
        tv2.setLayoutParams(new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, 1));
        tv1.setOnClickListener(mItemClickListener);
        tv2.setOnClickListener(mItemClickListener);
        container.addView(tv1);
        container.addView(tv2);
        tv1.setTextColor(SELECTED_TEXT_COLOR);
        tv2.setTextColor(NORMAL_TEXT_COLOR);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed) {
            indicator.getLayoutParams().width = getMeasuredWidth() / 2;
        }
    }

    public int getSelectedIndex() {
        return mSelectedIndex;
    }

    public void setSelectedIndex(int index) {
        mSelectedIndex = index;
        selectedText = tv1;
        indicator.animate().setDuration(0).setStartDelay(500).translationX(tv1.getLeft());
        if (index == 0) {
            tv1.setTextColor(SELECTED_TEXT_COLOR);
            tv2.setTextColor(NORMAL_TEXT_COLOR);
        } else {
            tv1.setTextColor(NORMAL_TEXT_COLOR);
            tv2.setTextColor(SELECTED_TEXT_COLOR);
        }
    }

    private OnClickListener mItemClickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view != selectedText) {
                selectedText = view;
                indicator.animate().setDuration(250).translationX(view.getLeft());
                if (view == tv1) {
                    tv1.setTextColor(SELECTED_TEXT_COLOR);
                    tv2.setTextColor(NORMAL_TEXT_COLOR);
                    mSelectedIndex = 0;
                    if (mListener != null) {
                        mListener.onStickyTabItemSelect(0);
                    }
                } else {
                    tv1.setTextColor(NORMAL_TEXT_COLOR);
                    tv2.setTextColor(SELECTED_TEXT_COLOR);
                    mSelectedIndex = 1;
                    if (mListener != null) {
                        mListener.onStickyTabItemSelect(1);
                    }
                }
            }
        }
    };
}
