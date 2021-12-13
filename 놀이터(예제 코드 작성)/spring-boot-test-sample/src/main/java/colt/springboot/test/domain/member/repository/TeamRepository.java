package colt.springboot.test.domain.member.repository;

import colt.springboot.test.domain.member.model.Team;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamRepository extends JpaRepository<Team, Long> {
}
