package matthew633jdi.dailyseed.mbookstore.member;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    public JoinMemberResponse join(JoinMemberRequest request) {
        //
        return null;
    }
}
