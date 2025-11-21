package com.nexus.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        final String requestPath = request.getServletPath();
        
        // Ignorar rotas públicas do Swagger e autenticação
        if (requestPath.startsWith("/api/auth/") ||
            requestPath.startsWith("/swagger-ui") ||
            requestPath.startsWith("/v3/api-docs") ||
            requestPath.startsWith("/webjars") ||
            requestPath.startsWith("/h2-console") ||
            requestPath.startsWith("/swagger-resources") ||
            requestPath.startsWith("/configuration") ||
            requestPath.equals("/swagger-ui.html") ||
            requestPath.equals("/")) {
            filterChain.doFilter(request, response);
            return;
        }
        
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Extrair o token removendo "Bearer " e tratando casos de duplicação
        String tokenPart = authHeader.substring(7).trim();
        
        // Se o usuário digitou "Bearer Bearer token", remover o "Bearer" extra
        if (tokenPart.startsWith("Bearer ")) {
            tokenPart = tokenPart.substring(7).trim();
        }
        
        jwt = tokenPart;
        if (jwt.isEmpty()) {
            filterChain.doFilter(request, response);
            return;
        }
        
        try {
            userEmail = jwtService.extractUsername(jwt);
        } catch (Exception e) {
            filterChain.doFilter(request, response);
            return;
        }

        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
                if (jwtService.validateToken(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    log.debug("✅ Autenticação bem-sucedida para usuário: {} com roles: {}", 
                            userEmail, userDetails.getAuthorities());
                } else {
                    log.warn("⚠️ Token JWT inválido ou expirado para usuário: {}", userEmail);
                }
            } catch (Exception e) {
                log.error("❌ Erro ao autenticar usuário {}: {}", userEmail, e.getMessage());
                // Não bloqueia a requisição, apenas loga o erro
            }
        }
        filterChain.doFilter(request, response);
    }
}

