package Memenergy;

import org.owasp.html.PolicyFactory;
import org.owasp.html.Sanitizers;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;


@SpringBootApplication
public class MemenergyApplication {
    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }
    @Bean
    public PolicyFactory policy(){return Sanitizers.BLOCKS.and(Sanitizers.FORMATTING).and(Sanitizers.IMAGES).and(Sanitizers.LINKS).and(Sanitizers.STYLES).and(Sanitizers.TABLES);}

    /*@Bean
    public ServletContextInitializer servletContextInitializer(@Value("${secure.cookie}") boolean secure) {
        return new ServletContextInitializer() {

            @Override
            public void onStartup(ServletContext servletContext) throws ServletException {
                servletContext.getSessionCookieConfig().setSecure(secure);
            }
        };
    }*/

    public static void main(String[] args) {
        SpringApplication.run(MemenergyApplication.class, args);
    }

}
