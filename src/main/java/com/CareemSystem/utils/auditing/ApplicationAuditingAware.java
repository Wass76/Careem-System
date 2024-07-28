package com.CareemSystem.utils.auditing;


import com.CareemSystem.user.Model.Client;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public class ApplicationAuditingAware implements AuditorAware<Integer> {

    @Override
    public Optional<Integer> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();
        if(authentication == null ||
        ! authentication.isAuthenticated() ||
         authentication instanceof AnonymousAuthenticationToken){
            return Optional.empty();
        }
        Client userPrincipal = (Client) authentication.getPrincipal();
        return Optional.ofNullable(userPrincipal.getId());
    }
}
