package tests.login;

import com.github.javafaker.Faker;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.MockMvcPrint;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import sample.Application;

import java.util.Locale;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

/**
 * Created by egor on 30.03.17.
 */

@SpringBootTest(classes = Application.class)
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc(print = MockMvcPrint.NONE)
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class LoginTest {
    @Autowired
    private MockMvc mockMvc;

    private String userLogin;
    private String password;

    @Before
    public void setUp() {
        final Faker faker = new Faker(new Locale("en-US"));
        password = faker.internet().password(8, 10);
        userLogin = faker.name().username();
    }

    @Test
    public void loginOk() throws Exception {

        final JSONObject json = new JSONObject();

        json.put("login", userLogin);
        json.put("password", password);

        mockMvc
                .perform(
                        post("/api/user/signup")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json.toString()))
                .andExpect(jsonPath("status").value("200 OK"));

        mockMvc.perform(post("/api/user/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.toString()))
                .andExpect(jsonPath("status").value("200 OK"));
    }

    @Test
    public void loginFail() throws Exception {

        final JSONObject json = new JSONObject();

        json.put("login", userLogin);
        json.put("password", password);

        mockMvc.perform(post("/api/user/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.toString()))
                .andExpect(jsonPath("status").value("404 Not Found"));
    }

}