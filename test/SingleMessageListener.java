import static org.junit.Assert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import org.hamcrest.Matcher;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.packet.Message;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

// 受け取ったメッセージを1つだけ保存し、そのメッセージの内容をアサートするクラス
public class SingleMessageListener implements MessageListener {

    private final ArrayBlockingQueue<Message> messages = new ArrayBlockingQueue<Message>(1);

    public void processMessage(Chat chat, Message message) {
        messages.add(message);
    }

    @SuppressWarnings("unchecked")
    public void receivesAMessage(Matcher<? super String> messageMatcher) throws InterruptedException {
        final Message message = messages.poll(5, TimeUnit.SECONDS);
        assertThat("Message", message, is(notNullValue()));
        assertThat(message.getBody(), messageMatcher);
    }
}
