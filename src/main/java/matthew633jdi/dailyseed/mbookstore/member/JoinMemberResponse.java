package matthew633jdi.dailyseed.mbookstore.member;

public record JoinMemberResponse(
        String email,
        String name,
        UserRole role
) {
    public static JoinMemberResponse from(Member member) {
        return new JoinMemberResponse(member.getEmail(), member.getName(), member.getRole());
    }
}
