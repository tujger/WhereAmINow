package ru.wtg.whereaminowserver.helpers;

import org.java_websocket.WebSocket;
import org.json.JSONObject;

import java.util.Date;

import static ru.wtg.whereaminowserver.helpers.Constants.USER_ACCURACY;
import static ru.wtg.whereaminowserver.helpers.Constants.USER_ALTITUDE;
import static ru.wtg.whereaminowserver.helpers.Constants.USER_BEARING;
import static ru.wtg.whereaminowserver.helpers.Constants.USER_LATITUDE;
import static ru.wtg.whereaminowserver.helpers.Constants.USER_LONGITUDE;
import static ru.wtg.whereaminowserver.helpers.Constants.USER_PROVIDER;
import static ru.wtg.whereaminowserver.helpers.Constants.USER_SPEED;
import static ru.wtg.whereaminowserver.helpers.Constants.USER_TIMESTAMP;

/**
 * Created 10/9/16.
 */

public class MyUser {
    private WebSocket webSocket;
//    private ArrayList<MyPosition> positions;
    private MyPosition position;
    private String deviceId;
    private String control;
    private String model;
    private String manufacturer;
    private String os;
    private String name;
    private long created;
    private long changed;
    private int color;
    private int number;


    public MyUser(WebSocket webSocket, String deviceId) {
        this.webSocket = webSocket;
        this.deviceId = deviceId;
        created = new Date().getTime();
//        positions = new ArrayList<MyPosition>();

        newControl();
        System.out.println("USER CONTROL:" + control);
        calculateHash();
    }

    public String calculateHash() {
        return calculateHash(control);
    }

    public String calculateHash(String control) {
        return Utils.getEncryptedHash(control + ":" + deviceId);
    }

    public String getControl() {
        return control;
    }

    public String newControl() {
        control = Utils.getUnique();
        return control;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getModel() {
        return model;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public String getAddress() {
        if (webSocket == null) return null;
        if (webSocket.getRemoteSocketAddress() == null) return null;
        return webSocket.getRemoteSocketAddress().toString();
    }

    public void setConnection(WebSocket connection) {
        this.webSocket = connection;
    }

    public WebSocket getConnection() {
        return webSocket;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public void setOs(String os) {
        this.os = os;
    }

    public String toString() {
        String res = "";
        res += "number:" + number;
        res += ", deviceId:" + deviceId;
        res += ", address:" + webSocket.getRemoteSocketAddress();
        res += ", created:" + getCreated() + "/" + new Date(getCreated()).toString();
        res += ", changed:" + getChanged() + "/" + new Date(getChanged()).toString();
        res += ", control:" + getControl();
        if (hasName()) res += ", name:" + name;
        if (model != null) res += ", model:" + model;
        if (manufacturer != null) res += ", manufacturer:" + manufacturer;
        if (os != null) res += ", os:" + os;
//        res += ", positions:" + positions.size();

        return res;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean hasName() {
        return name != null;
    }

    public void send(JSONObject o) {
        send(o.toString());
    }

    public void send(String text) {
        webSocket.send(text);
    }

    public void disconnect() {
        webSocket.close();
    }

    public void addPosition(JSONObject message) {
        if (message.has(USER_TIMESTAMP)) {
            MyPosition pos = new MyPosition();

            long timestamp = message.getLong(USER_TIMESTAMP);

            if (message.has(USER_LATITUDE)) pos.latitude = message.getDouble(USER_LATITUDE);
            if (message.has(USER_LONGITUDE)) pos.longitude = message.getDouble(USER_LONGITUDE);
            if (message.has(USER_ALTITUDE)) pos.altitude = message.getDouble(USER_ALTITUDE);
            if (message.has(USER_ACCURACY)) pos.accuracy = message.getDouble(USER_ACCURACY);
            if (message.has(USER_BEARING)) pos.bearing = message.getDouble(USER_BEARING);
            if (message.has(USER_SPEED)) pos.speed = message.getDouble(USER_SPEED);
            if (message.has(USER_PROVIDER)) pos.provider = message.getString(USER_PROVIDER);
            pos.timestamp = timestamp;

//            positions.add(pos);
            setPosition(pos);
            setChanged();
        }
    }

    public long getCreated() {
        return created;
    }

    public MyPosition getPosition() {
        return position;
/*
        if (positions.size() > 0) {
            return positions.get(positions.size() - 1);
        }
        return new MyPosition();
*/
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public void setPosition(MyPosition position) {
        this.position = position;
    }

    class MyPosition {
        public double latitude;
        public double longitude;
        public double altitude;
        public double accuracy;
        public double bearing;
        public double speed;
        public long timestamp;
        public String provider;

        public String toString() {
            return "Position [latitude:" + latitude + ", longitude:" + longitude
                    + ", altitude:" + altitude + ", accuracy:" + accuracy + ", bearing:" + bearing
                    + ", provider:" + provider + ", speed:" + speed + ", timestamp:" + timestamp + "]";
        }

        public JSONObject toJSON() {
            JSONObject o = new JSONObject();
            o.put(USER_LATITUDE, latitude);
            o.put(USER_LONGITUDE, longitude);
            o.put(USER_ALTITUDE, altitude);
            o.put(USER_ACCURACY, accuracy);
            o.put(USER_BEARING, bearing);
            o.put(USER_SPEED, speed);
            o.put(USER_PROVIDER, provider);
            o.put(USER_TIMESTAMP, timestamp);
            return o;
        }

    }

    public Long getChanged() {
        return changed;
    }

    public void setChanged(Long changed) {
        this.changed = changed;
    }

    public void setChanged() {
        changed = new Date().getTime();
    }

/*
    public ArrayList<MyPosition> getPositions() {
        return positions;
    }
*/

}