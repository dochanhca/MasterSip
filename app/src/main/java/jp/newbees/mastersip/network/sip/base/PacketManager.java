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
import jp.newbees.mastersip.network.UpdateMessageStateProcesser;
import jp.newbees.mastersip.network.sip.ChattingProcessor;
import jp.newbees.mastersip.network.sip.CoinChangedProcessor;
import jp.newbees.mastersip.utils.Constant;
import jp.newbees.mastersip.utils.JSONUtils;
import jp.newbees.mastersip.utils.Logger;

/**
 * Created by vietbq on 1/6/17.
 */

public class PacketManager {
    private static PacketManager instance;
    private final BlockingQueue<Runnable> processorQueue;
    private static int NUMBER_OF_CORES =
            Runtime.getRuntime().availableProcessors();
    private static final int KEEP_ALIVE_TIME = 3;
    // Sets the Time Unit to seconds
    private static final TimeUnit KEEP_ALIVE_TIME_UNIT = TimeUnit.SECONDS;
    private final ThreadPoolExecutor processorThreadPool;
    private Handler handler;
    private String TAG;

    private PacketManager(){
        TAG = this.getClass().getSimpleName();
        //Prevent init object
        processorQueue = new LinkedBlockingDeque<>();

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

    public static void initInstance(){
        instance = new PacketManager();
    }

    public final BaseSocketProcessor getProcessor(PacketItem data) {
        String action = data.getAction();
        BaseSocketProcessor processor = null;
        switch (action) {
            case Constant.SOCKET.ACTION_CHATTING:
                processor = new ChattingProcessor();
                break;
            case Constant.SOCKET.ACTION_CHANGE_MESSAGE_STATE:
                processor = new UpdateMessageStateProcesser();
                break;
        }
        if (processor != null) {
            processor.setHandler(handler);
            processor.setPacketItem(data);
        } else if (action.equalsIgnoreCase(Constant.SOCKET.ACTION_COIN_CHANGED)) {
            processor = new CoinChangedProcessor();
            processor.setHandler(handler);
            processor.setPacketItem(data);
        }
        return processor;
    }

    private void executeProcessor(BaseSocketProcessor processor){
        processorThreadPool.execute(processor);
    }

    public static PacketManager getInstance() {
        if (instance == null){
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
            }else {
                Logger.e(TAG,"No processor handle ACTION " + packetItem.getAction());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
