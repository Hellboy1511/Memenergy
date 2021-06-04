package Memenergy.security;

import Memenergy.data.User;
import Memenergy.database.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class UserRepositoryAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    UserService userService;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    LoggedUser loggedUser;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        User user = this.userService.get(authentication.getName());

        String password = (String) authentication.getCredentials();
        if(user == null || !passwordEncoder.matches(password,user.getPassword())){
            throw new BadCredentialsException("Wrong credentials");
        }

        List<GrantedAuthority> roles = new ArrayList<>();
        for (String role :
                user.getRoles()) {
            roles.add(new SimpleGrantedAuthority(role));
        }

        loggedUser.setLoggedUser(user);

        return new UsernamePasswordAuthenticationToken(user.getUsername(),password,roles);

    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
