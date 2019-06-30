package com.example.nomik.boardgamemanager;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.AdaptiveIconDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.xmlpull.v1.XmlPullParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

import noman.googleplaces.NRPlaces;
import noman.googleplaces.Place;
import noman.googleplaces.PlaceType;
import noman.googleplaces.PlacesException;
import noman.googleplaces.PlacesListener;

import static android.support.constraint.Constraints.TAG;
import static android.support.v4.content.ContextCompat.getSystemService;


public class FragmentMap extends Fragment implements OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener,  GoogleMap.OnMarkerClickListener {
    private ViewGroup rootView;
    private GoogleApiClient mGoogleApiClient;
    //private static final LatLng DEFAULT_LOCATION = new LatLng(35.050607, 126.722983);
    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 2002;
    private static final int UPDATE_INTERVAL_MS = 15000;
    private static final int FASTEST_UPDATE_INTERVAL_MS = 15000;
    private static final LatLng curPoint = new LatLng(37.619395, 127.058998); // 현재 위치 광운대
    private static final LatLng curPoint2 = new LatLng(37.619715, 127.059909); // 모임 1 비마관
    private static final LatLng curPoint3 = new LatLng(37.620760, 127.056959); // 모임 2 한울관
    private static final LatLng curPoint4 = new LatLng(37.619808, 127.057560); // 모임 3 문화관



    final static double mLatitude = 37.513111;   //위도
    final static double mLongitude = 127.102669;  //경도
    GoogleMap.OnMarkerClickListener markerClickListener;


    private GoogleMap map;
    private MapView mapView = null;
    private GoogleApiClient googleApiClient = null;
    private Marker currentMarker = null;
    private MapFragment mapFragment;
    MarkerOptions myLocationMaker;

    List<Marker> previous_marker = null;
    private final static int MAXENTRIES = 5;
    private String[] LikelyPlaceNames = null;
    private String[] LikelyAddresses = null;
    private String[] LikelyAttributions = null;
    private LatLng[] LikelyLatLngs = null;
    private AdaptiveIconDrawable inflater;
    private Context context;
    TextView textView;
    Button button;


    public FragmentMap() {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //AutoPermissions.Companion.parsePermissios(this,requestCode,permissions,this);
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = (View) inflater.inflate(R.layout.fragment_map, container, false);
        mapView = (MapView) view.findViewById(R.id.map);
        mapView.getMapAsync(this);
        context = container.getContext();

        return view;


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d("Map", "지도 준비됨");
        map = googleMap;
        map.getUiSettings().setZoomControlsEnabled(true);
        map.setMyLocationEnabled(true);
        map.setOnMyLocationButtonClickListener(this);
        map.setOnMyLocationClickListener(this);

        // 맵 클릭 리스너 설정
        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(final LatLng latLng) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                LayoutInflater inflater = getLayoutInflater();
                final View view = inflater.inflate(R.layout.info, null);
                builder.setView(view);
                final Button button_submit = (Button) view.findViewById(R.id.button1);
                final EditText editText1 = (EditText) view.findViewById(R.id.edittext1);
                final EditText editText2 = (EditText) view.findViewById(R.id.edittext2);
                final EditText editText3 = (EditText) view.findViewById(R.id.edittext3);


                final AlertDialog dialog = builder.create();
                button_submit.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        String string_editText1 = editText1.getText().toString();
                        String string_editText2 = editText2.getText().toString();
                        String string_editText3 = editText3.getText().toString();
                        //Toast.makeText(context, string_placeTitle+"\n"+string_placeDesc,Toast.LENGTH_SHORT).show();


                        MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.position(latLng);
                        markerOptions.title(string_editText1);
                        markerOptions.snippet("인원 수 :"+string_editText2+", 구성원 :"+string_editText3);
                        markerOptions.draggable(true);
                        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));

                        map.addMarker(markerOptions);

                        dialog.dismiss();
                    }
                });

                dialog.show();

            }
        });

        // infoWindwow 클릭 리스너 설정
        map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(final Marker marker) {
                // 마커 정보 가져오기
                String title = marker.getTitle();
                String snippet = marker.getSnippet();
                // snippet 자르기 ", "

                LayoutInflater inflater = getLayoutInflater();
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                final View view = inflater.inflate(R.layout.info, null);
                builder.setView(view);
                final Button button_submit = (Button) view.findViewById(R.id.button1);
                final Button button_delete = (Button) view.findViewById(R.id.button2);
                final EditText editText1 = (EditText) view.findViewById(R.id.edittext1);
                final EditText editText2 = (EditText) view.findViewById(R.id.edittext2);
                final EditText editText3 = (EditText) view.findViewById(R.id.edittext3);
                editText1.setText(title);
                //editText2.setText(words[0]);
                //editText3.setText(words[1]);

                final AlertDialog dialog = builder.create();
                button_submit.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {

                        String string_editText1 = editText1.getText().toString();
                        String string_editText2 = editText2.getText().toString();
                        String string_editText3 = editText3.getText().toString();
                        //Toast.makeText(context, string_placeTitle+"\n"+string_placeDesc,Toast.LENGTH_SHORT).show();

                        marker.setTitle(string_editText1);
                        marker.setSnippet("인원 수 :"+string_editText2+", 구성원 :"+string_editText3);

                        dialog.dismiss();
                        marker.showInfoWindow();
                    }
                });
                button_delete.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        marker.remove();
                    }
                });

                dialog.show();
            }
        });
        map.addMarker(new MarkerOptions().position(curPoint).title("내위치")).showInfoWindow();
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(curPoint, 15));
        oneMarker();
        twoMarker();
        threeMarker();
        //map.addMarker(new MarkerOptions().position(curPoint2).title("모임1")).showInfoWindow();
        //googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(curPoint2 , 15));
        //map.addMarker(new MarkerOptions().position(curPoint3).title("모임2")).showInfoWindow();
        //map.addMarker(new MarkerOptions().position(curPoint2).title("모임1"));
        //Cue.showInfoWindow();
        //map.setOnInfoWindowClickListener(Cue);
        //map.setOnMapClickListener(this);
        //googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(curPoint2, 15));
        //showPlaceInformation(curPoint);
        //map.addMarker(new MarkerOptions().position(curPoint3).title("모임2").snippet("여기는 모임2입니다"));
        //MapsInitializer.initialize(this.getActivity());
        //CameraUpdate cameraUpdate= CameraUpdateFactory.newLatLngZoom(new LatLng(37.619467,127.058945), 14);
        //googleMap.animateCamera(cameraUpdate);
        //googleMap.addMarker(new MarkerOptions().position(new LatLng(37.619467,127.058945)).title("광운대"));
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
    }

    @Override
    public boolean onMyLocationButtonClick() {
        //startLocationService();
        //CameraPosition cameraPosition = new CameraPosition.Builder().target(MOUNTAIN_VIEW);
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(curPoint)      // Sets the center of the map to Mountain View
                .zoom(17)                   // Sets the zoom
                .bearing(360)                // Sets the orientation of the camera to east
                .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                .build();                   // Creates a CameraPosition from the builder
        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        return false;
    }



    public void oneMarker() {

        LatLng seoul = new LatLng(37.619715, 127.059909);

        MarkerOptions makerOptions = new MarkerOptions();
        makerOptions
                .position(seoul)
                .title("모임1.")
                .snippet("4명, 유동국")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                .alpha(0.5f);


        map.addMarker(makerOptions).showInfoWindow(); //.showInfoWindow();
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(seoul, 16));


    }
    public void twoMarker() {
        LatLng seoul = new LatLng(37.620760, 127.056959);



        MarkerOptions makerOptions = new MarkerOptions();
        makerOptions
                .position(seoul)
                .title("모임2.")
                .snippet("참여인원:2명")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                .alpha(0.5f);


        map.addMarker(makerOptions).showInfoWindow(); //.showInfoWindow();
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(seoul, 16));

    }
    public void threeMarker() {
        LatLng seoul = new LatLng(37.619808, 127.057560);

        MarkerOptions makerOptions = new MarkerOptions();
        makerOptions
                .position(seoul)
                .title("모임2.")
                .snippet("참여인원:2명")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                .alpha(0.5f);


        map.addMarker(makerOptions).showInfoWindow(); //.showInfoWindow();
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(seoul, 16));

    }



        @Override
        public void onDestroyView () {
            super.onDestroyView();
        }

        @Override
        public void onStart () {
            super.onStart();
            mapView.onStart();
        }

        @Override
        public void onStop () {
            super.onStop();
            mapView.onStop();

            if (googleApiClient != null && googleApiClient.isConnected())
                googleApiClient.disconnect();
        }

        @Override
        public void onSaveInstanceState (Bundle outState){
            super.onSaveInstanceState(outState);
            mapView.onSaveInstanceState(outState);
        }

        @Override
        public void onResume () {
            super.onResume();
            mapView.onResume();
            if (map != null)
                map.setMyLocationEnabled(false);

        }

        @Override
        public void onPause () {
            super.onPause();
            mapView.onPause();

            if (googleApiClient != null && googleApiClient.isConnected()) {
                LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, (com.google.android.gms.location.LocationListener) this);
                googleApiClient.disconnect();
            }
        }

        @Override
        public void onLowMemory () {
            super.onLowMemory();
            mapView.onLowMemory();
        }

        @Override
        public void onDestroy () {
            super.onDestroy();
            mapView.onDestroy();
        }

        @Override
        public void onActivityCreated (@Nullable Bundle savedInstanceState){
            super.onActivityCreated(savedInstanceState);

            //액티비티가 처음 생성될 때 실행되는 함수
            MapsInitializer.initialize(getActivity().getApplicationContext());

            if (mapView != null) {
                mapView.onCreate(savedInstanceState);
            }
        }

        private Location getLastKnownLocation (String gpsProvider){
            return null;
        }

        private Object getSystemService (String locationService){
            return this;
        }


        public void onProviderDisabled (String provider){
        }
        public void onProviderEnabled (String provider){
        }
        public void onStatusChanged (String provider,int status, Bundle extras){
        }
        private void showCurrentLocation (Double latitude, Double longitude)
        {
            LatLng curPoint = new LatLng(latitude, longitude);
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(curPoint, 15));
            showMyLocationMaker(curPoint);
        }

        private void showMyLocationMaker (LatLng curPoint){
            if (myLocationMaker == null) {
                myLocationMaker = new MarkerOptions();
                myLocationMaker.position(curPoint);
                myLocationMaker.title("내 위치\n");
                myLocationMaker.snippet("GPS로 확인한 위치");
                //myLocationMaker.icon(BitmapDescriptorFactory.fromResource(R.drawable.mylocation));
                map.addMarker(myLocationMaker);
                //map.addMarker(new MarkerOptions().position(curPoint).title("내위치"));

            } else {
                myLocationMaker.position(curPoint);
            }
        }

        @Override
        public boolean onMarkerClick (Marker marker){
            return true;
        }


    }



