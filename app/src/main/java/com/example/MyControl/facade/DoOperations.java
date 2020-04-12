package com.example.MyControl.facade;
import java.io.IOException;
public interface DoOperations {
    String readMedia(String key)throws IOException;
    void closeStream()throws IOException;
    void setStream()throws IOException;
    void writingStream(String message)throws IOException;
}
