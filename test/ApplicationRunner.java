// アプリケーション制御を行うためのテストヘルパークラス
public class ApplicationRunner {
    public static final String SNIPER_ID = "sniper";
    public static final String SNIPER_PASSWORD = "sniper";
    public static final String XMPP_HOST_NAME = "localhost";
    public static final String STATUS_JOINING = "STATUS_JOINING";
    public static final String STATUS_LOST = "STATUS_LOST";

    private AuctionSniperDriver driver;

    public void startBiddingIn(final FakeAuctionServer auction) {
        Thread thread = new Thread("Test Application") {
            @Override public void run() {
                try {
                    Main.main(XMPP_HOST_NAME, SNIPER_ID, SNIPER_PASSWORD, auction.getItemId());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        thread.setDaemon(true);
        thread.start();
        driver = new AuctionSniperDriver(1000);
        driver.showsSniperStatus(STATUS_JOINING);
    }
    public void showsSniperHasLostAuction() {
        driver.showsSniperStatus(STATUS_LOST);
    }
    public void stop() {
        if (driver != null) {
            driver.dispose();
        }
    }
}

