package com.example.MyControl.adapter_pattern;

import java.io.IOException;

public interface ServiceMediaNetwork {
    void writeSocket(String message)throws IOException;
    String readSocket() throws  IOException;
    void sendLocalAddress() throws  IOException;
    void closeSocketStream() throws  IOException;
}
