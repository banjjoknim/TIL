package colt.springboot.test.domain.member;

public class MemberResponse {
    private Long id;
    private String name;

    public MemberResponse(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
