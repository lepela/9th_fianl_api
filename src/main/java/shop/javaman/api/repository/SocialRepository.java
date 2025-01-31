package shop.javaman.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import shop.javaman.api.entity.Social;
import shop.javaman.api.entity.enums.Provider;

import java.util.Optional;
public interface SocialRepository extends JpaRepository<Social, Long> {
    Optional<Social> findByProviderAndProviderId(Provider provider, String providerId);
    Optional<Social> findByMemberIdAndProvider(Long memberId, Provider provider);
}
