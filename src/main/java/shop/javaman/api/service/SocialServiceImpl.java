package shop.javaman.api.service;

import java.util.Optional;

import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import shop.javaman.api.entity.Member;
import shop.javaman.api.entity.Role;
import shop.javaman.api.entity.SocialAccount;
import shop.javaman.api.repository.MemberRepository;
import shop.javaman.api.repository.SocialAccountRepository;

@Service
@AllArgsConstructor
public class SocialServiceImpl implements SocialService{
  private final MemberRepository memberRepository;
  private final SocialAccountRepository socialAccountRepository;

  @Override
  public void unlinkSocialAccount(Long memberId, String provider) {
    // 소셜 계정을 찾음
    SocialAccount socialAccount = socialAccountRepository.findByMemberIdAndProvider(memberId, provider)
      .orElseThrow(() -> new IllegalArgumentException("해당 소셜 계정이 존재하지 않습니다."));

    // 소셜 계정 삭제
    socialAccountRepository.delete(socialAccount);
  }

  public Member processSocialLogin(OAuth2User oAuth2User, String provider) {
      String email = oAuth2User.getAttribute("email"); // 소셜에서 제공받은 이메일
      String providerId = oAuth2User.getName(); // 소셜의 고유 ID

      // 1. 소셜 계정이 이미 연결된 경우 처리
      Optional<SocialAccount> existingAccount =
          socialAccountRepository.findByProviderAndProviderId(provider, providerId);
      if (existingAccount.isPresent()) {
          return existingAccount.get().getMember();
      }

      // 2. 동일 이메일로 기존 회원이 존재하는 경우 처리
      Optional<Member> existingMember = memberRepository.findByEmail(email);
      if (existingMember.isPresent()) {
          // 기존 회원 계정에 소셜 계정을 연결
          Member member = existingMember.get();
          SocialAccount socialAccount = new SocialAccount();
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
      newMember.setRole(Role.USER);
      memberRepository.save(newMember);

      SocialAccount socialAccount = new SocialAccount();
      socialAccount.setMember(newMember);
      socialAccount.setProvider(provider);
      socialAccount.setProviderId(providerId);
      socialAccountRepository.save(socialAccount);

      return newMember;
  }
}
