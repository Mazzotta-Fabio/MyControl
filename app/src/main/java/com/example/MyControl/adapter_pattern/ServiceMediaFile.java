package com.example.MyControl.adapter_pattern;

import java.io.IOException;

public interface ServiceMediaFile {
    String readFile(String key)throws IOException;
    void writeFile(String key,String value)throws IOException;
    void closeStreamFile()throws IOException;
}
