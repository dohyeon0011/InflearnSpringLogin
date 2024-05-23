package hello.login;

import hello.login.web.argumentresolver.LoginMemberArgumentResolver;
import hello.login.web.filter.LogFilter;
import hello.login.web.filter.LoginCheckFilter;
import hello.login.web.interceptor.LogInterceptor;
import hello.login.web.interceptor.LogincheckInterceptor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.Filter;
import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    // 직접 만든 @Login 어노테이션을 사용하기 위해 WebMvcConfigurer 에서 오버라이딩
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new LoginMemberArgumentResolver());
    }

    // 스프링에 제공하는 URL 경로는 서블릿 기술이 제공하는 URL 경로와 완전히 다르고, 자세하고 세밀하게 설정 가능.
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LogInterceptor()) // 인터셉터 등록
                .order(1) // 인터셉터 호출 순위
                .addPathPatterns("/**") // 여기 하위 전부 다(** : 경로 끝까지) 인터셉터 적용
                .excludePathPatterns("/css/**", "/*.ico", "/error"); // 이 경로는 interceptor 먹이지마(인터셉터 제외할 패턴)

        registry.addInterceptor(new LogincheckInterceptor()) // URI 화이트리스트와 블랙 리스트 설정하면서 인터셉터 등록
                .order(2)
                .addPathPatterns("/**") // 모든 경로에 인터셉터를 적용하지만
                .excludePathPatterns("/", "members/add", "/login", "/logout", "/css/**", "/*.ico", "/error"); // 여긴 검증하지말자
    }

    // was를 띄울 때 필터를 같이 넣어줌
//    @Bean
    public FilterRegistrationBean logFilter() {
        FilterRegistrationBean<Filter> filterFilterRegistrationBean = new FilterRegistrationBean<>();
        filterFilterRegistrationBean.setFilter(new LogFilter());
        filterFilterRegistrationBean.setOrder(1); // 필터 순서 정해주기(필터가 체인으로 여러 개 들어갈 수도 있어서)
        filterFilterRegistrationBean.addUrlPatterns("/*");

        return filterFilterRegistrationBean;
    }

//    @Bean
    public FilterRegistrationBean loginCheckFilter() {
        FilterRegistrationBean<Filter> filterFilterRegistrationBean = new FilterRegistrationBean<>();
        filterFilterRegistrationBean.setFilter(new LoginCheckFilter());
        filterFilterRegistrationBean.setOrder(2); // 필터 순서 정해주기(필터가 체인으로 여러 개 들어갈 수도 있어서)
        filterFilterRegistrationBean.addUrlPatterns("/*");

        return filterFilterRegistrationBean;
    }

}
