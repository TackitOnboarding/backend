package org.example.tackit.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Member {
    // id, name, email, password, createdAt
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    private AccountStatus status;

    private LocalDateTime createdAt = LocalDateTime.now();

    private AccountStatus accountStatus;  // 탈퇴 계정을 위해

    // 연차 계산 책임은 Member 도메인 내부에 분리
    // member 도메인 내에서만 쓰이기 때문에 private
    /*
    private int calculateYearsOfService() {
        return LocalDate.now().getYear() - this.joinedYear + 1;
    }

    // 마이페이지 응답을 만들어 반환한다.

    public MemberMypageResponse generateMypageResponse() {
        return new MemberMypageResponse(
                this.name,
                this.email,
                this.calculateYearsOfService(),
        );
    }

    // 닉네임 변경 책임은 Member 도메인 내부에 분리
    /*
    public void updateNickname(String newNickname) {
        this.nickname = newNickname;
    }
     */

    // 비밀번호 변경 책임은 Member 도메인 내부에 분리
    public void changePassword(String encodedNewPassword) {
        this.password = encodedNewPassword;
    }

    // 자신을 비활성화는 책임 부여
    public void deactivate() {
        this.accountStatus = AccountStatus.DELETED;
    }

    /*
    public MemberMypageResponse generateMypageResponse() {
        return MemberMypageResponse.builder()
                .email(this.member.getEmail()) // 계정 이메일
                .nickname(this.nickname)       // 해당 소속 닉네임
                .profileImageUrl(this.profileImageUrl) // 해당 소속 프로필 이미지
                .organization(this.organization.getName()) // 소속 이름
                .memberType(this.memberType)   // NEWBIE / SENIOR
                .build();
    }

     */

}
