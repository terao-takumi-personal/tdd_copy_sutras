import org.junit.After;
import org.junit.Test;

public class AuctionSniperEndToEndTest {
    private final FakeAuctionServer auction = new FakeAuctionServer("item-54321");
    private final ApplicationRunner application = new ApplicationRunner();

    @Test
    public void sniperJoinsAuctionUntilAuctionCloses() throws Exception {
        auction.startSellingItem();
        application.startBiddingIn(auction); // ステップ2 オークションに参加する
        auction.hasReceivedJoinRequestFrom(ApplicationRunner.SNIPER_XMPP_ID); // アプリケーションから参加を受け付ける
        auction.announceClosed(); // 終了をアプリケーション側に伝える
        application.showsSniperHasLostAuction(); // 参加者(=スナイパー)に落札失敗を伝える
    }

    @Test public void sniperMakesAHigherBidButLoses() throws Exception {
        auction.startSellingItem();
        application.startBiddingIn(auction);
        auction.hasReceivedJoinRequestFrom(ApplicationRunner.SNIPER_XMPP_ID);
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



