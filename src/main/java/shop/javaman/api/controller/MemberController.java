package shop.javaman.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import shop.javaman.api.entity.dto.MemberDto;
import shop.javaman.api.security.CustomUserDetails;
import shop.javaman.api.service.MemberService;


@RestController
@RequestMapping("member")
public class MemberController {
  @Autowired
  private MemberService memberService;

  @GetMapping("info")
  public Object memberInfo(@AuthenticationPrincipal OAuth2User oAuth2User) {
    return oAuth2User.getAttributes();
  }

  @GetMapping("myInfo")
  public Object myInfo(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
    return MemberDto.builder()
      .id(customUserDetails.getId())
      .email(customUserDetails.getEmail())
      .accessToken(customUserDetails.getAccessToken())
      .roles(customUserDetails.getRoles())
      .socialAccount(customUserDetails.getSocialAccount())
      .build();
  }

  @GetMapping("cud")
  public Object cud(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
    return customUserDetails;
  }
}
