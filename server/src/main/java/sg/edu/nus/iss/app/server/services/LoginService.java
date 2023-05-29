package sg.edu.nus.iss.app.server.services;

import java.time.Instant;
import java.util.Base64;
import java.util.Date;
// import java.util.Optional;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.DefaultJwt;
import sg.edu.nus.iss.app.server.models.TokenVerification;
import sg.edu.nus.iss.app.server.repositories.UserRepository;

@Service
public class LoginService {

    @Autowired
    Environment environment;
    
    @Autowired
    UserRepository userRepository;

    //the below cause the cyclic dependency. I had put the delete entire account method here (I shifted it to a new Service UserService instead.)
    // @Autowired
    // S3Service s3Service;

    // @Autowired
    // LikeRepository likeRepository;

    public Boolean comparePasswords(String givenPassword, String dbPassword){
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        if(bCryptPasswordEncoder.matches(givenPassword, dbPassword)){
            return true;
        } else{
            return false;
        }
    }

    public String createToken(String email){
        
        Instant now = Instant.now();
        Instant expiration = now.plusSeconds(300);
        Date expDate = Date.from(expiration);
        byte[] secretKeyBytes = Base64.getEncoder().encode(environment.getProperty("JWTSECRET").getBytes());
        SecretKey secretKey = new SecretKeySpec(secretKeyBytes, SignatureAlgorithm.HS512.getJcaName());

        // System.out.println("the secret key is : "  + environment.getProperty("JWTSECRET"));
        String token = Jwts.builder()
            .setSubject(email)
            .setIssuedAt(Date.from(now))
            .setExpiration(expDate)
            .signWith(secretKey,SignatureAlgorithm.HS512)
            .compact();
        return token;

    }
    public TokenVerification verifyJWTToken (String token) {
        byte[] secretKeyBytes = Base64.getEncoder().encode(environment.getProperty("JWTSECRET").getBytes());
        SecretKey secretKey = new SecretKeySpec(secretKeyBytes, SignatureAlgorithm.HS512.getJcaName());
        JwtParser jwtParser = Jwts.parserBuilder().setSigningKey(secretKey).build();
        Jwt<Header, Claims> jwt = new DefaultJwt<Claims>(null, null);
        try {
            jwt = jwtParser.parse(token);
        } catch (Exception e) {
        }
        String extractedSubject = "null";
        Instant longlongago = Instant.now().minusSeconds(1000);
        Date exp = Date.from(longlongago);
        if(jwt.getBody() != null){
            extractedSubject = jwt.getBody().getSubject();
            exp  = jwt.getBody().getExpiration();
        }
        Instant now = Instant.now();
        Date dateNow = Date.from(now);
        System.out.println("the datenow: " + dateNow.getTime());
        System.out.println("the exp date: " + exp.getTime());
        TokenVerification r = new TokenVerification();
        if(dateNow.getTime() >= exp.getTime()){
            System.out.println("the token is expired");
            r.setResult(false);
            return r;

        } else{
            System.out.println("the toke is not expired");
        }
        System.out.println("the extracted subject from verify token: " + extractedSubject);
        r.setEmail(extractedSubject);
        r.setResult(true);
        return r;


    }

    
}
