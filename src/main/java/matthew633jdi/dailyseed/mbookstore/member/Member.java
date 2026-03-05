package matthew633jdi.dailyseed.mbookstore.member;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import matthew633jdi.dailyseed.mbookstore.BaseEntity;

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

    @Enumerated(EnumType.STRING)
    private UserRole role;

    @Builder
    public Member(String email, String password, String name, UserRole role) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.role = role;
    }

    public void changeRole(UserRole role) {
        this.role = role;
    }
}