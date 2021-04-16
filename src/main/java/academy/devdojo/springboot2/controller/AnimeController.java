package academy.devdojo.springboot2.controller;

//import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;

//import java.time.LocalDateTime;
import java.util.List;

import javax.validation.Valid;

import org.springdoc.api.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import academy.devdojo.springboot2.model.Anime;
import academy.devdojo.springboot2.requests.anime.AnimePostRequestBody;
import academy.devdojo.springboot2.requests.anime.AnimePutRequestBody;
import academy.devdojo.springboot2.service.AnimeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
//import academy.devdojo.springboot2.util.DateUtil;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/animes")
@RequiredArgsConstructor
//@AllArgsConstructor
public class AnimeController {
	
	//private final DateUtil dateUtil;
	private final AnimeService animeService;
	
	@GetMapping
	@Operation(summary = "List all animes paginated", description= "The default size is 20, use the parameter size to change the default value",
	tags = {"anime"})
	public ResponseEntity<Page<Anime>> list(@ParameterObject Pageable pageable){
		
		// return Arrays.asList(new Anime("DBZ"), new Anime("Death Note")); // Eu mesmo fiz essa linha e dá certo!
		//log.info(dateUtil.localDateTimeToDatabaseStyle(LocalDateTime.now()));
		
		return ResponseEntity.ok(animeService.listAll(pageable));		
	}
	
	@GetMapping("/all")
	@Operation(tags= {"anime"}, summary="List all animes in the Database")
	public ResponseEntity<List<Anime>> listAll(){		
		return ResponseEntity.ok(animeService.listAllNonPageable());
	}

	
	@GetMapping("/{id}")
	public ResponseEntity<Anime> findById(@PathVariable long id) {
		return ResponseEntity.ok(animeService.findByIdOrThrowBadRequestException(id));
	}
	
	@GetMapping("/by-id/{id}")
	public ResponseEntity<Anime> findByIdAuthenticationPrincipal(@PathVariable long id, 
														@AuthenticationPrincipal UserDetails userDetails) {
		//log.info(userDetails);
		return ResponseEntity.ok(animeService.findByIdOrThrowBadRequestException(id));
	}
	
	@GetMapping("/find")
	public ResponseEntity<List<Anime>> findByName(@RequestParam(defaultValue = "Bleach") String name){
		return ResponseEntity.ok(animeService.findByName(name));
	}
	
	@PostMapping
	public ResponseEntity<Anime> save(@RequestBody @Valid AnimePostRequestBody animePostRequestBody){		
		return new ResponseEntity<>(animeService.save(animePostRequestBody), HttpStatus.CREATED);
	}
	
	@DeleteMapping("/admin/{id}")
	@ApiResponses(value = {@ApiResponse(responseCode = "204", description = "Operation Successful"),
				 @ApiResponse(responseCode="400", description = "When Anime does not exist in the Database")})
	public ResponseEntity<Void> delete(@PathVariable long id){
		animeService.delete(id);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	
	@PutMapping
	public ResponseEntity<Void> replace(@RequestBody AnimePutRequestBody animePutRequestBody){
		animeService.replace(animePutRequestBody);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT); 
	}
	
}
