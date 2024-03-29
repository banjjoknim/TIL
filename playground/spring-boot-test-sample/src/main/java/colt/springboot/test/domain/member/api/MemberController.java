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

    // 컨트롤러 테스트시 andDo(print())에서 한글이 깨질 경우, produces를 추가해주면 해결된다.
    @GetMapping(value = "", produces = "application/json; charset=utf-8")
    public ResponseEntity<List<MemberResponse>> retrieveMembers() {
        List<MemberResponse> memberResponses = memberService.findMembers();
        return ResponseEntity.ok(memberResponses);
    }

    @GetMapping(value = "/{memberId}", produces = "application/json; charset=utf-8")
    public ResponseEntity<MemberResponse> retrieveMember(@PathVariable("memberId") Long id) {
        MemberResponse memberResponse = memberService.findMember(id);
        return ResponseEntity.ok(memberResponse);
    }

    @PostMapping(value = "", produces = "application/json; charset=utf-8")
    public ResponseEntity<MemberResponse> createMember(@RequestBody MemberRequest memberRequest) {
        MemberResponse memberResponse = memberService.saveMember(memberRequest);
        return ResponseEntity.created(URI.create("/members/" + memberResponse.getId())).body(memberResponse);
    }
}
