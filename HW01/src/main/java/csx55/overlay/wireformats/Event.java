package csx55.overlay.wireformats;

import java.io.IOException;

public interface Event {

      /**
     * Marshall Algorhtim, makes data into byte array
     * @return byte array of marshalled data
     * @throws IOException
     */
    byte[] getBytes() throws IOException;  


    /**
     * Get the type of the event
     * @return int representing the event type
     */
    int getType();                         
}