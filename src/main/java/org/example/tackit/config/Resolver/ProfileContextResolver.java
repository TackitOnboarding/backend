package org.example.tackit.config.Resolver;

import lombok.extern.slf4j.Slf4j;
import org.example.tackit.common.dto.ActiveProfile;
import org.example.tackit.common.dto.ProfileContext;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Slf4j
@Component
public class ProfileContextResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        // 컨트롤러 파라미터에 @ActiveProfile이 붙어있고 타입이 ProfileContext인 경우에만 동작
        return parameter.hasParameterAnnotation(ActiveProfile.class) &&
                parameter.getParameterType().equals(ProfileContext.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {

        // 1. 헤더에서 값 읽기
        String idStr = webRequest.getHeader("Active-Profile-Id");

        // 2. 값이 없는 경우 처리 (null 방지)
        if (idStr == null) {
            log.warn("멀티 프로필 헤더가 누락되었습니다. ID: {}", idStr);
            throw new IllegalArgumentException("Required 'Active-Profile-Id' header is not present.");
        }

        try {
            Long id = Long.parseLong(idStr);

            log.info("성공적으로 프로필을 로드했습니다. ID: {}", id);
            return new ProfileContext(id);

        } catch (NumberFormatException e) {
            log.error("프로필 헤더 형식이 잘못되었습니다. ID: {}", idStr);
            throw new IllegalArgumentException("Invalid format for 'Active-Profile-Id' header. It must be a number.");
        }
    }
}
