package shop.javaman.api.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import shop.javaman.api.entity.Member;
import shop.javaman.api.repository.MemberRepository;

@SpringBootTest
@Transactional
class MemberServiceIntegrationTest {

  @Autowired
  private MemberService memberService;

  @Autowired
  private MemberRepository memberRepository;

  @Test
  void testRegisterAndLogin() {
    // 회원가입
    Member member = new Member();
    member.setEmail("test@example.com");
    member.setUsername("testuser");
    member.setPassword("password");

    // Member savedMember = memberService.register(member);
    // assertNotNull(savedMember.getId());

    // // 로그인
    // Member loggedInMember = memberService.login("test@example.com", "password");
    // assertNotNull(loggedInMember);
    // assertEquals(savedMember.getEmail(), loggedInMember.getEmail());
  }
}
