// Node.java - This should be in csx55.overlay.node package
package csx55.overlay.node;

import csx55.overlay.wireformats.Event;


import java.io.IOException;
import java.net.Socket;

public interface Node {

/**
    * Incoming from TCPReceiverThread, puts event into synchronized queue
    * <p> Interface method from Node </p>
    * @param event Event received
    * @param socket Socket from which event was received
    * @throws IOException
*/
    void onEvent(Event event, Socket socket) throws IOException;

}