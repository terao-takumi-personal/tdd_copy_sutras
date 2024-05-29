import org.hamcrest.Matcher;
import org.jivesoftware.smack.*;
import org.jivesoftware.smack.packet.Message;

import static java.lang.String.format;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class FakeAuctionServer {
    public static final String ITEM_ID_AS_LOGIN = "auction-%s";
    public static final String AUCTION_RESOURCE = "Auction";
    public static final String XMPP_HOSTNAME = "localhost";
    public static final String AUCTION_PASSWORD = "auction";

    private final String itemId;
    private final XMPPConnection connection;
    private Chat currentChat;
    private final SingleMessageListener messageListener = new SingleMessageListener();


    public FakeAuctionServer(String itemId) {
        this.itemId = itemId; // オークションの出品者(のような)ユーザーとしてログインさせるために使う
        this.connection = new XMPPConnection(XMPP_HOSTNAME); // Smack(メッセンジャーライブラリ)のクライアントクラス
    }

    public void startSellingItem() throws XMPPException {
        // 接続開始
        connection.connect();
        // ログインする。ログインにはJIDとパスワードを利用する
        // JIDは「auction-item-12345」のようなIDとしている。
        connection.login(format(ITEM_ID_AS_LOGIN, itemId), AUCTION_PASSWORD, AUCTION_RESOURCE);
        // ChatListenerを登録している
        connection.getChatManager().addChatListener(
                // 匿名内部クラス。一度しか使わないクラスなのでここで宣言している。
                // ChatMessageListenerインターフェースの実装を行っている。
                new ChatManagerListener() {
                    public void chatCreated(Chat chat, boolean createdLocally) {
                        // 新しく作成されたチャットセッションを自身が参照できるようにする
                        currentChat = chat;
                        // 新しくチャットを受け取ると、自作のmessageListenerが呼び出されるようにしている
                        // 内部でメッセージの内容を格納したあと、nullでないことをアサートする
                        chat.addMessageListener(messageListener);
                    }
                }
        );
    }

    public String getItemId() {
        return itemId;
    }

    public void hasReceivedJoinRequestFrom(String sniperId) throws InterruptedException {
        receivesAMessageMatching(sniperId, equalTo(Main.JOIN_COMMAND_FORMAT));
    }

    public void reportPrice(int price, int increment, String bidder) throws XMPPException {
        currentChat.sendMessage(
                String.format("SOLVersion: 1.1; Event: PRICE; CurrentPrice: %d; "
                        + "Increment; %d; Bidder: %s;", price, increment, bidder)
        );
    }

    public void hasReceivedBid(int bid, String sniperId) throws InterruptedException {
        receivesAMessageMatching(sniperId, equalTo(format(Main.BID_COMMAND_FORMAT, bid)));
        assertThat(currentChat.getParticipant(), equalTo(sniperId));
    }

    public void announceClosed() throws XMPPException {
        currentChat.sendMessage(new Message());
    }

    public void stop() {
        connection.disconnect();
    }

    private void receivesAMessageMatching(String sniperId, Matcher<? super String> messageMatcher)
    throws InterruptedException {
        messageListener.receivesAMessage(messageMatcher);
        assertThat(currentChat.getParticipant(), equalTo(sniperId));
    }
}
