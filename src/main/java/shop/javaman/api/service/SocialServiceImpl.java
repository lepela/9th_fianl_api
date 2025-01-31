package shop.javaman.api.service;

import java.util.Optional;
import java.util.Set;

import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import shop.javaman.api.entity.Member;
import shop.javaman.api.entity.Social;
import shop.javaman.api.entity.enums.Provider;
import shop.javaman.api.entity.enums.Role;
import shop.javaman.api.repository.MemberRepository;
import shop.javaman.api.repository.SocialRepository;

@Service
@AllArgsConstructor
public class SocialServiceImpl implements SocialService{
  private final MemberRepository memberRepository;
  private final SocialRepository socialAccountRepository;

  @Override
  public void unlinkSocialAccount(Long memberId, Provider provider) {
    // 소셜 계정을 찾음
    Social socialAccount = socialAccountRepository.findByMemberIdAndProvider(memberId, provider)
      .orElseThrow(() -> new IllegalArgumentException("해당 소셜 계정이 존재하지 않습니다."));

    // 소셜 계정 삭제
    socialAccountRepository.delete(socialAccount);
  }

  public Member processSocialLogin(OAuth2User oAuth2User, Provider provider) {
    String email = oAuth2User.getAttribute("email"); // 소셜에서 제공받은 이메일
    String providerId = oAuth2User.getName(); // 소셜의 고유 ID

    // 1. 소셜 계정이 이미 연결된 경우 처리
    Optional<Social> existingAccount =
      socialAccountRepository.findByProviderAndProviderId(provider, providerId);
    if (existingAccount.isPresent()) {
      return existingAccount.get().getMember();
    }

    // 2. 동일 이메일로 기존 회원이 존재하는 경우 처리
    Optional<Member> existingMember = memberRepository.findByEmail(email);
    if (existingMember.isPresent()) {
      // 기존 회원 계정에 소셜 계정을 연결
      Member member = existingMember.get();
      Social socialAccount = new Social();
      socialAccount.setMember(member);
      socialAccount.setProvider(provider);
      socialAccount.setProviderId(providerId);
      socialAccountRepository.save(socialAccount);
      return member;
    }

    // 3. 새 회원가입 플로우 처리
    Member newMember = new Member();
    newMember.setEmail(email);
    newMember.setUsername(oAuth2User.getAttribute("name"));
    newMember.setRoles(Set.of(Role.USER));
    memberRepository.save(newMember);

    Social socialAccount = new Social();
    socialAccount.setMember(newMember);
    socialAccount.setProvider(provider);
    socialAccount.setProviderId(providerId);
    socialAccountRepository.save(socialAccount);

    return newMember;
  }
}
