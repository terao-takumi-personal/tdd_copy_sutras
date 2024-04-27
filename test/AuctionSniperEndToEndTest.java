import org.junit.After;
import org.junit.Test;

public class AuctionSniperEndToEndTest {
    private final FakeAuctionServer auction = new FakeAuctionServer("item-54321");
    private final ApplicationRunner application = new ApplicationRunner();

    @Test
    public void sniperJoinsAuctionUntilAuctionCloses() throws Exception {
        auction.startSellingItem(); // オークションを開始する。
        application.startBiddingIn(auction); // ステップ2 オークションに参加する
        auction.hasReceivedJoinRequestFromSniper(); // アプリケーションから参加を受け付ける
        auction.announceClosed(); // 終了をアプリケーション側に伝える
        application.showsSniperHasLostAuction(); // 参加者(=スナイパー)に落札失敗を伝える
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



