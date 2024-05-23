package hello.login.web.interceptor;

import hello.login.web.SessionConst;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

// HandlerInterceptor는 LoginCheckFilter와 다르게 URI 화이트 리스트를 설정하지 않아도 됨. -> 인터셉터 등록할 때 다 할 수 있어서(WebConfig Class)
// 특별한 문제가 없으면 인터셉터를 사용하는 것이 국룰
@Slf4j
public class LogincheckInterceptor implements HandlerInterceptor {

    // 컨트롤러 호출 전에 세션(인증)만 확인하면 되니까 이것만 오버라이딩
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestURI = request.getRequestURI();

        log.info("인증 체크 인터셉터 실행 {}", requestURI);

        HttpSession session = request.getSession();

        if (session == null || session.getAttribute(SessionConst.LOGIN_MEMBER) == null) {
            log.info("미인증 사용자 요청");
            // 로그인으로 redirect
            response.sendRedirect("/login?redirectURL=" + requestURI);
            return false;
        }

        return true;
    }
}
