package shop.javaman.api.service;

import shop.javaman.api.entity.Member;

public interface MemberService {
  Member login(String identifier, String password);
  Member register(Member member);
}
