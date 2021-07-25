package colt.springboot.test.domain.member;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RequestMapping("/members")
@RestController
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping("")
    public ResponseEntity<List<MemberResponse>> retrieveMembers() {
        List<MemberResponse> memberResponses = memberService.findMembers();
        return ResponseEntity.ok(memberResponses);
    }

    @GetMapping("/{memberId}")
    public ResponseEntity<MemberResponse> retrieveMember(@PathVariable("memberId") Long id) {
        MemberResponse memberResponse = memberService.findMember(id);
        return ResponseEntity.ok(memberResponse);
    }

    @PostMapping("")
    public ResponseEntity<Long> createMember(@RequestBody MemberRequest memberRequest) {
        Member member = memberService.saveMember(memberRequest);
        return ResponseEntity.created(URI.create("/members/" + member.getId())).body(member.getId());
    }
}
