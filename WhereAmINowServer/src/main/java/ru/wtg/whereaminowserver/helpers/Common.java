package ru.wtg.whereaminowserver.helpers;

import org.json.JSONObject;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static ru.wtg.whereaminowserver.helpers.Constants.WSS_PORT;
import static ru.wtg.whereaminowserver.helpers.HtmlGenerator.HREF;
import static ru.wtg.whereaminowserver.helpers.HtmlGenerator.LINK;
import static ru.wtg.whereaminowserver.helpers.HtmlGenerator.REL;
import static ru.wtg.whereaminowserver.helpers.HtmlGenerator.SCRIPT;
import static ru.wtg.whereaminowserver.helpers.HtmlGenerator.SRC;

/**
 * Created 1/23/2017.
 */

public class Common {

    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS z", Locale.getDefault());

    public static JSONObject fetchGeneralInfo() {
        JSONObject o = new JSONObject();

        try {
            String wss = "ws://" + InetAddress.getLocalHost().getHostAddress() + ":" + WSS_PORT;
            o.put("uri", wss);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return o;
    }

    public static void log(String... text) {
        String str = "";
        for(int i = 0; i < text.length; i++){
            str += text[i] + " ";
        }
        System.out.println(Common.dateFormat.format(new Date()) + "/" + str);
    }

}
