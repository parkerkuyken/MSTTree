package csx55.overlay.wireformats;

import java.io.IOException;

public interface Event {
    byte[] getBytes() throws IOException;  // Marshalling: convert object to byte[]
    int getType();                         // Returns message type from Protocol
}