package shop.javaman.api.security.jwt;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter{
  private final AuthenticationManager authenticationManager;
  private final JwtUtil jwtUtil; // JWT 토큰 생성 유틸리티

  @Override
  public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
      throws AuthenticationException {
    try {
      // JSON 요청에서 username, password 파싱
      ObjectMapper objectMapper = new ObjectMapper();
      Map<String, String> loginRequest = objectMapper.readValue(request.getReader(), Map.class);

      String username = loginRequest.get("email");
      String password = loginRequest.get("password");

      UsernamePasswordAuthenticationToken authenticationToken =
          new UsernamePasswordAuthenticationToken(username, password);

      return authenticationManager.authenticate(authenticationToken);
    } catch (IOException e) {
      throw new RuntimeException("로그인 요청을 처리하는 중 오류 발생", e);
    }
  }

  @Override
  protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
    // 인증 성공 시 JWT 토큰 생성 및 반환
    String username = authResult.getName();
    String token = jwtUtil.generateAccessToken(username);

    Map<String, String> responseBody = new HashMap<>();
    responseBody.put("accessToken", token);

    response.setContentType("application/json");
    new ObjectMapper().writeValue(response.getOutputStream(), responseBody);
  }

  @Override
  protected void unsuccessfulAuthentication(
      HttpServletRequest request,
      HttpServletResponse response,
      AuthenticationException failed
  ) throws IOException, ServletException {
    // 로그인 실패 처리
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    response.setContentType("application/json");

    Map<String, String> errorResponse = new HashMap<>();
    errorResponse.put("error", "로그인 실패: " + failed.getMessage());

    new ObjectMapper().writeValue(response.getOutputStream(), errorResponse);
  }
}
