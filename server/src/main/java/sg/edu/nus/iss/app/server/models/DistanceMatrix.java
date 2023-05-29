package sg.edu.nus.iss.app.server.models;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;

public class DistanceMatrix implements Serializable{
    private String status;
    private String distance;
    
    public static DistanceMatrix create(String json) throws IOException{
        DistanceMatrix d = new DistanceMatrix();
        try(InputStream is = new ByteArrayInputStream(json.getBytes())){
            JsonReader r = Json.createReader(is);
            JsonObject o = r.readObject();
            String theDistance;
            JsonObject test = o.getJsonArray("rows").get(0).asJsonObject().getJsonArray("elements").get(0).asJsonObject().getJsonObject("distance");
            if(test != null){
                theDistance =  o.getJsonArray("rows").get(0).asJsonObject().getJsonArray("elements").get(0).asJsonObject().getJsonObject("distance").getString("text");
            } else{
                theDistance = "";
            }
             
            String theStatus =  o.getJsonArray("rows").get(0).asJsonObject().getJsonArray("elements").get(0).asJsonObject().getString("status");
            d.setDistance(theDistance);
            d.setStatus(theStatus);
        }
        return d; 

    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }
    
}
