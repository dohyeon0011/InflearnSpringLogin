package hello.login.web.login;

import hello.login.domain.login.LoginService;
import hello.login.domain.member.Member;
import hello.login.web.SessionConst;
import hello.login.web.session.SessionManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@Slf4j
@Controller
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;
    private final SessionManager sessionManager;

    @GetMapping("/login")
    public String loginForm(@ModelAttribute("loginForm") LoginForm loginForm) {
        return "login/loginForm";
    }

    /*@PostMapping("/login")
    public String loginV1(@Valid @ModelAttribute LoginForm loginForm, BindingResult bindingResult, HttpServletResponse response) {
        if (bindingResult.hasErrors()) {
            return "login/loginForm";
        }

        Member loginMember = loginService.login(loginForm.getLoginId(), loginForm.getPassword());

        if (loginMember == null) {
            bindingResult.reject("loginFail", "아이디 또는 비밀번호가 맞지 않습니다."); // 글로벌 오류(디비까지 뒤져서 확인해야함, 객체만으로는 불가능 -> 이래서 @ScriptAssert가 별로)
            return "login/loginForm";
        }

        // 로그인 성공 처리 TODO
        // 세션 쿠키로 로그인 세션 유지(쿠키에 시간 정보를 주지 않으면 세션 쿠키(브라우저 종료시 모두 종료)
        // 근데 쿠키는 중간에 탈취도 가능하고 값을 빼올 수 있어서 보안에 있어서 매우 위험함.(대안이 필요(토큰 사용)
        Cookie idCookie = new Cookie("memberId", String.valueOf(loginMember.getId()));
        response.addCookie(idCookie);   // 응답에 쿠키를 넣어 클라이언트(웹 브라우저)에 줌.

        return "redirect:/";
    }*/


    /*// 직접 만든 SessionManager 사용
    @PostMapping("/login")
    public String loginV2(@Valid @ModelAttribute LoginForm loginForm, BindingResult bindingResult, HttpServletResponse response) {
        if (bindingResult.hasErrors()) {
            return "login/loginForm";
        }

        Member loginMember = loginService.login(loginForm.getLoginId(), loginForm.getPassword());

        if (loginMember == null) {
            bindingResult.reject("loginFail", "아이디 또는 비밀번호가 맞지 않습니다."); // 글로벌 오류(디비까지 뒤져서 확인해야함, 객체만으로는 불가능 -> 이래서 @ScriptAssert가 별로)
            return "login/loginForm";
        }

        // 로그인 성공 처리 TODO
        // 세션 관리자를 통해 세션을 생성하고, 회원 데이터를 보관
        sessionManager.createSession(loginMember, response);

        return "redirect:/";
    }*/



    /*// HttpSession 사용 -> 사용자가 마지막으로 요청한 시간을 기준으로 세션 시간을 연장(메모리에 세션을 저장함)
    // 실무에서는 세션에는 최소한의 데이터만 보관해야한다.(데이터 용량 * 사용자 수로 세션의 메모리 사용량이 늘어나 장애가 생길 수 있어서)
    // 자주 사용하는 간단한 정보만(ex) id, name 이런 정보들)
    @PostMapping("/login")
    public String loginV3(@Valid @ModelAttribute LoginForm loginForm, BindingResult bindingResult, HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            return "login/loginForm";
        }

        Member loginMember = loginService.login(loginForm.getLoginId(), loginForm.getPassword());

        if (loginMember == null) {
            bindingResult.reject("loginFail", "아이디 또는 비밀번호가 맞지 않습니다."); // 글로벌 오류(디비까지 뒤져서 확인해야함, 객체만으로는 불가능 -> 이래서 @ScriptAssert가 별로)
            return "login/loginForm";
        }

        // 로그인 성공 처리 TODO
        // 세션이 있으면 있는 세션 반환, 없으면 신규 세션을 생성
        // getSession() : default가 true로 세션이 있으면 기존 세션을 반환, 세션이 없으면 새로운 세션을 생성해서 반환.
        // false 이면 세션이 있으면 세션을 반환하고, 없으면 새로운 세션 생성하지 않고 null 반환
        HttpSession session = request.getSession();

        // 세션에 로그인 회원 정보 보관
        session.setAttribute(SessionConst.LOGIN_MEMBER, loginMember);

        return "redirect:/";
    }*/


    // Servlet Filter 적용
    @PostMapping("/login")
    public String loginV4(@Valid @ModelAttribute LoginForm loginForm, BindingResult bindingResult,
                          @RequestParam(defaultValue = "/") String redirectURL, HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            return "login/loginForm";
        }

        Member loginMember = loginService.login(loginForm.getLoginId(), loginForm.getPassword());

        if (loginMember == null) {
            bindingResult.reject("loginFail", "아이디 또는 비밀번호가 맞지 않습니다."); // 글로벌 오류(디비까지 뒤져서 확인해야함, 객체만으로는 불가능 -> 이래서 @ScriptAssert가 별로)
            return "login/loginForm";
        }

        // 로그인 성공 처리 TODO
        // 세션이 있으면 있는 세션 반환, 없으면 신규 세션을 생성
        // getSession() : default가 true로 세션이 있으면 기존 세션을 반환, 세션이 없으면 새로운 세션을 생성해서 반환.
        // false 이면 세션이 있으면 세션을 반환하고, 없으면 새로운 세션 생성하지 않고 null 반환
        HttpSession session = request.getSession();

        // 세션에 로그인 회원 정보 보관
        session.setAttribute(SessionConst.LOGIN_MEMBER, loginMember);   // JSESSIONID로 개인 고유 세션 id에 값으로 멤버 객체를 넣어줌

        // Servlet Filter redirectURL 설정(비로그인 상태로 아이템 폼에 갔다가 로그인 폼으로 리다이렉트 당하고 로그인 하면 아이템 폼으로 가게 해주는)
        return "redirect:" + redirectURL;
    }





    /*// 로그아웃 방법은 쿠키 지워 버리기(시간 없애기)
    @PostMapping("/logout")
    public String logoutV1(HttpServletResponse response) {
        expireCookie(response, "memberId");
        return "redirect:/";
    }
*/



    /*// SessionManager 사용
    // 로그아웃 방법은 쿠키 지워 버리기(시간 없애기)
    @PostMapping("/logout")
    public String logoutV2(HttpServletRequest request) {
        sessionManager.expire(request);
        return "redirect:/";
    }*/



    // HttpSession 사용
    @PostMapping("/logout")
    public String logoutV3(HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session != null) {
            session.invalidate();   // 세션과 그 안에 있는 데이터가 모두 날라감
        }
        return "redirect:/";
    }




    private static void expireCookie(HttpServletResponse response, String cookieName) {
        Cookie cookie = new Cookie(cookieName, null);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }

}
