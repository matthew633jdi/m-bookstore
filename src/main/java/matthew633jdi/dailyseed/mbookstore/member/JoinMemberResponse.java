package matthew633jdi.dailyseed.mbookstore.member;

public record JoinMemberResponse(
        String email,
        String name
) {
    public static JoinMemberResponse from(Member member) {
        return new JoinMemberResponse(member.getEmail(), member.getName());
    }
}
