package product.config;

import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

@org.springframework.context.annotation.Configuration
public class RestTemplateConfig {

    @Bean
    //beans get injected into spring container
    //gives access to rest template throughout app
    public RestTemplate restTemplate(){
        final RestTemplate restTemplate = new RestTemplate();
        restTemplate.setMessageConverters(Collections.singletonList(new MappingJackson2HttpMessageConverter()));
        //should convert any response to json
        return new RestTemplate();
    }

}
