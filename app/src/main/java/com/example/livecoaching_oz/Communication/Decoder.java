package com.example.livecoaching_oz.Communication;

public interface Decoder {
    void decodeResponse(String rep);
    void errorMessage(String err);
}
