package com.tesinitsyn.userservice.service;

import com.tesinitsyn.userservice.repository.MyUserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MyUserDetailsService implements UserDetailsService {

    private final MyUserRepository myUserRepository;

    public MyUserDetailsService(MyUserRepository myUserRepository) {
        this.myUserRepository = myUserRepository;
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return myUserRepository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException(username));
    }

    public Boolean findUser(String username) throws UsernameNotFoundException {
         var user = myUserRepository.findByEmail(username);
         return user.isPresent();
    }
}
