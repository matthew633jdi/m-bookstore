package matthew633jdi.dailyseed.mbookstore.base;

import jakarta.persistence.Embeddable;
import lombok.*;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
public class Address {

    private String street;
    private String detail;
    private String zipcode;

    @Builder
    public Address(String street, String detail, String zipcode) {
        this.street = street;
        this.detail = detail;
        this.zipcode = zipcode;
    }

    public String getFullAddress() {
        return street + " " + detail + ", " + zipcode;
    }
}
