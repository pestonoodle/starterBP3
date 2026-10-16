package com.progettiportfoglio.multichat.security;

import com.progettiportfoglio.multichat.auth.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

// Non aggiungere @Component: il filter viene creato nella SecurityConfig.
public class TokenAuthenticationFilter extends OncePerRequestFilter {

    private final TokenService tokenService;

    public TokenAuthenticationFilter(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return "OPTIONS".equals(request.getMethod())
                || "/ws".equals(path)
                || ("POST".equals(request.getMethod())
                    && ("/api/auth/login".equals(path)
                        || "/api/auth/register".equals(path)));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                   HttpServletResponse response,
                                   FilterChain filterChain)
            throws ServletException, IOException {

        String token = extractBearerToken(request.getHeader(HttpHeaders.AUTHORIZATION));

        if (token != null) {
            // TODO [FILTER-01]: completare findUserByToken nel TokenService.
            // Il filter confronta il token con il DB attraverso quel servizio.
            tokenService.findUserByToken(token).ifPresent(user -> {
                var authentication = new UsernamePasswordAuthenticationToken(
                        user, null, List.of(new SimpleGrantedAuthority("ROLE_USER")));
                var context = SecurityContextHolder.createEmptyContext();
                context.setAuthentication(authentication);
                SecurityContextHolder.setContext(context);
            });
        }

        // Se manca l'autenticazione, Spring Security rispondera 401
        // alle richieste dirette alle API protette.
        filterChain.doFilter(request, response);
    }

    public static String extractBearerToken(String header) {
        if (header == null || !header.startsWith("Bearer ")) {
            return null;
        }
        String token = header.substring(7).trim();
        return token.isEmpty() ? null : token;
    }
}