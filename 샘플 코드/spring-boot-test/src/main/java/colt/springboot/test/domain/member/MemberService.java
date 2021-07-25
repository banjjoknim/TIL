package colt.springboot.test.domain.member;

import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
public class MemberService {

    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public List<MemberResponse> findMembers() {
        return memberRepository.findAll().stream()
                .map(member -> new MemberResponse(member.getId(), member.getName()))
                .collect(toList());
    }

    public MemberResponse findMember(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new NotFoundMemberException("존재하지 않는 멤버입니다."));
        return new MemberResponse(member.getId(), member.getName());
    }

    public Member saveMember(MemberRequest memberRequest) {
        Member member = Member.builder()
                .name(memberRequest.getName())
                .build();
        return memberRepository.save(member);
    }
}
