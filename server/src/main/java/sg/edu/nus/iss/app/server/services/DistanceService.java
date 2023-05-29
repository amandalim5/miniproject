package sg.edu.nus.iss.app.server.services;

import java.io.IOException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import sg.edu.nus.iss.app.server.models.DistanceMatrix;

@Service
public class DistanceService {
    @Autowired
    Environment environment;

    private static final String DISTANCE_MATRIX_URL 
                        = "https://maps.googleapis.com/maps/api/distancematrix/json";

    public Optional<DistanceMatrix> getDistance(String origin, String destination) throws IOException{
        String apiKey = environment.getProperty("DISTANCE_KEY");
        String distanceURL = UriComponentsBuilder
                                .fromUriString(DISTANCE_MATRIX_URL)
                                .queryParam("origins", origin)
                                .queryParam("destinations", destination)
                                .queryParam("key", apiKey)
                                .toUriString();
        System.out.println("this is the url: " + distanceURL);
        RestTemplate template = new RestTemplate();
        ResponseEntity<String> resp = null;
        resp = template.getForEntity(distanceURL,String.class);
        System.out.println(resp);
        DistanceMatrix d = DistanceMatrix.create(resp.getBody());
        System.out.println(d);
        if(d != null)
            return Optional.of(d);                        
        return Optional.empty();
    }
    
}
