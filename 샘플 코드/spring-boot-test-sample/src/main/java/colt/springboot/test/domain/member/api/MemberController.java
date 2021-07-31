package colt.springboot.test.domain.member.api;

import colt.springboot.test.domain.member.dto.MemberRequest;
import colt.springboot.test.domain.member.dto.MemberResponse;
import colt.springboot.test.domain.member.service.MemberService;
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
    public ResponseEntity<MemberResponse> createMember(@RequestBody MemberRequest memberRequest) {
        MemberResponse memberResponse = memberService.saveMember(memberRequest);
        return ResponseEntity.created(URI.create("/members/" + memberResponse.getId())).body(memberResponse);
    }
}
