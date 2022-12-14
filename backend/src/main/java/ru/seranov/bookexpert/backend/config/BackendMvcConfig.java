package ru.seranov.bookexpert.backend.config;

import lombok.NonNull;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.EncodedResourceResolver;
import org.springframework.web.servlet.resource.PathResourceResolver;
import org.springframework.web.servlet.resource.WebJarsResourceResolver;

@Configuration
public class BackendMvcConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(final ResourceHandlerRegistry registry) {
        WebMvcConfigurer.super.addResourceHandlers(registry);
        registry.addResourceHandler("/css/**")
                .addResourceLocations("classpath:/css/")
                .resourceChain(true)
                .addResolver(new EncodedResourceResolver());
        registry.addResourceHandler("/scripts/**")
                .addResourceLocations("classpath:/scripts/")
                .resourceChain(true)
                .addResolver(new EncodedResourceResolver());
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("/webjars/")
                .resourceChain(false)
                .addResolver(new WebJarsResourceResolver())
                .addResolver(new PathResourceResolver())
                .addResolver(new EncodedResourceResolver());
        ;

    }

    public void addViewControllers(@NonNull final ViewControllerRegistry registry) {
        registry.addViewController("/home").setViewName("home");
        registry.addViewController("/").setViewName("home");
        registry.addViewController("/hello").setViewName("hello");
        registry.addViewController("/rest").setViewName("rest");
        registry.addViewController("/websocket").setViewName("websocket");
        registry.addViewController("/login").setViewName("login");
    }
}
