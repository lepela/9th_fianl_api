package shop.javaman.api.service;

import org.springframework.security.oauth2.core.user.OAuth2User;

import shop.javaman.api.entity.Member;
import shop.javaman.api.entity.enums.Provider;

public interface SocialService {
  void unlinkSocialAccount(Long memberId, Provider provider);
  Member processSocialLogin(OAuth2User oAuth2User, Provider provider);
}
