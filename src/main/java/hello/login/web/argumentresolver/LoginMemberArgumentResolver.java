package hello.login.web.argumentresolver;

import hello.login.domain.member.Member;
import hello.login.web.SessionConst;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

// ArgumentResolver : HTTP Header, Session, Cookie 등 직접적이지 않은 방식이나, 외부 데이터 저장소로부터 데이터를 바인딩 해야할 때 사용
@Slf4j
public class LoginMemberArgumentResolver implements HandlerMethodArgumentResolver {

    // 주어진 메서드의 파라미터가 이 ArgumentResolver에서 지원하는 타입인지 검사.
    // @Login 어노테이션이 있으면서 Member 타입이면 해당 ArgumentResolver가 사용된다 .
    // 반복 수행(새로고침)하면 내부 캐시에 저장을 해둬서 이 메서드는 다시 또 실행하지 않고 resolveArgument만 실행함
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        log.info("supportsParameter 실행");

        boolean hasParameterAnnotation = parameter.hasParameterAnnotation(Login.class); // @Login 애노테이션이 있으면서 `Member` 타입이면 해당 `ArgumentResolver`가 사용된다.
        boolean hasMemberType = Member.class.isAssignableFrom(parameter.getParameterType()); // @Login 어노테이션이 붙은 객체(클래스)가 Member.class 타입인지 확인

        return hasParameterAnnotation && hasMemberType;
    }

    // 컨트롤러 호출 직전에 호출 되어서 필요한 파라미터 정보를 생성해준다. 여기서는 세션에 있는 로그인 회원 정보인
    // `member` 객체를 찾아서 반환해준다. 이후 스프링MVC는 컨트롤러의 메서드를 호출 하면서
    // 여기에서 반환된 `member` 객체를 파라미터에 전달해준다.
    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {

        log.info("resolverArgument 실행");

        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest(); // getNativeRequest() -> HttpServletRequest로 캐스팅하기
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;    // Member에 null을 넣어버림(@Login 어노테이션을 붙인 컨트롤러(HomeController 옆 객체(Member))
        }

        return session.getAttribute(SessionConst.LOGIN_MEMBER); // 옵션 + 커멘드 + n (코드 압축)
    }

}
