package hello.login.web.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

// 스프링 인터셉터는 컨트롤러와 디스패처 서블릿 사이에서 컨트롤러 호출 직전에 호출된다.
@Slf4j
public class LogInterceptor implements HandlerInterceptor {

    public static final String LOG_ID = "logId";

    // 컨트롤러 호출 전
    // 예외인 상황에서 안 찍힘
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestURI = request.getRequestURI();
        String uuid = UUID.randomUUID().toString();

        request.setAttribute(LOG_ID, uuid); // static 맴버 변수로 두기엔 싱글톤로 쓰여서 값이 바꿔치기 당할 수도 있음.

        // @RequestMapping을 사용할 경우 HandlerMethod가 넘어옴
        // 정적 리소스 : ResourceHttpRequestHandler
        if (handler instanceof HandlerMethod) {
            HandlerMethod hm = (HandlerMethod) handler; // 호출할 컨트롤러 메서드의 모든 정보가 포함되어 있다.
        }

        log.info("REQUEST [{}][{}][{}]", uuid, requestURI, handler); // (handler) 어떤 컨트롤러가 호출돼서 넘어오는지
        return true; // true로 하면 다음 컨트롤러가 호출됨. false면 진행x
    }

    // 컨트롤러 호출 후(View에 전달하기 전에 호출되기 때문에 model에 추가적인 데이터를 담을 수 있음)
    // 예외인 상황에서 안 찍힘
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        log.info("postHandle [{}]", modelAndView); // View와 Model에 담긴 정보
    }

    // 요청 완료 이후
    // 예외 상황에서도 무조건 호출됨.
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        String requestURI = request.getRequestURI();
        String logId = (String) request.getAttribute(LOG_ID);

        log.info("RESPONSE [{}][{}][{}]", logId, requestURI, handler);

        if (ex != null) {
            log.error("afterCompletion error!!", ex);   // 오류는 {}로 값 안 넣어줘도 됨.
        }


    }
}
