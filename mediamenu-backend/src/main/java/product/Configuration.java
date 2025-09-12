package product;

import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@org.springframework.context.annotation.Configuration
public class Configuration {

    @Bean
    //beans get injected into spring container
    //gives access to rest template throughout app
    public RestTemplate restTemplate(){
        //configure your rest template options
        return new RestTemplate();
    }

}
