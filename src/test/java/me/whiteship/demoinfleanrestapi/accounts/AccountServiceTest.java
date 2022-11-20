package me.whiteship.demoinfleanrestapi.accounts;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class AccountServiceTest {



    @Autowired
    AccountService accountService;

    @Autowired
    AccountRepository acountRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Test
    public void findByUsername(){
        //Given
        String password = "keesun";
        String username = "keesun@email.com";

        Account account = Account.builder()
                .email(username)
                .password(password)
                .roles(Set.of(AccountRole.ADMIN , AccountRole.USER))
                .build();

        this.accountService.saveAccount(account);

        //When
        UserDetailsService userDetailService = (UserDetailsService)accountService;
        UserDetails userDetails = userDetailService.loadUserByUsername(username);

        //Thne
        assertThat(this.passwordEncoder.matches(password , userDetails.getPassword())).isTrue();

    }

    @Test
    public void findByUsernameFail(){
        String username = "random@email.com";

        /*try{
            accountService.loadUserByUsername(username);
            fail("supposed to be failed");
        } catch(UsernameNotFoundException e){
            assertThat(e.getMessage()).containsSequence(username);
        }*/
        Exception exception = assertThrows(UsernameNotFoundException.class , () -> {
            accountService.loadUserByUsername(username);
        });

        assertThat(exception.getMessage()).contains(username);





    }


}