//package org.example.tackit.config;
//
//import org.example.tackit.domain.admin.service.AdminFreePostService;
//import org.example.tackit.domain.admin.service.AdminQnAPostService;
//import org.example.tackit.domain.admin.service.AdminTipPostService;
//import org.example.tackit.domain.admin.service.ReportedPostService;
//import org.example.tackit.domain.entity.Post;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import java.util.Map;
//
//@Configuration
//public class ReportedPostServiceConfig {
//    @Bean
//    public Map<Post, ReportedPostService> reportedPostServices(
//            AdminFreePostService free,
//            AdminTipPostService tip,
//            AdminQnAPostService qna
//    ) {
//        return Map.of(
//                Post.Free, free,
//                Post.QnA, qna,
//                Post.Tip, tip
//        );
//    }
//}
