package com.elit.outildedecision.filters;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

public class JwtAuthorizationFilter extends OncePerRequestFilter {
  @Value("${jwt.secret}")
  private String jwtSecret;

  /**
   * This method is called by the servlet container each time a request/response pair is passed
   * through the chain due to a client request for a resource at the end of the chain.
   *
   * @param request  The request to process
   * @param response The response associated with the request
   * @param filterChain The filter chain
   * @throws ServletException if the processing fails for any other reason
   * @throws IOException if an I/O error occurs during this filter's processing of the request
   */
  @Override
  protected void doFilterInternal(final HttpServletRequest request,
                                  final HttpServletResponse response,
                                  final FilterChain filterChain)
      throws ServletException, IOException {
    if ("/refreshToken".equals(request.getServletPath())) {
      filterChain.doFilter(request, response);
    } else {
      String jwtAuthorizationToken = request.getHeader("Authorization");
      if (jwtAuthorizationToken != null && jwtAuthorizationToken.startsWith("Bearer ")) {
        try {
          String jwt = jwtAuthorizationToken.substring(7);
          Algorithm algorithm =
              Algorithm.HMAC256(jwtSecret);
          JWTVerifier verifier =
              JWT.require(algorithm).build(); // Require an algorithm and a secret:
          DecodedJWT decodedJWT = verifier.verify(jwt);
          String username = decodedJWT.getSubject();
          String[] roles = decodedJWT.getClaim("roles").asArray(String.class);
          Collection<GrantedAuthority> authorities = new ArrayList<>();
          for (String r : roles) {
            authorities.add(new SimpleGrantedAuthority(r));
          }
          UsernamePasswordAuthenticationToken authentificationToken =
              new UsernamePasswordAuthenticationToken(username, null, authorities);
          SecurityContextHolder.getContext().setAuthentication(authentificationToken);
          filterChain.doFilter(request, response);
        } catch (Exception e) {
          response.setHeader("error", "Invalid token");
          response.sendError(403);
        }

      } else {
        filterChain.doFilter(request, response);
      }
    }
  }
}
