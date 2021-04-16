package academy.devdojo.springboot2.exception;

import java.time.LocalDateTime;

import lombok.Data;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
public class ExceptionDetails {
	private String title;
	private Integer status;
	private String details;	
	private String developerMessage;
	private LocalDateTime timestamp;

}
