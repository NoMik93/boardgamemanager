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
import android.support.constraint.Constraints;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

import noman.googleplaces.NRPlaces;
import noman.googleplaces.Place;
import noman.googleplaces.PlaceType;
import noman.googleplaces.PlacesException;
import noman.googleplaces.PlacesListener;

import static android.content.Context.TELEPHONY_SERVICE;
import static android.support.constraint.Constraints.TAG;
import static android.support.v4.content.ContextCompat.getSystemService;


public class FragmentMap extends Fragment implements OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMyLocationClickListener, GoogleMap.OnMarkerClickListener {
    private static final LatLng curPoint = new LatLng(37.619395, 127.058998); // 현재 위치 광운대
    private MapView mapView = null;
    private GoogleApiClient googleApiClient = null;
    private GoogleMap map;

    private Context context;

    ArrayList<MyMarker> markers = new ArrayList<>();
    String myName;

    public class MyMarker {
        private int marker_id;
        private String title;
        private String date;
        private double latitude;
        private double longitude;
        private String phoneNum;
        private ArrayList<Entry> entries = new ArrayList<>();

        public MyMarker(String title) {
            this.title = title;
        }

        public MyMarker(int marker_id, String title, String date, double latitude, double longitude, String phoneNum) {
            this.marker_id = marker_id;
            this.title = title;
            this.date = date;
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
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        myName = ((MainActivity) getActivity()).getMyName();
        mapView = view.findViewById(R.id.map);
        mapView.getMapAsync(this);
        context = container.getContext();
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
        googleMap.setOnMyLocationButtonClickListener(this);
        googleMap.setOnMyLocationClickListener(this);
        googleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(final LatLng latLng) {

                final double latitude = latLng.latitude;
                final double longitude = latLng.longitude;

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                LayoutInflater inflater = getLayoutInflater();
                View view = inflater.inflate(R.layout.alertdialog_marker, null);
                builder.setView(view);
                Button button_create = view.findViewById(R.id.button_marker_create);
                Button button_cancel = view.findViewById(R.id.button_marker_cancel);
                final EditText title = view.findViewById(R.id.editText_marker_title);
                final EditText date = view.findViewById(R.id.editText_marker_date);


                final AlertDialog dialog = builder.create();
                button_create.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        Entry entry = new Entry(myName, getPhoneNumber());
                        final MyMarker myMarker = new MyMarker(-1, title.getText().toString(), date.getText().toString(), latitude, longitude, getPhoneNumber());
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
                        dialog.dismiss();
                    }
                });
                button_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });

        googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(final Marker marker) {

                // 마커 정보 가져오기
                String title = marker.getTitle();
                MyMarker myMarker = markers.get(markers.indexOf(new MyMarker(title)));

                if(myMarker.getPhoneNum().equals(getPhoneNumber())) {
                    showHostDialog(marker, myMarker);
                } else {
                    showEntryDialog(marker, myMarker);
                }
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

            MarkerOptions makerOptions = new MarkerOptions();
            makerOptions.position(latLng).title(m.getTitle());
            map.addMarker(makerOptions).showInfoWindow();
        }

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(curPoint, 15));
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
        LayoutInflater inflater = getLayoutInflater();
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = inflater.inflate(R.layout.alertdialog_marker_host, null);
        builder.setView(view);
        Button button_update = view.findViewById(R.id.button_marker_host_update);
        Button button_delete = view.findViewById(R.id.button_marker_host_delete);
        Button button_cancel = view.findViewById(R.id.button_marker_host_cancel);
        final EditText editText_title = view.findViewById(R.id.editText_marker_host_title);
        final EditText editText_date = view.findViewById(R.id.editText_marker_host_date);
        TextView textView_entry = view.findViewById(R.id.textView_marker_host_entry);

        editText_title.setText(myMarker.getTitle());
        editText_date.setText(myMarker.getDate());
        String enteies = new String();
        for (Entry e:myMarker.getEntries()) {
            enteies += "이름: " + e.getName() + ", 연락처: " + e.getPhoneNum() + "\n";
        }
        textView_entry.setText(enteies);

        final AlertDialog dialog = builder.create();
        button_update.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                myMarker.setTitle(editText_title.getText().toString());
                myMarker.setDate(editText_date.getText().toString());
                Thread thread = new Thread() {
                    @Override
                    public void run() {
                        updateMapData(myMarker);
                    }
                };
                thread.start();
                dialog.dismiss();
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
                dialog.dismiss();
                marker.remove();
            }
        });
        button_cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void showEntryDialog(final Marker marker, final MyMarker myMarker) {
        LayoutInflater inflater = getLayoutInflater();
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = inflater.inflate(R.layout.alertdialog_marker_entry, null);
        builder.setView(view);
        Button button_enter = view.findViewById(R.id.button_marker_entry_enter);
        Button button_cancel = view.findViewById(R.id.button_marker_entry_cancel);
        TextView textView_title = view.findViewById(R.id.textView_marker_entry_title);
        TextView textView_date = view.findViewById(R.id.textView_marker_entry_date);
        TextView textView_entry = view.findViewById(R.id.textView_marker_entry_entry);

        textView_title.setText(myMarker.getTitle());
        textView_date.setText(myMarker.getDate());
        String enteies = new String();
        for (Entry e:myMarker.getEntries()) {
            enteies += "이름: " + e.getName() + ", 연락처: " + e.getPhoneNum() + "\n";
        }
        textView_entry.setText(enteies);
        // 참여자는 버튼 비활성, 주최자는 모임 수정및 삭제 가능능

        final AlertDialog dialog = builder.create();

        button_enter.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Thread thread = new Thread() {
                    @Override
                    public void run() {
                        entry(myMarker);
                    }
                };
                thread.start();
                dialog.dismiss();
                marker.showInfoWindow();
            }
        });
        button_cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void getMapData() {
        markers.clear();
        HttpURLConnection conn = null;
        try {
            String urlString = "http://192.168.0.174:8080/bgm/DBConnection";
            URL url = new URL(urlString);
            conn = (HttpURLConnection) url.openConnection();

            conn.setReadTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestProperty("mode", "mapping");
            conn.setRequestProperty("Content-Type", "application/json; charset=EUC-KR");
            conn.setRequestProperty("Accept", "application/json");

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                InputStream is = conn.getInputStream();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] byteBuffer = new byte[1024];
                byte[] byteData = null;
                int length = 0;
                while ((length = is.read(byteBuffer, 0, byteBuffer.length)) != -1) {
                    baos.write(byteBuffer, 0, length);
                }
                byteData = baos.toByteArray();
                String responseString = new String(byteData);
                final JSONArray jsonArray = new JSONArray(responseString);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);
                    MyMarker myMarker = new MyMarker(object.getInt("id"), object.getString("title"), object.getString("date"), Double.parseDouble(object.getString("Latitude")), Double.parseDouble(object.getString("Longitude")), object.getString("phonenum"));
                    JSONArray entries = object.getJSONArray("entry");
                    for(int j = 0; j < entries.length(); j++) {
                        JSONObject entry = entries.getJSONObject(j);
                        myMarker.addEntry(new Entry(entry.getString("name"), entry.getString("phoneNum")));
                    }
                    markers.add(myMarker);
                }
            } else {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getContext(), "서버 연결에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            if(conn != null)
                conn.disconnect();
        }
    }

    private void setMapData(MyMarker myMarker) {
        HttpURLConnection conn = null;
        try {
            String urlString = "http://192.168.0.174:8080/bgm/DBConnection";
            URL url = new URL(urlString);
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestProperty("mode", "saveMarker");
            conn.setRequestProperty("Content-Type", "application/json; charset=EUC-KR");
            conn.setRequestProperty("Accept", "application/json");


            OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream(), "EUC-KR");
            JSONObject obj = new JSONObject();
            obj.put("title", myMarker.getTitle());
            obj.put("date", myMarker.getDate());
            obj.put("latitude", myMarker.getLatitude());
            obj.put("longitude", myMarker.getLongitude());
            obj.put("name", myMarker.getEntries().get(0).getName());
            obj.put("phoneNum", myMarker.getPhoneNum());
            osw.write(obj.toString());
            osw.close();

            int responseCode = conn.getResponseCode();
            if(responseCode == HttpURLConnection.HTTP_OK) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getContext(), "모임이 등록되었습니다.", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getContext(), "모임 등록에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }finally {
            if(conn != null)
                conn.disconnect();
        }
    }

    private void updateMapData(MyMarker myMarker) {
        HttpURLConnection conn = null;
        try {
            String urlString = "http://192.168.0.174:8080/bgm/DBConnection";
            URL url = new URL(urlString);
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestProperty("mode", "updateMarker");
            conn.setRequestProperty("Content-Type", "application/json; charset=EUC-KR");
            conn.setRequestProperty("Accept", "application/json");


            OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream(), "EUC-KR");
            String id = new String();
            id += myMarker.getId();
            JSONObject obj = new JSONObject();
            obj.put("id", id);
            obj.put("title", myMarker.getTitle());
            obj.put("date", myMarker.getDate());
            osw.write(obj.toString());
            osw.close();

            int responseCode = conn.getResponseCode();
            if(responseCode == HttpURLConnection.HTTP_OK) {
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
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            if(conn != null)
                conn.disconnect();
        }
    }

    private void deleteMapData(MyMarker myMarker) {
        HttpURLConnection conn = null;
        try {
            String urlString = "http://192.168.0.174:8080/bgm/DBConnection";
            URL url = new URL(urlString);
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestProperty("mode", "deleteMarker");
            String id = new String();
            id += myMarker.getId();
            conn.setRequestProperty("id", id);
            conn.setRequestProperty("Content-Type", "application/json; charset=EUC-KR");
            conn.setRequestProperty("Accept", "application/json");

            int responseCode = conn.getResponseCode();
            if(responseCode == HttpURLConnection.HTTP_OK) {
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
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(conn != null)
                conn.disconnect();
        }
    }

    private void entry(MyMarker myMarker) {
        HttpURLConnection conn = null;
        try {
            String urlString = "http://192.168.0.174:8080/bgm/DBConnection";
            URL url = new URL(urlString);
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestProperty("mode", "entry");
            conn.setRequestProperty("Content-Type", "application/json; charset=EUC-KR");
            conn.setRequestProperty("Accept", "application/json");


            OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream(), "EUC-KR");
            JSONObject obj = new JSONObject();
            obj.put("id", myMarker.getId());
            obj.put("name", myName);
            obj.put("phoneNum", getPhoneNumber());
            osw.write(obj.toString());
            osw.close();

            int responseCode = conn.getResponseCode();
            if(responseCode == HttpURLConnection.HTTP_OK) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getContext(), "모임에 참가/취소되었습니다.", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getContext(), "모임 참가/취소에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            if(conn != null)
                conn.disconnect();
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



