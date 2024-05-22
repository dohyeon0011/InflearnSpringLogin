package hello.login.domain.member;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class MemberRepository {

    private static final Logger log = LoggerFactory.getLogger(MemberRepository.class);
    private static Map<Long, Member> store = new HashMap<>();
    private static long sequence = 0L;

    public Member save(Member member) {
        member.setId(++sequence);
        log.info("save : member = {}", member);
        store.put(member.getId(), member);
        return member;
    }

    public Member findById(Long id) {
        return store.get(id);
    }

    public List<Member> findAll() {
        return new ArrayList<>(store.values());
    }

    public Optional<Member> findByLoginId(String loginId) { // null이 있을 수도 있으니 Optional로
        /*List<Member> all = findAll();
        for (Member member : all) {
            if (member.getLoginId().equals(loginId)) {
                return Optional.of(member);
            }
        }
        return Optional.empty();*/

        // 람다식
        return findAll().stream()
                .filter(member -> member.getLoginId().equals(loginId)) // 이 루프 안에서 조건을 만족하는 애들만 다음으로 넘어감
                .findFirst(); // 먼저 나오는 애를 받아서 반환
    }



}
