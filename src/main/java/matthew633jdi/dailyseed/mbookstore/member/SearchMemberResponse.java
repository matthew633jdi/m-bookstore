package matthew633jdi.dailyseed.mbookstore.member;

public record SearchMemberResponse(
        String email,
        String name,
        String phone,
        String address

) {
    public static SearchMemberResponse from(Member member) {
        return new SearchMemberResponse(member.getEmail(), member.getName(), member.getPhoneNumber(), member.getAddress().getFullAddress());
    }
}