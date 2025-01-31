package shop.javaman.api.security;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import jakarta.transaction.Transactional;
import lombok.Getter;
import lombok.ToString;
import shop.javaman.api.entity.Member;
import shop.javaman.api.entity.enums.Role;

@Getter
@ToString
public class CustomUserDetails implements UserDetails, OAuth2User{
  private final Long id;
  private final String email;
  private final Set<Role> roles;
  private final List<Map<String, Object>> socialAccount;
  private final Map<String, Object> attributes; 
  private final boolean firstLogin;
  private final String accessToken;
  private final String refreshToken;


  @Override
  public String getName() {
    return email;
  }

  public CustomUserDetails(Member member, Map<String, Object> attributes, String accessToken, String refreshToken) {
    id = member.getId();
    email = member.getEmail();
    roles = member.getRoles();
    Map<String, Object> social = new HashMap<>();

    this.attributes = attributes != null ? new HashMap<>(attributes) : new HashMap<>();

    socialAccount = member.getSocialAccounts().stream().map(s -> {
      social.put("provider", s.getProvider().name());
      social.put("providerId", s.getProviderId());
      return social;
    }).toList();

    this.accessToken = accessToken;
    this.refreshToken = refreshToken;
    this.firstLogin = member.isFirstLogin();
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return roles.stream().map(r -> (GrantedAuthority) () -> "ROLE_" + r).toList();
  }

  @Override
  public String getPassword() {
    return "[PROTECTED]";
  }

  @Override
  public String getUsername() {
    return email;
  }
  
}
