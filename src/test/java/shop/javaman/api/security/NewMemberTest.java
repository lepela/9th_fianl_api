package shop.javaman.api.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.Rollback;

import jakarta.transaction.Transactional;
import shop.javaman.api.entity.Member;
import shop.javaman.api.repository.MemberRepository;

@SpringBootTest
public class NewMemberTest {
  @Autowired
  private PasswordEncoder encoder;
  @Autowired
  private MemberRepository repository;

  @Test
  @Transactional
  @Rollback(false)
  public void testInputPassword() {
    Member member = repository.findByEmail("lepelaka@gmail.com").orElseThrow();
    member.setPassword(encoder.encode("1234"));
    repository.save(member);
  }
}
