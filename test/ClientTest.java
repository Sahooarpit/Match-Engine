import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ClientTest {

    @Test
    void testClientCreation() {
        Client client = new Client("testId", "testUsername");
        assertEquals("testId", client.getClientId());
        assertEquals("testUsername", client.getUsername());
    }
}
