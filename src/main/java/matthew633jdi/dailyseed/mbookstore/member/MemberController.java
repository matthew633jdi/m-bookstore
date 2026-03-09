package matthew633jdi.dailyseed.mbookstore.member;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping(value = "/signup", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<JoinMemberResponse> signup(@Valid @RequestBody JoinMemberRequest request) {
        return ResponseEntity.status(CREATED).body(memberService.join(request));
    }

    @GetMapping("/find")
    public SearchMemberResponse getMemberBy(@Valid @ModelAttribute SearchMemberRequest request) {
        return memberService.findByPhone(request);
    }
}
