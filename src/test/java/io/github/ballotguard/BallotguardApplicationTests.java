package io.github.ballotguard;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.mail.MailSenderAutoConfiguration")
class BallotguardApplicationTests {

	@Test
	void contextLoads() {

	}

}
