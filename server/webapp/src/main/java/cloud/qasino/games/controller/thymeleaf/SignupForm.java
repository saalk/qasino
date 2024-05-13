package cloud.qasino.games.controller.thymeleaf;

import cloud.qasino.games.database.security.Visitor;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

@Getter
@Setter
public class SignupForm {

	private static final String NOT_BLANK_MESSAGE = "{notBlank.message}";
	private static final String EMAIL_MESSAGE = "{email.message}";

	@NotBlank(message = SignupForm.NOT_BLANK_MESSAGE)
	@Email(message = SignupForm.EMAIL_MESSAGE)
	private String email;

	@NotBlank(message = SignupForm.NOT_BLANK_MESSAGE)
	private String username;

    @NotBlank(message = SignupForm.NOT_BLANK_MESSAGE)
	private String password;

	@NotBlank(message = SignupForm.NOT_BLANK_MESSAGE)
	private String alias;

	public Visitor createVisitor() {
        return new Visitor.Builder()
				.withEmail(getEmail())
				.withUsername(getUsername())
				.withPassword(getPassword())
				.withAlias(getAlias())
				.withAliasSequence(1)
				.build();
	}
}
