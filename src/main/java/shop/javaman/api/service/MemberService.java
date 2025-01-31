package shop.javaman.api.service;

import shop.javaman.api.entity.dto.MemberDto;

public interface MemberService {
  // Member login(String identifier, String password);
  // Member register(Member member);
  MemberDto getMemberByid(Long memberId);
}
