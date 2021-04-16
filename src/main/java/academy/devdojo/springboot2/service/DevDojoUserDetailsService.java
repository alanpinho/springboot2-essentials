package academy.devdojo.springboot2.service;

import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import academy.devdojo.springboot2.repository.DevDojoUserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DevDojoUserDetailsService implements UserDetailsService{

		private final DevDojoUserRepository devDojoUserRepository;
		
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {		
		return Optional.ofNullable(devDojoUserRepository
				.findByUsername(username)).orElseThrow(() -> new UsernameNotFoundException("DevDojo User not found"));
									
	}

}
