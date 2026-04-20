package com.disasterrelief.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String location = resolveFrontendLocation();
        log.info("Serving frontend static files from: {}", location);

        // Serve all static frontend files (HTML, CSS, JS, assets)
        registry.addResourceHandler("/**")
                .addResourceLocations(location)
                .setCachePeriod(0)   // no cache in dev — hot-reload friendly
                .resourceChain(false);
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // http://localhost:8080 → index.html
        registry.addRedirectViewController("/", "/index.html");
    }

    /**
     * Resolves the frontend directory path regardless of whether the app
     * is launched from the 'backend/' module directory (mvn spring-boot:run)
     * or the project root (some IDE run configurations).
     */
    private String resolveFrontendLocation() {
        Path cwd = Paths.get(System.getProperty("user.dir"));

        // Case 1: running from disaster-relief-platform/backend/
        Path candidate = cwd.resolve("../frontend").normalize();
        if (Files.isDirectory(candidate)) {
            return "file:" + candidate.toAbsolutePath().toString().replace("\\", "/") + "/";
        }

        // Case 2: running from disaster-relief-platform/ (project root)
        candidate = cwd.resolve("frontend").normalize();
        if (Files.isDirectory(candidate)) {
            return "file:" + candidate.toAbsolutePath().toString().replace("\\", "/") + "/";
        }

        // Fallback — Spring Boot will log a warning if this doesn't resolve
        log.warn("Could not locate frontend directory from CWD={}. " +
                 "Place the 'frontend' folder next to the 'backend' folder.", cwd);
        return "classpath:/static/";
    }
}
