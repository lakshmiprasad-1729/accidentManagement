package com.vsp.accidentManagement.filters;

import com.vsp.accidentManagement.Repo.userRepo;
import com.vsp.accidentManagement.models.User;
import com.vsp.accidentManagement.utilities.JWTutil;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Component
public class JWTfilter extends OncePerRequestFilter {

    @Autowired
    private JWTutil jwtutil;

    @Autowired
    private userRepo userrepo;

    @Autowired
    private UserDetailsService userDetailsService; // Use Spring's UserDetailsService

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {

        final String header = request.getHeader("Authorization");
        String token = null;
        String email = null;

        // Extract token from Authorization header
        if (header != null && header.startsWith("Bearer "))
            token = header.substring(7);

        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("auth-token".equals(cookie.getName())) {
                    token = cookie.getValue();
                    System.out.println("Found auth-token in cookies");
                    break;
                }
            }
        }

           if(token != null){
               try {
                   email = jwtutil.getemailFromToken(token);
                   System.out.println("Extracted email from token: " + email);
               } catch (ExpiredJwtException e) {
                   System.out.println("JWT token expired: " + e.getMessage());
               } catch (Exception e) {
                   System.out.println("JWT token invalid: " + e.getMessage());
               }
           }

        // Validate token and set authentication
        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            try {
                // Load user details
                UserDetails userDetails = userDetailsService.loadUserByUsername(email);

                // Validate token
                if (jwtutil.validateToken(token, email)) {
                    System.out.println("Token validation successful for user: " + email);

                    // Create authentication token with proper authorities
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );

                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // Set authentication in security context
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    System.out.println("Authentication set successfully");

                } else {
                    System.out.println("Token validation failed for user: " + email);
                }

            } catch (Exception e) {
                System.out.println("Error during authentication: " + e.getMessage());
                e.printStackTrace();
            }
        }

        // Continue filter chain
        chain.doFilter(request, response);
    }
}