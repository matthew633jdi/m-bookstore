package matthew633jdi.dailyseed.mbookstore.member;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Boolean existsByEmail(String email);

    Boolean existsByPhoneNumber(String phoneNumber);
}