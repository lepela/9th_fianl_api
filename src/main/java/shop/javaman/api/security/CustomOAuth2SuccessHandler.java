package shop.javaman.api.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import shop.javaman.api.entity.dto.MemberDto;
import java.io.IOException;

@Log4j2
@Component
@RequiredArgsConstructor
public class CustomOAuth2SuccessHandler implements AuthenticationSuccessHandler {
  private final ObjectMapper objectMapper = new ObjectMapper();
  @Override
  @Transactional
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                      Authentication authentication) throws IOException, ServletException {

  // 세션기반 성공코드

  // log.info("OAuth2 로그인 성공!");

  // OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
  // String email = (String) oAuth2User.getAttributes().get("email");

  // Optional<Member> optionalMember = memberRepository.findByEmail(email);

  // String targetUrl;
  // if (optionalMember.isPresent()) {
  //   Member member = optionalMember.get();

  //   // 특정 조건에 따라 URI 분기 처리
  //   if (member.isFirstLogin()) {
  //     targetUrl = "/welcome";  // 첫 로그인 시 웰컴 페이지로
  //   } else if (member.getRoles().contains(Role.ADMIN)) {
  //     targetUrl = "/admin/dashboard";  // 관리자는 대시보드로
  //   } else {
  //     targetUrl = "/home";  // 기본 유저는 홈 페이지로
  //   }

  //   // 첫 로그인 여부 업데이트
  //   if (member.isFirstLogin()) {
  //     member.setFirstLogin(false);
  //     memberRepository.save(member);
  //   }
  // } else {
  //   targetUrl = "/error"; // 데이터베이스에 사용자 정보가 없으면 오류 페이지로
  // }

  // log.info("리다이렉트 대상 URL: {}", targetUrl);
  // response.sendRedirect(targetUrl);
  // }
    log.info("OAuth2 로그인 성공!");

    // OAuth2User → CustomUserDetails로 변환하여 기존 JWT 활용
    CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
    
    // JWT를 다시 발급할 필요 없이, 기존 값을 사용
    String accessToken = userDetails.getAccessToken();

    // MemberDto 생성
    MemberDto memberDto = new MemberDto(userDetails);
    memberDto.setAccessToken(accessToken); // 기존 발급된 JWT 설정

    // JSON으로 변환 및 응답
    response.setContentType("application/json; charset=UTF-8");
    response.getWriter().write(objectMapper.writeValueAsString(memberDto));
  }
}
