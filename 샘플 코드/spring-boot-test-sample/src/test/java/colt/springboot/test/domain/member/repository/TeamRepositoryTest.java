package colt.springboot.test.domain.member.repository;

import colt.springboot.test.domain.member.model.Member;
import colt.springboot.test.domain.member.model.Team;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class TeamRepositoryTest {

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    void cascadeInTeamSaveTest() {
        // given
        Team team = Team.builder()
                .name("team")
                .build();
        Member member = Member.builder()
                .name("member")
                .team(team)
                .build();
        team.getMembers().add(member);

        // when
        teamRepository.save(team);

        // then
        assertThat(memberRepository.findAll()).hasSize(1);
    }

    @Test
    void cascadeInTeamRemoveTest() {
        // given
        Team team = Team.builder()
                .name("team")
                .build();
        Member member = Member.builder()
                .name("member")
                .team(team)
                .build();
        team.getMembers().add(member);
        teamRepository.save(team);

        // when
        teamRepository.delete(team);

        // then
        assertThat(memberRepository.findAll().isEmpty()).isTrue();
    }

    @Test
    void orphanRemovalInTeamTest() {
        // given
        Team team = Team.builder()
                .name("team")
                .build();
        Member member = Member.builder()
                .name("member")
                .team(team)
                .build();
        team.getMembers().add(member);
        teamRepository.save(team);

        // when
        Member remove = team.getMembers().remove(0);
        memberRepository.delete(remove);

        // then
        assertThat(memberRepository.findAll()).hasSize(0);
        assertThat(teamRepository.findAll().isEmpty()).isTrue();
    }
}