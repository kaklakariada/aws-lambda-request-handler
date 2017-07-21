package com.github.kaklakariada.aws.lambda.service;

import static java.util.Collections.singletonMap;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.github.kaklakariada.aws.lambda.model.request.ApiGatewayRequest;

public class ServiceCacheTest {

	private static final String STAGE1 = "stage1";
	private static final String STAGE2 = "stage2";
	private static final String STAGE_ENV_VARIABLE = "stage";
	private static final Map<String, String> ENV_STAGE1 = singletonMap(STAGE_ENV_VARIABLE, STAGE1);
	private static final Map<String, String> ENV_STAGE2 = singletonMap(STAGE_ENV_VARIABLE, STAGE2);
	private static final String STAGE_VAR_CONFIG_VALUE_NAME = "configValue";
	private static final String SETTING_VAL_A = "val1";
	private static final String SETTING_VAL_B = "val2";

	private ServiceCache<MyServiceParams> cache;
	private ApiGatewayRequest requestA;
	private ApiGatewayRequest requestB;

	@Before
	public void setup() {
		cache = new ServiceCache<>(new MyServiceFactory());
		requestA = new ApiGatewayRequest();
		requestA.setStageVariables(singletonMap(STAGE_VAR_CONFIG_VALUE_NAME, SETTING_VAL_A));
		requestB = new ApiGatewayRequest();
		requestB.setStageVariables(singletonMap(STAGE_VAR_CONFIG_VALUE_NAME, SETTING_VAL_B));
	}

	@Test
	public void testServicesRegistered() {
		final MyServiceParams paramStage1ValA = cache.getParams(requestA, ENV_STAGE1);
		final MyServiceParams paramStage1ValB = cache.getParams(requestB, ENV_STAGE1);
		final MyServiceParams paramStage2ValA = cache.getParams(requestA, ENV_STAGE2);
		final MyServiceParams paramStage2ValB = cache.getParams(requestB, ENV_STAGE2);

		final MyServiceB serviceStage1ValA = cache.getService(MyServiceB.class, paramStage1ValA);
		final MyServiceB serviceStage1ValB = cache.getService(MyServiceB.class, paramStage1ValB);
		final MyServiceB serviceStage2ValA = cache.getService(MyServiceB.class, paramStage2ValA);
		final MyServiceB serviceStage2ValB = cache.getService(MyServiceB.class, paramStage2ValB);

		assertThat(serviceStage1ValA.getValue(), equalTo("Stage: " + STAGE1 + ", config: " + SETTING_VAL_A));
		assertThat(serviceStage1ValB.getValue(), equalTo("Stage: " + STAGE1 + ", config: " + SETTING_VAL_B));
		assertThat(serviceStage2ValA.getValue(), equalTo("Stage: " + STAGE2 + ", config: " + SETTING_VAL_A));
		assertThat(serviceStage2ValB.getValue(), equalTo("Stage: " + STAGE2 + ", config: " + SETTING_VAL_B));
	}

	private static class MyServiceFactory implements ServiceFactory<MyServiceParams> {

		@Override
		public MyServiceParams extractServiceParams(ApiGatewayRequest request, Map<String, String> env) {
			return new MyServiceParams(env.get(STAGE_ENV_VARIABLE),
					request.getStageVariables().get(STAGE_VAR_CONFIG_VALUE_NAME));
		}

		@Override
		public void registerServices(ServiceRegistry registry, MyServiceParams params) {
			final ServiceInitializer<MyServiceA> serviceA = registry.init(() -> new MyServiceA(params.getStage()));
			registry.addService(MyServiceA.class, serviceA);
			registry.addService(MyServiceB.class, () -> new MyServiceB(serviceA.get(), params.getConfigValue()));
		}
	}

	private static class MyServiceA {
		private final String stage;

		public MyServiceA(String stage) {
			this.stage = stage;
		}

		public String getStage() {
			return stage;
		}
	}

	private static class MyServiceB {
		private final MyServiceA serviceA;
		private final String configValue;

		public MyServiceB(MyServiceA serviceA, String configValue) {
			this.serviceA = serviceA;
			this.configValue = configValue;
		}

		public String getValue() {
			return "Stage: " + serviceA.getStage() + ", config: " + configValue;
		}
	}

	private static class MyServiceParams implements ServiceParams {
		private final String stage;
		private final String configValue;

		public MyServiceParams(String stage, String configValue) {
			this.stage = stage;
			this.configValue = configValue;
		}

		public String getStage() {
			return stage;
		}

		public String getConfigValue() {
			return configValue;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((configValue == null) ? 0 : configValue.hashCode());
			result = prime * result + ((stage == null) ? 0 : stage.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			final MyServiceParams other = (MyServiceParams) obj;
			if (configValue == null) {
				if (other.configValue != null) {
					return false;
				}
			} else if (!configValue.equals(other.configValue)) {
				return false;
			}
			if (stage == null) {
				if (other.stage != null) {
					return false;
				}
			} else if (!stage.equals(other.stage)) {
				return false;
			}
			return true;
		}
	}
}
