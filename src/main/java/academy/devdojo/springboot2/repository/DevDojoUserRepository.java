package academy.devdojo.springboot2.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import academy.devdojo.springboot2.model.DevDojoUser;

@Repository
public interface DevDojoUserRepository extends JpaRepository<DevDojoUser, Long>{
	DevDojoUser findByUsername(String username);
}
