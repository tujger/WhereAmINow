package ru.wtg.whereaminow.service_helpers;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.support.v7.app.NotificationCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;

import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;
import io.nlopez.smartlocation.location.config.LocationAccuracy;
import io.nlopez.smartlocation.location.config.LocationParams;
import ru.wtg.whereaminow.MainActivity;
import ru.wtg.whereaminow.R;
import ru.wtg.whereaminow.State;
import ru.wtg.whereaminow.WhereAmINowService;
import ru.wtg.whereaminow.helpers.MyUser;
import ru.wtg.whereaminow.helpers.MyUsers;
import ru.wtg.whereaminow.helpers.Utils;

import static ru.wtg.whereaminow.State.ACCEPTED;
import static ru.wtg.whereaminow.State.CHANGE_NAME;
import static ru.wtg.whereaminow.State.DISCONNECTED;
import static ru.wtg.whereaminow.State.ERROR;
import static ru.wtg.whereaminow.State.MAKE_ACTIVE;
import static ru.wtg.whereaminow.State.MAKE_INACTIVE;
import static ru.wtg.whereaminow.State.SELECT_USER;
import static ru.wtg.whereaminow.State.STOPPED;
import static ru.wtg.whereaminowserver.helpers.Constants.BROADCAST;
import static ru.wtg.whereaminowserver.helpers.Constants.BROADCAST_MESSAGE;
import static ru.wtg.whereaminowserver.helpers.Constants.RESPONSE_INITIAL;
import static ru.wtg.whereaminowserver.helpers.Constants.RESPONSE_MESSAGE;
import static ru.wtg.whereaminowserver.helpers.Constants.RESPONSE_NUMBER;
import static ru.wtg.whereaminowserver.helpers.Constants.RESPONSE_PRIVATE;
import static ru.wtg.whereaminowserver.helpers.Constants.RESPONSE_STATUS;
import static ru.wtg.whereaminowserver.helpers.Constants.RESPONSE_STATUS_ACCEPTED;
import static ru.wtg.whereaminowserver.helpers.Constants.RESPONSE_STATUS_DISCONNECTED;
import static ru.wtg.whereaminowserver.helpers.Constants.RESPONSE_STATUS_ERROR;
import static ru.wtg.whereaminowserver.helpers.Constants.RESPONSE_STATUS_STOPPED;
import static ru.wtg.whereaminowserver.helpers.Constants.RESPONSE_STATUS_UPDATED;
import static ru.wtg.whereaminowserver.helpers.Constants.RESPONSE_TOKEN;
import static ru.wtg.whereaminowserver.helpers.Constants.TRACKING_ACTIVE;
import static ru.wtg.whereaminowserver.helpers.Constants.TRACKING_CONNECTING;
import static ru.wtg.whereaminowserver.helpers.Constants.TRACKING_DISABLED;
import static ru.wtg.whereaminowserver.helpers.Constants.USER_ACCURACY;
import static ru.wtg.whereaminowserver.helpers.Constants.USER_ALTITUDE;
import static ru.wtg.whereaminowserver.helpers.Constants.USER_BEARING;
import static ru.wtg.whereaminowserver.helpers.Constants.USER_DISMISSED;
import static ru.wtg.whereaminowserver.helpers.Constants.USER_JOINED;
import static ru.wtg.whereaminowserver.helpers.Constants.USER_LATITUDE;
import static ru.wtg.whereaminowserver.helpers.Constants.USER_LONGITUDE;
import static ru.wtg.whereaminowserver.helpers.Constants.USER_MESSAGE;
import static ru.wtg.whereaminowserver.helpers.Constants.USER_NAME;
import static ru.wtg.whereaminowserver.helpers.Constants.USER_NUMBER;
import static ru.wtg.whereaminowserver.helpers.Constants.USER_PROVIDER;
import static ru.wtg.whereaminowserver.helpers.Constants.USER_SPEED;
import static ru.wtg.whereaminowserver.helpers.Constants.USER_TIMESTAMP;
import static ru.wtg.whereaminowserver.helpers.Constants.WSS_SERVER_HOST;

/**
 * Created 10/8/16.
 */

public class MyTracking {

    private final Context context;
    private State state;
    private MyWebSocketClient webClient;
    private URI serverUri;
    private int status = TRACKING_DISABLED;

    public MyTracking(Context context) throws URISyntaxException {
        this(context, WSS_SERVER_HOST);
    }

    public MyTracking(Context context, String host) throws URISyntaxException {
        this(context, new URI("wss://"+host+":8081"));
    }

    private MyTracking(Context context, URI serverUri) {
        this.context = context;
        this.serverUri = serverUri;
        System.out.println("CONNECTTO:"+serverUri.toString());
        state = State.getInstance();
        try {
            webClient = new MyWebSocketClient(this.serverUri);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        webClient.setTracking(this);
    }

    public OnLocationUpdatedListener locationUpdatedListener = new OnLocationUpdatedListener() {
        @Override
        public void onLocationUpdated(Location location) {
            System.out.println("Service:onLocationChanged");

            if(status == TRACKING_ACTIVE) {
                try {
                    JSONObject o = Utils.locationToJson(location);
                    o.put(RESPONSE_STATUS,RESPONSE_STATUS_UPDATED);
                    o.put(RESPONSE_NUMBER,state.getUsers().getMyNumber());
                    fromServer(o);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                webClient.put(USER_LATITUDE, location.getLatitude());
                webClient.put(USER_LONGITUDE, location.getLongitude());
                webClient.put(USER_ACCURACY, location.getAccuracy());
                webClient.put(USER_ALTITUDE, location.getAltitude());
                webClient.put(USER_BEARING, location.getBearing());
                webClient.put(USER_SPEED, location.getSpeed());
                webClient.put(USER_PROVIDER, location.getProvider());
                webClient.put(USER_TIMESTAMP, location.getTime());

                webClient.sendUpdate();
            }
        }
    };

    private void enableLocationManager() {
//        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || !locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
//            System.err.println("Service:NOT locationManager.isProviderEnabled");
//            return;
//        }
        LocationParams.Builder builder = new LocationParams.Builder()
                .setAccuracy(LocationAccuracy.HIGH).setDistance(1).setInterval(1000);

        SmartLocation.with(context).location().continuous().config(builder.build()).start(locationUpdatedListener);
    }

    public void start() {
        webClient.setToken(null);
        doTrack();
    }

    public void join(String token) {
        webClient.setToken(token);
        doTrack();
    }

    private void doTrack(){
        setStatus(TRACKING_CONNECTING);
        Intent notificationIntent = new Intent(state, MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(state, 0, notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(state)
                .setSmallIcon(R.drawable.ic_navigation_twinks_white_24dp)
                .setContentTitle(state.getApplication().getString(R.string.app_name))
                .setContentText("Doing some work...")
                .setAutoCancel(true)
                .addAction(R.drawable.ic_navigation_twinks_black_24dp, "View", pendingIntent)
                .addAction(R.drawable.ic_clear_black_24dp, "Stop",
                        PendingIntent.getService(state, (int) System.currentTimeMillis(), new Intent(state, WhereAmINowService.class).putExtra("mode", "stop"),0))
                .setContentIntent(pendingIntent)
                .build();

        state.setNotification(notification);
        state.getService().startForeground(1976, notification);

        webClient.start();

        enableLocationManager();
    }

    public void stop() {
        try {
            JSONObject o = new JSONObject();
            o.put(RESPONSE_STATUS,RESPONSE_STATUS_STOPPED);
            fromServer(o);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    void fromServer(final JSONObject o) {
        System.out.println("FROMSERVER:" + o);

        if(status == TRACKING_DISABLED) return;
        try {
            switch (o.getString(RESPONSE_STATUS)) {
                case RESPONSE_STATUS_DISCONNECTED:
                    if(!state.disconnected()) {
                        state.fire(DISCONNECTED);
                    }
                    state.getMe().fire(SELECT_USER, 0);
                    break;
                case RESPONSE_STATUS_ACCEPTED:
                    setStatus(TRACKING_ACTIVE);
                    if (o.has(RESPONSE_TOKEN)) {
                        state.setToken(o.getString(RESPONSE_TOKEN));
                    }
                    if (o.has(RESPONSE_NUMBER)) {
                        state.getUsers().setMyNumber(o.getInt(RESPONSE_NUMBER));
                    }
                    if (o.has(RESPONSE_INITIAL)) {
                        JSONArray initialUsers = o.getJSONArray(RESPONSE_INITIAL);
                        for (int i = 0; i < initialUsers.length(); i++) {
                            JSONObject u = initialUsers.getJSONObject(i);
                            state.getUsers().addUser(u);
                        }
                    }
                    state.fire(ACCEPTED);
                    break;
                case RESPONSE_STATUS_ERROR:
                    SmartLocation.with(context).location().stop();
                    stop();
                    setStatus(TRACKING_DISABLED);

                    String message = o.getString(RESPONSE_MESSAGE);
                    if(message == null) message = "Failed join to tracking.";
                    state.fire(ERROR, message);
                    break;
                case RESPONSE_STATUS_UPDATED:
                    if (o.has(USER_DISMISSED)) {
                        int number = o.getInt(USER_DISMISSED);
                        state.getUsers().forUser(number,new MyUsers.Callback() {
                            @Override
                            public void call(Integer number, final MyUser myUser) {
                                myUser.fire(MAKE_INACTIVE);
                            }
                        });
                        state.fire(USER_DISMISSED);
                    } else if (o.has(USER_JOINED)) {
                        int number = o.getInt(USER_JOINED);
                        state.getUsers().addUser(o);
                        state.getUsers().forUser(number,new MyUsers.Callback() {
                            @Override
                            public void call(Integer number, MyUser myUser) {
                                myUser.fire(MAKE_ACTIVE);
                            }
                        });
                        state.fire(USER_JOINED);
                    }
                    if (o.has(USER_PROVIDER)) {
                        final Location location = Utils.jsonToLocation(o);
                        int number = o.getInt(USER_NUMBER);
                        state.getUsers().forUser(number,new MyUsers.Callback() {
                            @Override
                            public void call(Integer number, MyUser myUser) {
                                myUser.addLocation(location);
                            }
                        });
                    }
                    if (o.has(USER_NAME)) {
                        int number = o.getInt(USER_NUMBER);
                        final String name = o.getString(USER_NAME);
                        state.getUsers().forUser(number,new MyUsers.Callback() {
                            @Override
                            public void call(Integer number, MyUser myUser) {
                                myUser.fire(CHANGE_NAME,name);
                            }
                        });
                    }
                    if (o.has(USER_MESSAGE)) {
                        int number = o.getInt(USER_NUMBER);
                        final String text = o.getString(USER_MESSAGE);
//                        state.fire(USER_MESSAGE, );
                        state.getUsers().forUser(number,new MyUsers.Callback() {
                            @Override
                            public void call(Integer number, MyUser myUser) {
                                if(o.has(RESPONSE_PRIVATE)){
                                    myUser.fire(State.PRIVATE_MESSAGE, text);
                                } else {
                                    myUser.fire(State.USER_MESSAGE, text);
                                }
                            }
                        });
                    }
                    break;
                case RESPONSE_STATUS_STOPPED:
                    SmartLocation.with(context).location().stop();
                    state.getUsers().removeAllUsersExceptMe();
                    setStatus(TRACKING_DISABLED);
                    state.getMe().fire(SELECT_USER, 0);
                    state.getService().stopForeground(true);
                    state.setNotification(null);

                    webClient.removeToken();
                    webClient.stop();
                    state.fire(STOPPED);
                    break;
            }
        }catch(JSONException e){
            e.printStackTrace();
        }
        state.sendBroadcast(new Intent(BROADCAST).putExtra(BROADCAST_MESSAGE, o.toString()));

    }

    public int getStatus() {
        return status;
    }

    private void setStatus(int status) {
        this.status = status;
    }

    public String getHost(){
        return serverUri.getHost();
    }

    public void sendMessage(String key,String value){
        webClient.put(key, value);
        webClient.sendUpdate();
    }

    public void sendMessage(JSONObject json){
        Iterator<String> iter = json.keys();
        while(iter.hasNext()){
            String entry = iter.next();
            try {
                Object value = json.get(entry);
                if(value instanceof String){
                    webClient.put(entry, json.getString(entry));
                } else if(value instanceof Integer){
                    webClient.put(entry, json.getInt(entry));
                } else if(value instanceof Boolean){
                    webClient.put(entry, json.getBoolean(entry));
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        webClient.sendUpdate();
    }

}
