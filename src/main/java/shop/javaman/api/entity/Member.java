package shop.javaman.api.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.Builder.Default;
import shop.javaman.api.entity.enums.AccountStatus;
import shop.javaman.api.entity.enums.Role;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 기본 키

    @Column(nullable = true, unique = true)
    private String email; // 이메일 (로그인 ID로 사용)

    private String password; // 비밀번호 (소셜 회원의 경우 NULL 허용)

    private String username; // 사용자 이름

    @Enumerated(EnumType.STRING)
    private AccountStatus accountStatus;

    @Column(nullable = false)
    private boolean emailVerified;

    @Enumerated(EnumType.STRING)
    @ElementCollection(fetch = FetchType.LAZY)
    private Set<Role> roles; // 권한

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt; // 생성일자

    private LocalDateTime updatedAt; // 수정일자

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    @Default
    private List<Social> socialAccounts = new ArrayList<>(); // 연관된 소셜 계정들

    @Column(nullable = false)
    @Default
    private boolean firstLogin = true;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
