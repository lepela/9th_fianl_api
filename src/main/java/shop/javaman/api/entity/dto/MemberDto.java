package shop.javaman.api.entity.dto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import shop.javaman.api.entity.Member;
import shop.javaman.api.entity.enums.Role;
import shop.javaman.api.security.CustomUserDetails;

@Data
@Builder
@AllArgsConstructor
public class MemberDto {
  private final Long id;
  private final String email;
  private final Set<Role> roles;
  private final List<Map<String, Object>> socialAccount;
  private final boolean firstLogin;

  private String accessToken;

  public MemberDto(Member member) {
    id = member.getId();
    email = member.getEmail();
    roles = member.getRoles();
    Map<String, Object> social = new HashMap<>();

    socialAccount = member.getSocialAccounts().stream().map(s -> {
      social.put("provider", s.getProvider().name());
      social.put("providerId", s.getProviderId());
      return social;
    }).toList();
    firstLogin = member.isFirstLogin();
  }

  public MemberDto(CustomUserDetails userDetails) {
    id = userDetails.getId();
    email = userDetails.getEmail();
    roles = userDetails.getRoles();
    socialAccount = userDetails.getSocialAccount();
    firstLogin = userDetails.isFirstLogin();
  }



  public Member toEntity() {
    return Member.builder()
      .id(id)
      .email(email)
      .roles(roles)
      .build();
  }
}
