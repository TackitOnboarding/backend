package org.example.tackit.domain.mypage.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.tackit.config.S3.S3UploadService;
import org.example.tackit.domain.admin.repository.AdminMemberRepository;
import org.example.tackit.domain.entity.Member;
import org.example.tackit.domain.mypage.dto.response.UpdateNicknameResponse;
import org.example.tackit.domain.mypage.dto.response.UpdatePasswordResponse;
import org.example.tackit.domain.mypage.dto.response.UpdateProfileImageResponse;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/*
@RequiredArgsConstructor
@Service
public class UpdateMemberService {
    private final AdminMemberRepository adminMemberRepository;
    private final PasswordEncoder passwordEncoder;
    private final S3UploadService s3UploadService;

    // 닉네임 변경 서비스
    @Transactional
    public UpdateNicknameResponse changeNickname(String email, String newNickname) {
        Member member = adminMemberRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(email + " not found"));

        if (adminMemberRepository.existsByNickname(newNickname)) {
            throw new IllegalArgumentException("이미 사용 중인 닉네임입니다.");
        }

        String before = member.getNickname(); // 변경 전 닉네임 저장
        member.updateNickname(newNickname);   // 도메인에게 변경 위임

        return new UpdateNicknameResponse(before, newNickname);
    }

    // 비밀번호 변경 서비스
    @Transactional
    public UpdatePasswordResponse updatePassword(String email, String currentPassword, String newPassword) {
        Member member = adminMemberRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(email + " not found"));

        // 기존 비밀번호와 입력받은 현재 비밀번호 일치 여부 확인
        if (!passwordEncoder.matches(currentPassword, member.getPassword())) {
            throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
        }

        String encodedNewPassword = passwordEncoder.encode(newPassword);
        member.changePassword(encodedNewPassword);

        return new UpdatePasswordResponse(true, "비밀번호 변경 성공");
    }

    // 프로필 이미지 업로드
    @Transactional
    public UpdateProfileImageResponse uploadProfileImage(String email, MultipartFile file) throws IOException {
        Member member = adminMemberRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(email + " not found"));

        // 기존 이미지 있으면 삭제
        if (member.getProfileImageUrl() != null) {
            s3UploadService.deleteImage(member.getProfileImageUrl());
        }

        // 새 이미지 업로드
        String imageUrl = s3UploadService.saveFile(file);
        member.updateProfileImage(imageUrl);

        return new UpdateProfileImageResponse(imageUrl);
    }

    //  프로필 이미지 삭제
    @Transactional
    public void deleteProfileImage(String email) {
        Member member = adminMemberRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(email + " not found"));

        if (member.getProfileImageUrl() != null) {
            s3UploadService.deleteImage(member.getProfileImageUrl());
            member.deleteProfileImage();
        }
    }


}

 */
