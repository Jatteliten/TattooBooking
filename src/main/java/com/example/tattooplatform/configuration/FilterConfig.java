package com.example.tattooplatform.configuration;

import com.example.tattooplatform.filter.AntiBotFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.web.servlet.FilterRegistrationBean;

@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<AntiBotFilter> antiBotFilter() {
        FilterRegistrationBean<AntiBotFilter> registrationBean = new FilterRegistrationBean<>();

        registrationBean.setFilter(new AntiBotFilter());
        registrationBean.addUrlPatterns("/flash/*", "/products/*", "/send-booking-request");
        registrationBean.setOrder(1);

        return registrationBean;
    }
}
