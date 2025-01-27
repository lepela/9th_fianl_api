package shop.javaman.api.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@ToString
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 기본 키

    @Column(nullable = false, unique = true)
    private String email; // 이메일 (로그인 ID로 사용)

    private String password; // 비밀번호 (소셜 회원의 경우 NULL 허용)

    private String username; // 사용자 이름

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role; // 권한

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt; // 생성일자

    private LocalDateTime updatedAt; // 수정일자

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SocialAccount> socialAccounts = new ArrayList<>(); // 연관된 소셜 계정들

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
