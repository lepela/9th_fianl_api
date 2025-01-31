package shop.javaman.api.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import shop.javaman.api.entity.Member;
import shop.javaman.api.repository.MemberRepository;

import java.util.Optional;

class MemberServiceTest {

  @Mock
  private MemberRepository memberRepository;

  @Mock
  private BCryptPasswordEncoder passwordEncoder; // Mock 객체로 선언

  @InjectMocks
  private MemberServiceImpl memberService;

  MemberServiceTest() {
    MockitoAnnotations.openMocks(this); // Mock 객체 초기화
  }

  @Test
  void testLoginSuccess() {
    String email = "test@example.com";
    String rawPassword = "password";
    String encodedPassword = "$2a$10$abcde12345xyz67890"; // 암호화된 비밀번호 예제

    Member mockMember = new Member();
    mockMember.setEmail(email);
    mockMember.setPassword(encodedPassword);

    // Mock 동작 정의
    when(memberRepository.findByEmail(email)).thenReturn(Optional.of(mockMember));
    when(passwordEncoder.matches(rawPassword, encodedPassword)).thenReturn(true);

    // Member result = memberService.login(email, rawPassword);

    // assertNotNull(result);
    // assertEquals(email, result.getEmail());
  }

  @Test
  void testLoginFailure() {
    String email = "test@example.com";
    String rawPassword = "wrongPassword";

    Member mockMember = new Member();
    mockMember.setEmail(email);
    mockMember.setPassword("$2a$10$abcde12345xyz67890"); // 암호화된 비밀번호 예제

    // Mock 동작 정의
    when(memberRepository.findByEmail(email)).thenReturn(Optional.of(mockMember));
    when(passwordEncoder.matches(rawPassword, mockMember.getPassword())).thenReturn(false);

    // Exception exception = assertThrows(IllegalArgumentException.class, () ->
    //   memberService.login(email, rawPassword)
    // );

    // assertEquals("아이디 또는 비밀번호가 잘못되었습니다.", exception.getMessage());
  }
}
