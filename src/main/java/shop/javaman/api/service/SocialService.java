package shop.javaman.api.service;

import org.springframework.security.oauth2.core.user.OAuth2User;

import shop.javaman.api.entity.Member;

public interface SocialService {
  void unlinkSocialAccount(Long memberId, String provider);
  Member processSocialLogin(OAuth2User oAuth2User, String provider);
}
