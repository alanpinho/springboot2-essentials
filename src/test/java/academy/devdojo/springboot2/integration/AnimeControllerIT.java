package academy.devdojo.springboot2.integration;

import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;

import academy.devdojo.springboot2.model.Anime;
import academy.devdojo.springboot2.model.DevDojoUser;
import academy.devdojo.springboot2.repository.AnimeRepository;
import academy.devdojo.springboot2.repository.DevDojoUserRepository;
import academy.devdojo.springboot2.requests.anime.AnimePostRequestBody;
import academy.devdojo.springboot2.util.AnimeCreator;
import academy.devdojo.springboot2.util.AnimePostRequestBodyCreator;
import academy.devdojo.springboot2.wrapper.PageableResponse;
import lombok.extern.log4j.Log4j2;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
@Log4j2
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
public class AnimeControllerIT {
	
	@Autowired
	@Qualifier(value= "testRestTemplateUserCreator")
	private TestRestTemplate testRestTemplateRoleUser;
	
	@Autowired
	@Qualifier(value = "testRestTemplateAdminCreator")
	private TestRestTemplate testRestTemplateRoleAdmin;
	
	@Autowired
	private AnimeRepository animeRepository;
	
	@Autowired
	private DevDojoUserRepository devDojoUserRepository;
	
	private static final DevDojoUser USER = DevDojoUser.builder()
			.name("DevDojo Academy")
			.username("devdojo")
			.password("{bcrypt}$2a$10$Rn.oqbk7nqai1LpVtSo.yu9vQdvfZp6Czk4FikLIgNZc7vXMLc1W6")
			.authorities("ROLE_USER")
			.build();
	
	private static final DevDojoUser ADMIN = DevDojoUser.builder()
			.name("Alan Junior")
			.username("alan")
			.password("{bcrypt}$2a$10$Rn.oqbk7nqai1LpVtSo.yu9vQdvfZp6Czk4FikLIgNZc7vXMLc1W6")
			.authorities("ROLE_USER,ROLE_ADMIN")
			.build();
	
	
	@TestConfiguration
	@Lazy
	static class Config {
		@Bean("testRestTemplateUserCreator")
		public TestRestTemplate testRestTemplateUserCreator(@Value("${local.server.port}") int port) {
			RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder()
					.rootUri("http://localhost:"+port)
					.basicAuthentication("devdojo", "academy");
			return new TestRestTemplate(restTemplateBuilder);			
		}
		
		@Bean("testRestTemplateAdminCreator")
		public TestRestTemplate testRestTemplateAdminCreator(@Value("${local.server.port}") int port) {
			RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder()
					.rootUri("http://localhost:"+port)
					.basicAuthentication("alan", "academy");
			return new TestRestTemplate(restTemplateBuilder);
		}
	}
		
	
	@Test
	@DisplayName("list returns anime page when successful")
	void list_ReturnsAnimePage_WhenSuccessful() {
		Anime savedAnime = animeRepository.save(AnimeCreator.createAnimeToBeSaved());
		
		devDojoUserRepository.save(USER);

		
		String expectedName = savedAnime.getName();
		log.info(savedAnime);
		
		PageableResponse<Anime> animePage = testRestTemplateRoleUser.exchange(
																"/animes", 
																HttpMethod.GET, 
																null, 
																new ParameterizedTypeReference<PageableResponse<Anime>>() {})
														.getBody();
		
		
		Assertions.assertThat(animePage.toList()).isNotEmpty().hasSize(1);
		Assertions.assertThat(animePage).isNotNull();
		Assertions.assertThat(animePage.toList().get(0).getName()).isEqualTo(expectedName);
	}
	
	@Test
	@DisplayName("listAll returns a list of Anime when successful")
	void listAll_ReturnsListOfAnimes_WhenSuccessful() {
		Anime savedAnime = animeRepository.save(AnimeCreator.createValidAnime());
		devDojoUserRepository.save(USER);
		
		String expectedName = savedAnime.getName();
		List<Anime> animes = testRestTemplateRoleUser.exchange(
												"/animes/all", 
												HttpMethod.GET, 
												null, 
												new ParameterizedTypeReference<List<Anime>>() {})
												.getBody();
		log.info(savedAnime);
		
		Assertions.assertThat(animes).isNotNull()
									.isNotEmpty()
									.hasSize(1);
		
		Assertions.assertThat(animes.get(0).getName()).isEqualTo(expectedName);
	}
	
	@Test
	@DisplayName("findById returns anime when successful")
	void findById_ReturnsAnime_WhenSuccessful() {
		//persisting in database
		Anime savedAnime = animeRepository.save(AnimeCreator.createAnimeToBeSaved());
		//persisting USER in database
		devDojoUserRepository.save(USER);
		//id to check
		Long expectedId = savedAnime.getId();		
		// req
		Anime anime = testRestTemplateRoleUser.getForObject("/animes/{id}", Anime.class, expectedId);
		//log.info("ANIME {}", anime);
		//log.info("SAVEDANIME {}", savedAnime);
		
		Assertions.assertThat(anime).isNotNull();
		Assertions.assertThat(anime.getId()).isEqualTo(savedAnime.getId());
	}
	
	@Test
	@DisplayName("findById returns anime by id when successful")
	void findById_ReturnsAnimeById_WhenSuccessful() {		
		Anime savedAnime = animeRepository.save(AnimeCreator.createAnimeToBeSaved());		
		devDojoUserRepository.save(USER);
		
		Long expectedId = savedAnime.getId();		
		Anime anime = testRestTemplateRoleUser.getForObject("/animes/by-id/{id}", Anime.class, expectedId);		
		
		Assertions.assertThat(anime).isNotNull();
		Assertions.assertThat(anime.getId()).isEqualTo(savedAnime.getId());
	}
	
	@Test
	@DisplayName("findByName returns a list of anime when successful")
	void findByName_ReturnsListOfAnime_WhenSuccessful() {
		Anime animeSaved = animeRepository.save(AnimeCreator.createAnimeToBeSaved());
		devDojoUserRepository.save(USER);
		String expectedName = animeSaved.getName();
		
		String url = String.format("/animes/find?name=%s", expectedName);
		
		List<Anime> animes = testRestTemplateRoleUser.exchange(url, HttpMethod.GET, null,
										new ParameterizedTypeReference<List<Anime>>() {}).getBody();
			
		Assertions.assertThat(animes).isNotEmpty().isNotNull();
		Assertions.assertThat(animes.get(0).getName()).isEqualTo(expectedName);
	}
	
	@Test
	@DisplayName("findByName returns an empty List of anime when anime is not found")
	void findByName_ReturnsEmptyListOfAnime_WhenAnimeIsNotFound() {
		devDojoUserRepository.save(USER);
		List<Anime> animes = testRestTemplateRoleUser.exchange("/animes/find?name=dbz", 
										HttpMethod.GET, 
										null, 
										new ParameterizedTypeReference<List<Anime>>() {})
								.getBody();
		
		
		Assertions.assertThat(animes).isNotNull().isEmpty();
	}
	
	@Test
	@DisplayName("save persists anime when successful")
	void save_PersistsAnime_WhenSuccessful() {
		AnimePostRequestBody animePostRequestBody = AnimePostRequestBodyCreator.createAnimePostRequestBody();
		devDojoUserRepository.save(USER);
		
		ResponseEntity<Anime> animeResponseEntity= testRestTemplateRoleUser.postForEntity(
														"/animes", 
														animePostRequestBody, 
														Anime.class);
		
		Assertions.assertThat(animeResponseEntity).isNotNull();
		Assertions.assertThat(animeResponseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		Assertions.assertThat(animeResponseEntity.getBody()).isNotNull();
		Assertions.assertThat(animeResponseEntity.getBody().getId()).isNotNull();
		
	}
	
	@Test
	@DisplayName("replace updates anime when successful")
	void replace_UpdatesAnime_WhenSuccessful() {
		Anime animeSaved = animeRepository.save(AnimeCreator.createAnimeToBeSaved());
		devDojoUserRepository.save(USER);
		animeSaved.setName("new name");
		
		ResponseEntity<Void> animeResponseEntity = testRestTemplateRoleUser.exchange("/animes", 
																				HttpMethod.PUT, 
																				new HttpEntity<>(animeSaved), 
																				Void.class);
		
		Assertions.assertThat(animeResponseEntity).isNotNull();
		Assertions.assertThat(animeResponseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
		
	}
	
	@Test
	@DisplayName("delete removes anime when successful")
	void delete_RemovesAnime_WhenSuccessful() {
		Anime animeSaved = animeRepository.save(AnimeCreator.createAnimeToBeSaved());
		devDojoUserRepository.save(ADMIN);
		
		ResponseEntity<Void> animeResponseEntity = testRestTemplateRoleAdmin.exchange("/animes/admin/{id}", 
																				HttpMethod.DELETE, 
																				null, 
																				Void.class, 
																				animeSaved.getId());
		Assertions.assertThat(animeResponseEntity).isNotNull();
		Assertions.assertThat(animeResponseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
	}
	
	@Test
	@DisplayName("delete returns 403 when user is not Admin")
	void delete_Returns403_WhenUserIsNotAdmin() {
		Anime animeSaved = animeRepository.save(AnimeCreator.createAnimeToBeSaved());
		devDojoUserRepository.save(USER);
		
		ResponseEntity<Void> animeResponseEntity = testRestTemplateRoleUser.exchange("/animes/admin/{id}", 
				HttpMethod.DELETE, 
				null, 
				Void.class, 
				animeSaved.getId());
		Assertions.assertThat(animeResponseEntity).isNotNull();
		Assertions.assertThat(animeResponseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
	}
}
