package org.cloud.client.net;

public interface OutputHandler {

    public void handleMessage(String message);

    public void handleNewConnection(String message);
}
