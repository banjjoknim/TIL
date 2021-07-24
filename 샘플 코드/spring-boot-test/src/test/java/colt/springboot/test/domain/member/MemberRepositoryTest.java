package colt.springboot.test.domain.member;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DataJpaTest
class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @ParameterizedTest
    @ValueSource(strings = {"적절한", "이름으로", "멤버를", "저장한다"})
    void 멤버를_저장한다(String name) {
        // given
        Member member = new Member(name);

        // when
        Member savedMember = memberRepository.save(member);

        // then
        assertAll(
                () -> assertThat(savedMember).isNotNull(),
                () -> assertThat(memberRepository.findAll()).hasSize(1)
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {"적절한", "이름으로", "멤버의", "이름을", "변경한다"})
    void 멤버의_이름을_변경한다(String name) {
        // given
        Member savedMember = memberRepository.save(new Member("멤버"));

        // when
        savedMember.changeName(name);

        // then
        assertThat(savedMember.getName()).isEqualTo(name);
    }
}