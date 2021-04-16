package academy.devdojo.springboot2.controller;

import java.util.Collections;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import academy.devdojo.springboot2.model.Anime;
import academy.devdojo.springboot2.requests.anime.AnimePostRequestBody;
import academy.devdojo.springboot2.service.AnimeService;
import academy.devdojo.springboot2.util.AnimeCreator;
import academy.devdojo.springboot2.util.AnimePostRequestBodyCreator;
import academy.devdojo.springboot2.util.AnimePutRequestBodyCreator;

@ExtendWith(SpringExtension.class)
class AnimeControllerTest {
	
	@InjectMocks
	private AnimeController animeController;
	@Mock
	private AnimeService animeServiceMock;
	
	@BeforeEach
	void setUp() {
		
		PageImpl<Anime> animePage = new PageImpl<>(List.of(AnimeCreator.createValidAnime()));
		
		BDDMockito.when(animeServiceMock.listAll(ArgumentMatchers.any())).thenReturn(animePage);
		
		BDDMockito.when(animeServiceMock.listAllNonPageable()).thenReturn(List.of(AnimeCreator.createValidAnime()));
		
		BDDMockito.when(animeServiceMock.findByIdOrThrowBadRequestException(ArgumentMatchers.anyLong()))
										.thenReturn(AnimeCreator.createValidAnime());
		
		BDDMockito.when(animeServiceMock.findByName(ArgumentMatchers.anyString()))
										.thenReturn(List.of(AnimeCreator.createValidAnime()));
		
		BDDMockito.when(animeServiceMock.save(ArgumentMatchers.any(AnimePostRequestBody.class)))
																.thenReturn(AnimeCreator.createValidAnime());		
	}
	
	@Test
	@DisplayName("Returns anime page when successful")
	void list_ReturnsAnimePage_WhenSuccessful() {
		String expectedName = AnimeCreator.createValidAnime().getName();
		Page<Anime> animePage = animeController.list(null).getBody();
		
		Assertions.assertThat(animePage.toList()).isNotEmpty().hasSize(1);
		Assertions.assertThat(animePage).isNotNull();
		Assertions.assertThat(animePage.toList().get(0).getName()).isEqualTo(expectedName);
	}
	
	@Test
	@DisplayName("listAll returns a list of Anime when successful")
	void listAll_ReturnsListOfAnimes_WhenSuccessful() {
		String expectedName = AnimeCreator.createValidAnime().getName();
		List<Anime> animes = animeController.listAll().getBody();
		
		Assertions.assertThat(animes).isNotNull()
										.isNotEmpty()
										.hasSize(1);
		
		Assertions.assertThat(animes.get(0).getName()).isEqualTo(expectedName);
	}
	
	@Test
	@DisplayName("findById returns anime when successful")
	void findById_ReturnsAnime_WhenSuccessful() {
		Long expectedId = AnimeCreator.createValidAnime().getId();
		Anime anime = animeController.findById(1).getBody();
		
		Assertions.assertThat(anime).isNotNull();
		Assertions.assertThat(anime.getId()).isNotNull().isEqualTo(expectedId);
	}
	
	@Test
	@DisplayName("findByName returns a list of anime when successful")
	void findByName_ReturnsListOfAnime_WhenSuccessful() {
		String expectedName = AnimeCreator.createValidAnime().getName();
		List<Anime> animes = animeController.findByName("Anime").getBody();
		
		Assertions.assertThat(animes).isNotEmpty().isNotNull();
		Assertions.assertThat(animes.get(0).getName()).isEqualTo(expectedName);
	}
	
	@Test
	@DisplayName("findByName returns an empty List of anime when anime is not found")
	void findByName_ReturnsEmptyListOfAnime_WhenAnimeIsNotFound() {
		BDDMockito.when(animeServiceMock.findByName(ArgumentMatchers.anyString()))
										.thenReturn(Collections.emptyList());
		List<Anime> animes = animeController.findByName("anime").getBody();
		
		Assertions.assertThat(animes).isNotNull()
										.isEmpty();		
	}
	
	@Test
	@DisplayName("save returns anime when successful")
	void save_ReturnsAnime_WhenSuccessful() {
		Anime anime = animeController.save(AnimePostRequestBodyCreator.createAnimePostRequestBody()).getBody();
		
		Assertions.assertThat(anime).isNotNull().isEqualTo(AnimeCreator.createValidAnime());
	}
	
	@Test
	@DisplayName("replace updates anime when successful")
	void replace_UpdatesAnime_WhenSuccessful() {
		ResponseEntity<Void> entity = animeController.replace(AnimePutRequestBodyCreator.createAnimePutRequestBody());
		
		Assertions.assertThatCode(() -> animeController.replace(AnimePutRequestBodyCreator.createAnimePutRequestBody()))
																							.doesNotThrowAnyException();;
		Assertions.assertThat(entity).isNotNull();
		Assertions.assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
	}
	
	@Test
	@DisplayName("delete removes anime when successful")
	void delete_RemovesAnime_WhenSuccessful() {
		ResponseEntity<Void> entity = animeController.delete(1L);
		
		Assertions.assertThatCode(() -> animeController.delete(1L)).doesNotThrowAnyException();
		Assertions.assertThat(entity).isNotNull();
		Assertions.assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
	}

}
