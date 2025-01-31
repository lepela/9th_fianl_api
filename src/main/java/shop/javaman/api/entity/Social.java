package shop.javaman.api.entity;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import shop.javaman.api.entity.enums.Provider;

@Table(
  uniqueConstraints = {
    @UniqueConstraint(columnNames = {"provider", "providerId"})
  }
)
@Entity
@Getter
@Setter
public class Social {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Provider provider; // 소셜 제공자 (Enum)

  @Column(nullable = false)
  private String providerId; // 소셜 제공자 고유 ID

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "member_id", nullable = false)
  private Member member;

  private String accessToken; // 소셜 인증 토큰 (옵션)
  private LocalDateTime linkedAt; // 연결 시간

  // Map 변환
  public Map<String, Object> toMap() {
    Map<String, Object> map = new HashMap<>();
    map.put("provider", provider.name());
    map.put("providerId", providerId);
    return map;
  }

}
