package org.example.tackit.domain.report.service;

import lombok.RequiredArgsConstructor;
import org.example.tackit.domain.entity.Report;
import org.example.tackit.domain.entity.TargetType;
import org.example.tackit.domain.entity.org.MemberOrg;
import org.example.tackit.domain.entity.post.Comment;
import org.example.tackit.domain.entity.post.Post;
import org.example.tackit.domain.memberOrg.component.MemberOrgValidator;
import org.example.tackit.domain.post.repository.CommentRepository;
import org.example.tackit.domain.post.repository.PostRepository;
import org.example.tackit.domain.report.dto.ReportReqDto;
import org.example.tackit.domain.report.repository.ReportRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportService {

  private final ReportRepository reportRepository;
  private final MemberOrgValidator memberOrgValidator;
  private final PostRepository postRepository;
  private final CommentRepository commentRepository;

  @Transactional
// 🚨 게시글 신고 로직
  public void reportPost(ReportReqDto reqDto, Long requesterMemberOrgId, Long postId) {
    MemberOrg reporter = memberOrgValidator.validateActiveMembership(requesterMemberOrgId);

    Post post = postRepository.findById(postId)
        .orElseThrow(() -> new IllegalArgumentException("해당 게시글을 찾을 수 없습니다."));

    if (post.getWriter().getId().equals(reporter.getId())) {
      throw new IllegalStateException("자신의 게시글은 신고할 수 없습니다.");
    }

    Report report = Report.builder()
        .reporter(reporter)
        .targetMember(post.getWriter())
        .targetId(post.getId())
        .targetType(TargetType.POST)
        .postId(post.getId())
        .postType(post.getPostType())
        .reportedPostTitle(post.getTitle())
        .reportedContent(post.getContent())
        .reportReason(reqDto.getReportReason())
        .detailReason(reqDto.getDetailReason())
        .build();

    post.receiveReport();
    reportRepository.save(report);
  }

  // 댓글 신고
  @Transactional
  public void reportComment(ReportReqDto reqDto, Long requesterMemberOrgId, Long commentId) {
    MemberOrg reporter = memberOrgValidator.validateActiveMembership(requesterMemberOrgId);

    Comment comment = commentRepository.findByIdWithPost(commentId)
        .orElseThrow(() -> new IllegalArgumentException("해당 댓글을 찾을 수 없습니다."));

    Post post = comment.getPost();

    Report report = Report.builder()
        .reporter(reporter)
        .targetMember(comment.getWriter())
        .targetId(comment.getId())
        .targetType(TargetType.COMMENT)
        .postId(post.getId())
        .postType(post.getPostType())
        .reportedPostTitle(post.getTitle())
        .reportedContent(comment.getContent())
        .reportReason(reqDto.getReportReason())
        .detailReason(reqDto.getDetailReason())
        .build();

    comment.receiveReport();
    reportRepository.save(report);
  }


    /*
    @Transactional
    public void createReport(ReportRequestDto dto, String email, String org) {
        Member reporter = memberRepository.findByEmailAndOrganization(email, org)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        Report report = Report.fromDto(dto, reporter);

        reportRepository.save(report);
    }
    */
}
