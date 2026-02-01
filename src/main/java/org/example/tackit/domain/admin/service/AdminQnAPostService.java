package org.example.tackit.domain.admin.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.tackit.domain.admin.dto.ReportedPostDTO;
import org.example.tackit.domain.admin.repository.AdminQnAPostRepository;
import org.example.tackit.domain.entity.QnAPost;
import org.example.tackit.domain.entity.AccountStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class AdminQnAPostService implements ReportedPostService {
    private final AdminQnAPostRepository adminQnAPostRepository;

    // 비활성화 질문 게시글 전체 조회
    @Override
    public Page<ReportedPostDTO> getDeletedPosts(Pageable pageable) {

        return adminQnAPostRepository.findAllByAccountStatusAndReportCountGreaterThanEqual(AccountStatus.DELETED, 3, pageable)
                .map(ReportedPostDTO::fromEntity);
    }

    // 신고 게시글 완전 삭제
    @Override
    @Transactional
    public void deletePost(Long id) {
        QnAPost post = adminQnAPostRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("해당 게시글이 존재하지 않습니다."));

        adminQnAPostRepository.delete(post);
    }

    // 신고 게시글 활성 전환
    @Override
    @Transactional
    public void activatePost(Long id) {
        QnAPost post = adminQnAPostRepository.findById(id)
                .orElseThrow( () -> new EntityNotFoundException("해당 게시글이 존재하지 않습니다."));

        post.activate();
    }
}
