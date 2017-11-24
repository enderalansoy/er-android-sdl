package com.easyroute.ui.dialog;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

import com.easyroute.R;
import com.easyroute.constant.Event;
import com.easyroute.content.Preference;
import com.easyroute.content.constant.FuelType;
import com.easyroute.content.constant.VehicleType;
import com.easyroute.content.model.VehicleInfo;
import com.easyroute.utility.Analytics;

import java.util.List;
/**
 * Created by imenekse on 06/02/17.
 */

public class VehicleInfoDialog extends BaseDialog implements OnClickListener {

    private static final String EXTRA_IS_FIRST_SHOW = "extra.isFirstShow";

    private Button btnSave;
    private Button btnLater;
    private ImageButton ibClose;
    private Spinner spFuelType;
    private Spinner spVehicleType;
    private EditText etInCityFuelConsumption;
    private EditText etOutCityFuelConsumption;

    private VehicleInfo mSavedVehicleInfo;
    private List<String> mFuelTypes;
    private List<String> mVehicleTypes;
    private ArrayAdapter<String> mFuelTypesAdapter;
    private ArrayAdapter<String> mVehicleTypesAdapter;

    private boolean mIsFirstShow;

    public static VehicleInfoDialog newInstance(boolean isCloseable) {
        VehicleInfoDialog fragment = new VehicleInfoDialog();
        Bundle args = new Bundle();
        args.putBoolean(EXTRA_IS_FIRST_SHOW, isCloseable);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getLayoutId() {
        return R.layout.dialog_vehicle_info;
    }

    @Override
    public void initViews() {
        btnSave = (Button) findViewById(R.id.btnSave);
        btnLater = (Button) findViewById(R.id.btnLater);
        ibClose = (ImageButton) findViewById(R.id.ibClose);
        spFuelType = (Spinner) findViewById(R.id.spFuelType);
        spVehicleType = (Spinner) findViewById(R.id.spVehicleType);
        etInCityFuelConsumption = (EditText) findViewById(R.id.etInCityFuelConsumption);
        etOutCityFuelConsumption = (EditText) findViewById(R.id.etOutCityFuelConsumption);
    }

    @Override
    public void defineObjects() {
        if (getArguments() != null) {
            mIsFirstShow = getArguments().getBoolean(EXTRA_IS_FIRST_SHOW, true);
        }
        mSavedVehicleInfo = Preference.getInstance(context).getVehicleInfo();
        mFuelTypes = FuelType.getAllNames(context);
        mVehicleTypes = VehicleType.getAllNames(context);
        mFuelTypesAdapter = new ArrayAdapter<>(context, R.layout.item_spinner_adapter, mFuelTypes);
        mFuelTypesAdapter.setDropDownViewResource(R.layout.item_spinner_adapter_drop_down);
        mVehicleTypesAdapter = new ArrayAdapter<>(context, R.layout.item_spinner_adapter, mVehicleTypes);
        mVehicleTypesAdapter.setDropDownViewResource(R.layout.item_spinner_adapter_drop_down);
    }

    @Override
    public void bindEvents() {
        btnSave.setOnClickListener(this);
        btnLater.setOnClickListener(this);
        ibClose.setOnClickListener(this);
    }

    @Override
    public void setProperties() {
        spFuelType.setAdapter(mFuelTypesAdapter);
        spVehicleType.setAdapter(mVehicleTypesAdapter);
        if (mSavedVehicleInfo != null) {
            etInCityFuelConsumption.setText(mSavedVehicleInfo.getInCityFuelConsumption() + "");
            etOutCityFuelConsumption.setText(mSavedVehicleInfo.getHighwayFuelConsumption() + "");
            FuelType fuelType = mSavedVehicleInfo.getFuelType();
            VehicleType vehicleType = mSavedVehicleInfo.getVehicleType();
            spFuelType.setSelection(mFuelTypes.indexOf(fuelType.getName(context)));
            spVehicleType.setSelection(mVehicleTypes.indexOf(vehicleType.getName(context)));
        } else {
            etInCityFuelConsumption.setText(R.string.vehicle_info_dialog_default_consumption_in_city);
            etOutCityFuelConsumption.setText(R.string.vehicle_info_dialog_default_consumption_out_city);
            etInCityFuelConsumption.setSelection(etInCityFuelConsumption.getText().length());
        }
        if (mIsFirstShow) {
            btnLater.setVisibility(View.VISIBLE);
            ibClose.setVisibility(View.GONE);
        }
    }

    @Override
    public void onLayoutCreate() {
        Preference.getInstance(context).setIsVehicleInfoDialogShownForFirstTime(true);
    }

    @Override
    public void onClick(View v) {
        if (btnSave == v) {
            if (validate()) {
                float inCityFuelConsumption = Float.parseFloat(etInCityFuelConsumption.getText().toString());
                float outCityFuelConsumptionStr = Float.parseFloat(etOutCityFuelConsumption.getText().toString());
                VehicleInfo vehicleInfo = new VehicleInfo();
                vehicleInfo.setInCityFuelConsumption(inCityFuelConsumption);
                vehicleInfo.setHighwayFuelConsumption(outCityFuelConsumptionStr);
                vehicleInfo.setFuelType(FuelType.values()[spFuelType.getSelectedItemPosition()]);
                vehicleInfo.setVehicleType(VehicleType.values()[spVehicleType.getSelectedItemPosition()]);
                Preference.getInstance(context).setVehicleInfo(vehicleInfo);
                Analytics.vehicleInfo(context, vehicleInfo);
                sendEvent(Event.VEHICLE_INFO_DIALOG_SAVE);
                dismiss();
            }
        } else if (ibClose == v) {
            dismiss();
        } else if (btnLater == v) {
            dismiss();
        }
    }

    private boolean validate() {
        String inCityFuelConsumptionStr = etInCityFuelConsumption.getText().toString().trim();
        String outCityFuelConsumptionStr = etOutCityFuelConsumption.getText().toString().trim();
        if (TextUtils.isEmpty(inCityFuelConsumptionStr)) {
            etInCityFuelConsumption.setError(getString(R.string.default_validation));
            return false;
        } else if (TextUtils.isEmpty(outCityFuelConsumptionStr)) {
            etOutCityFuelConsumption.setError(getString(R.string.default_validation));
            return false;
        }
        return true;
    }
}
