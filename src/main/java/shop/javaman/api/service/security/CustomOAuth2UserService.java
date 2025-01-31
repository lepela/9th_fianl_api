package shop.javaman.api.service.security;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import org.hibernate.Hibernate;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import shop.javaman.api.entity.Member;
import shop.javaman.api.entity.Social;
import shop.javaman.api.entity.enums.AccountStatus;
import shop.javaman.api.entity.enums.Provider;
import shop.javaman.api.entity.enums.Role;
import shop.javaman.api.repository.MemberRepository;
import shop.javaman.api.repository.SocialRepository;
import shop.javaman.api.security.CustomUserDetails;
import shop.javaman.api.security.jwt.JwtUtil;

@RequiredArgsConstructor
@Service
@Log4j2
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
  private final MemberRepository memberRepository;
  private final SocialRepository socialRepository;
  private final JwtUtil jwtUtil;

  @Override
  @Transactional
  public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
    OAuth2User oAuth2User = super.loadUser(userRequest);

    String providerName = userRequest.getClientRegistration().getRegistrationId();
    Provider provider;
    try {
      provider = Provider.valueOf(providerName.toUpperCase());
    } catch (IllegalArgumentException e) {
      throw new OAuth2AuthenticationException("Unsupported provider: " + providerName);
    }

    Map<String, Object> attributes = new HashMap<>(oAuth2User.getAttributes());

    String providerId = getProviderId(provider, attributes);
    String email = getEmail(provider, attributes);
    String username = getUsername(provider, attributes);

    if (email == null || email.isBlank()) {
      email = provider.name().toLowerCase() + "_" + providerId + "@noemail.com";
    }
    Member member = findOrCreateMember(provider, providerId, email, username);

    // JWT 발급
    String accessToken = jwtUtil.generateAccessToken(email);
    String refreshToken = jwtUtil.generateRefreshToken(email);
    log.info(member); // ? 여기서 true인데? why?
    // CustomUserDetails로 반환
    return new CustomUserDetails(member, attributes, accessToken, refreshToken);
  }

  private Member findOrCreateMember(Provider provider, String providerId, String email, String username) {
    AtomicBoolean isNewMember = new AtomicBoolean(false);
    Member member = socialRepository.findByProviderAndProviderId(provider, providerId)
    .map(Social::getMember)
    .orElseGet(() -> {
      isNewMember.set(true);
      Member newMember = new Member();
      newMember.setEmail(email);
      newMember.setUsername(username);
      newMember.setRoles(Set.of(Role.USER));
      newMember.setAccountStatus(AccountStatus.UNVERIFIED);
      newMember.setFirstLogin(isNewMember.get());
      memberRepository.save(newMember);

      Social newSocial = new Social();
      newSocial.setProvider(provider);
      newSocial.setProviderId(providerId);
      newSocial.setMember(newMember);
      socialRepository.save(newSocial);
      newMember.getSocialAccounts().add(newSocial);
      return newMember;
    });

    if (!isNewMember.get() && member.isFirstLogin()) {
      member.setFirstLogin(false);
      memberRepository.save(member);
    }
    Hibernate.initialize(member.getRoles());
    Hibernate.initialize(member.getSocialAccounts());
    log.info(member.getSocialAccounts());
    return member;
  }

  @SuppressWarnings("unchecked")
  private String getProviderId(Provider provider, Map<String, Object> attributes) {
    switch (provider) {
      case GOOGLE:
        return (String) attributes.get("sub");
      case KAKAO:
        return String.valueOf(attributes.get("id"));
      case NAVER:
        return (String) ((Map<String, Object>) attributes.get("response")).get("id");
      case GITHUB:
        return String.valueOf(attributes.get("id"));
      default:
        throw new IllegalArgumentException("Unsupported provider: " + provider);
    }
  }

  /**
   * 카카오, 네이버, 깃허브는 이메일의 신뢰성이 부족함.
   * 
   * @param provider
   * @param attributes
   * @return
   */
  private String getEmail(Provider provider, Map<String, Object> attributes) {
    switch (provider) {
      case GOOGLE:
      case KAKAO:
      case NAVER:
      case GITHUB:
        return (String) attributes.getOrDefault("email", null);
      default:
        return null;
    }
  }

  @SuppressWarnings("unchecked")
  private String getUsername(Provider provider, Map<String, Object> attributes) {
    switch (provider) {
      case GOOGLE:
        return (String) attributes.getOrDefault("name", "Unknown Google User");
      case KAKAO:
        Map<String, Object> kakaoProperties = (Map<String, Object>) attributes.getOrDefault("properties", Collections.emptyMap());
        return (String) kakaoProperties.getOrDefault("nickname", "Unknown Kakao User");
      case NAVER:
        Map<String, Object> naverResponse = (Map<String, Object>) attributes.getOrDefault("response", Collections.emptyMap());
        return (String) naverResponse.getOrDefault("name", "Unknown Naver User");
      case GITHUB:
        return (String) attributes.getOrDefault("login", "Unknown GitHub User");
      default:
        return "Unknown User";
    }
  }
}
