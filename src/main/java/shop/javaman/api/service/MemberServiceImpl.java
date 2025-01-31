package shop.javaman.api.service;

import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import shop.javaman.api.entity.Member;
import shop.javaman.api.entity.dto.MemberDto;
import shop.javaman.api.repository.MemberRepository;

@AllArgsConstructor
@Service
public class MemberServiceImpl implements MemberService{
  private final MemberRepository memberRepository;
  // private final PasswordEncoder passwordEncoder;
  @Override
  public MemberDto getMemberByid(Long memberId) {
    return new MemberDto(memberRepository.findById(memberId).orElseThrow());
  }

  
  // @Override
  // public Member login(String identifier, String password) {
  //   // 이메일 또는 사용자 지정 ID로 회원 조회
  //   Optional<Member> memberOptional = 
  //     memberRepository.findByEmail(identifier)
  //       .or(() -> memberRepository.findByUsername(identifier));

  //   Member member = memberOptional
  //     .orElseThrow(() -> new IllegalArgumentException("아이디 또는 비밀번호가 잘못되었습니다."));

  //   // 비밀번호 검증
  //   if (!passwordEncoder.matches(password, member.getPassword())) {
  //     throw new IllegalArgumentException("아이디 또는 비밀번호가 잘못되었습니다.");
  //   }

  //   return member; // 로그인 성공
  // }

  // @Override
  // public Member register(Member member) {
  //   if (memberRepository.findByEmail(member.getEmail()).isPresent()) {
  //     throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
  //   }
  //   if (memberRepository.findByUsername(member.getUsername()).isPresent()) {
  //     throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
  //   }

  //   member.setPassword(passwordEncoder.encode(member.getPassword()));
  //   return memberRepository.save(member);
  // }
}
