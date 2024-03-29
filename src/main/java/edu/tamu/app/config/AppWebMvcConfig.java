package edu.tamu.app.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.mail.javamail.ConfigurableMimeFileTypeMap;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.ResourceUrlEncodingFilter;

import edu.tamu.app.model.User;
import edu.tamu.app.model.repo.UserRepo;
import edu.tamu.weaver.auth.resolver.WeaverCredentialsArgumentResolver;
import edu.tamu.weaver.auth.resolver.WeaverUserArgumentResolver;
import edu.tamu.weaver.validation.resolver.WeaverValidatedModelMethodProcessor;

@EnableWebMvc
@Configuration
@EntityScan(basePackages = { "edu.tamu.app.model" })
@EnableJpaRepositories(basePackages = { "edu.tamu.app.model.repo" })
public class AppWebMvcConfig implements WebMvcConfigurer {

    @Value("${app.security.allow-access}")
    private String[] hosts;

    @Autowired
    private List<HttpMessageConverter<?>> converters;

    @Autowired
    private UserRepo userRepo;

    @Bean
    public ResourceUrlEncodingFilter resourceUrlEncodingFilter() {
        return new ResourceUrlEncodingFilter();
    }

    @Bean
    public ConfigurableMimeFileTypeMap configurableMimeFileTypeMap() {
        return new ConfigurableMimeFileTypeMap();
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // @formatter:off
        registry.addMapping("/**")
                .allowedOrigins(hosts)
                .allowCredentials(false)
                .allowedMethods("GET", "DELETE", "PUT", "POST")
                .allowedHeaders("Origin", "Content-Type", "Access-Control-Allow-Origin", "x-requested-with", "jwt", "data", "x-forwarded-for")
                .exposedHeaders("jwt");
        // @formatter:on
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        if (!registry.hasMappingForPattern("/images/**")) {
            registry.addResourceHandler("/images/**").addResourceLocations("classpath:/images/");
        }
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(new WeaverValidatedModelMethodProcessor(converters));
        argumentResolvers.add(new WeaverCredentialsArgumentResolver());
        argumentResolvers.add(new WeaverUserArgumentResolver<User, UserRepo>(userRepo));
    }

    @Bean
    public ServletWebServerFactory servletWebServerFactory() {
        return new TomcatServletWebServerFactory();
    }

}
