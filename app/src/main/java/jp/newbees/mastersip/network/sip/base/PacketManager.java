package jp.newbees.mastersip.network.sip.base;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import org.json.JSONException;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import jp.newbees.mastersip.model.PacketItem;
import jp.newbees.mastersip.network.sip.AdminHangUpProcessor;
import jp.newbees.mastersip.network.sip.BusyCallProcessor;
import jp.newbees.mastersip.network.sip.CancelCallProcessor;
import jp.newbees.mastersip.network.sip.CompetitorChangeBackgroundStateProcessor;
import jp.newbees.mastersip.network.sip.ChangeCallingStatusProcessor;
import jp.newbees.mastersip.network.sip.ChattingProcessor;
import jp.newbees.mastersip.network.sip.CoinChangedProcessor;
import jp.newbees.mastersip.network.sip.HangUpForGirlProcessor;
import jp.newbees.mastersip.network.sip.PauseCallProcessor;
import jp.newbees.mastersip.network.sip.ReceivingReadMessageProcessor;
import jp.newbees.mastersip.network.sip.RunOutOfCoinProcessor;
import jp.newbees.mastersip.utils.Constant;
import jp.newbees.mastersip.utils.JSONUtils;
import jp.newbees.mastersip.utils.Logger;

/**
 * Created by vietbq on 1/6/17.
 */

public class PacketManager {
    private static PacketManager instance;
    private static final int NUMBER_OF_CORES =
            Runtime.getRuntime().availableProcessors();
    private static final int KEEP_ALIVE_TIME = 3;
    // Sets the Time Unit to seconds
    private static final TimeUnit KEEP_ALIVE_TIME_UNIT = TimeUnit.SECONDS;
    private final ThreadPoolExecutor processorThreadPool;
    private Handler handler;
    private final String TAG = getClass().getSimpleName();

    private PacketManager() {
        //Prevent init object
        BlockingQueue<Runnable> processorQueue = new LinkedBlockingDeque<>();

        // Creates a thread pool manager
        processorThreadPool = new ThreadPoolExecutor(
                NUMBER_OF_CORES,       // Initial pool size
                NUMBER_OF_CORES,       // Max pool size
                KEEP_ALIVE_TIME,
                KEEP_ALIVE_TIME_UNIT,
                processorQueue);

        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                BaseSocketProcessor processor = (BaseSocketProcessor) msg.obj;
                processor.didProcess(processor.getResult());
            }
        };
    }

    private BaseSocketProcessor getProcessor(PacketItem data) {
        String action = data.getAction();
        BaseSocketProcessor processor = null;
        switch (action) {
            case Constant.SOCKET.ACTION_CHATTING:
                processor = new ChattingProcessor();
                break;
            case Constant.SOCKET.ACTION_CHANGE_MESSAGE_STATE:
                processor = new ReceivingReadMessageProcessor();
                break;
            case Constant.SOCKET.ACTION_COIN_CHANGED:
                processor = new CoinChangedProcessor();
                break;
            case Constant.SOCKET.ACTION_CHANGE_CALLING_STATUS:
                processor = new ChangeCallingStatusProcessor();
                break;
            case Constant.SOCKET.ACTION_CANCEL_CALL:
                processor = new CancelCallProcessor();
                break;
            case Constant.SOCKET.ACTION_BUSY_CALL:
                processor = new BusyCallProcessor();
                break;
            case Constant.SOCKET.ACTION_HANG_UP_FOR_GIRL_BLOCK_ZERO:
                processor = new HangUpForGirlProcessor();
                break;
            case Constant.SOCKET.ACTION_RUN_OUT_OF_COINS:
            case Constant.SOCKET.ACTION_ABOUT_RUN_OUT_OF_COINS:
                processor = new RunOutOfCoinProcessor();
                break;
            case Constant.SOCKET.ACTION_ADMIN_HANG_UP:
                processor = new AdminHangUpProcessor();
                break;
            case Constant.SOCKET.ACTION_GSM_CALL_STATE:
                processor = new PauseCallProcessor();
                break;
            case Constant.SOCKET.ACTION_ENTER_FOREGROUND:
            case Constant.SOCKET.ACTION_ENTER_BACKGROUND:
                processor = new CompetitorChangeBackgroundStateProcessor();
                break;
            default:
                break;
        }
        if (processor != null) {
            processor.setHandler(handler);
            processor.setPacketItem(data);
        }

        return processor;
    }

    private void executeProcessor(BaseSocketProcessor processor) {
        processorThreadPool.execute(processor);
    }

    public static PacketManager getInstance() {
        if (instance == null) {
            instance = new PacketManager();
        }
        return instance;
    }

    public final void processData(String raw) {
        try {
            Logger.e(TAG, raw);
            PacketItem packetItem = JSONUtils.parsePacketItem(raw);
            BaseSocketProcessor processor = getProcessor(packetItem);
            if (processor != null) {
                this.executeProcessor(processor);
            } else {
                Logger.e(TAG, "No processor handle ACTION " + packetItem.getAction());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
