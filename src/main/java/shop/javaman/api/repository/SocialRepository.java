package shop.javaman.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import shop.javaman.api.entity.Social;

import java.util.Optional;
public interface SocialRepository extends JpaRepository<Social, Long> {
    Optional<Social> findByProviderAndProviderId(String provider, String providerId);
    Optional<Social> findByMemberIdAndProvider(Long memberId, String provider);
}
