package com.vsp.accidentManagement.filters;

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
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JWTfilter extends OncePerRequestFilter {

    @Autowired
    private JWTutil jwtutil;

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {

        String token = null;
        String email = null;

        // Skip JWT processing for public endpoints
        String requestURI = request.getRequestURI();
        if (requestURI.equals("/user/login") || requestURI.equals("/user/register")) {
            chain.doFilter(request, response);
            return;
        }

        // Extract token from Authorization header (preferred method)
        final String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            System.out.println("Found Bearer token in Authorization header");
        }

        // Fallback: Extract token from cookies if not found in header
        if (token == null && request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("auth-token".equals(cookie.getName())) {
                    token = cookie.getValue();
                    System.out.println("Found auth-token in cookies");
                    break;
                }
            }
        }

        // Process token if found
        if (token != null) {
            try {
                email = jwtutil.getemailFromToken(token);
                System.out.println("Extracted email from token: " + email);
            } catch (ExpiredJwtException e) {
                System.out.println("JWT token expired: " + e.getMessage());
                // Continue to next filter - let Spring Security handle the 401
            } catch (Exception e) {
                System.out.println("JWT token invalid: " + e.getMessage());
                // Continue to next filter - let Spring Security handle the 401
            }
        }

        // Validate token and set authentication
        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                System.out.println("Attempting to load user details for email: " + email);

                // Load user details
                UserDetails userDetails = userDetailsService.loadUserByUsername(email);
                System.out.println("UserDetails loaded successfully:");
                System.out.println("  Username: " + userDetails.getUsername());
                System.out.println("  Authorities: " + userDetails.getAuthorities());
                System.out.println("  Enabled: " + userDetails.isEnabled());
                System.out.println("  Account Non Expired: " + userDetails.isAccountNonExpired());
                System.out.println("  Account Non Locked: " + userDetails.isAccountNonLocked());
                System.out.println("  Credentials Non Expired: " + userDetails.isCredentialsNonExpired());

                // Validate token
                if (jwtutil.validateToken(token, email)) {
                    System.out.println("Token validation successful for user: " + email);

                    // Create authentication token
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );

                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // Set authentication in security context
                    SecurityContextHolder.getContext().setAuthentication(authToken);

                    // Verify authentication was set
                    if (SecurityContextHolder.getContext().getAuthentication() != null) {
                        System.out.println("✓ Authentication set successfully!");
                        System.out.println("  Principal: " + SecurityContextHolder.getContext().getAuthentication().getPrincipal().getClass().getSimpleName());
                        System.out.println("  Authorities: " + SecurityContextHolder.getContext().getAuthentication().getAuthorities());
                        System.out.println("  Is Authenticated: " + SecurityContextHolder.getContext().getAuthentication().isAuthenticated());
                    } else {
                        System.out.println("✗ ERROR: Authentication was not set in SecurityContext!");
                    }

                } else {
                    System.out.println("✗ Token validation failed for user: " + email);
                }

            } catch (UsernameNotFoundException e) {
                System.out.println("✗ User not found: " + email);
                e.printStackTrace();
            } catch (Exception e) {
                System.out.println("✗ Error during authentication process: " + e.getMessage());
                e.printStackTrace();
            }
        } else if (email == null) {
            System.out.println("No email extracted from token");
        } else {
            System.out.println("Authentication already exists in SecurityContext");
        }

        // Continue with the filter chain
        chain.doFilter(request, response);
    }
}