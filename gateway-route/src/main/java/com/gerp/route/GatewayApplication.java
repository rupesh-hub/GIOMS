package com.gerp.route;

import org.bouncycastle.util.encoders.Base64;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * <p>
 *     This application handles all the incoming request and handle it respectively.
 * </p>
 */
@SpringBootApplication
@RestController
@EnableDiscoveryClient
public class GatewayApplication {

	private final String DEFAULT_USER = "clientId";
	private final String DEFAULT_PASS = "secret";
	@Autowired private RouteDefinitionLocator locator;

	/**
	 * <p>
	 *     This method is used to route the request to respective micro services.
	 *     Ref: http://localhost:8070/actuator/gateway/routes
	 * </p>
	 * @param builder
	 * @return
	 */
	@Bean
	public RouteLocator routes(RouteLocatorBuilder builder) {
		return builder.routes()
				.route(p -> p.path("/custom")
						.filters(f ->
										f
											.rewritePath("/custom", "/modified/exists")
												.addRequestHeader(HttpHeaders.AUTHORIZATION,"Basic " + new String(
														Base64.encode(
																(DEFAULT_USER + ":" + DEFAULT_PASS).getBytes(StandardCharsets.ISO_8859_1))
												))
											.hystrix(c -> c.setName("custom").setFallbackUri("forward:/fallback"))
						).uri("lb://custom"))
				.build();
	}

	/**
	 * <p>
	 *     This method is used as fallback response if any micro service is down.
	 * </p>
	 * @return
	 */
	@GetMapping("/fallback")
	public ResponseEntity<?> fallback() {
		System.out.println("fallback enabled");
		throw new RuntimeException("ss");
//		HttpHeaders headers = new HttpHeaders();
//		headers.add("fallback", "true");
//		return new ResponseEntity<>(new com.infodev.route.GlobalResponse(false,"Service Currently Not Available",new String[0]), HttpStatus.SERVICE_UNAVAILABLE);
	}

	@Bean
	public List<GroupedOpenApi> apis() {
		List<String> services = Arrays.asList(
				"attendance",
				"usermgmt",
				"kasamu",
				"sbm",
				"darta-chalani",
				"dms",
				"tms",
				"sampati-bibaran",
				"templating-service"
		);
		List<GroupedOpenApi> groups = new ArrayList<>();
		List<RouteDefinition> definitions = locator.getRouteDefinitions().collectList().block();
		definitions.stream()
				.filter(routeDefinition -> services.contains(routeDefinition.getId()))
				.forEach(routeDefinition -> {
					String name = routeDefinition.getId();
					GroupedOpenApi.builder().pathsToMatch("/" + name + "/**").setGroup(name.toUpperCase()).build();
				});
		return groups;
	}

	/**
	 * <p>
	 *     This is just a spring application runner.
	 * </p>
	 * @param args
	 */
	public static void main(String[] args) {
		SpringApplication.run(GatewayApplication.class, args);
	}


}
