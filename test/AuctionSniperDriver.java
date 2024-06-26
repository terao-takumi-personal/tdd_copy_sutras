import auctionsniper.ui.MainWindow;
import com.objogate.wl.swing.driver.JFrameDriver; // railsで言うcapybaraみたいなもんか
import com.objogate.wl.swing.AWTEventQueueProber;
import com.objogate.wl.swing.driver.JLabelDriver;
import com.objogate.wl.swing.gesture.GesturePerformer;

import static org.hamcrest.CoreMatchers.equalTo;

// アプリケーションのGUI操作を制御し、GUIの表示アサートを行うクラス
public class AuctionSniperDriver extends JFrameDriver {
    public AuctionSniperDriver(int timeoutMillis) {
        super(
                new GesturePerformer(),
                JFrameDriver.topLevelFrame(
                        named(MainWindow.MAIN_WINDOW_NAME),
                        showingOnScreen()
                ),
                new AWTEventQueueProber(timeoutMillis, 100)
        );
    }

    public void showsSniperStatus(String statusText) {
        new JLabelDriver(
                this, named(MainWindow.SNIPER_STATUS_NAME)
        ).hasText(equalTo(statusText));
    }
}
