package shop.javaman.api.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.MacAlgorithm;
import jakarta.annotation.PostConstruct;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Log4j2
@Component
public class JwtUtil {
  @Value("${jwt.secret}")
  private String secretKey; // 환경 변수에서 로드

  private static final long ACCESS_TOKEN_VALIDITY = 1000 * 60 * 60; // 1시간
  private static final long REFRESH_TOKEN_VALIDITY = 1000 * 60 * 60 * 24 * 7; // 7일

  // 최신 jjwt 방식의 HMAC 알고리즘
  private static final MacAlgorithm ALGORITHM = Jwts.SIG.HS256;
  private SecretKey key;

  @PostConstruct
  public void init() {
    try {
      if (secretKey == null || secretKey.trim().isEmpty()) {
        throw new IllegalArgumentException("JWT 시크릿 키가 설정되지 않았습니다.");
      }
      key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
    } catch (Exception e) {
      log.error("JWT 키 초기화 오류: {}", e.getMessage());
      throw new RuntimeException("JWT 키 초기화 실패", e);
    }
  }

  /** JWT 액세스 토큰 생성 */
  public String generateAccessToken(String email) {
    return generateToken(email, ACCESS_TOKEN_VALIDITY);
  }

  /** JWT 리프레시 토큰 생성 */
  public String generateRefreshToken(String email) {
    return generateToken(email, REFRESH_TOKEN_VALIDITY);
  }

  /** JWT 토큰 생성 공통 메서드 */
  private String generateToken(String email, long expirationTime) {
    return Jwts.builder()
      .subject(email)
      .issuedAt(new Date())
      .expiration(new Date(System.currentTimeMillis() + expirationTime))
      .signWith(key, ALGORITHM)
      .compact();
  }

  /** JWT 토큰 검증 및 파싱 */
  private Claims parseToken(String token) {
    return Jwts.parser()
      .verifyWith(key)
      .build()
      .parseSignedClaims(token)
      .getPayload();
  }

  /** 토큰에서 이메일 추출 */
  public String getEmailFromToken(String token) {
    try {
      return parseToken(token).getSubject();
    } catch (JwtException e) {
      log.error("토큰 파싱 오류: {}", e.getMessage());
      return null;
    }
  }

  /** 토큰 유효성 검증 */
  public boolean validateToken(String token) {
    try {
      parseToken(token);
      return true;
    } catch (JwtException e) {
      log.error("JWT 유효성 검증 실패: {}", e.getMessage());
      return false;
    }
  }
}
