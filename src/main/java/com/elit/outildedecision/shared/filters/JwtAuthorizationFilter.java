package com.elit.outildedecision.shared.filters;

import com.elit.outildedecision.shared.auth.exception.InvalidJwtException;
import com.elit.outildedecision.shared.auth.service.AuthenticatedToken;
import com.elit.outildedecision.shared.auth.service.JwtService;
import com.elit.outildedecision.shared.security.AppUserPrincipal;
import com.elit.outildedecision.shared.user.service.implementation.UserServiceImpl;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

/** Jwt authorization filter class. */
public class JwtAuthorizationFilter extends OncePerRequestFilter {

  /**
   * The JWT service used to resolve and verify tokens.
   */
  private final JwtService jwtService;

  private final UserServiceImpl customUserDetailsService;

  /**
   * Constructor of JwtAuthorizationFilter.
   *
   * @param jwtService               the JWT service
   * @param customUserDetailsService user custom service
   */
  public JwtAuthorizationFilter(final JwtService jwtService, final UserServiceImpl customUserDetailsService) {
    this.jwtService = jwtService;
    this.customUserDetailsService = customUserDetailsService;
  }

  /**
   * This method is called by the servlet container each time a request/response pair is passed
   * through the chain due to a client request for a resource at the end of the chain.
   *
   * @param request     The request to process
   * @param response    The response associated with the request
   * @param filterChain The filter chain
   * @throws ServletException if the processing fails for any other reason
   * @throws IOException      if an I/O error occurs during this filter's processing of the request
   */
  @Override
  protected void doFilterInternal(final HttpServletRequest request,
                                  final HttpServletResponse response,
                                  final FilterChain filterChain)
    throws ServletException, IOException {
    if (shouldAllowRequestWithoutAuthentication(request)) {
      filterChain.doFilter(request, response);
      return;
    }
    Optional<String> token = jwtService.resolveToken(request.getHeader("Authorization"));
    if (token.isPresent()) {
      try {
        AuthenticatedToken authenticatedToken = jwtService.verify(token.get());
        AppUserPrincipal appUserPrincipal =
          new AppUserPrincipal(customUserDetailsService.findByEmail(authenticatedToken.subject()));

        Collection<GrantedAuthority> authorities = new ArrayList<>();
        for (String role : authenticatedToken.roles()) {
          authorities.add(new SimpleGrantedAuthority(role));
        }
        UsernamePasswordAuthenticationToken authentificationToken =
          new UsernamePasswordAuthenticationToken(appUserPrincipal, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(authentificationToken);
      } catch (InvalidJwtException e) {
        logger.warn("Invalid JWT rejected", e);
        response.setHeader("error", "Invalid token");
        response.sendError(HttpServletResponse.SC_FORBIDDEN);
        return;
      }
    }
    filterChain.doFilter(request, response);
  }

  /**
   * This method is called by the servlet container each time a request/response pair is passed
   * to check if the request should be allowed without authentication.
   *
   * @param request The request to process
   * @return true if the request should be allowed without authentication, false otherwise
   */
  private boolean shouldAllowRequestWithoutAuthentication(final HttpServletRequest request) {
    String requestURI = request.getRequestURI();
    return requestURI.equals("/login") || requestURI.equals("/refreshToken");
  }
}
