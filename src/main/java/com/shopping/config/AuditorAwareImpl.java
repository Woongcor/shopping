package com.shopping.config;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public class AuditorAwareImpl implements AuditorAware<String> {
    @Override
    public Optional<String> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication() ;
        String userId = "" ;
        if(authentication != null){
            userId = authentication.getName() ; // getName에 이메일이 들어감, securityconfig에서 usernameparameter에 이메일이 들어가 있기 때문에 이메일이 들어간다.
        }


        return Optional.of(userId) ;
    }
}
