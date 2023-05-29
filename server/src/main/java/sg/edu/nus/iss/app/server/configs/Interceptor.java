package sg.edu.nus.iss.app.server.configs;

import java.util.Enumeration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import sg.edu.nus.iss.app.server.models.TokenVerification;
import sg.edu.nus.iss.app.server.services.LoginService;

@Configuration
public class Interceptor implements HandlerInterceptor {

    @Autowired
    LoginService loginService;

    @Override
    public boolean preHandle(HttpServletRequest request,HttpServletResponse response, Object handler){

        System.out.println("This is the preHandle.");
        System.out.println("++++++++++++++++++++++++ this is the request uri: " + request.getRequestURI() + "++++++++++++++++++++++++++++++++++++++");
        
        String token = request.getHeader("JWToken");
        String origin = request.getHeader("origin");
        System.out.println("The origin is: " + origin);
        if(origin == null){
            return false;
        }
        if(!origin.startsWith("https://projectclient.vercel.app")){

            return false;
        }

        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Methods", "GET,HEAD,OPTIONS,POST,PUT,DELETE");
        response.setHeader("Access-Control-Allow-Headers", "Access-Control-Allow-Headers, Origin,Accept, X-Requested-With, Content-Type, Access-Control-Request-Method, Access-Control-Request-Headers, jwtoken");

        Enumeration<String> headerNames = request.getHeaderNames();

        if (headerNames != null) {
                while (headerNames.hasMoreElements()) {
                    String s = headerNames.nextElement();
                    System.out.println("======> " + s);
                        System.out.println("Header: " + request.getHeader(s));
                }
        }

        String uri = request.getRequestURI();
        // if(uri.contains("register") || uri.contains("login") || uri.contains("token") || uri.contains("sendResetToken")){
        //     System.out.println("this is publicly available requests...");
        //     return true;
        // }
        if(uri.contains("updatePassword") || uri.contains("getProfile") || uri.contains("updateProfile") || uri.contains("getProfileIdOpp") || uri.contains("likeAPerson") || uri.contains("deletepic") || uri.contains("deleteAccount") || uri.contains("saveMessage")){
            System.out.println("This is the JWToken: " + token);
            System.out.println("checking token in the prehandle");
            TokenVerification t = loginService.verifyJWTToken(token);
            if(!t.getResult()){
                System.out.println("the check returned that the token is not valid");
                return false;
            }
            System.out.println("the check returned that the token is valid");
            return true;
        }


        return true;
    }
}
