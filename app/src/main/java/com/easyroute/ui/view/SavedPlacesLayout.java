package com.easyroute.ui.view;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.easyroute.R;
import com.easyroute.content.model.Place;

import java.util.List;
/**
 * Created by imenekse on 21/01/17.
 */

public class SavedPlacesLayout extends LinearLayout implements OnClickListener {

    public interface OnSavedPlacesItemClickListener {

        void onHomePlaceSelect();

        void onWorkPlaceSelect();

        void onRecentPlaceSelect(Place Place);
    }

    private final int RESOURCE = R.layout.layout_saved_places;
    private final int RECENT_PLACE_LIST_ITEM_RESOURCE = R.layout.item_place_picker_recent_places;

    private LinearLayout llHomePlaceButton;
    private LinearLayout llWorkPlaceButton;
    private CardView cvPlaces;
    private LinearLayout llPlacesList;
    private TextView tvHomePlaceAddress;
    private TextView tvWorkPlaceAddress;
    private ImageView ivHomePlaceIcon;
    private ImageView ivWorkPlaceIcon;

    private LayoutInflater mLayoutInflater;
    private OnSavedPlacesItemClickListener mOnSavedPlacesItemClickListener;
    private Place mHomePlace;
    private Place mWorkPlace;

    public SavedPlacesLayout(Context context) {
        super(context);
        init();
    }

    public SavedPlacesLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mLayoutInflater = LayoutInflater.from(getContext());
        mLayoutInflater.inflate(RESOURCE, this);
        llHomePlaceButton = (LinearLayout) findViewById(R.id.llHomePlaceButton);
        llWorkPlaceButton = (LinearLayout) findViewById(R.id.llWorkPlaceButton);
        cvPlaces = (CardView) findViewById(R.id.cvRecentPlaces);
        llPlacesList = (LinearLayout) findViewById(R.id.llRecentPlacesList);
        tvHomePlaceAddress = (TextView) findViewById(R.id.tvHomePlaceAddress);
        tvWorkPlaceAddress = (TextView) findViewById(R.id.tvWorkPlaceAddress);
        ivHomePlaceIcon = (ImageView) findViewById(R.id.ivHomePlaceIcon);
        ivWorkPlaceIcon = (ImageView) findViewById(R.id.ivWorkPlaceIcon);
        llHomePlaceButton.setOnClickListener(this);
        llWorkPlaceButton.setOnClickListener(this);
    }

    public void setOnSavedPlacesItemClickListener(OnSavedPlacesItemClickListener onSavedPlacesItemClickListener) {
        mOnSavedPlacesItemClickListener = onSavedPlacesItemClickListener;
    }

    @Override
    public void onClick(View v) {
        if (v == llHomePlaceButton) {
            mOnSavedPlacesItemClickListener.onHomePlaceSelect();
        } else if (v == llWorkPlaceButton) {
            mOnSavedPlacesItemClickListener.onWorkPlaceSelect();
        }
    }

    public void setRecentPlaces(List<Place> places) {
        if (places == null || places.isEmpty()) {
            cvPlaces.setVisibility(View.GONE);
        } else {
            cvPlaces.setVisibility(View.VISIBLE);
            llPlacesList.removeAllViews();
            for (int i = 0; i < places.size(); i++) {
                Place place = places.get(i);
                View row = mLayoutInflater.inflate(RECENT_PLACE_LIST_ITEM_RESOURCE, null);
                ImageView ivIcon = (ImageView) row.findViewById(R.id.ivIcon);
                TextView tvPrimary = (TextView) row.findViewById(R.id.tvPrimary);
                TextView tvSecondary = (TextView) row.findViewById(R.id.tvSecondary);
                View vDivider = row.findViewById(R.id.vDivider);
                if (i == places.size() - 1) {
                    vDivider.setVisibility(View.GONE);
                }
                tvPrimary.setText(place.getPrimaryText());
                tvSecondary.setText(place.getSecondaryText());
                if (place.isAirport()) {
                    ivIcon.setImageResource(R.drawable.ic_airport);
                } else {
                    ivIcon.setImageResource(R.drawable.ic_place);
                }
                row.setTag(place);
                row.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Place Place = (Place) v.getTag();
                        mOnSavedPlacesItemClickListener.onRecentPlaceSelect(Place);
                    }
                });
                llPlacesList.addView(row);
            }
        }
    }

    public void setHomePlace(Place place) {
        mHomePlace = place;
        if (place == null) {
            tvHomePlaceAddress.setText(R.string.place_auto_complete_activity_set_location_label);
            ivHomePlaceIcon.setImageResource(R.drawable.ic_home_gray);
        } else {
            llHomePlaceButton.setVisibility(View.VISIBLE);
            tvHomePlaceAddress.setText(place.getAddress());
            ivHomePlaceIcon.setImageResource(R.drawable.ic_home_blue);
        }
    }

    public void setWorkPlace(Place place) {
        mWorkPlace = place;
        if (place == null) {
            tvWorkPlaceAddress.setText(R.string.place_auto_complete_activity_set_location_label);
            ivWorkPlaceIcon.setImageResource(R.drawable.ic_workplace_gray);
        } else {
            llWorkPlaceButton.setVisibility(View.VISIBLE);
            tvWorkPlaceAddress.setText(place.getAddress());
            ivWorkPlaceIcon.setImageResource(R.drawable.ic_workplace_blue);
        }
    }
}
