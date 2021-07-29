package colt.springboot.test.domain.member.model;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MemberTest {

    @ParameterizedTest
    @ValueSource(strings = {"멤버의", "이름은", "5글자", "이내여야", "한다"})
    void 멤버의_이름이_5글자_이내일때는_멤버가_생성된다(String name) {
        // given

        // when
        Member member = new Member(name);

        // when
        assertThat(member.getName()).isEqualTo(name);
    }

    @ParameterizedTest
    @ValueSource(strings = {"이름의 길이가", "5글자보다 크면", "예외가 발생한다"})
    void 멤버의_이름이_5글자를_초과할경우_예외가_발생된다(String name) {
        // given

        // when

        // then
        assertThatThrownBy(() -> new Member(name))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest
    @NullAndEmptySource
    void 멤버의_이름이_비어있거나_널이면_예외가_발생된다(String name) {
        // given

        // when

        // then
        assertThatThrownBy(() -> new Member(name))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {"이름을", "변경하면", "잘변경된다"})
    void 멤버의_이름이_변경된다(String name) {
        // given
        Member member = new Member("멤버");

        // when
        member.changeName(name);

        // then
        assertThat(member.getName()).isEqualTo(name);
    }
}
