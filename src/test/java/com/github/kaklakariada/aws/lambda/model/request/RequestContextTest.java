package com.github.kaklakariada.aws.lambda.model.request;

import static java.util.Collections.singletonMap;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.nullValue;
import static org.hamcrest.core.IsSame.sameInstance;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RequestContextTest {

	private static final String KEY = "key";
	private static final String VALUE = "value";
	private RequestContext requestContext;
	private Map<String, String> map;

	@BeforeEach
	public void setup() {
		requestContext = new RequestContext();
		map = singletonMap(KEY, VALUE);
	}

	@Test
	void testNullHeadersReturnsEmptyMap() {
		requestContext.setAuthorizer(null);
		assertThat(requestContext.getAuthorizer().size(), equalTo(0));
		assertThat(requestContext.getAuthorizer(KEY), nullValue());
	}

	@Test
	void testNotNullHeaders() {
		requestContext.setAuthorizer(map);
		assertThat(requestContext.getAuthorizer(), sameInstance(map));
		assertThat(requestContext.getAuthorizer(KEY), equalTo(VALUE));
	}
}
