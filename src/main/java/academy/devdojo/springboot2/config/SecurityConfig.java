package academy.devdojo.springboot2.config;

//import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;

import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import academy.devdojo.springboot2.service.DevDojoUserDetailsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
@Log4j2
public class SecurityConfig extends WebSecurityConfigurerAdapter{

	private final DevDojoUserDetailsService devDojoUserDetailsService;

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf().disable()
//			.csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()).and()			
			.authorizeRequests()
			.antMatchers("/animes/admin/**").hasRole("ADMIN")
			.antMatchers("/animes/**").hasRole("USER")
			.antMatchers("/actuator/**").permitAll()
			.anyRequest()
			.authenticated()
			.and()
			.formLogin()
			.and()
			.httpBasic();
	}
	
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
		log.info("ENCODED {}", passwordEncoder.encode("academy"));		
		
		auth.inMemoryAuthentication()
			.withUser("alan2")
			.password(passwordEncoder.encode("academy"))
			.roles("USER", "ADMIN")
			.and()
			.withUser("devdojo2")
			.password(passwordEncoder.encode("academy"))
			.roles("USER");
		
		auth.userDetailsService(devDojoUserDetailsService)
			.passwordEncoder(passwordEncoder);
	}
}
