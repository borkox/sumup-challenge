package com.bmarkov.challenge.rest;

import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;

import java.nio.charset.Charset;

import static org.apache.commons.io.IOUtils.resourceToString;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class JobRestControllerTest {

	@LocalServerPort
	private int port;

	@Autowired
	private TestRestTemplate restTemplate;

	@Test
	void testTasksAsBash() throws Exception {
		String request = resourceToString("/example.json", Charset.forName("UTF-8"));
		String expected = resourceToString("/expected_bash.txt", Charset.forName("UTF-8"));
		expected = expected.replaceAll("\r","");
		assertThat(this.restTemplate.postForObject(
				"http://localhost:" + port + "/api/job/tasksAsBash",
				request,
				String.class)).isEqualTo(expected);
	}

	@Test
	void testTasksInOrder() throws Exception {
		String request = resourceToString("/example.json", Charset.forName("UTF-8"));
		String expected = resourceToString("/expected_tasks.json", Charset.forName("UTF-8"));
		expected = expected.replaceAll("\r","");
		JSONAssert.assertEquals(
				expected,
				this.restTemplate.postForObject(
				"http://localhost:" + port + "/api/job/tasksInOrder",
				request,
				String.class),JSONCompareMode.STRICT);

	}
}