package io.github.ballotguard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Timer;
import java.util.TimerTask;

@SpringBootApplication
public class BallotguardApplication {

	private static RestTemplate restTemplate = new RestTemplate();

	public static void main(String[] args) {
		SpringApplication.run(BallotguardApplication.class, args);

		Timer timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				String url = "https://ballotguard-api.onrender.com/public/health-check";
				ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
				System.out.println("Keeping server alive. Response: " + response.getStatusCode());

			}
		}, 60000, 600000);
	}

}
