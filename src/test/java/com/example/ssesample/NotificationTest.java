package com.example.ssesample;


import com.example.ssesample.service.NotificationService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

// MockMvcRequestBuilders의 정적 메소드를 이용하여 RequestBuilder 객체를 만들어서 perform 인자로 대입
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class NotificationTest {
    @Autowired
    private MockMvc mockMvc;

    private NotificationService notificationService;
    @Autowired
    public NotificationTest(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @BeforeAll
    static void beforeAll() {
        System.out.println("## @BeforeAll 호출 ##");
        System.out.println();
    }

    @AfterAll
    static void afterAll() {
        System.out.println("## @AfterAll 호출 ##");
        System.out.println();
    }
    @Test
    @DisplayName("알림구독테스트")
    void subscribe_test() throws Exception{
        String userName = "lhj";
        String notiType = "LIKE";
        String url = "test";
        mockMvc.perform(get("/subscribe/"+userName)).andExpect(status().isOk());
        notificationService.send(userName, notiType, url);
    }

    @Test
    @DisplayName("알림전송테스트")
    void send_test() {
        String userName = "leetory";
        String notiType = "LIKE";
        String url = "test";
        // 내용이 찍히지 않는다, 왜냐면 DB에 따로 저장하지 않고 memory hashmap에 저장되기 때문
        notificationService.send(userName, notiType, url);
    }
}
