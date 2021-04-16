package academy.devdojo.springboot2.util;

import academy.devdojo.springboot2.requests.anime.AnimePostRequestBody;

public class AnimePostRequestBodyCreator {
	public static AnimePostRequestBody createAnimePostRequestBody() {
		return AnimePostRequestBody.builder()
							.name(AnimeCreator.createAnimeToBeSaved().getName())
							.build();
	}
}
