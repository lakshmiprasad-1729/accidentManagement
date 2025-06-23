package com.vsp.accidentManagement.services;

import com.vsp.accidentManagement.Repo.userRepo;
import com.vsp.accidentManagement.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Optional;


@Component
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    private userRepo userrepo;


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

       User user = userrepo.findByEmail(email).orElseThrow( ()->new UsernameNotFoundException("no user not found exception"+email));



         return new UserPrincipal(user);

    }
}
