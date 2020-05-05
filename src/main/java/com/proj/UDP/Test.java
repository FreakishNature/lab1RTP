package com.proj.UDP;

import java.io.IOException;

public class Test {

    public static void main(String[] args) throws IOException {
        Client client = new Client();

        String echo = client.sendEcho("hello server");
        System.out.println(echo);

        echo = client.sendEcho("stop");
        System.out.println(echo);

        client.close();
    }
}