package shop.javaman.api.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("member")
public class MemberController {
  
  @GetMapping("info")
  public Object memberInfo(@AuthenticationPrincipal OAuth2User oAuth2User) {
    return oAuth2User.getAttributes();
  }
}
