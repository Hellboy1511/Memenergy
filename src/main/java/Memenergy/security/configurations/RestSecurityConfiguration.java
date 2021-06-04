package Memenergy.security.configurations;

import Memenergy.security.UserRepositoryAuthenticationProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@Order(1)
public class RestSecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    UserRepositoryAuthenticationProvider authenticationProvider;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(authenticationProvider);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //api
        http.antMatcher("/api/**");
        http.authorizeRequests().antMatchers("/api/login").permitAll();
        http.authorizeRequests().antMatchers("/api/logout").permitAll();
        // /user
        http.authorizeRequests().antMatchers(HttpMethod.POST,"/api/user").permitAll();
        http.authorizeRequests().antMatchers(HttpMethod.GET,"/api/user/*").permitAll();
        http.authorizeRequests().antMatchers(HttpMethod.PUT,"/api/user/*").authenticated(); // owner
        http.authorizeRequests().antMatchers(HttpMethod.DELETE,"/api/user/*").authenticated(); //owner
        http.authorizeRequests().antMatchers(HttpMethod.PATCH,"/api/user/*").authenticated(); //owner

        http.authorizeRequests().antMatchers(HttpMethod.POST,"/api/user/*/follow").authenticated();
        http.authorizeRequests().antMatchers(HttpMethod.GET,"/api/user/*/follow/*").authenticated();
        http.authorizeRequests().antMatchers(HttpMethod.DELETE,"/api/user/*/follow/*").authenticated();

        http.authorizeRequests().antMatchers(HttpMethod.GET,"/api/user/*/followers","/api/user/*/followers/*").permitAll();
        http.authorizeRequests().antMatchers(HttpMethod.GET,"/api/user/*/following","/api/user/*/following/*").permitAll();
        http.authorizeRequests().antMatchers(HttpMethod.GET,"/api/user/*/likes").permitAll();

        http.authorizeRequests().antMatchers(HttpMethod.GET,"/api/user/*/image").permitAll();
        http.authorizeRequests().antMatchers(HttpMethod.POST,"/api/user/*/image").authenticated(); //owner

        // /post
        http.authorizeRequests().antMatchers(HttpMethod.POST,"/api/post").authenticated();
        http.authorizeRequests().antMatchers(HttpMethod.GET,"/api/post/*").permitAll();
        http.authorizeRequests().antMatchers(HttpMethod.PUT,"/api/post/*").authenticated(); //admin + owner
        http.authorizeRequests().antMatchers(HttpMethod.PATCH,"/api/post/*").authenticated(); //admin + owner
        http.authorizeRequests().antMatchers(HttpMethod.DELETE,"/api/post/*").authenticated(); //owner

        http.authorizeRequests().antMatchers(HttpMethod.POST,"/api/post/*/report").authenticated();
        http.authorizeRequests().antMatchers(HttpMethod.GET,"/api/post/*/report/*").hasAnyRole("admin");
        http.authorizeRequests().antMatchers(HttpMethod.PUT,"/api/post/*/report/*").authenticated(); //owner
        http.authorizeRequests().antMatchers(HttpMethod.PATCH,"/api/post/*/report/*").authenticated(); //owner
        http.authorizeRequests().antMatchers(HttpMethod.DELETE,"/api/post/*/report/*").authenticated(); //owner

        http.authorizeRequests().antMatchers(HttpMethod.GET,"/api/post/*/reports","/api/post/*/reports/*").hasAnyRole("admin");
        http.authorizeRequests().antMatchers(HttpMethod.GET,"/api/post/*/likes").permitAll();

        http.authorizeRequests().antMatchers(HttpMethod.POST,"/api/post/*/like").authenticated();
        http.authorizeRequests().antMatchers(HttpMethod.GET,"/api/post/*/like").permitAll();

        http.authorizeRequests().antMatchers(HttpMethod.POST,"/api/post/*/image").authenticated(); //owner
        http.authorizeRequests().antMatchers(HttpMethod.GET,"/api/post/*/image").permitAll();

        // /users
        http.authorizeRequests().antMatchers(HttpMethod.GET,"/api/users").permitAll();
        http.authorizeRequests().antMatchers(HttpMethod.GET,"/api/users/filtered").permitAll();
        http.authorizeRequests().antMatchers(HttpMethod.GET,"/api/users/*").permitAll();

        // /posts
        http.authorizeRequests().antMatchers(HttpMethod.GET,"/api/posts").permitAll();
        http.authorizeRequests().antMatchers(HttpMethod.GET,"/api/posts/filtered").permitAll();
        http.authorizeRequests().antMatchers(HttpMethod.GET,"/api/posts/reported","/api/posts/reported/*").hasAnyRole("admin");
        http.authorizeRequests().antMatchers(HttpMethod.GET,"/api/posts/reports","/api/posts/reports/*").hasAnyRole("admin");
        http.authorizeRequests().antMatchers(HttpMethod.GET,"/api/posts/*").permitAll();

        // /comments
        http.authorizeRequests().antMatchers(HttpMethod.GET,"/api/comments/*","/api/comments/*/*").permitAll();
        http.authorizeRequests().antMatchers(HttpMethod.GET,"/api/comments/reported","/api/comments/reported/*").hasAnyRole("admin");
        http.authorizeRequests().antMatchers(HttpMethod.GET,"/api/comments/reports","/api/comments/reports/*").hasAnyRole("admin");

        // /comment
        http.authorizeRequests().antMatchers(HttpMethod.POST,"/api/post/*").authenticated();
        http.authorizeRequests().antMatchers(HttpMethod.GET,"/api/comment/*").permitAll();
        http.authorizeRequests().antMatchers(HttpMethod.PUT,"/api/comment/*").authenticated(); //admin + owner
        http.authorizeRequests().antMatchers(HttpMethod.PATCH,"/api/comment/*").authenticated(); //admin + owner
        http.authorizeRequests().antMatchers(HttpMethod.DELETE,"/api/comment/*").authenticated(); //admin + owner

        http.authorizeRequests().antMatchers(HttpMethod.POST,"/api/comment/*/report").authenticated();
        http.authorizeRequests().antMatchers(HttpMethod.GET,"/api/comment/*/report/*").hasAnyRole("admin");
        http.authorizeRequests().antMatchers(HttpMethod.PUT,"/api/comment/*/report/*").authenticated(); //owner
        http.authorizeRequests().antMatchers(HttpMethod.PATCH,"/api/comment/*/report/*").authenticated(); //owner
        http.authorizeRequests().antMatchers(HttpMethod.DELETE,"/api/comment/*/report/*").authenticated(); //owner
        http.authorizeRequests().antMatchers(HttpMethod.GET,"/api/comment/*/reports","/api/comment/*/reports/*").hasAnyRole("admin");


        http.authorizeRequests().antMatchers(HttpMethod.GET,"/api/error/*").hasAnyRole("exceptionHandler");
        http.authorizeRequests().antMatchers(HttpMethod.GET,"/api/errors").hasAnyRole("exceptionHandler");
        http.authorizeRequests().antMatchers(HttpMethod.DELETE,"/api/error/*").hasAnyRole("exceptionHandler");

        http.authorizeRequests().anyRequest().permitAll();//denyAny()

        //csrf
        http.csrf().disable();

        //http basic
        http.httpBasic();
    }
}
