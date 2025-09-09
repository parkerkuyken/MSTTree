// Node.java - This should be in csx55.overlay.node package
package csx55.overlay.node;

import csx55.overlay.wireformats.Event;

import java.io.IOException;
import java.net.Socket;

public interface Node {
    void onEvent(Event event, Socket socket) throws IOException;
}