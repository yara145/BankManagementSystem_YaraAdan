package com.example.BankManagementSys.Security;

import com.example.BankManagementSys.Exceptions.NoTokenProvidedException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JWTFilter extends OncePerRequestFilter {

    @Autowired
    private JWTUtils jwtUtils;

    @Value("${application.security.openURLs}")
    private List<String> openURLs;
    private final AntPathMatcher matcher = new AntPathMatcher(); // Initialize AntPathMatcher
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String path = request.getServletPath();

        boolean isOpenURL = openURLs.stream().anyMatch(pattern -> matcher.match(pattern, path));

        if (!isOpenURL) {


            // new new new
            if (path.startsWith("/otp/")) {
                filterChain.doFilter(request, response);
                return;
            }//new new new
            try {
                isValidRequest(request);
            } catch (ExpiredJwtException | NoTokenProvidedException e) {
                changeResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized request");
                return;
            } catch (UnsupportedJwtException | MalformedJwtException e) {
                changeResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Bad authentication data");
                return;
            }

        }
        filterChain.doFilter(request, response);

    }
    private void changeResponse(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.getWriter().write(message);
        response.getWriter().flush();
    }

    private void isValidRequest(HttpServletRequest request) throws NoTokenProvidedException {
        String token = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (token == null) {
            throw new NoTokenProvidedException();
        }

        token = token.replaceFirst("^Bearer\\s*", "");
        if ("".equals(token)) {
            throw new UnsupportedJwtException(token + " is not well formatted");
        }

        this.jwtUtils.verifyToken(token);
    }

}