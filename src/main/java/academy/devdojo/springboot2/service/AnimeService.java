package academy.devdojo.springboot2.service;



import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import academy.devdojo.springboot2.exception.BadRequestException;
import academy.devdojo.springboot2.mapper.AnimeMapper;
import academy.devdojo.springboot2.model.Anime;
import academy.devdojo.springboot2.repository.AnimeRepository;
import academy.devdojo.springboot2.requests.anime.AnimePostRequestBody;
import academy.devdojo.springboot2.requests.anime.AnimePutRequestBody;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AnimeService {
	
	private AnimeRepository animeRepository;	
	
	public Page<Anime> listAll(Pageable pageable){		
		return animeRepository.findAll(pageable);		
	}
	
	public List<Anime> listAllNonPageable() {
		return animeRepository.findAll();
	}
	
	public Anime findByIdOrThrowBadRequestException(long id) {
		return animeRepository.findById(id)
				.orElseThrow(() -> new BadRequestException("Anime not found"));								
	}
	
	public List<Anime> findByName(String name){
		return animeRepository.findByName(name);
	}

	@Transactional
	public Anime save(AnimePostRequestBody animePostRequestBody){			
		return animeRepository.save(AnimeMapper.INSTANCE.toAnime(animePostRequestBody));		
	}
	

	public void delete(long id) {
		animeRepository.delete(findByIdOrThrowBadRequestException(id));		
	}

	public void replace(AnimePutRequestBody animePutRequestBody) {
		findByIdOrThrowBadRequestException(animePutRequestBody.getId());		
		Anime anime = AnimeMapper.INSTANCE.toAnime(animePutRequestBody);
		
		animeRepository.save(anime);		
	}

}
