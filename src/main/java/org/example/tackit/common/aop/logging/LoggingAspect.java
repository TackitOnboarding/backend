package org.example.tackit.common.aop.logging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class LoggingAspect {
  // private final MemberLogRepository memberLogRepository;
  // private final FreeMemberJPARepository freeMemberJPARepository;

    /*
    @Around("execution(* org.example.tackit..*Controller.*(..))")
    public Object logUserAction(ProceedingJoinPoint joinPoint) throws Throwable {
        HttpServletRequest request =
                ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

        long start = System.currentTimeMillis();
        Object result = null;
        // 기본 정보
        String uri = request.getRequestURI();
        String method = request.getMethod();
        String userAgent = request.getHeader("User-Agent");
        String ip = request.getRemoteAddr();

        // 사용자 정보
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth != null ? auth.getName() : "anonymous";

        String org = null;
        if (!"anonymous".equals(email)) {
            Member member = freeMemberJPARepository.findByEmail(email).orElse(null);
            if (member != null) {
                org = member.getOrganization();
            }
        }

        try {
            result = joinPoint.proceed();
            return result;
        } finally {
            long execTime = System.currentTimeMillis() - start;

            // 자동으로 유추한 action/resource
            String[] uriParts = uri.split("/");
            String resource = uriParts.length > 1 ? uriParts[1] : "unknown";
            String action = method.toLowerCase() + "_" + (uriParts.length > 2 ? uriParts[2] : "default");

            // 로그저장
            MemberLog memberLog = MemberLog.builder()
                    .memberRole(memberRole)
                    .memberType(memberType)
                    .memberId(email)
                    .organization(org)
                    .ipAddress(ip)
                    .memberAgent(userAgent)
                    .timestamp(LocalDateTime.now())
                    .executionTime(execTime)
                    .requestUri(uri)
                    .action(action)
                    .resource(resource)
                    .build();

            memberLogRepository.save(memberLog);

            log.info("User [{}] performed [{}] on [{}] in {}ms from [{}]",
                    email, action, uri, execTime, ip);
        }
    }

     */
}
