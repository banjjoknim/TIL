package colt.springboot.test.domain.member.repository;

import colt.springboot.test.domain.member.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
}
