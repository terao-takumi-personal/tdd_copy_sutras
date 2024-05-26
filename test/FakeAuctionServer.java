import org.jivesoftware.smack.*;
import org.jivesoftware.smack.packet.Message;

import static java.lang.String.format;

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

    public void hasReceivedJoinRequestFromSniper() throws InterruptedException {
        messageListener.receivesAMessage();
    }

    public void announceClosed() throws XMPPException {
        currentChat.sendMessage(new Message());
    }

    public void stop() {
        connection.disconnect();
    }
}
