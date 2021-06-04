package Memenergy.security.configurations;

import Memenergy.security.UserRepositoryAuthenticationProvider;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.PortMapper;
import org.springframework.security.web.PortMapperImpl;
import org.springframework.security.web.PortResolverImpl;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;

import java.util.Map;

@Configuration
@Order()
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Value("${server.port}")
    private int serverPort;

    @Value("${server.redirect.port}")
    private int redirectPort;

    @Autowired
    public UserRepositoryAuthenticationProvider authenticationProvider;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //static pages
        //css
        http.authorizeRequests().antMatchers("/css/*").permitAll();
        http.authorizeRequests().antMatchers("/css/assets/**/*").permitAll();
        //js
        http.authorizeRequests().antMatchers("/*.js").permitAll();
        //static images
        http.authorizeRequests().antMatchers("/assets/**/*").permitAll();


        //web
        // root
        http.authorizeRequests().antMatchers("/").permitAll();

        // /user
        http.authorizeRequests().antMatchers("/user").permitAll();
        http.authorizeRequests().antMatchers("/user/*").permitAll();
        http.authorizeRequests().antMatchers("/user/*/posts","/user/*/posts/*").permitAll();
        http.authorizeRequests().antMatchers("/user/*/followers","/user/*/followers/*").permitAll();
        http.authorizeRequests().antMatchers("/user/*/following","/user/*/following/*").permitAll();
        http.authorizeRequests().antMatchers("/user/*/likes").permitAll();

        // /post
        http.authorizeRequests().antMatchers("/post").authenticated();
        http.authorizeRequests().antMatchers("/post/*").permitAll();
        http.authorizeRequests().antMatchers("/post/*/report").authenticated();
        http.authorizeRequests().antMatchers("/post/*/forceVisibility").hasAnyRole("admin");
        http.authorizeRequests().antMatchers("/post/*/reports","/post/*/reports/*").hasAnyRole("admin");
        http.authorizeRequests().antMatchers("/post/*/likes").permitAll();
        http.authorizeRequests().antMatchers("/post/*/edit").authenticated(); //admin + owner
        http.authorizeRequests().antMatchers("/post/*/*").permitAll();

        // /users
        http.authorizeRequests().antMatchers("/users").permitAll();
        http.authorizeRequests().antMatchers("/users/filtered").permitAll();
        http.authorizeRequests().antMatchers("/users/*").permitAll();

        // /posts
        http.authorizeRequests().antMatchers("/posts").permitAll();
        http.authorizeRequests().antMatchers("/posts/filtered").permitAll();
        http.authorizeRequests().antMatchers("/posts/reported","/posts/reported/*").hasAnyRole("admin");
        http.authorizeRequests().antMatchers("/posts/*").permitAll();

        // /comments
        http.authorizeRequests().antMatchers("/comments/reported","/comments/reported/*").hasAnyRole("admin");

        // /comment
        http.authorizeRequests().antMatchers("/comment/*").permitAll();
        http.authorizeRequests().antMatchers("/comment/*/edit").authenticated(); //admin+owner
        http.authorizeRequests().antMatchers("/comment/*/report").authenticated();
        http.authorizeRequests().antMatchers("/comment/*/forceVisibility").hasAnyRole("admin");
        http.authorizeRequests().antMatchers("/comment/*/reports","/comment/*/reports/*").hasAnyRole("admin");

        //last pages
        http.authorizeRequests().antMatchers("/about").permitAll();
        http.authorizeRequests().antMatchers("/myprofile").authenticated();
        http.authorizeRequests().antMatchers("/myprofile/edit").authenticated();


        http.authorizeRequests().antMatchers("/error").permitAll();
        http.authorizeRequests().antMatchers("/errors").hasAnyRole("exceptionHandler");


        //Login/session control mappings
        http.authorizeRequests().antMatchers("/register").permitAll();
        http.authorizeRequests().antMatchers("/register/error/all").permitAll();
        http.authorizeRequests().antMatchers("/register/error/username").permitAll();
        http.authorizeRequests().antMatchers("/register/error/email").permitAll();
        http.authorizeRequests().antMatchers("/login").permitAll();
        http.authorizeRequests().antMatchers("/logout").permitAll();
        http.authorizeRequests().antMatchers("/loginError").permitAll();

        //Private pages (deny any any)
        http.authorizeRequests().anyRequest().hasAnyRole("admin");//denyAny();

        //Login form
        http.formLogin().loginPage("/login");
        http.formLogin().usernameParameter("username");
        http.formLogin().passwordParameter("password");
        http.formLogin().defaultSuccessUrl("/myprofile");
        http.formLogin().failureUrl("/loginError");

        //Logout
        http.logout().logoutUrl("/logout");
        http.logout().logoutSuccessUrl("/");

        http.exceptionHandling().accessDeniedPage("/forbidden");

        http.requestCache().requestCache(this.requestCache());
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(authenticationProvider);
    }

    private PortMapper portMapper() {
        PortMapperImpl portMapper = new PortMapperImpl();
        Map<String, String> mappings = Maps.newHashMap();
        mappings.put(Integer.toString(serverPort), Integer.toString(redirectPort));
        portMapper.setPortMappings(mappings);
        return portMapper;
    }

    private RequestCache requestCache() {
        HttpSessionRequestCache requestCache = new HttpSessionRequestCache();
        PortResolverImpl portResolver = new PortResolverImpl();
        portResolver.setPortMapper(portMapper());
        requestCache.setPortResolver(portResolver);
        return requestCache;
    }
}
