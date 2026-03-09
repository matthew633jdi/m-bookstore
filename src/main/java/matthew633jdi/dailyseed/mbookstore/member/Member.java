package matthew633jdi.dailyseed.mbookstore.member;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import matthew633jdi.dailyseed.mbookstore.base.Address;
import matthew633jdi.dailyseed.mbookstore.base.BaseEntity;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    @Embedded
    private Address address;

    @Builder
    public Member(String email, String password, String name, String phoneNumber, UserRole role, Address address) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.role = role;
        this.address = address;
    }

    public void changeRole(UserRole role) {
        this.role = role;
    }

    public void changeAddress(Address address) {
        this.address = address;
    }
}