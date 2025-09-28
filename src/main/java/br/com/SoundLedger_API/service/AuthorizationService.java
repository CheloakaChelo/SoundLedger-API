package br.com.SoundLedger_API.service;

import br.com.SoundLedger_API.dao.IUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AuthorizationService implements UserDetailsService {

    @Autowired
    IUser dao;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return dao.findByEmail(email);
    }
}
