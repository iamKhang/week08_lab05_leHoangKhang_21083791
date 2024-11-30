package vn.edu.iud.fit.lehoangkhang.week08_lab05_lehoangkhang_21083791.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import vn.edu.iud.fit.lehoangkhang.week08_lab05_lehoangkhang_21083791.enums.AccountRole;
import vn.edu.iud.fit.lehoangkhang.week08_lab05_lehoangkhang_21083791.models.Account;
import vn.edu.iud.fit.lehoangkhang.week08_lab05_lehoangkhang_21083791.repositories.AccountRepository;
import vn.edu.iud.fit.lehoangkhang.week08_lab05_lehoangkhang_21083791.models.CustomUserDetails;

import java.util.Collections;
import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private AccountRepository accountRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = accountRepository.findByCandidate_EmailOrCompany_Email(username, username)
            .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy tài khoản"));

        String displayName;
        String email;
        
        if (account.getRole() == AccountRole.CANDIDATE && account.getCandidate() != null) {
            displayName = account.getCandidate().getFullName();
            email = account.getCandidate().getEmail();
        } else if (account.getRole() == AccountRole.EMPLOYER && account.getCompany() != null) {
            displayName = account.getCompany().getName();
            email = account.getCompany().getEmail();
        } else {
            throw new UsernameNotFoundException("Tài khoản không hợp lệ");
        }

        List<GrantedAuthority> authorities = Collections.singletonList(
            new SimpleGrantedAuthority("ROLE_" + account.getRole().name())
        );

        return new CustomUserDetails(
            email,
            account.getPassword(),
            authorities,
            displayName
        );
    }
}
