package colt.springboot.test.domain.member.api;

import colt.springboot.test.domain.member.dto.MemberRequest;
import colt.springboot.test.domain.member.dto.MemberResponse;
import colt.springboot.test.domain.member.exception.NotFoundMemberException;
import colt.springboot.test.domain.member.service.MemberService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 이 클래스는 독립 실행형 설정으로 MockMVC를 사용하여 컨트롤러를 테스트하는 방법을 보여준다.
 * <p>
 * https://dadadamarine.github.io/java/spring/2019/03/16/spring-boot-validation.html - 저자의 글.
 * <p>
 * https://github.com/json-path/JsonPath - JsonPath를 사용하는 방법.
 */
@ExtendWith(MockitoExtension.class)
public class MemberControllerMockMvcStandaloneTest {

    private MockMvc mockMvc;

    @Mock // Mock 객체로 선언한다.
    private MemberService memberService;

    @InjectMocks // @Mock이 선언된 객체들을 @InjectMocks이 선언된 객체에 의존 주입해준다.
    private MemberController memberController;

    // 이 객체는 아래의 setUp() 메서드 내에서 호출하는 initFields() 메서드 의해 초기화된다.
    private JacksonTester<MemberRequest> memberRequestJacksonTester;

    @BeforeEach
    void setUp() {
//        만약 Mockito Extension을 사용하지 않을 경우 필요하며, @Mock이 선언된 객체들을 초기화 해준다.
//        MockitoAnnotations.initMocks(this); -> Deprecated 되었음.

//        만약 Mockito Extension을 사용하지 않을 경우 필요하며, @Mock이 선언된 객체들을 초기화 해준다.
//        MockitoAnnotations.openMocks(this);

        // 여기서는 Spring context가 없기 때문에 @AutoConfigureJsonTesters를 사용할 수 없다.
        // 따라서 아래와 같이 JacksonTester를 초기화 해준다.
        JacksonTester.initFields(this, new ObjectMapper());

        // MockMvc 독립 실행형 설정(접근) 방식
        mockMvc = MockMvcBuilders.standaloneSetup(memberController)
                .setControllerAdvice(new MemberExceptionHandler())
//                .addFilter(new MemberFilter())
                .build();
    }

    @DisplayName("멤버 리스트를 조회하는 상황을 테스트한다.")
    @Test
    void canRetrieveMembers() throws Exception {
        // given
        given(memberService.findMembers())
                .willReturn(Arrays.asList(new MemberResponse(1L, "member1"), new MemberResponse(2L, "member2")));

        // when
        ResultActions resultActions = mockMvc.perform(get("/members")
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding("utf-8"));

        // then
        resultActions.andExpect(jsonPath("$").isNotEmpty())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$..id").hasJsonPath())
                .andExpect(jsonPath("$..name").hasJsonPath())
                .andDo(print());
    }

    @DisplayName("멤버가 존재할 경우에 아이디로 멤버를 조회하는 상황을 테스트한다.")
    @Test
    void canRetrieveMemberByIdWhenExists() throws Exception {
        // given
        given(memberService.findMember(1L))
                .willReturn(new MemberResponse(1L, "member1"));

        // when
        ResultActions resultActions = mockMvc.perform(get("/members/1")
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding("utf-8"));


        // then
        resultActions.andExpect(jsonPath("$..id").hasJsonPath())
                .andExpect(jsonPath("$..name").hasJsonPath())
                .andExpect(jsonPath("$..id").value(1))
                .andExpect(jsonPath("$..name").value("member1"))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @DisplayName("멤버가 존재하지 않을 경우에 아이디로 멤버를 조회하는 상황을 테스트한다.")
    @Test
    void canRetrieveMemberByIdWhenDoesNotExists() throws Exception {
        // given
        given(memberService.findMember(1L))
                .willThrow(new NotFoundMemberException("존재하지 않는 멤버입니다."));

        // when
        ResultActions resultActions = mockMvc.perform(get("/members/1")
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding("utf-8"));

        // then
        resultActions.andExpect(status().isNotFound())
                .andExpect(result -> assertThat(result.getResolvedException().getMessage()).isEqualTo("존재하지 않는 멤버입니다."))
                .andExpect(content().string(""))
                .andDo(print());
    }

    @DisplayName("특정 헤더가 요청에 포함되었는지 테스트한다.")
    @Test
    void headerIsPresent() throws Exception {
        // given

        // when
        ResultActions resultActions = mockMvc.perform(get("/members/1")
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding("utf-8")
                .header("test-header", "콜트"));
        MvcResult mvcResult = resultActions.andReturn();

        // then
        resultActions.andExpect(status().isOk())
                .andExpect(result -> assertThat(result.getRequest().getHeader("test-header")).isEqualTo("콜트"))
                .andDo(print());
    }

    @DisplayName("새로운 멤버를 생성하는 상황을 테스트한다.")
    @Test
    void createMember() throws Exception {
        // given
        MemberRequest memberRequest = new MemberRequest("콜트");
        given(memberService.saveMember(any(MemberRequest.class)))
                .willReturn(new MemberResponse(1L, "콜트"));

        // when
        ResultActions resultActions = mockMvc.perform(post("/members")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .characterEncoding("utf-8")
                .content(memberRequestJacksonTester.write(memberRequest).getJson())
        );

        // then
        resultActions.andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("콜트"))
                .andDo(print());
    }
}
