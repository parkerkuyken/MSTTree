package csx55.overlay.wireformats;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;
import java.io.IOException;

public class RegisterTest {

    @Test
    public void testRegisterRequestMarshallingAndUnmarshalling() throws IOException {
        String ip = "127.0.0.1";
        int port = 5000;

        // Create request object
        Register request = new Register(ip, port);
        byte[] data = request.getBytes();

        // Reconstruct from bytes
        Register reconstructed = new Register(data);

        assertEquals(Protocol.REGISTER_REQUEST, reconstructed.getType());
        assertEquals(ip, reconstructed.getIpAddress());
        assertEquals(port, reconstructed.getPortNumber());
        assertEquals(0, reconstructed.getStatusCode()); // byte defaults to 0
        assertNull(reconstructed.getAdditionalInfo()); // String defaults to null
    }

    @Test
    public void testRegisterResponseMarshallingAndUnmarshalling() throws IOException {
        byte statusCode = Protocol.SUCCESS;
        String info = "Registration successful";

        // Create response object
        Register response = new Register(statusCode, info);
        byte[] data = response.getBytes();

        // Reconstruct from bytes
        Register reconstructed = new Register(data);

        assertEquals(Protocol.REGISTER_RESPONSE, reconstructed.getType());
        assertEquals(statusCode, reconstructed.getStatusCode());
        assertEquals(info, reconstructed.getAdditionalInfo());
        assertNull(reconstructed.getIpAddress()); // String defaults to null
        assertEquals(0, reconstructed.getPortNumber()); // int defaults to 0
    }

    @Test
    public void testGettersForRequest() {
        Register request = new Register("192.168.1.10", 8080);

        assertEquals(Protocol.REGISTER_REQUEST, request.getType());
        assertEquals("192.168.1.10", request.getIpAddress());
        assertEquals(8080, request.getPortNumber());
        assertEquals(0, request.getStatusCode());
        assertNull(request.getAdditionalInfo());
    }

    @Test
    public void testGettersForResponse() {
        Register response = new Register(Protocol.FAILURE, "Failure: Already registered");

        assertEquals(Protocol.REGISTER_RESPONSE, response.getType());
        assertEquals(Protocol.FAILURE, response.getStatusCode());
        assertEquals("Failure: Already registered", response.getAdditionalInfo());
        assertNull(response.getIpAddress());
        assertEquals(0, response.getPortNumber());
    }

    // @Test
    // public void testInvalidMessageType() throws IOException {
    //     // Create a request but corrupt the message type in bytes
    //     Register request = new Register("127.0.0.1", 8080);
    //     byte[] data = request.getBytes();
        
    //     // Corrupt the message type (first 4 bytes)
    //     data[0] = (byte) 0xFF; // Set to invalid message type
        
    //     assertThrows(IOException.class, () -> {
    //         new Register(data);
    //     });
    // }

    @Test
    public void testEdgeCaseIPAddresses() throws IOException {
        String[] testIPs = {"", "localhost", "255.255.255.255", "::1"};
        
        for (String ip : testIPs) {
            Register request = new Register(ip, 8080);
            byte[] data = request.getBytes();
            Register reconstructed = new Register(data);
            
            assertEquals(ip, reconstructed.getIpAddress());
        }
    }

    @Test
    public void testEdgeCasePortNumbers() throws IOException {
        int[] testPorts = {0, 1, 65535, 1024};
        
        for (int port : testPorts) {
            Register request = new Register("127.0.0.1", port);
            byte[] data = request.getBytes();
            Register reconstructed = new Register(data);
            
            assertEquals(port, reconstructed.getPortNumber());
        }
    }
}