package com.example.nomik.boardgamemanager;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import static android.content.Context.INPUT_METHOD_SERVICE;
import static android.content.Context.TELEPHONY_SERVICE;


public class FragmentMap extends Fragment implements OnMapReadyCallback, GoogleMap.OnMyLocationClickListener, GoogleMap.OnMarkerClickListener {
    private static final LatLng basicPoint = new LatLng(37.619395, 127.058998); // 현재 위치 광운대
    private MapView mapView = null;
    private GoogleApiClient googleApiClient = null;
    private GoogleMap map;

    ArrayList<MyMarker> markers = new ArrayList<>();
    String myName;

    public class MyMarker {
        private int marker_id;
        private String title;
        private String date;
        private String detail;
        private double latitude;
        private double longitude;
        private String phoneNum;
        private ArrayList<Entry> entries = new ArrayList<>();

        public MyMarker(String title) {
            this.title = title;
        }

        public MyMarker(int marker_id, String title, String date, String detail, double latitude, double longitude, String phoneNum) {
            this.marker_id = marker_id;
            this.title = title;
            this.date = date;
            this.detail = detail;
            this.latitude = latitude;
            this.longitude = longitude;
            this.phoneNum = phoneNum;
        }

        public int getId() {
            return marker_id;
        }

        public String getTitle() {
            return title;
        }

        public String getDate() {
            return date;
        }

        public String getDetail() {
            return detail;
        }

        public double getLatitude() {
            return latitude;
        }

        public double getLongitude() {
            return longitude;
        }

        public String getPhoneNum() {
            return phoneNum;
        }

        public ArrayList<Entry> getEntries() {
            return entries;
        }

        public void addEntry(Entry entry) {
            entries.add(entry);
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public void setDetail(String detail) {
            this.detail = detail;
        }

        public void entry() {
            Entry entry = new Entry(myName, getPhoneNumber());
            if(entries.contains(entry)) {
                entries.remove(entry);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getContext(), "모임 참가가 취소되었습니다.", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                entries.add(entry);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getContext(), "모임에 참가되었습니다.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }

        @Override
        public boolean equals(@androidx.annotation.Nullable Object obj) {
            return title.equals(((MyMarker) obj).getTitle());
        }
    }

    public class Entry {
        private String name;
        private String phoneNum;

        public Entry(String name, String phoneNum) {
            this.name = name;
            this.phoneNum = phoneNum;
        }

        public String getName() {
            return name;
        }

        public String getPhoneNum() {
            return phoneNum;
        }

        @Override
        public boolean equals(@androidx.annotation.Nullable Object obj) {
            if(name.equals(((Entry)obj).getName()))
                if(phoneNum.equals(((Entry)obj).getPhoneNum()))
                    return true;
            return false;
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        myName = ((MainActivity) getActivity()).getMyName();
        mapView = view.findViewById(R.id.map);
        mapView.getMapAsync(this);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                getMapData();
            }
        });
        thread.start();
        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        //참여자든 주최자든 지도상 클릭할시 모임 생성은 가능
        map = googleMap;
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.setMyLocationEnabled(true);
        googleMap.setOnMyLocationClickListener(this);
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                MapView mapView = getView().findViewById(R.id.map);
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)mapView.getLayoutParams();
                params.weight = 100;
                mapView.setLayoutParams(params);
                ScrollView hostView = getView().findViewById(R.id.map_hostView);
                params = (LinearLayout.LayoutParams)hostView.getLayoutParams();
                params.weight = 0;
                hostView.setLayoutParams(params);
                hostView.setVisibility(View.GONE);
                ScrollView entryView = getView().findViewById(R.id.map_entryView);
                params = (LinearLayout.LayoutParams)hostView.getLayoutParams();
                params.weight = 0;
                entryView.setLayoutParams(params);
                entryView.setVisibility(View.GONE);
                LinearLayout newMarkerView = getView().findViewById(R.id.map_newMarker);
                params = (LinearLayout.LayoutParams)newMarkerView.getLayoutParams();
                params.weight = 0;
                newMarkerView.setLayoutParams(params);
                newMarkerView.setVisibility(View.GONE);

                InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(hostView.getWindowToken(), 0);
                inputMethodManager.hideSoftInputFromWindow(newMarkerView.getWindowToken(), 0);
            }
        });
        googleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(final LatLng latLng) {

                final double latitude = latLng.latitude;
                final double longitude = latLng.longitude;

                MapView mapView = getView().findViewById(R.id.map);
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)mapView.getLayoutParams();
                params.weight = 60;
                mapView.setLayoutParams(params);
                ScrollView hostView = getView().findViewById(R.id.map_hostView);
                params = (LinearLayout.LayoutParams)hostView.getLayoutParams();
                params.weight = 0;
                hostView.setLayoutParams(params);
                hostView.setVisibility(View.GONE);
                LinearLayout newMarkerView = getView().findViewById(R.id.map_newMarker);
                params = (LinearLayout.LayoutParams)newMarkerView.getLayoutParams();
                params.weight = 40;
                newMarkerView.setLayoutParams(params);
                newMarkerView.setVisibility(View.VISIBLE);
                ScrollView entryView = getView().findViewById(R.id.map_entryView);
                params = (LinearLayout.LayoutParams)entryView.getLayoutParams();
                params.weight = 4;
                entryView.setLayoutParams(params);
                entryView.setVisibility(View.GONE);

                Button button_create = newMarkerView.findViewById(R.id.button_marker_create);
                final EditText title = newMarkerView.findViewById(R.id.editText_marker_title);
                final EditText date = newMarkerView.findViewById(R.id.editText_marker_date);
                final EditText detail = newMarkerView.findViewById(R.id.editText_marker_detail);
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd, HH:mm", Locale.KOREA);
                date.setHint("형식: " + dateFormat.format(new Date()));

                button_create.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        Entry entry = new Entry(myName, getPhoneNumber());
                        final MyMarker myMarker = new MyMarker(-1, title.getText().toString(), date.getText().toString(), detail.getText().toString(), latitude, longitude, getPhoneNumber());
                        myMarker.addEntry(entry);
                        Thread thread = new Thread() {
                            @Override
                            public void run() {
                                setMapData(myMarker);
                            }
                        };
                        thread.start();

                        markers.add(myMarker);

                        LatLng latLng = new LatLng(myMarker.getLatitude(), myMarker.getLongitude());
                        MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.position(latLng);
                        markerOptions.title(myMarker.getTitle());
                        markerOptions.draggable(true);
                        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));

                        map.addMarker(markerOptions);
                    }
                });
            }
        });

        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                String title = marker.getTitle();
                MyMarker myMarker = markers.get(markers.indexOf(new MyMarker(title)));

                if(myMarker.getPhoneNum().equals(getPhoneNumber())) {
                    showHostDialog(marker, myMarker);
                } else {
                    showEntryDialog(marker, myMarker);
                }
                return true;
            }
        });

        Thread thread = new Thread(){
            @Override
            public void run() {
                getMapData();
            }
        };
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for (MyMarker m:markers) {
            LatLng latLng = new LatLng(m.getLatitude(), m.getLongitude());

            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng).title(m.getTitle());
            if(m.getPhoneNum().equals(getPhoneNumber()))
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
            map.addMarker(markerOptions);
        }

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(basicPoint, 15));
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();

        if (googleApiClient != null && googleApiClient.isConnected())
            googleApiClient.disconnect();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
        if (map != null)
            map.setMyLocationEnabled(false);
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();

        if (googleApiClient != null && googleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, (com.google.android.gms.location.LocationListener) this);
            googleApiClient.disconnect();
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        MapsInitializer.initialize(getActivity().getApplicationContext());
        if (mapView != null) {
            mapView.onCreate(savedInstanceState);
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return true;
    }

    private void showHostDialog(final Marker marker, final MyMarker myMarker) {
        MapView mapView = getView().findViewById(R.id.map);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)mapView.getLayoutParams();
        params.weight = 60;
        mapView.setLayoutParams(params);
        ScrollView entryView = getView().findViewById(R.id.map_entryView);
        params = (LinearLayout.LayoutParams)entryView.getLayoutParams();
        params.weight = 0;
        entryView.setLayoutParams(params);
        entryView.setVisibility(View.GONE);
        LinearLayout newMarkerView = getView().findViewById(R.id.map_newMarker);
        params = (LinearLayout.LayoutParams)newMarkerView.getLayoutParams();
        params.weight = 0;
        newMarkerView.setLayoutParams(params);
        newMarkerView.setVisibility(View.GONE);
        ScrollView hostView = getView().findViewById(R.id.map_hostView);
        params = (LinearLayout.LayoutParams)hostView.getLayoutParams();
        params.weight = 40;
        hostView.setLayoutParams(params);
        hostView.setVisibility(View.VISIBLE);

        InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(hostView.getWindowToken(), 0);
        inputMethodManager.hideSoftInputFromWindow(newMarkerView.getWindowToken(), 0);

        Button button_update = hostView.findViewById(R.id.button_marker_host_update);
        Button button_delete = hostView.findViewById(R.id.button_marker_host_delete);

        final EditText editText_title = hostView.findViewById(R.id.editText_marker_host_title);
        final EditText editText_date = hostView.findViewById(R.id.editText_marker_host_date);
        final EditText editText_detail = hostView.findViewById(R.id.editText_marker_host_detail);
        TextView textView_entry = hostView.findViewById(R.id.textView_marker_host_entry);

        editText_title.setText(myMarker.getTitle());
        editText_date.setText(myMarker.getDate());
        editText_detail.setText(myMarker.getDetail());
        String enteies = new String();
        for (Entry e:myMarker.getEntries()) {
            enteies += "이름: " + e.getName() + ", 연락처: " + e.getPhoneNum() + "\n";
        }
        textView_entry.setText(enteies);

        button_update.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                myMarker.setTitle(editText_title.getText().toString());
                myMarker.setDate(editText_date.getText().toString());
                myMarker.setDetail(editText_detail.getText().toString());
                Thread thread = new Thread() {
                    @Override
                    public void run() {
                        updateMapData(myMarker);
                    }
                };
                thread.start();
                marker.showInfoWindow();
            }
        });
        button_delete.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Thread thread = new Thread() {
                    @Override
                    public void run() {
                        deleteMapData(myMarker);
                    }
                };
                thread.start();
                marker.remove();
            }
        });
    }

    private void showEntryDialog(final Marker marker, final MyMarker myMarker) {
        MapView mapView = getView().findViewById(R.id.map);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)mapView.getLayoutParams();
        params.weight = 60;
        mapView.setLayoutParams(params);
        ScrollView hostView = getView().findViewById(R.id.map_hostView);
        params = (LinearLayout.LayoutParams)hostView.getLayoutParams();
        params.weight = 0;
        hostView.setLayoutParams(params);
        hostView.setVisibility(View.GONE);
        LinearLayout newMarkerView = getView().findViewById(R.id.map_newMarker);
        params = (LinearLayout.LayoutParams)newMarkerView.getLayoutParams();
        params.weight = 0;
        newMarkerView.setLayoutParams(params);
        newMarkerView.setVisibility(View.GONE);
        ScrollView entryView = getView().findViewById(R.id.map_entryView);
        params = (LinearLayout.LayoutParams)entryView.getLayoutParams();
        params.weight = 40;
        entryView.setLayoutParams(params);
        entryView.setVisibility(View.VISIBLE);

        InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(hostView.getWindowToken(), 0);
        inputMethodManager.hideSoftInputFromWindow(newMarkerView.getWindowToken(), 0);

        Button button_enter = entryView.findViewById(R.id.button_marker_entry_enter);
        TextView textView_title = entryView.findViewById(R.id.textView_marker_entry_title);
        TextView textView_date = entryView.findViewById(R.id.textView_marker_entry_date);
        TextView textView_detail = entryView.findViewById(R.id.textView_marker_entry_detail);
        final TextView textView_entry = entryView.findViewById(R.id.textView_marker_entry_entry);

        textView_title.setText(myMarker.getTitle());
        textView_date.setText(myMarker.getDate());
        textView_detail.setText(myMarker.getDetail());
        String enteies = new String();
        for (Entry e:myMarker.getEntries()) {
            enteies += "이름: " + e.getName() + ", 연락처: " + e.getPhoneNum() + "\n";
        }
        textView_entry.setText(enteies);
        // 참여자는 버튼 비활성, 주최자는 모임 수정및 삭제 가능능

        button_enter.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Thread thread = new Thread() {
                    @Override
                    public void run() {
                        entry(myMarker);
                    }
                };
                thread.start();
                try {
                    thread.join();
                    String enteies = new String();
                    for (Entry e:myMarker.getEntries()) {
                        enteies += "이름: " + e.getName() + ", 연락처: " + e.getPhoneNum() + "\n";
                    }
                    textView_entry.setText(enteies);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                marker.showInfoWindow();
            }
        });
    }

    private void getMapData() {
        markers.clear();
        ServerConnect serverConnect = new ServerConnect("mapping");
        JSONArray result = serverConnect.getJSONArray();
        try {
            if(result.getString(0).equals("success")) {
                JSONArray jsonArray = result.getJSONArray(1);
                if(jsonArray.length() > 0) {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject object = jsonArray.getJSONObject(i);
                        MyMarker myMarker = new MyMarker(object.getInt("id"), object.getString("title"), object.getString("date"), object.getString("detail"), Double.parseDouble(object.getString("Latitude")), Double.parseDouble(object.getString("Longitude")), object.getString("phonenum"));
                        JSONArray entries = object.getJSONArray("entry");
                        for(int j = 0; j < entries.length(); j++) {
                            JSONObject entry = entries.getJSONObject(j);
                            myMarker.addEntry(new Entry(entry.getString("name"), entry.getString("phoneNum")));
                        }
                        markers.add(myMarker);
                    }
                }
            } else {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getContext(), "서버에 연결할 수 없습니다.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setMapData(MyMarker myMarker) {
        JSONObject obj = new JSONObject();
        try {
            obj.put("title", myMarker.getTitle());
            obj.put("date", myMarker.getDate());
            obj.put("detail", myMarker.getDetail());
            obj.put("latitude", myMarker.getLatitude());
            obj.put("longitude", myMarker.getLongitude());
            obj.put("name", myMarker.getEntries().get(0).getName());
            obj.put("phoneNum", myMarker.getPhoneNum());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ServerConnect serverConnect = new ServerConnect("saveMarker", obj.toString());
        if(serverConnect.send()){
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getContext(), "저장이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getContext(), "저장에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void updateMapData(MyMarker myMarker) {
        String id = new String();
        id += myMarker.getId();
        JSONObject obj = new JSONObject();
        try {
            obj.put("id", id);
            obj.put("title", myMarker.getTitle());
            obj.put("date", myMarker.getDate());
            obj.put("detail", myMarker.getDetail());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ServerConnect serverConnect = new ServerConnect("updateMarker", obj.toString());
        if(serverConnect.send()){
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getContext(), "모임이 수정되었습니다.", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getContext(), "모임 수정에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void deleteMapData(MyMarker myMarker) {
        ServerConnect serverConnect = new ServerConnect("deleteMarker", String.valueOf(myMarker.getId()));
        if(serverConnect.send()){
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getContext(), "모임이 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getContext(), "모임 삭제에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void entry(MyMarker myMarker) {
        JSONObject obj = new JSONObject();
        try {
            obj.put("id", myMarker.getId());
            obj.put("name", myName);
            obj.put("phoneNum", getPhoneNumber());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ServerConnect serverConnect = new ServerConnect("entry", obj.toString());
        if(serverConnect.send()){
            myMarker.entry();
        } else {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getContext(), "모임 참가/취소에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private String getPhoneNumber() {
        TelephonyManager tm = (TelephonyManager) getContext().getSystemService(TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_PHONE_STATE}, 1);
        }
        String pNumber = tm.getLine1Number();
        return pNumber;
    }
}



