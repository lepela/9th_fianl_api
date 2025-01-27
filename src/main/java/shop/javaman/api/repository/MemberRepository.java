package shop.javaman.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import shop.javaman.api.entity.Member;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByEmail(String email);
    Optional<Member> findByUsername(String username);

    @Query("SELECT m FROM Member m JOIN FETCH m.socialAccounts WHERE m.email = :email")
    Optional<Member> findByEmailWithSocialAccounts(String email);
}
