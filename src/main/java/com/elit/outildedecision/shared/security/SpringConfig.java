
package com.elit.outildedecision.shared.security;

import com.elit.outildedecision.shared.auth.service.JwtService;
import com.elit.outildedecision.shared.filters.JwtAuthenticationFilter;
import com.elit.outildedecision.shared.filters.JwtAuthorizationFilter;
import com.elit.outildedecision.shared.user.service.UserService;
import com.elit.outildedecision.shared.user.service.implementation.UserServiceImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.http.HttpMethod;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.List;


/** Spring config class. */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SpringConfig {

  private final UserServiceImpl customUserDetailsService;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;

  @Value("${frontend.value-url}")
  private String frontEndUrl;


  /**
   * Construit la configuration Spring Security de l'application.
   *
   * @param customUserDetailsService le {@link UserServiceImpl} utilisé comme UserDetailsService applicatif.
   * @param passwordEncoder l'encodeur de mots de passe.
   * @param jwtService le service JWT utilisé par les filtres d'authentification et d'autorisation.
   */
  public SpringConfig(
    final UserServiceImpl customUserDetailsService,
    final PasswordEncoder passwordEncoder,
    final JwtService jwtService) {

    this.customUserDetailsService = customUserDetailsService;
    this.passwordEncoder = passwordEncoder;
    this.jwtService = jwtService;
  }

  /**
   * Expose un {@link MethodSecurityExpressionHandler} personnalisé pour
   * permettre l'utilisation d'expressions métier dans les annotations
   * de sécurité method-level (ex: {@code @PreAuthorize}).
   *
   * @param userService le service métier injecté dans la root d'expressions
   * @return un {@link MethodSecurityExpressionHandler} basé sur
   *         {@link CustomMethodSecurityExpressionHandler}
   */
  @Bean
  public MethodSecurityExpressionHandler methodSecurityExpressionHandler(
    final UserService userService) {
    return new CustomMethodSecurityExpressionHandler(userService);
  }

  /**
   * Provider d’authentification DAO configuré.
   * Volontairement non exposé en bean : un bean {@link AuthenticationProvider}
   * global déclencherait l'avertissement Spring Security sur l'auto-configuration
   * du {@code UserDetailsService}. Il est câblé explicitement dans
   * l'{@link AuthenticationManager} et la {@link SecurityFilterChain}.
   *
   * @return un {@link AuthenticationProvider} prêt à l'emploi
   */
  private AuthenticationProvider authenticationProvider() {
    final DaoAuthenticationProvider provider = new DaoAuthenticationProvider(customUserDetailsService);
    provider.setPasswordEncoder(passwordEncoder);
    return provider;
  }

  /**
   * Expose un {@link AuthenticationManager} explicite basé sur le
   * {@link DaoAuthenticationProvider} applicatif, utilisé par le filtre de login JWT.
   *
   * @return l'{@link AuthenticationManager} de l'application
   */
  @Bean
  public AuthenticationManager authenticationManager() {
    return new ProviderManager(authenticationProvider());
  }

  /**
   * Chaîne de filtres de sécurité HTTP (stateless JWT).
   *
   * @param http l'objet de configuration HTTP Security
   * @param authenticationManager le gestionnaire d'authentification à utiliser
   * @return la {@link SecurityFilterChain} construite
   * @throws Exception en cas de problème de configuration
   */
  @Bean
  public SecurityFilterChain securityFilterChain(
    final org.springframework.security.config.annotation.web.builders.HttpSecurity http,
    final AuthenticationManager authenticationManager) throws Exception {

    // Filtre d’authentification JWT (login)
    final JwtAuthenticationFilter jwtAuthenticationFilter =
      new JwtAuthenticationFilter(authenticationManager, customUserDetailsService, jwtService);
    jwtAuthenticationFilter.setRequiresAuthenticationRequestMatcher(
      PathPatternRequestMatcher.withDefaults().matcher(HttpMethod.POST, "/login")
    );

    http
      // CSRF désactivé (API stateless JWT)
      .csrf(csrf -> csrf.disable())

      // Autoriser les frames (H2 console)
      .headers(headers -> headers.frameOptions(frame -> frame.disable()))

      // CORS
      .cors(cors -> cors.configurationSource(corsConfigurationSource()))

      // Stateless (JWT)
      .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

      // Autorisations : requestMatchers() remplace antMatchers() en Boot 3
      .authorizeHttpRequests(auth -> auth
        // H2 console (optionnel)
        .requestMatchers("/h2-console/**").permitAll()
        // Swagger (springdoc 2.x avec Boot 3)
        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
        // Endpoints publics
        .requestMatchers("/", "/login", "/refreshToken").permitAll()
        .anyRequest().permitAll()
      )

      // Brancher le provider d'authentification
      .authenticationProvider(authenticationProvider())

      // Filtres :
      // - Filtre d’authentification (login JWT)
      .addFilterAt(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
      // - Filtre d’autorisation (vérification du token)
      .addFilterBefore(new JwtAuthorizationFilter(jwtService, customUserDetailsService),
        UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }

  /**
   * Crée et configure la source CORS de l'application.
   *
   * @return la {@link CorsConfigurationSource} contenant la configuration CORS
   */
  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    final CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOriginPatterns(List.of(frontEndUrl, "*"));
    configuration.setAllowedMethods(List.of("HEAD", "GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
    configuration.setAllowCredentials(true);
    configuration.setAllowedHeaders(List.of(
      "Authorization",
      "Content-Type",
      "X-Requested-With",
      "accept",
      "Origin",
      "Access-Control-Request-Method",
      "Access-Control-Request-Headers",
      "Access-Control-Allow-Origin"
    ));
    final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }
}
