package other;

import master.Message;

public interface Handler {

    public void handle(Message message) throws Exception;
}
