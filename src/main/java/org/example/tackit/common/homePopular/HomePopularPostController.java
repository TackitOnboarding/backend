package org.example.tackit.common.homePopular;

import lombok.RequiredArgsConstructor;
import org.example.tackit.common.dto.ActiveProfile;
import org.example.tackit.common.dto.ProfileContext;
import org.example.tackit.domain.auth.login.security.CustomUserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/home")
@RequiredArgsConstructor
public class HomePopularPostController {

    private final HomePopularPostService homePopularPostService;

    @GetMapping("/popular")
    public ResponseEntity<List<HomePopularPostRespDto>> getPopularPosts(
            @AuthenticationPrincipal CustomUserDetails user,
            @ActiveProfile ProfileContext profile
            ) {
        List<HomePopularPostRespDto> result = homePopularPostService.getPopularPosts(profile.id());
        return ResponseEntity.ok(result);
    }
}