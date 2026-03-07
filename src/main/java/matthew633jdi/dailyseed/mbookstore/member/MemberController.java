package matthew633jdi.dailyseed.mbookstore.member;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping(value = "/signup", consumes = MediaType.APPLICATION_JSON_VALUE)
    public JoinMemberResponse signup(@Valid @RequestBody JoinMemberRequest request) {
        return memberService.join(request);
    }

    @GetMapping("/find")
    public SearchMemberResponse getMemberBy(@Valid @ModelAttribute SearchMemberRequest request) {
        return memberService.findByPhone(request);
    }
}
