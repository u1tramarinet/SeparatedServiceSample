package com.u1tramarinet.separatedservicesample.lib;

import com.u1tramarinet.separatedservicesample.lib.ICallback;

interface IRemoteSeparatedService {
    int getRandomNumber();
    void registerCallback(in ICallback callback);
    void unregisterCallback(in ICallback callback);
}