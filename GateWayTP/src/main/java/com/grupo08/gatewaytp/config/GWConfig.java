package com.grupo08.gatewaytp.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtGrantedAuthoritiesConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class GWConfig {
    @Bean
    public RouteLocator configurarRutas(RouteLocatorBuilder builder,
                                        @Value("http://localhost:8081") String uriEstaciones,
                                        @Value("http://localhost:8082") String uriAlquileres,
                                        @Value("http://localhost:8083") String uriTarifas){
        return builder.routes()
                // Ruteo al Microservicio de Estaciones
                .route(p -> p.path("/api/estaciones/**").uri(uriEstaciones))
                // Ruteo al Microservicio de Alquileres
                .route(p -> p.path("/api/alquileres/**").uri(uriAlquileres))
                // Ruteo al Microservicio de Tarifas
                .route(p -> p.path("/api/tarifas/**").uri(uriTarifas))
                .build();
    }


    @Bean
    public SecurityWebFilterChain filterChain(ServerHttpSecurity http) throws Exception {
        http.authorizeExchange(exchanges -> exchanges
                        .pathMatchers(HttpMethod.GET, "/api/alquileres/**").hasRole("ADMINISTRADOR")
                        .pathMatchers(HttpMethod.DELETE, "/api/alquileres/**").hasRole("ADMINISTRADOR")
                        .pathMatchers(HttpMethod.POST, "/api/estaciones/**").hasRole("ADMINISTRADOR")
                        .pathMatchers(HttpMethod.PUT, "/api/estaciones/**").hasRole("ADMINISTRADOR")
                        .pathMatchers(HttpMethod.DELETE, "/api/estaciones/**").hasRole("ADMINISTRADOR")
                        .pathMatchers("/api/tarifas/**").hasRole("ADMINISTRADOR")

                        // Cualquier otra petición...
                        .anyExchange().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))
                .csrf(csrf -> csrf.disable());
        return http.build();
    }


    @Bean
    public ReactiveJwtAuthenticationConverter jwtAuthenticationConverter() {
        var jwtAuthenticationConverter = new ReactiveJwtAuthenticationConverter();
        var grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();

        grantedAuthoritiesConverter.setAuthoritiesClaimName("authorities");

        grantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");

        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(
                new ReactiveJwtGrantedAuthoritiesConverterAdapter(grantedAuthoritiesConverter));


        return jwtAuthenticationConverter;
    }


}

