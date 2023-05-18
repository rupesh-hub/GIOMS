package com.gerp.route.filters;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.cors.reactive.CorsUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

import static org.springframework.http.HttpMethod.*;

/**
 * <p>
 *     Used as global cors filter.
 * </p>
 */
@Configuration
@Slf4j
public class CorsConfiguration extends org.springframework.web.cors.CorsConfiguration {

    @Value("${allowed-origins}")
    private String allowedOrigins;
    private static final List < String > ALLOWED_HEADERS = Arrays.asList( "Referer" , "Content-Type" , "Authorization","x-requested-with","authorization","credential","X-XSRF-TOKEN" );
    private static final List< HttpMethod > ALLOWED_METHODS = Arrays.asList( GET , PUT , POST, DELETE , OPTIONS );
    private static final String COMMA_SEPARATOR = ",";
    private static final String WILDCARD = "*";
    private static final String MAX_AGE = "3600";
    private static final Logger LOGGER = LoggerFactory.getLogger( CorsConfiguration.class );


    @Bean
    public WebFilter corsFilter() {
        return (ServerWebExchange ctx, WebFilterChain chain) -> {
            ServerHttpRequest request = ctx.getRequest();
            if (CorsUtils.isCorsRequest(request)) {

                ServerHttpResponse response = ctx.getResponse();
                HttpHeaders headers = response.getHeaders();
                prepareCorsHeaders( request , response );
//                headers.add("Access-Control-Allow-Origin", allowedOrigins);
//                headers.add("Access-Control-Allow-Methods", ALLOWED_METHODS.stream().);
//                headers.add("Access-Control-Max-Age", MAX_AGE);
//                headers.add("Access-Control-Allow-Headers",String.valueOf(ALLOWED_HEADERS));
//                headers.add("Access-Control-Allow-Credentials", "true");
                if (request.getMethod() == HttpMethod.OPTIONS) {
                    response.setStatusCode(HttpStatus.OK);
                    return Mono.empty();
                }
            }
            return chain.filter(ctx);
        };
    }

    // block access to unknown origins
    private void prepareCorsHeaders ( ServerHttpRequest request , ServerHttpResponse response ) {
        String referer = request.getHeaders().getFirst( "Origin" );
        String allowedOrigin = safeAllowedOrigin(referer);

        LOGGER.debug( "Receiving request from {}, returning allowedOrigins: {}" , referer , allowedOrigin );

        HttpHeaders headers = response.getHeaders();
        headers.setAccessControlAllowOrigin( allowedOrigin );
        headers.setAccessControlAllowMethods( ALLOWED_METHODS );
        headers.setAccessControlAllowHeaders( ALLOWED_HEADERS );
    }

    // get all allowed headers
    /**
     * @param referer comma seperated or single allowed origins value
     * **/
    public String safeAllowedOrigin (String referer) {
        final String allowedOrigin;
        log.info("Allowed origin are following: "+ allowedOrigins);
        if ( allowedOrigins == null) {
            allowedOrigin = null;
        } else if ( allowedOrigins.contains( COMMA_SEPARATOR ) ) {
            allowedOrigin = Arrays.stream( allowedOrigins.split( COMMA_SEPARATOR ) )
                    .map( String::trim )
                    .filter( referer::contains ) // only list if url matches
                    .findFirst().orElse( null );
        } else {
            allowedOrigin = allowedOrigins;
        }
        return allowedOrigin;
    }
}
