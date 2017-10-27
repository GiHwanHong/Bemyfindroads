package com.example.gihwan.bemyfindroads;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.kakao.kakaolink.KakaoLink;
import com.kakao.kakaolink.KakaoTalkLinkMessageBuilder;
import com.kakao.util.KakaoParameterException;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


/**
 * Created by user on 2017-05-03.
 */

public class ChooseActivity extends Activity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    //디폴트 위치, Seoul
    private static final LatLng DEFAULT_LOCATION = new LatLng(37.56, 126.97);
    private static final String TAG = "GoogleMap";
    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 2002;
    private static final int UPDATE_INTERVAL_MS = 1000;  // 1초
    private static final int FASTEST_UPDATE_INTERVAL_MS = 1000; // 1초
    private GoogleApiClient mGoogleApiClient = null;
    private GoogleMap mGoogleMap = null;
    private Marker currentMarker = null;
    private ChooseActivity mActivity;
    public String markerTitle;

    public KakaoLink kakaolink; // 카카오톡 메신저를 사용하기 위함
    final String stringImg = "http://k.kakaocdn.net/14/dn/btqch2ewVOS/1evWn4hcFAt9vEVq6sKIdk/o.jpg"; // 메시지와 함께 같이 전송되는 이미지

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose);

        mActivity = this;
        final MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);  // 맵에 뿌리는 것

        try {
            kakaolink = KakaoLink.getKakaoLink(ChooseActivity.this);
            mapFragment.getMapAsync(this);
        } catch (Exception e) {
            Log.e("KaKao_Error : ", e.getMessage());
        }
    }

    @Override
    public void onPause() {
        //위치 업데이트 중지
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
        super.onPause();
    }

    @Override
    protected void onStop() {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.unregisterConnectionCallbacks(this);
            mGoogleApiClient.unregisterConnectionFailedListener(this);

            if (mGoogleApiClient.isConnected()) {
                LocationServices.FusedLocationApi
                        .removeLocationUpdates(mGoogleApiClient, this);
                mGoogleApiClient.disconnect();
            }

        }
        super.onDestroy();
    }

    @Override
    public void onMapReady(GoogleMap map) {
        Log.d(TAG, "onMapReady");
        mGoogleMap = map;

        mGoogleMap.getUiSettings().setCompassEnabled(true);
        mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(15));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //API 23 이상이면 런타임 퍼미션 처리 필요

            int hasFineLocationPermission = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION);

            if (hasFineLocationPermission == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions(mActivity,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            } else {
                if (mGoogleApiClient == null) {
                    buildGoogleApiClient();
                }

                if (ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {

                    mGoogleMap.setMyLocationEnabled(true);

                }
            }
        } else {

            if (mGoogleApiClient == null) {
                buildGoogleApiClient();
            }
            mGoogleMap.setMyLocationEnabled(true);
        }

    }


    @Override
    public void onLocationChanged(Location location) {

        Log.d(TAG, "onLocationChanged");
        markerTitle = getCurrentAddress(location);
        String markerSnippet = "위도:" + String.valueOf(location.getLatitude())
                + " 경도:" + String.valueOf(location.getLongitude());

        //현재 위치에 마커 생성
        setCurrentLocation(location, markerTitle, markerSnippet);
    }

    protected synchronized void buildGoogleApiClient() {

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                         .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();

    }
    public void Btn_Choose(View v) {
        switch (v.getId()) {
            case R.id.naviBus:
                Intent Gobus = new Intent(getApplicationContext(), BusActivity.class);
                startActivity(Gobus);
                break;
            case R.id.naviStart:
                final KakaoTalkLinkMessageBuilder kakaoTalkLinkMessageBuilder
                        = kakaolink.createKakaoTalkLinkMessageBuilder();
                Intent Gonavi = new Intent(getApplicationContext(), NaviActivity.class);
                try {
                    kakaoTalkLinkMessageBuilder
                            .addText("현재위치는 \n -> '" + markerTitle + "' 입니다") //링크 객체에 현재 위치 정보가 담긴 문자 넣기
                            .addImage(stringImg, 155, 135)
                            .addAppButton("위치를 지도로 보기");
                } catch (KakaoParameterException e) {
                    e.printStackTrace();
                }
                try {
                    kakaolink.sendMessage(kakaoTalkLinkMessageBuilder, this);   // 메시지 전송
                } catch (KakaoParameterException e) {
                    e.printStackTrace();
                }
                startActivity(Gonavi);
                break;
            case R.id.Administrator:  //회원정보 확인
                Intent Administor = new Intent(getApplicationContext(), AdministratorActivity.class);
                startActivity(Administor);
                break;

        }
    }

    @Override
    public void onConnected(Bundle connectionHint) {

        Log.d(TAG, "onConnected");

        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(UPDATE_INTERVAL_MS);
        locationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_MS);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {

                LocationServices.FusedLocationApi
                        .requestLocationUpdates(mGoogleApiClient, locationRequest, this);

            }
        } else {
            Log.d(TAG, "onConnected : call FusedLocationApi");
            LocationServices.FusedLocationApi
                    .requestLocationUpdates(mGoogleApiClient, locationRequest, this);

            mGoogleMap.getUiSettings().setCompassEnabled(true);
            //mGoogleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        }
    }


    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Location location = null;
        location.setLatitude(DEFAULT_LOCATION.latitude);
        location.setLongitude(DEFAULT_LOCATION.longitude);

        setCurrentLocation(location, "위치정보 가져올 수 없음",
                "위치 퍼미션과 GPS 활성 요부 확인하세요");
    }

    @Override
    public void onConnectionSuspended(int cause) {
        if (cause == CAUSE_NETWORK_LOST)
            Log.e(TAG, "onConnectionSuspended(): Google Play services " +
                    "connection lost.  Cause: network lost.");
        else if (cause == CAUSE_SERVICE_DISCONNECTED)
            Log.e(TAG, "onConnectionSuspended():  Google Play services " +
                    "connection lost.  Cause: service disconnected");
    }


    public String getCurrentAddress(Location location) {

        //지오코더... GPS를 주소로 변환
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        List<Address> addresses;

        try {

            addresses = geocoder.getFromLocation(
                    location.getLatitude(),
                    location.getLongitude(),
                    1);
        } catch (IOException ioException) {
            //네트워크 문제
            Toast.makeText(this, "지오코더 서비스 사용불가", Toast.LENGTH_LONG).show();
            return "지오코더 서비스 사용불가";
        } catch (IllegalArgumentException illegalArgumentException) {
            Toast.makeText(this, "잘못된 GPS 좌표", Toast.LENGTH_LONG).show();
            return "잘못된 GPS 좌표";

        }

        if (addresses == null || addresses.size() == 0) {
            Toast.makeText(this, "주소 미발견", Toast.LENGTH_LONG).show();
            return "주소 미발견";

        } else {
            Address address = addresses.get(0);
            return address.getAddressLine(0).toString();
        }

    }

    public void setCurrentLocation(final Location location, String markerTitle, String markerSnippet) {

        if (currentMarker != null) currentMarker.remove();

        if (location != null) {
            LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());

            //마커를 원하는 이미지로 변경해줘야함
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(currentLocation);
            markerOptions.title(markerTitle);
            markerOptions.snippet(markerSnippet);
            markerOptions.draggable(true);
            markerOptions.icon(BitmapDescriptorFactory
                    .defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
            currentMarker = mGoogleMap.addMarker(markerOptions);

            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));

            //오른쪽 상단에 gps_icon을 클릭하게 되면 밑의 코드가 실행 된다
            mGoogleMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener()
            {
                @Override
                public boolean onMyLocationButtonClick()
                {
                    Log.e("Inside","Click part");
                    LatLng ll = new LatLng(location.getLatitude(),location.getLongitude());
                    CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll, 20);
                    mGoogleMap.animateCamera(update);
                    return false;
                }
            });
            return;
        }

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(DEFAULT_LOCATION);
        markerOptions.title(markerTitle);
        markerOptions.snippet(markerSnippet);
        markerOptions.draggable(true);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        currentMarker = mGoogleMap.addMarker(markerOptions);

        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(DEFAULT_LOCATION));
    }
}
