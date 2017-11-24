package com.easyroute.ui.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.easyroute.R;
import com.easyroute.content.Preference;
import com.inrix.sdk.model.Incident.IncidentType;

import java.util.ArrayList;
import java.util.List;

import io.codetail.animation.ViewAnimationUtils;
/**
 * Created by imenekse on 06/02/17.
 */

public class OptionsActivity extends BaseActivity implements OnClickListener {

    public static final String EXTRA_REVEAL_ANIM_HORIZONTAL_POSITION = "extra.revealAnimHorizontalPosition";
    public static final String EXTRA_REVEAL_ANIM_VERTICAL_POSITION = "extra.revealAnimVerticalPosition";

    private ViewGroup vgRoot;
    private ImageButton ibClose;
    private ImageView ivAccident;
    private ImageView ivCongestion;
    private ImageView ivPolice;
    private ImageView ivRoadClosure;
    private ImageView ivConstruction;
    private ImageView ivTrafficCamera;
    private ImageView ivHazard;
    private ImageView ivEvent;
    private ImageView ivParking;

    private List<View> mAllIncidentTypesViews;
    private List<IncidentType> mActivatedIncidentTypes;
    private int mRevealAnimHorizontalPosition;
    private int mRevealAnimVerticalPosition;
    private boolean mIsSelectedIndidentTypesChanged;

    @Override
    public int getLayoutId() {
        return R.layout.activity_options;
    }

    @Override
    public void initViews() {
        vgRoot = (ViewGroup) findViewById(R.id.root);
        ibClose = (ImageButton) findViewById(R.id.ibClose);
        ivAccident = (ImageView) findViewById(R.id.ivAccident);
        ivCongestion = (ImageView) findViewById(R.id.ivCongestion);
        ivPolice = (ImageView) findViewById(R.id.ivPolice);
        ivRoadClosure = (ImageView) findViewById(R.id.ivRoadClosure);
        ivConstruction = (ImageView) findViewById(R.id.ivConstruction);
        ivTrafficCamera = (ImageView) findViewById(R.id.ivTrafficCamera);
        ivHazard = (ImageView) findViewById(R.id.ivHazard);
        ivEvent = (ImageView) findViewById(R.id.ivEvent);
        ivParking = (ImageView) findViewById(R.id.ivParking);
    }

    @Override
    public void defineObjects(Bundle state) {
        mRevealAnimHorizontalPosition = getIntent().getIntExtra(EXTRA_REVEAL_ANIM_HORIZONTAL_POSITION, 0);
        mRevealAnimVerticalPosition = getIntent().getIntExtra(EXTRA_REVEAL_ANIM_VERTICAL_POSITION, 0);
        mActivatedIncidentTypes = Preference.getInstance(context).getActivatedIncidentTypes();
        mAllIncidentTypesViews = new ArrayList<>();
        mAllIncidentTypesViews.add(ivAccident);
        mAllIncidentTypesViews.add(ivCongestion);
        mAllIncidentTypesViews.add(ivPolice);
        mAllIncidentTypesViews.add(ivRoadClosure);
        mAllIncidentTypesViews.add(ivConstruction);
        mAllIncidentTypesViews.add(ivHazard);
        mAllIncidentTypesViews.add(ivEvent);
        //        mAllIncidentTypesViews.add(ivTrafficCamera);
        //        mAllIncidentTypesViews.add(ivParking);
    }

    @Override
    public void bindEvents() {
        ibClose.setOnClickListener(this);
        ivAccident.setOnClickListener(mIncidentTypeClickListener);
        ivCongestion.setOnClickListener(mIncidentTypeClickListener);
        ivPolice.setOnClickListener(mIncidentTypeClickListener);
        ivRoadClosure.setOnClickListener(mIncidentTypeClickListener);
        ivConstruction.setOnClickListener(mIncidentTypeClickListener);
        ivTrafficCamera.setOnClickListener(mIncidentTypeClickListener);
        ivHazard.setOnClickListener(mIncidentTypeClickListener);
        ivEvent.setOnClickListener(mIncidentTypeClickListener);
        ivParking.setOnClickListener(mIncidentTypeClickListener);
    }

    @Override
    public void setProperties() {
        vgRoot.setVisibility(View.INVISIBLE);
        ivAccident.setTag(IncidentType.ACCIDENT);
        ivCongestion.setTag(IncidentType.CONGESTION);
        ivPolice.setTag(IncidentType.POLICE);
        ivRoadClosure.setTag(IncidentType.ROAD_CLOSURE);
        ivConstruction.setTag(IncidentType.CONSTRUCTION);
        ivHazard.setTag(IncidentType.HAZARD);
        ivEvent.setTag(IncidentType.EVENT);
        ivAccident.setAlpha(0.5f);
        ivCongestion.setAlpha(0.5f);
        ivPolice.setAlpha(0.5f);
        ivRoadClosure.setAlpha(0.5f);
        ivConstruction.setAlpha(0.5f);
        ivHazard.setAlpha(0.5f);
        ivEvent.setAlpha(0.5f);
        for (IncidentType activatedIncidentType : mActivatedIncidentTypes) {
            for (View view : mAllIncidentTypesViews) {
                IncidentType viewIncidentType = (IncidentType) view.getTag();
                if (viewIncidentType == activatedIncidentType) {
                    view.setAlpha(1);
                    break;
                }
            }
        }
        //        ivTrafficCamera.setTag(IncidentType.);
        //        ivParking.setTag(IncidentType.);
    }

    @Override
    public void onLayoutCreate() {
        startRevealAnim(true);
    }

    @Override
    public void onBackPressed() {
        startRevealAnim(false);
    }

    @Override
    public void onClick(View v) {
        if (ibClose == v) {
            startRevealAnim(false);
        }
    }

    public void saveActivatedIncidentTypes() {
        ArrayList<IncidentType> list = new ArrayList<>();
        for (View view : mAllIncidentTypesViews) {
            float alpha = view.getAlpha();
            if (alpha == 1) {
                IncidentType incidentType = (IncidentType) view.getTag();
                list.add(incidentType);
            }
        }
        Preference.getInstance(context).setActivatedIncidentTypes(list);
    }

    private void startRevealAnim(boolean isOpening) {
        int cx = mRevealAnimHorizontalPosition;
        int cy = mRevealAnimVerticalPosition;
        float radius = (float) Math.hypot(cx, cy);
        Animator anim;
        if (isOpening) {
            anim = ViewAnimationUtils.createCircularReveal(vgRoot, cx, cy, 0, radius);
            vgRoot.setVisibility(View.VISIBLE);
        } else {
            anim = ViewAnimationUtils.createCircularReveal(vgRoot, cx, cy, radius, 0);
            anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    vgRoot.setVisibility(View.INVISIBLE);
                    if (mIsSelectedIndidentTypesChanged) {
                        saveActivatedIncidentTypes();
                        setResult(RESULT_OK);
                    } else {
                        setResult(RESULT_CANCELED);
                    }
                    finish();
                }
            });
        }
        anim.start();
    }

    private OnClickListener mIncidentTypeClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            float alpha = v.getAlpha();
            mIsSelectedIndidentTypesChanged = true;
            if (alpha == 1) {
                v.animate().alpha(0.5f).setDuration(200);
            } else {
                v.animate().alpha(1f).setDuration(200);
            }
        }
    };
}
