package org.example.tackit.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.tackit.domain.mypage.dto.response.MemberMypageResponse;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String nickname;

    @Column(nullable = false)
    private String organization;

    private String profileImageUrl;

    /*
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;
     */

    @Enumerated(EnumType.STRING)
    private MemberRole memberRole;

    @Enumerated(EnumType.STRING)
    private MemberType memberType;

    @Column(nullable = false, name = "joined_year")
    private int joinedYear;

    private Status status;
    private LocalDateTime createdAt = LocalDateTime.now();

    // 연차 계산 책임은 Member 도메인 내부에 분리
    // member 도메인 내에서만 쓰이기 때문에 private
    private int calculateYearsOfService() {
        return LocalDate.now().getYear() - this.joinedYear + 1;
    }

    // 마이페이지 응답을 만들어 반환한다.
    public MemberMypageResponse generateMypageResponse() {
        return new MemberMypageResponse(
                this.nickname,
                this.email,
                this.organization,
                this.getMemberRole(),
                this.getMemberType(),
                this.joinedYear,
                this.calculateYearsOfService(),
                this.profileImageUrl
        );
    }

    // 닉네임 변경 책임은 Member 도메인 내부에 분리
    public void updateNickname(String newNickname) {
        this.nickname = newNickname;
    }

    // 비밀번호 변경 책임은 Member 도메인 내부에 분리
    public void changePassword(String encodedNewPassword) {
        this.password = encodedNewPassword;
    }

    // 자신을 비활성화는 책임 부여
    public void deactivate() {
        this.status = Status.DELETED;
    }

    // 프로필 이미지 변경
    public void updateProfileImage(String imageUrl) {
        this.profileImageUrl = imageUrl;
    }

    // 프로필 이미지 삭제
    public void deleteProfileImage() {
        this.profileImageUrl = null;
    }

}
