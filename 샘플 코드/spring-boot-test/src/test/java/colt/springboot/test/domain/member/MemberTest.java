package colt.springboot.test.domain.member;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class MemberTest {

    @ParameterizedTest
    @MethodSource("createMember")
    void 멤버를_생성한다(Long id, String name) {
        // given

        // when
        Member member = new Member(id, name);

        // when
        assertAll(
                () -> assertThat(member.getId()).isEqualTo(id),
                () -> assertThat(member.getName()).isEqualTo(name)
        );
    }

    private static Stream<Arguments> createMember() {
        return Stream.of(
                Arguments.of(1L, "member1")
        );
    }
}