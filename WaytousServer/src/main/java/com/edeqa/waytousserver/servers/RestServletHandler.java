package com.edeqa.waytousserver.servers;

import com.edeqa.waytousserver.helpers.Common;
import com.edeqa.waytousserver.helpers.Constants;
import com.edeqa.waytousserver.helpers.HttpDPConnection;
import com.edeqa.waytousserver.helpers.RequestWrapper;
import com.edeqa.waytousserver.helpers.Utils;
import com.google.common.net.HttpHeaders;

import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.zip.GZIPOutputStream;

import javax.servlet.ServletException;
import javax.sound.midi.Soundbank;

import static com.edeqa.waytousserver.helpers.Constants.SENSITIVE;


/**
 * Created 1/19/17.
 */
@SuppressWarnings("HardCodedStringLiteral")
public class RestServletHandler extends AbstractServletHandler {

    private static final String LOG = "RSH";
    private V1 v1;

    public RestServletHandler() {
        super();
        v1 = new V1();
    }

    @Override
    public void init() throws ServletException {
        super.init();
        initDataProcessor();
    }

    @SuppressWarnings("HardCodedStringLiteral")
    @Override
    public void perform(RequestWrapper requestWrapper) throws IOException {

        try {
            URI uri = requestWrapper.getRequestURI();
            String host = null;
            try {
                host = requestWrapper.getRequestHeader(HttpHeaders.HOST).get(0);
                host = host.split(":")[0];
            } catch (Exception e) {
                e.printStackTrace();
            }
            Common.log(LOG, host + uri.getPath(), requestWrapper.getRemoteAddress());

//        List<String> parts = Arrays.asList(uri.getPath().split("/"));
            JSONObject json = new JSONObject();
            boolean printRes;

//        switch(exchange.getRequestMethod()) {
//            case HttpMethods.GET:
            json.put("status", "success");

            switch (uri.getPath()) {
                case "/rest/v1/getVersion":
                    printRes = v1.getVersion(json);
                    break;
                case "/rest/v1/getSounds":
                    printRes = v1.getSounds(json);
                    break;
                case "/rest/v1/getContent":
                    printRes = v1.getContent(json, requestWrapper);
                    break;
                case "/rest/v1/getLocales":
                    printRes = v1.getLocales(json, requestWrapper);
                    break;
                case "/rest/v1/join":
                    printRes = v1.join(json, requestWrapper);
                    break;
                default:
                    printRes = noAction(json);
                    break;
            }
//                break;
//            case HttpMethods.PUT:
//                break;
//            case HttpMethods.POST:
//                break;
//        }

            if (printRes) Utils.sendResultJson.call(requestWrapper, json);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }



    private boolean noAction(JSONObject json) {
        Common.log(LOG, "perform:noAction", json);
        json.put("status", "error");
        json.put("reason", "Action not defined");
        return true;
    }


    abstract class RestComponent {
        abstract boolean getVersion(JSONObject json);
    }





    public class V1 extends RestComponent{
        @Override
        boolean getVersion(JSONObject json) {
            json.put("version", Constants.SERVER_BUILD);
            return true;
        }
        boolean getSounds(JSONObject json) {
            File dir = new File(SENSITIVE.getWebRootDirectory() + "/sounds");
            File[] files = dir.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.endsWith(".mp3");
                }
            });
            ArrayList<String> list = new ArrayList<>();
            list.add("none.mp3");
            if(files != null) {
                for(File file: files) {
                    if(!list.contains(file.getName())) list.add(file.getName());
                }
            }
            json.put("files", list);
            return true;
        }
        boolean join(JSONObject json, RequestWrapper requestWrapper) {
            try {
                InputStreamReader isr = new InputStreamReader(requestWrapper.getRequestBody(),"utf-8");
                BufferedReader br = new BufferedReader(isr);
                String body = br.readLine();
                br.close();

                Common.log("Rest",requestWrapper.getRemoteAddress(), "joinV1:", body);
                Common.getInstance().getDataProcessor(requestWrapper.getRequestURI().getPath().split("/")[3]).onMessage(new HttpDPConnection(requestWrapper), body);
            } catch (Exception e) {
                e.printStackTrace();
                json.put("status", "error");
                json.put("reason", "Action failed");
                json.put("message", e.getMessage());
                Utils.sendResultJson.call(requestWrapper,json);
            }
            return false;
        }
        boolean getLocales(final JSONObject json, final RequestWrapper requestWrapper) {
            File dir = new File(SENSITIVE.getWebRootDirectory() + "/resources");
            try {
                File[] files = dir.listFiles(/*new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return dir.isDirectory();
                }
            }*/);
                Map<String, String> map = new LinkedHashMap<>();
                map.put("en", "En");

                if(files != null) {
                    for(File file: files) {
                        if(file.isDirectory()) {
                            String name = file.getName().toLowerCase();
                            name = name.substring(0,1).toUpperCase() + name.substring(1);
                            map.put(file.getName(), name);
                        }
                    }
                }
                json.put("locales", map);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return true;
            }

        }
        boolean getContent(final JSONObject json, final RequestWrapper requestWrapper) {

            try {
                StringBuilder buf = new StringBuilder();
                InputStream is = requestWrapper.getRequestBody();
                int b;
                while ((b = is.read()) != -1) {
                    buf.append((char) b);
                }
                is.close();

                JSONObject options = new JSONObject(buf.toString());
                Common.log(LOG,"Content requested: " + options);

                ArrayList<File> files = new ArrayList<>();

                if(options.has("type")) {
                    if (options.has("locale") && options.has("resource")) {
                        files.add(new File(SENSITIVE.getWebRootDirectory() + "/" + options.getString("type") + "/" + options.getString("locale") + "/" + options.getString("resource")));
                    }
                    if (options.has("resource")) {
                        files.add(new File(SENSITIVE.getWebRootDirectory() + "/" + options.getString("type") + "/en/" + options.getString("resource")));
                    }
                } else {
                    if (options.has("locale") && options.has("resource")) {
                        files.add(new File(SENSITIVE.getWebRootDirectory() + "/content/" + options.getString("locale") + "/" + options.getString("resource")));
                    }
                    if (options.has("resource")) {
                        files.add(new File(SENSITIVE.getWebRootDirectory() + "/content/en/" + options.getString("resource")));
                    }
                }

                boolean exists = false;
                File file = null;
                for (File f : files) {
                    if (f.getCanonicalPath().equals(f.getAbsolutePath()) && f.exists()) {
                        file = f;
                        exists = true;
                        break;
                    }
                }

                if(exists) {
                    String path = file.getAbsolutePath().replace(SENSITIVE.getWebRootDirectory(), "");
                    Common.log(LOG,"->", path);
                    requestWrapper.sendRedirect(path);
                    return false;
                } else {
                    Common.log(LOG,"Content not found.");
                    return true;
                }

            } catch (Exception e) {
                e.printStackTrace();
                return true;
            }

        }
    }

}
