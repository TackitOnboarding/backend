package org.example.tackit.domain.admin.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.tackit.domain.freeBoard.Free_post.repository.FreePostJPARepository;
import org.example.tackit.domain.admin.dto.ReportedPostDTO;
import org.example.tackit.domain.admin.repository.AdminFreePostRepository;
import org.example.tackit.domain.entity.FreePost;
import org.example.tackit.domain.entity.ActiveStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class AdminFreePostService implements ReportedPostService{
    private final AdminFreePostRepository adminFreePostRepository;
    private final FreePostJPARepository freePostRepository;

    // 비활성화 자유 게시글 전체 조회
    @Override
    public Page<ReportedPostDTO> getDeletedPosts(Pageable pageable) {
        return adminFreePostRepository.findAllByActiveStatusAndReportCountGreaterThanEqual(ActiveStatus.DELETED, 3, pageable)
                .map(ReportedPostDTO::fromEntity);
    }

    // 신고 게시글 완전 삭제
    @Override
    @Transactional
    public void deletePost(Long id) {
        FreePost post = freePostRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("해당 게시글이 존재하지 않습니다."));

        freePostRepository.delete(post);
    }

    // 신고 게시글 활성 전환
    @Override
    @Transactional
    public void activatePost(Long id) {
        FreePost post = freePostRepository.findById(id)
                .orElseThrow( () -> new EntityNotFoundException("해당 게시글이 존재하지 않습니다."));

        post.activate();
    }



}
