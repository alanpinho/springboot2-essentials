package academy.devdojo.springboot2.requests.anime;

import javax.validation.constraints.NotBlank;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AnimePostRequestBody {	
	
	@NotBlank(message = "The anime name cannot be empty")
	@Schema(description = "This is the anime's name", example="Death Note", required=true)
	private String name;	
		
}
