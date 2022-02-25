package com.bomber.service;

import com.bomber.asserter.AssertResult;
import com.bomber.asserter.Asserter;
import com.bomber.asserter.util.Asserters;
import com.bomber.http.StringEntityFactory;
import com.bomber.model.Assertion;
import com.bomber.model.HttpSample;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.HtmlUtils;

import java.util.List;
import java.util.Map;

import static com.bomber.http.StringEntityRender.renderPlainText;

@Service
public class HttpSampleExecutorService {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private final RestTemplate stringMessageRestTemplate;

	public HttpSampleExecutorService(RestTemplate stringMessageRestTemplate) {
		this.stringMessageRestTemplate = stringMessageRestTemplate;
	}

	public HttpSampleResult execute(HttpSample httpSample, Map<String, String> context) {
		HttpSampleResult result = new HttpSampleResult();
		try {
			RequestEntity<String> requestEntity = StringEntityFactory.create(httpSample, context);

			logger.info("Request entity:\n{}", renderPlainText(requestEntity));
			long startTime = System.nanoTime();
			ResponseEntity<String> responseEntity = stringMessageRestTemplate.exchange(requestEntity, String.class);
			long elapsedTimeInMillis = (System.nanoTime() - startTime) / 1_000_000;

			String responseMessage = renderPlainText(responseEntity);
			logger.info("Response entity:\n{}", responseMessage);
			result.setContent(responseMessage);
			result.setElapsedTimeInMillis(elapsedTimeInMillis);

			if (httpSample.getAssertions() != null) {
				AssertResult assertResult = assertThat(responseMessage, httpSample.getAssertions());
				if (!assertResult.isSuccessful()) {
					logger.error("Assert failure: {}", assertResult.getError());
					result.setError("Assert failure:\n\t" + assertResult.getError());
				}
			}
		} catch (HttpClientErrorException e) {
			// eg. 404 Not Found
			result.setError(e.getStatusCode() + "\n" + HtmlUtils.htmlEscape(e.getResponseBodyAsString()));
			logger.error(e.getMessage());
		} catch (Exception e) {
			result.setError(e.getMessage());
			logger.error("execute request failed", e);
		}
		return result;
	}

	private static AssertResult assertThat(String text, List<Assertion> models) {
		return models.stream().map(model -> assertThat(text, model)).filter(r -> !r.isSuccessful()).findFirst()
			.orElse(AssertResult.success());
	}

	private static AssertResult assertThat(String text, Assertion model) {
		Asserter asserter = Asserters.create(model.getAsserter());
		com.bomber.asserter.Assertion assertion = new com.bomber.asserter.Assertion();
		assertion.setCondition(model.getCondition());
		assertion.setExpression(model.getExpression());
		assertion.setExpected(model.getExpected());
		assertion.setText(text);
		return asserter.run(assertion);
	}

}
