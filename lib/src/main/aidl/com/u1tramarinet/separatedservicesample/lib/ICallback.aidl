package com.u1tramarinet.separatedservicesample.lib;

oneway interface ICallback {
    void onEvent(in String message);
}