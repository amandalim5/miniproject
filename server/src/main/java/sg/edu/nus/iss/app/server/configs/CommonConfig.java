package sg.edu.nus.iss.app.server.configs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

import sg.edu.nus.iss.app.server.repositories.UserRepository;

@EnableScheduling
@Configuration
public class CommonConfig implements WebMvcConfigurer {
    @Autowired 
    Interceptor interceptor;

    @Autowired
    UserRepository userRepository;


    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry
                .addMapping("/**")
                .allowedOrigins("*")
                .allowCredentials(false)
                .allowedHeaders("*")
                .allowedMethods("*")
                .exposedHeaders("*")
                .maxAge(3600);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(interceptor);
    }
    // executes at 7:15 pm everyday...
    @Scheduled(cron = "0 15 19 * * ?", zone = "GMT+8")
    public void scheduleTaskUsingCronExpression() {
 
        long now = System.currentTimeMillis() / 1000;
        System.out.println("schedule tasks using cron jobs - " + now);
        userRepository.updateLikesAndChecks();
    }

    @Value("${DO_STORAGE_KEY}")
    private String accessKey; 
 
    @Value("${DO_STORAGE_SECRETKEY}")
    private String secretKey; 
 
    @Value("${DO_STORAGE_ENDPOINT}")
    private String endPoint; 
 
    @Value("${DO_STORAGE_REGION}")
    private String endPointRegion;

    @Bean
    public AmazonS3 createS3Client(){
         BasicAWSCredentials cred =
                 new BasicAWSCredentials(accessKey, secretKey);
         EndpointConfiguration ep = 
                 new EndpointConfiguration(endPoint, endPointRegion);
 
         return AmazonS3ClientBuilder.standard()
             .withEndpointConfiguration(ep)
             .withCredentials(new AWSStaticCredentialsProvider(cred))
             .build();
    }
}
