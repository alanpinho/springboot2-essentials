package academy.devdojo.springboot2.client;

import java.util.Arrays;
import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import academy.devdojo.springboot2.model.Anime;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class SpringClient {
	public static void main(String[] args) {
		ResponseEntity<Anime> entity = new RestTemplate().getForEntity("http://localhost:8080/animes/{id}", Anime.class, 5);
		//log.info(entity);
		
		Anime object = new RestTemplate().getForObject("http://localhost:8080/animes/{id}", Anime.class, 8);
		log.info(object);
		
		Anime[] animes = new RestTemplate().getForObject("http://localhost:8080/animes/all", Anime[].class);
		log.info(Arrays.toString(animes));
		
		ResponseEntity<List<Anime>> animeList = new RestTemplate().exchange("http://localhost:8080/animes/all", 
				HttpMethod.GET, null, new ParameterizedTypeReference<>() {});
		
		log.info(animeList);
		
		//Anime kingdom = Anime.builder().name("Kingdom").build();
		
		//Anime kingdomSaved = new RestTemplate().postForObject("http://localhost:8080/animes", kingdom, Anime.class, HttpMethod.POST);
		//log.info("saved anime {}", kingdomSaved);
		
		//POST
		Anime samuraiChamploo = Anime.builder().name("Samurai Champloo").build();
		ResponseEntity<Anime> samuraiChamplooSaved = new RestTemplate().exchange("http://localhost:8080/animes", 
																	HttpMethod.POST,
																	new HttpEntity<>(samuraiChamploo, createJsonHeaders()),
																	Anime.class);
		log.info(samuraiChamplooSaved);
		
		//PUT
		Anime animeToBeUpdated = samuraiChamplooSaved.getBody();
		animeToBeUpdated.setName("Samurai Champloo 2");
		
		ResponseEntity<Void> samuraiChamplooUpdated = new RestTemplate().exchange("http://localhost:8080/animes", 
																			HttpMethod.PUT, 
																			new HttpEntity<>(animeToBeUpdated, createJsonHeaders()),
																			Void.class);
		
		log.info(samuraiChamplooUpdated);
		
		//DELETE
		ResponseEntity<Void> samuraiChamplooDeleted = new RestTemplate().exchange("http://localhost:8080/animes/{id}", 
									HttpMethod.DELETE,
									null,
									Void.class, 
									animeToBeUpdated.getId());
		log.info(samuraiChamplooDeleted);
		
	}
	
	private static HttpHeaders createJsonHeaders() {
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		return httpHeaders;		
	} 
}
