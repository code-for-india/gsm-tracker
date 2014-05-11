package com.bleatware.rtrak;

import android.content.res.AssetManager;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * RescueTracker
 * User: vasuman
 * Date: 10/5/14
 * Time: 10:37 PM
 */
public class InfoServer extends NanoHTTPD {
    AssetManager manager;
    BackgroundService service;
    List<Map<String, String>> dump = new ArrayList<Map<String, String>>();
    public InfoServer(int port, BackgroundService service) {
        super(port);
        this.service = service;
    }

    public String getJSONArray() {
        List<JSONObject> objects = new ArrayList<JSONObject>();
        for(Map<String, String> i: dump) {
            JSONObject j = new JSONObject(i);
            objects.add(j);
        }
        return new JSONArray(objects).toString();
    }

    public void flush() {
        dump.clear();
    }
    @Override
    public Response serve(IHTTPSession session) {
        try {
            Log.d("Got request", session.getUri());
            if(session.getUri().equals("/")) {
                return new Response(Response.Status.OK, MIME_HTML, service.getAssets().open("index.html"));
            } else if(session.getUri().equals("/submit") && session.getMethod() == Method.POST) {
                session.parseBody(new HashMap<String, String>());
                Map<String, String> params = new HashMap<String, String>(session.getParms());
                String ip = session.getHeaders().get("remote-addr");
                for(ClientScanResult result: service.apControl.scan()) {
                    if(result.getIpAddr().equals(ip)) {
                        params.put("type", "mac");
                        params.put("id", result.getHWAddr());
                    }
                }
                dump.add(params);
                return new Response(Response.Status.OK, MIME_PLAINTEXT, "DATA OK!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return super.serve(session);
    }
}
