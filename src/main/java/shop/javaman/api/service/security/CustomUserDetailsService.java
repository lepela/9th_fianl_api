package shop.javaman.api.service.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.lang.Collections;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import shop.javaman.api.entity.Member;
import shop.javaman.api.repository.MemberRepository;
import shop.javaman.api.security.CustomUserDetails;
import shop.javaman.api.security.jwt.JwtUtil;

@RequiredArgsConstructor
@Service
public class CustomUserDetailsService implements UserDetailsService{
  private final JwtUtil jwtUtil;
  private final MemberRepository memberRepository;
  @Override
  @Transactional
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    Member member = memberRepository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException("username : " + username + " 은 미가입된 회원"));

    boolean isFirstLogin = member.isFirstLogin(); // firstLogin 여부 조회

    // 2번째 이후 로그인 시, firstLogin을 false로 업데이트
    if (isFirstLogin) {
      member.setFirstLogin(false);
      memberRepository.save(member);
    }
  
    // JWT 발급
    String accessToken = jwtUtil.generateAccessToken(username);
    String refreshToken = jwtUtil.generateRefreshToken(username);
    return new CustomUserDetails(member, Collections.emptyMap(), accessToken, refreshToken);
  }
}
