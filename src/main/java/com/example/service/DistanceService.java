package com.example.service;

import com.google.gson.JsonObject;

import java.net.URL;

public class DistanceService implements Runnable {
    String origins;
    String destinations;
    String result;

    public String getOrigins() {
        return origins;
    }

    public void setOrigins(String origins) {
        this.origins = origins;
    }

    public String getDestinations() {
        return destinations;
    }

    public void setDestinations(String destinations) {
        this.destinations = destinations;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    @Override
    public void run() {
        String urlString = "https://maps.googleapis.com/maps/api/distancematrix/json?"
                + "units=imperial&origins="+origins.replace(' ',
                ',')+"&destinations=" +
                destinations.replace(' ',
                        ',') +"&key=AIzaSyDHIdh10mg1corNw5R336jAkScXt8ozGLQ";
        String res = "";
        try {
            URL url = new URL(urlString);
            java.net.HttpURLConnection conn = (java.net.HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("GET");
            java.io.BufferedReader in = new java.io.BufferedReader(
                    new java.io.InputStreamReader(conn.getInputStream(), "UTF-8"));
            String line;
            while ((line = in.readLine()) != null) {
                res += line + "\n";
            }
            in.close();
        } catch (Exception e) {
            System.out.println("error in return ,and e is " + e.getMessage());
        }
        com.google.gson.JsonParser parser = new com.google.gson.JsonParser();
        String result="";
        try {
            JsonObject json = (JsonObject) parser.parse(res);
            String status = json.get("status").getAsString();
            if(!status.equals("OK")){
                result="error";
            }
            result=json.get("rows").getAsJsonArray().get(0).getAsJsonObject().get("elements")
                    .getAsJsonArray().get(0).getAsJsonObject().get("distance").getAsJsonObject()
                    .get("text").getAsString();
        } catch (Exception e) {
            e.printStackTrace();
            result="error";
        }
    }
}
