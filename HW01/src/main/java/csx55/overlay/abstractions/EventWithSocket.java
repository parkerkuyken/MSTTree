package csx55.overlay.abstractions;

import java.net.Socket;
import csx55.overlay.wireformats.*;

//Stateless Class, holds an event with its socket
public class EventWithSocket {
    public Event event;
    public Socket socket;

        public EventWithSocket(Event event, Socket socket) {
            this.event = event;
            this.socket = socket;
        }
    }
