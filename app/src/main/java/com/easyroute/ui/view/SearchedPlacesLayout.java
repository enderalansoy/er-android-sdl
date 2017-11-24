package com.easyroute.ui.view;

import android.content.Context;
import android.graphics.Typeface;
import android.text.style.CharacterStyle;
import android.text.style.StyleSpan;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.easyroute.R;
import com.google.android.gms.location.places.AutocompletePrediction;

import java.util.List;
/**
 * Created by imenekse on 21/01/17.
 */

public class SearchedPlacesLayout extends FrameLayout {

    public interface OnSearchedPlaceSelectListener {

        void onSearchedPlaceSelect(AutocompletePrediction prediction);
    }

    private final int RESOURCE = R.layout.layout_searched_places;
    private final CharacterStyle SEARCH_CHARACTER_STYLE = new StyleSpan(Typeface.BOLD);

    private LinearLayout llSearcedPlacesList;
    private LayoutInflater mLayoutInflater;
    private OnSearchedPlaceSelectListener mOnSearchedPlaceSelectListener;

    public SearchedPlacesLayout(Context context) {
        super(context);
        init();
    }

    public SearchedPlacesLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mLayoutInflater = LayoutInflater.from(getContext());
        mLayoutInflater.inflate(RESOURCE, this);
        llSearcedPlacesList = (LinearLayout) findViewById(R.id.llSearcedPlacesList);
    }

    public void setOnSearchedPlaceSelectListener(OnSearchedPlaceSelectListener onSearchedPlaceSelectListener) {
        mOnSearchedPlaceSelectListener = onSearchedPlaceSelectListener;
    }

    public void setAutoCompletePredictions(List<AutocompletePrediction> predictions) {
        llSearcedPlacesList.removeAllViews();
        if (predictions != null) {
            for (int i = 0; i < predictions.size(); i++) {
                AutocompletePrediction prediction = predictions.get(i);
                View row = mLayoutInflater.inflate(R.layout.item_place_picker_recent_places, null);
                View vDivider = row.findViewById(R.id.vDivider);
                TextView tvPrimary = (TextView) row.findViewById(R.id.tvPrimary);
                TextView tvSecondary = (TextView) row.findViewById(R.id.tvSecondary);
                ImageView ivIcon = (ImageView) row.findViewById(R.id.ivIcon);
                tvPrimary.setText(prediction.getPrimaryText(SEARCH_CHARACTER_STYLE));
                tvSecondary.setText(prediction.getSecondaryText(SEARCH_CHARACTER_STYLE));
                if (i == predictions.size() - 1) {
                    vDivider.setVisibility(View.GONE);
                }
                String fullText = prediction.getFullText(null).toString().toLowerCase();
                if (fullText.contains("havalimanı") || fullText.contains("airport") || fullText.contains("havaalanı")) {
                    ivIcon.setImageResource(R.drawable.ic_airport);
                }
                row.setTag(prediction);
                row.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AutocompletePrediction p = (AutocompletePrediction) v.getTag();
                        mOnSearchedPlaceSelectListener.onSearchedPlaceSelect(p);
                    }
                });
                llSearcedPlacesList.addView(row);
            }
        }
    }
}
