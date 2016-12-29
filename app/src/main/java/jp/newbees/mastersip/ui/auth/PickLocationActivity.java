package jp.newbees.mastersip.ui.auth;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.location.places.Places;

import java.util.ArrayList;

import jp.newbees.mastersip.R;
import jp.newbees.mastersip.model.SelectionItem;
import jp.newbees.mastersip.presenter.auth.GetProvincePresenter;
import jp.newbees.mastersip.ui.BaseActivity;
import jp.newbees.mastersip.ui.dialog.SelectionDialog;

/**
 * Created by ducpv on 12/14/16.
 */

public class PickLocationActivity extends BaseActivity implements GoogleApiClient.OnConnectionFailedListener,
        GetProvincePresenter.View, View.OnClickListener, SelectionDialog.OnSelectionDialogClick {
    public static final int PICK_LOCATION_REQUEST_CODE = 12;
    public static final int LOCATION_REQUEST_CODE = 13;
    public static final String PROVINCE_ITEM = "PROVINCE_ITEM";

    private CheckBox cbEnableLocation;
    private ViewGroup layoutPosition;
    private TextView txtPosition;

    private GoogleApiClient mGoogleApiClient;
    private AlertDialog dialog;

    private boolean isRequireGPS;

    private GetProvincePresenter getProvincePresenter;

    private SelectionItem provinceItem;
    private ArrayList<SelectionItem> provinceItems;

    @Override
    protected int layoutId() {
        return R.layout.activity_pick_location;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        cbEnableLocation = (CheckBox) findViewById(R.id.cb_enable_location);
        layoutPosition = (ViewGroup) findViewById(R.id.layout_position);
        txtPosition = (TextView) findViewById(R.id.txt_position);
        txtActionBarTitle = (TextView) findViewById(R.id.txt_action_bar_title);
        imgBack = (ImageView) findViewById(R.id.img_back);

        txtActionBarTitle.setText(getString(R.string.pick_location));

        layoutPosition.setOnClickListener(this);
        imgBack.setOnClickListener(this);

    }

    @Override
    protected void initVariables(Bundle savedInstanceState) {
        provinceItems = new ArrayList<>();
        String[] provinces = getResources().getStringArray(R.array.districts);
        for (int i = 0; i < provinces.length; i++) {
            SelectionItem selectionItem = new SelectionItem(i + 1, provinces[i]);
            provinceItems.add(selectionItem);
        }

        getProvincePresenter = new GetProvincePresenter(getApplicationContext(), this);

        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();

        cbEnableLocation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    checkLocationPermission();
                } else {
                    isRequireGPS = false;
                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isRequireGPS) {
            if (dialog != null) {
                dialog.dismiss();
            }
            isRequireGPS = false;
            checkLocationProviderAndOpenSettingIfNot();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_REQUEST_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                checkLocationProviderAndOpenSettingIfNot();
            }
        }
    }

    @Override
    public void onGetProvinceSuccess(SelectionItem selectionItem) {
        provinceItem = selectionItem;
        txtPosition.setText(provinceItem.getTitle());
        disMissLoading();
    }

    @Override
    public void onGetProvinceFailure(int errorCode, String errorMessage) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
        disMissLoading();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        Toast.makeText(this, connectionResult.getErrorMessage(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onClick(View view) {
        if (view == layoutPosition) {
            openSelectionDialog(getString(R.string.pick_location), provinceItems);
        }

        if (view == imgBack) {
            putDataBack();
        }
    }

    @Override
    public void onItemSelected(int position) {
        provinceItem = provinceItems.get(position);
        txtPosition.setText(provinceItem.getTitle());
    }

    @Override
    public void onBackPressed() {
        putDataBack();
    }

    private void checkLocationPermission() {
        if (mGoogleApiClient.isConnected()) {
            if (ContextCompat.checkSelfPermission(PickLocationActivity.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(PickLocationActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        LOCATION_REQUEST_CODE);
            } else {
                checkLocationProviderAndOpenSettingIfNot();
            }
        }

    }

    private void callPlaceDetectionApi() throws SecurityException {
        showLoading();

        PendingResult<PlaceLikelihoodBuffer> result = Places.PlaceDetectionApi
                .getCurrentPlace(mGoogleApiClient, null);
        result.setResultCallback(new ResultCallback<PlaceLikelihoodBuffer>() {
            @Override
            public void onResult(PlaceLikelihoodBuffer likelyPlaces) {
                /*
                if can't found any place show error message
                 */
                if (likelyPlaces.getCount() > 0) {
                    getProvincePresenter.getProvince(likelyPlaces.get(0).getPlace().getLatLng());
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.err_cant_get_location)
                            , Toast.LENGTH_SHORT).show();
                }

                likelyPlaces.release();
            }
        });
    }

    public void checkLocationProviderAndOpenSettingIfNot() {
        boolean isProviderEnabled = ((LocationManager) getSystemService(Context.LOCATION_SERVICE)).isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!isProviderEnabled) {
            // Show dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(this).setMessage(R.string.mess_enable_gps)
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent viewIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(viewIntent);
                        }
                    });
            dialog = builder.create();
            dialog.setCancelable(false);
            dialog.show();

            isRequireGPS = true;
        } else {
            callPlaceDetectionApi();
        }
    }

    private void openSelectionDialog(String title, ArrayList<SelectionItem> data) {
        SelectionDialog.openSelectionDialogFromActivity(getSupportFragmentManager(),
                data, title);
    }

    private void putDataBack() {
        if (provinceItem != null) {
            Intent intent = new Intent();
            intent.putExtra(PROVINCE_ITEM, (Parcelable) provinceItem);
            setResult(RESULT_OK, intent);
        }
        finish();
    }
}
