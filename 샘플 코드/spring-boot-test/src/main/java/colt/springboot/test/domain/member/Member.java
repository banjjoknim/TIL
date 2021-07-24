package colt.springboot.test.domain.member;

import lombok.Builder;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Builder
@Entity
public class Member {
    private static final int LIMIT_NAME_LENGTH = 5;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    protected Member() {
    }

    public Member(String name) {
        this(null, name);
    }

    public Member(Long id, String name) {
        validateName(name);
        this.id = id;
        this.name = name;
    }

    private void validateName(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("멤버의 이름은 비어있지 않아야 합니다.");
        }
        if (name.length() > LIMIT_NAME_LENGTH) {
            throw new IllegalArgumentException("멤버의 이름은 5글자 미만이어야 합니다.");
        }
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
