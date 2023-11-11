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
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class GWConfig {
    @Bean
    public RouteLocator configurarRutas(RouteLocatorBuilder builder,
                                        /*@Value("${gatewayTP.url-estaciones}") String uriEstaciones,
                                        @Value("${gatewayTP.url-alquileres}") String uriAlquileres,
                                        @Value("${gatewayTP.url-tarifas}") String uriTarifas) */
                                        @Value("http://localhost:8081") String uriEstaciones,
                                        @Value("http://localhost:8082") String uriAlquileres,
                                        @Value("http://localhost:8083") String uriTarifas){
        return builder.routes()
                // Ruteo al Microservicio de Estaciones
                .route(p -> p.path("/api/estaciones/**").uri(uriEstaciones))
                // Ruteo al Microservicio de Alquileres
                .route(p -> p.path("/api/entradas/**").uri(uriAlquileres))
                // Ruteo al Microservicio de Tarifas
                .route(p -> p.path("/api/tarifas/**").uri(uriTarifas))
                .build();
    }


    /*@Bean
    public SecurityWebFilterChain filterChain(ServerHttpSecurity http) throws Exception {
        http.authorizeExchange(exchanges -> exchanges


                        .pathMatchers(HttpMethod.GET, "/api/personas/**")
                        .hasRole("KEMPES_ADMIN")

                        .pathMatchers("/api/entradas/**")
                        .hasRole("KEMPES_ORGANIZADOR")

                        // Cualquier otra petición...
                        .anyExchange()
                        .authenticated()

                ).oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))
                .csrf(csrf -> csrf.disable());
        return http.build();
    }


    @Bean
    public ReactiveJwtAuthenticationConverter jwtAuthenticationConverter() {
        var jwtAuthenticationConverter = new ReactiveJwtAuthenticationConverter();
        var grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();

        // Se especifica el nombre del claim a analizar
        grantedAuthoritiesConverter.setAuthoritiesClaimName("authorities");
        // Se agrega este prefijo en la conversión por una convención de Spring
        grantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");

        // Se asocia el conversor de Authorities al Bean que convierte el token JWT a un objeto Authorization
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(
                new ReactiveJwtGrantedAuthoritiesConverterAdapter(grantedAuthoritiesConverter));
        // También se puede cambiar el claim que corresponde al nombre que luego se utilizará en el objeto
        // Authorization
        // jwtAuthenticationConverter.setPrincipalClaimName("user_name");

        return jwtAuthenticationConverter;
    }
    */


}

