package com.easyroute.ui.activity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Address;
import android.location.Location;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;

import com.easyroute.R;
import com.easyroute.content.Preference;
import com.easyroute.content.model.Place;
import com.easyroute.ui.view.GoogleMapView;
import com.easyroute.ui.view.SavedPlacesLayout;
import com.easyroute.ui.view.SavedPlacesLayout.OnSavedPlacesItemClickListener;
import com.easyroute.ui.view.SearchedPlacesLayout;
import com.easyroute.ui.view.SearchedPlacesLayout.OnSearchedPlaceSelectListener;
import com.easyroute.utility.Keyboard;
import com.easyroute.utility.MapUtils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.data.DataBufferUtils;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.AutocompletePredictionBuffer;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@SuppressWarnings({"MissingPermission"})
public class PlaceAutoCompleteActivity extends BaseActivity implements OnClickListener {

    public static final String EXTRA_PLACE_DATA = "extra.placeData";
    public static final String EXTRA_PREVIOUSLY_SELECTED_PLACE = "extra.previouslySelectedPlace";
    public static final String EXTRA_IS_CURRENT_LOCATION_SELECTABLE = "extra.isCurrentLocationSelectable";
    public static final String EXTRA_IS_SETTING_HOME_PLACE = "extra.isSettingHomePlace";
    public static final String EXTRA_IS_SETTING_WORK_PLACE = "extra.isSettingWorkPlace";

    private final int REQUEST_SPEECH_TO_TEXT = 1;
    private final int REQUEST_ENABLE_LOCATION_SERVICES = 2;
    private final int REQUEST_SET_HOME_PLACE = 3;
    private final int REQUEST_SET_WORK_PLACE = 4;
    private final LatLngBounds DEFAULT_LAT_LNG_BOUNDS = new LatLngBounds(new LatLng(41.0151, 28.9795), new LatLng(41.0151, 28.9795));

    private SavedPlacesLayout savedPlacesLayout;
    private SearchedPlacesLayout searchedPlacesLayout;
    private ImageButton ibBack;
    private ImageButton ibClear;
    private ImageButton ibVoice;
    private ImageButton ibMyLocation;
    private EditText etSearch;

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Location mCurrentLocation;
    private LatLngBounds mLatLngBounds;
    private Address mAddress;
    private String mAddressDisplayName;
    private Place mHomePlace;
    private Place mWorkPlace;
    private List<Place> mRecentPlaces;
    private PendingResult<AutocompletePredictionBuffer> mAutocompletePredictionResult;
    private PendingResult<PlaceBuffer> mPlaceDetailResult;
    private Place mPreviouslySelectedPlace;
    private boolean mIsLocationEnabledByAppForSelectMyLocation;
    private boolean mIsSettingHomePlace;
    private boolean mIsSettingWorkPlace;
    private boolean mIsCurrentLocationSelectable;

    @Override
    public int getLayoutId() {
        return R.layout.activity_place_auto_complete;
    }

    @Override
    public void initViews() {
        savedPlacesLayout = (SavedPlacesLayout) findViewById(R.id.savedPlaceLayout);
        searchedPlacesLayout = (SearchedPlacesLayout) findViewById(R.id.searchedPlacesLayout);
        ibBack = (ImageButton) findViewById(R.id.ibBack);
        ibClear = (ImageButton) findViewById(R.id.ibClear);
        ibVoice = (ImageButton) findViewById(R.id.ibVoice);
        ibMyLocation = (ImageButton) findViewById(R.id.ibMyLocation);
        etSearch = (EditText) findViewById(R.id.etSearch);
    }

    @Override
    public void defineObjects(Bundle state) {
        mGoogleApiClient = new GoogleApiClient.Builder(context).enableAutoManage(this, 0, mGoogleConnectionFailedListener).addApi(Places.GEO_DATA_API).addApi(LocationServices.API).addConnectionCallbacks(mGoogleConnectionCallbacks).build();
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLatLngBounds = DEFAULT_LAT_LNG_BOUNDS;
        mHomePlace = Preference.getInstance(context).getHomePlace();
        mWorkPlace = Preference.getInstance(context).getWorkPlace();
        mRecentPlaces = Preference.getInstance(context).getRecentPlaces();
        mPreviouslySelectedPlace = (Place) getIntent().getSerializableExtra(EXTRA_PREVIOUSLY_SELECTED_PLACE);
        mCurrentLocation = MapUtils.getLastKnownLocation(context);
        mIsSettingHomePlace = getIntent().getBooleanExtra(EXTRA_IS_SETTING_HOME_PLACE, false);
        mIsSettingWorkPlace = getIntent().getBooleanExtra(EXTRA_IS_SETTING_WORK_PLACE, false);
        mIsCurrentLocationSelectable = getIntent().getBooleanExtra(EXTRA_IS_CURRENT_LOCATION_SELECTABLE, false);
    }

    @Override
    public void bindEvents() {
        ibBack.setOnClickListener(this);
        ibClear.setOnClickListener(this);
        ibVoice.setOnClickListener(this);
        ibMyLocation.setOnClickListener(this);
        etSearch.addTextChangedListener(mSearchTextWatcher);
        savedPlacesLayout.setOnSavedPlacesItemClickListener(mOnSavedPlacesItemClickListener);
        searchedPlacesLayout.setOnSearchedPlaceSelectListener(mSearchedPlacesLayoutPredictionSelectListener);
    }

    @Override
    public void setProperties() {
        setResult(RESULT_CANCELED);
        if (!mIsSettingHomePlace && !mIsSettingWorkPlace) {
            savedPlacesLayout.setHomePlace(mHomePlace);
            savedPlacesLayout.setWorkPlace(mWorkPlace);
            savedPlacesLayout.setRecentPlaces(mRecentPlaces);
        } else {
            if (mIsSettingHomePlace) {
                etSearch.setHint(R.string.place_auto_complete_activity_set_home_address_hint);
                ibBack.setImageResource(R.drawable.ic_home_gray);
            } else if (mIsSettingWorkPlace) {
                etSearch.setHint(R.string.place_auto_complete_activity_set_work_address_hint);
                ibBack.setImageResource(R.drawable.ic_workplace_gray);
            }
            ibMyLocation.setVisibility(View.GONE);
            savedPlacesLayout.setVisibility(View.GONE);
        }
        if (!mIsCurrentLocationSelectable) {
            ibMyLocation.setVisibility(View.GONE);
        }
    }

    @Override
    public void onLayoutCreate() {
        if (mGoogleApiClient == null) {
            snackbar(R.string.google_api_client_connection_failed_alert_message, Snackbar.LENGTH_INDEFINITE);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient.isConnected()) {
            startLocationUpdates();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            stopLocationUpdates();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_SPEECH_TO_TEXT) {
            if (resultCode == RESULT_OK && data != null) {
                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                etSearch.setText(result.get(0));
                etSearch.setSelection(etSearch.length());
            }
        } else if (requestCode == REQUEST_ENABLE_LOCATION_SERVICES) {
            if (resultCode == RESULT_OK) {
                snackbar(R.string.current_location_is_beign_taken_snackbar_message);
            }
        } else if (requestCode == REQUEST_SET_HOME_PLACE) {
            if (resultCode == RESULT_OK) {
                Place place = (Place) data.getSerializableExtra(EXTRA_PLACE_DATA);
                Preference.getInstance(context).setHomePlace(place);
                mHomePlace = place;
                savedPlacesLayout.setHomePlace(mHomePlace);
            }
        } else if (requestCode == REQUEST_SET_WORK_PLACE) {
            if (resultCode == RESULT_OK) {
                Place place = (Place) data.getSerializableExtra(EXTRA_PLACE_DATA);
                Preference.getInstance(context).setWorkPlace(place);
                mWorkPlace = place;
                savedPlacesLayout.setWorkPlace(mWorkPlace);
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v == ibBack) {
            finish();
        } else if (v == ibClear) {
            etSearch.setText("");
        } else if (v == ibMyLocation) {
            if (mCurrentLocation == null) {
                mIsLocationEnabledByAppForSelectMyLocation = true;
                checkLocationSettings();
            } else {
                pickCurrentLocation();
            }
        } else if (v == ibVoice) {
            promptSpeechInput();
        }
    }

    private void pick(Place place) {
        if (!place.isCurrentLocation() && !mIsSettingHomePlace && !mIsSettingWorkPlace) {
            Preference.getInstance(context).addRecentPlace(place);
        }
        Intent intent = new Intent();
        intent.putExtra(EXTRA_PLACE_DATA, place);
        setResult(RESULT_OK, intent);
        finish();
    }

    private void pickCurrentLocation() {
        Place place = new Place();
        place.setCurrentLocation(true);
        pick(place);
    }

    private void search() {
        if (mAutocompletePredictionResult != null) {
            mAutocompletePredictionResult.cancel();
            mAutocompletePredictionResult = null;
        }
        String query = etSearch.getText().toString();
        AutocompleteFilter typeFilter = new AutocompleteFilter.Builder().setTypeFilter(AutocompleteFilter.TYPE_FILTER_NONE).setCountry("TR").build();
        mAutocompletePredictionResult = Places.GeoDataApi.getAutocompletePredictions(mGoogleApiClient, query, mLatLngBounds, typeFilter);
        mAutocompletePredictionResult.setResultCallback(new ResultCallback<AutocompletePredictionBuffer>() {
            @Override
            public void onResult(@NonNull AutocompletePredictionBuffer autocompletePredictions) {
                Status status = autocompletePredictions.getStatus();
                if (!status.isSuccess()) {
                    autocompletePredictions.release();
                    searchedPlacesLayout.setVisibility(View.GONE);
                    searchedPlacesLayout.setAutoCompletePredictions(null);
                } else {
                    ArrayList<AutocompletePrediction> predictions = DataBufferUtils.freezeAndClose(autocompletePredictions);
                    if (predictions == null || predictions.isEmpty()) {
                        searchedPlacesLayout.setVisibility(View.GONE);
                    } else {
                        searchedPlacesLayout.setVisibility(View.VISIBLE);
                        searchedPlacesLayout.setAutoCompletePredictions(predictions);
                    }
                }
            }
        });
    }

    private void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, mLocationListener);
    }

    private void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, mLocationListener);
    }

    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.place_auto_complete_activity_speech_to_text_dialog_title));
        try {
            startActivityForResult(intent, REQUEST_SPEECH_TO_TEXT);
        } catch (ActivityNotFoundException a) {
            snackbar(R.string.place_auto_complete_activity_speech_to_text_not_found_alert_message);
        }
    }

    private void checkLocationSettings() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest);
        builder.setAlwaysShow(true);
        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                Status status = result.getStatus();
                int statusCode = status.getStatusCode();
                if (statusCode == LocationSettingsStatusCodes.RESOLUTION_REQUIRED) {
                    try {
                        status.startResolutionForResult(activity, REQUEST_ENABLE_LOCATION_SERVICES);
                    } catch (IntentSender.SendIntentException e) {}
                } else if (statusCode == LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE) {
                    snackbar(R.string.enable_location_services_alert_message);
                }
            }
        });
    }

    private Place convertToPlace(AutocompletePrediction prediction, com.google.android.gms.location.places.Place googlePlace) {
        Place place = new Place();
        place.setName(googlePlace.getName().toString());
        place.setAddress(googlePlace.getAddress().toString());
        place.setLatLng(googlePlace.getLatLng());
        place.setId(prediction.getPlaceId());
        place.setFullText(prediction.getFullText(null).toString());
        place.setPrimaryText(prediction.getPrimaryText(null).toString());
        place.setSecondaryText(prediction.getSecondaryText(null).toString());
        return place;
    }

    private TextWatcher mSearchTextWatcher = new TextWatcher() {

        @Override
        public void afterTextChanged(Editable s) {
            String str = s.toString().trim();
            if (str.isEmpty()) {
                ibClear.setVisibility(View.GONE);
                ibVoice.setVisibility(View.VISIBLE);
                if (!mIsSettingHomePlace && !mIsSettingWorkPlace) {
                    if (mIsCurrentLocationSelectable) {
                        ibMyLocation.setVisibility(View.VISIBLE);
                    }
                    savedPlacesLayout.setVisibility(View.VISIBLE);
                }
            } else {
                ibClear.setVisibility(View.VISIBLE);
                ibVoice.setVisibility(View.GONE);
                if (!mIsSettingHomePlace && !mIsSettingWorkPlace) {
                    if (mIsCurrentLocationSelectable) {
                        ibMyLocation.setVisibility(View.GONE);
                    }
                    savedPlacesLayout.setVisibility(View.GONE);
                }
            }
            search();
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {}
    };

    private OnSavedPlacesItemClickListener mOnSavedPlacesItemClickListener = new OnSavedPlacesItemClickListener() {
        @Override
        public void onHomePlaceSelect() {
            if (mHomePlace == null) {
                Intent intent = new Intent(context, PlaceAutoCompleteActivity.class);
                intent.putExtra(PlaceAutoCompleteActivity.EXTRA_IS_SETTING_HOME_PLACE, true);
                startActivityForResult(intent, REQUEST_SET_HOME_PLACE);
            } else {
                pick(mHomePlace);
            }
        }

        @Override
        public void onWorkPlaceSelect() {
            if (mWorkPlace == null) {
                Intent intent = new Intent(context, PlaceAutoCompleteActivity.class);
                intent.putExtra(PlaceAutoCompleteActivity.EXTRA_IS_SETTING_WORK_PLACE, true);
                startActivityForResult(intent, REQUEST_SET_WORK_PLACE);
            } else {
                pick(mWorkPlace);
            }
        }

        @Override
        public void onRecentPlaceSelect(Place recentPlace) {
            pick(recentPlace);
        }
    };

    private OnSearchedPlaceSelectListener mSearchedPlacesLayoutPredictionSelectListener = new OnSearchedPlaceSelectListener() {
        @Override
        public void onSearchedPlaceSelect(final AutocompletePrediction prediction) {
            Keyboard.hide(context);
            etSearch.setFocusable(false);
            etSearch.setFocusableInTouchMode(false);
            if (mPlaceDetailResult != null) {
                mPlaceDetailResult.cancel();
                mPlaceDetailResult = null;
            }
            mPlaceDetailResult = Places.GeoDataApi.getPlaceById(mGoogleApiClient, prediction.getPlaceId());
            mPlaceDetailResult.setResultCallback(new ResultCallback<PlaceBuffer>() {
                @Override
                public void onResult(@NonNull PlaceBuffer places) {
                    if (places.getStatus().isSuccess()) {
                        com.google.android.gms.location.places.Place googlePlace = places.get(0);
                        Place place = convertToPlace(prediction, googlePlace);
                        pick(place);
                    }
                    places.release();
                }
            });
        }
    };

    private LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            mLatLngBounds = new LatLngBounds(new LatLng(location.getLatitude(), location.getLongitude()), new LatLng(location.getLatitude(), location.getLongitude()));
            mCurrentLocation = location;
            GoogleMapView.sCurrentLocation = mCurrentLocation;
            if (mIsLocationEnabledByAppForSelectMyLocation) {
                mIsLocationEnabledByAppForSelectMyLocation = false;
                pickCurrentLocation();
            }
        }
    };

    private GoogleApiClient.ConnectionCallbacks mGoogleConnectionCallbacks = new ConnectionCallbacks() {
        @Override
        public void onConnected(@Nullable Bundle bundle) {
            startLocationUpdates();
        }

        @Override
        public void onConnectionSuspended(int i) {}
    };

    private GoogleApiClient.OnConnectionFailedListener mGoogleConnectionFailedListener = new OnConnectionFailedListener() {
        @Override
        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
            snackbar(R.string.place_auto_complete_activity_google_api_client_connection_failed_alert_message, R.string.place_auto_complete_activity_google_api_client_connection_failed_alert_retry_button, new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mGoogleApiClient != null) {
                        mGoogleApiClient.connect();
                    }
                }
            });
        }
    };
}
