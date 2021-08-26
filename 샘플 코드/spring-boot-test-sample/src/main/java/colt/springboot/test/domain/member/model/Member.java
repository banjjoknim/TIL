package colt.springboot.test.domain.member.model;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@AllArgsConstructor
@Builder
@Entity
public class Member {
    private static final int LIMIT_NAME_LENGTH = 5;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

//    @ManyToOne(fetch = FetchType.LAZY)
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "teamId")
    private Team team;

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

    public void changeName(String name) {
        validateName(name);
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
}
