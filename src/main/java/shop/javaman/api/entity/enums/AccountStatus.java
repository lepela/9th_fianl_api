package shop.javaman.api.entity.enums;

public enum AccountStatus {
  UNVERIFIED, // 정상계정(이메일 인증 완료)
  VERIFIED, // 이메일 미인증 상태(소셜, 초기 가입 시기)
  SUSPEND, // 일시 정지(기한 존재)
  BAN, // 무기한 정지
  INACTIVE // 휴면 계정
}
