package shop.javaman.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import shop.javaman.api.security.CustomOAuth2SuccessHandler;
import shop.javaman.api.security.jwt.CustomAuthenticationFilter;
import shop.javaman.api.security.jwt.JwtAuthenticationFilter;
import shop.javaman.api.security.jwt.JwtUtil;
import shop.javaman.api.service.security.CustomOAuth2UserService;
import shop.javaman.api.service.security.CustomUserDetailsService;

import java.util.List;

@Configuration
@Log4j2
@RequiredArgsConstructor
public class SecurityConfig {
  private final CustomUserDetailsService customUserDetailsService;
  private final CustomOAuth2UserService customOAuth2UserService;
  private final JwtAuthenticationFilter authenticationFilter;
  private final CustomOAuth2SuccessHandler customOAuth2SuccessHandler;
  private final JwtUtil jwtUtil;

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http.csrf(csrf -> csrf.disable());
    http.sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
    http.authorizeHttpRequests(auth -> auth
      .requestMatchers("/", "/auth/login", "/oauth2/**", "/member/info").permitAll()
      .anyRequest().authenticated()
    );
    http.oauth2Login(oauth -> oauth
      .successHandler(customOAuth2SuccessHandler)
      .failureHandler((req, resp, ex) -> {
        log.error("oauth2 login failed : {}", ex.getMessage());
      })
      .failureUrl("/login/error=true")
      .userInfoEndpoint(ie -> ie.userService(customOAuth2UserService))
    );

    http.addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class);
    // http.addFilterAt(customAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public AuthenticationManager authenticationManager() {
    DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
    authenticationProvider.setUserDetailsService(customUserDetailsService);
    authenticationProvider.setPasswordEncoder(passwordEncoder());
    return new ProviderManager(List.of(authenticationProvider));
  }

  // @Bean
  // public CustomAuthenticationFilter customAuthenticationFilter(AuthenticationManager authenticationManager) {
  //   CustomAuthenticationFilter filter = new CustomAuthenticationFilter(authenticationManager, jwtUtil);
  //   filter.setFilterProcessesUrl("/auth/login");
  //   return filter;
  // }
}