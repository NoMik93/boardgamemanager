package com.example.nomik.boardgamemanager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class ServerConnect {
    private String urlString = "http://192.168.0.174:8080/bgm/DBConnection";
    private String mode, toSend;

    public ServerConnect(String mode) {
        this.mode = mode;
        this.toSend = null;
    }
    public ServerConnect(String mode, String toSend) {
        this.mode = mode;
        this.toSend = toSend;
    }

    public boolean send() {
        HttpURLConnection conn = null;
        try {
            conn = Connect();

            OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream(), "EUC-KR");
            osw.write(toSend);
            osw.close();

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK)
                return true;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return false;
        } catch (ProtocolException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            if (conn != null)
                conn.disconnect();
        }
        return false;
    }

    public JSONObject getJSONObjectWithSend(){
        HttpURLConnection conn = null;
        JSONObject jsonObject = null;
        try {
            conn = Connect();

            OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream(), "EUC-KR");
            osw.write(toSend);
            osw.close();

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
                jsonObject = new JSONObject(responseString);
                jsonObject.put("result", "success");
            } else {
                jsonObject = new JSONObject().put("result", "fail");
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            try {
                jsonObject = new JSONObject().put("result", "fail");
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
        } catch (ProtocolException e) {
            e.printStackTrace();
            try {
                jsonObject = new JSONObject().put("result", "fail");
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
            try {
                jsonObject = new JSONObject().put("result", "fail");
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            if(conn != null)
                conn.disconnect();
        }
        return jsonObject;
    }

    public JSONArray getJSONArray(){
        HttpURLConnection conn = null;
        JSONArray jsonArray = new JSONArray();
        try {
            conn = Connect();

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
                jsonArray.put(0, "success");
                jsonArray.put(new JSONArray(responseString));
            } else {
                jsonArray = new JSONArray().put(0, "fail");
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            try {
                jsonArray = new JSONArray().put(0, "fail");
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
        } catch (ProtocolException e) {
            e.printStackTrace();
            try {
                jsonArray = new JSONArray().put(0, "fail");
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
            try {
                jsonArray = new JSONArray().put(0, "fail");
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            if(conn != null)
                conn.disconnect();
        }
        return jsonArray;
    }

    public JSONArray getJSONArrayWithSend(){
        HttpURLConnection conn = null;
        JSONArray jsonArray = new JSONArray();
        try {
            conn = Connect();

            OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream(), "EUC-KR");
            osw.write(toSend);
            osw.close();

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
                jsonArray.put(0, "success");
                jsonArray.put(new JSONArray(responseString));
            } else {
                jsonArray = new JSONArray().put(0, "fail");
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            try {
                jsonArray = new JSONArray().put(0, "fail");
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
        } catch (ProtocolException e) {
            e.printStackTrace();
            try {
                jsonArray = new JSONArray().put(0, "fail");
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
            try {
                jsonArray = new JSONArray().put(0, "fail");
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            if(conn != null)
                conn.disconnect();
        }
        return jsonArray;
    }

    private HttpURLConnection Connect() throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setReadTimeout(3000);
        conn.setConnectTimeout(3000);
        conn.setRequestMethod("POST");
        conn.setDoInput(true);
        conn.setDoOutput(true);
        conn.setRequestProperty("mode", mode);
        conn.setRequestProperty("Content-Type", "application/json; charset=EUC-KR");
        conn.setRequestProperty("Accept", "application/json");
        return conn;
    }
}
