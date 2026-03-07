package matthew633jdi.dailyseed.mbookstore.member;

import lombok.RequiredArgsConstructor;
import matthew633jdi.dailyseed.mbookstore.exception.DomainException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public JoinMemberResponse join(JoinMemberRequest request) {
        if (memberRepository.existsByEmail(request.email())) {
            throw new DomainException(MemberErrorCode.DUPLICATE_EMAIL);
        }
        if (memberRepository.existsByPhoneNumber(request.phoneNumber())) {
            throw new DomainException(MemberErrorCode.DUPLICATE_PHONE);
        }

        String encodedPwd = passwordEncoder.encode(request.password());
        Member savedMemeber = memberRepository.save(request.toEntity(encodedPwd));
        return JoinMemberResponse.from(savedMemeber);
    }

    @Transactional(readOnly = true)
    public SearchMemberResponse findByPhone(SearchMemberRequest request) {
        Optional<Member> optionalMember = memberRepository.findByPhoneNumber(request.phone());
        if (optionalMember.isEmpty()) {
            throw new DomainException(MemberErrorCode.NOTFOUND_MEMBER);
        }

        return SearchMemberResponse.from(optionalMember.get());
    }
}