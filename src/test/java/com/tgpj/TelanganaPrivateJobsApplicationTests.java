package com.tgpj;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
		"spring.mongodb.uri=mongodb://localhost:27017/tgmagazine-test",
		"spring.data.mongodb.auto-index-creation=false",
		"app.jwt.secret=test-only-signing-secret-with-at-least-32-bytes"
})
class TelanganaPrivateJobsApplicationTests {

	@Test
	void contextLoads() {
	}

}
