package shop.javaman.api.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
public class SocialAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 기본 키

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member; // 연관된 회원

    @Column(nullable = false)
    private String provider; // 소셜 제공자 (Google, Facebook 등)

    @Column(nullable = false)
    private String providerId; // 소셜 제공자 ID

    private String accessToken; // 소셜 인증 토큰 (필요 시)

    @Column(nullable = false, updatable = false)
    private LocalDateTime linkedAt; // 소셜 계정 연결 시간

    @PrePersist
    public void prePersist() {
        this.linkedAt = LocalDateTime.now();
    }
}
