import org.junit.After;
import org.junit.Test;

public class AuctionSniperEndToEndTest {
    private final FakeAuctionServer auction = new FakeAuctionServer("item-54321");
    private final ApplicationRunner application = new ApplicationRunner();

    @Test
    public void sniperJoinsAuctionUntilAuctionCloses() throws Exception {
        // オークションを開始する。
        // 出品者的なユーザーとしてログインしておいて、チャットメッセージが送信されたときに内容をアサートできる準備をここで行っている
        // ここで準備するんかーという感じ。まあヘルパークラスなので、今はそんなに設計にこだわらなくてもいいと思う
        auction.startSellingItem();

        application.startBiddingIn(auction); // ステップ2 オークションに参加する
        auction.hasReceivedJoinRequestFromSniper(); // アプリケーションから参加を受け付ける
        auction.announceClosed(); // 終了をアプリケーション側に伝える
        application.showsSniperHasLostAuction(); // 参加者(=スナイパー)に落札失敗を伝える
    }

    @Test public void sniperMakesAHigherBidButLoses() throws Exception {
        auction.startSellingItem();
        application.startBiddingIn(auction);
        auction.hasReceivedJoinRequestFromSniper();
        auction.reportPrice(1000, 98, "other bidder");
        application.hasShownSniperIsBidding();
        auction.hasReceivedBid(1098, ApplicationRunner.SNIPER_XMPP_ID);
        auction.announceClosed();
        application.showsSniperHasLostAuction();
    }

    @After
    public void stopAuction() {
        auction.stop();
    }

    @After
    public void stopApplication() {
        application.stop();
    }
}



