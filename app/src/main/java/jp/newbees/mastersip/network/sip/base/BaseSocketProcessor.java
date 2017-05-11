package jp.newbees.mastersip.network.sip.base;

import android.os.Handler;
import android.os.Message;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;

import jp.newbees.mastersip.model.PacketItem;
import jp.newbees.mastersip.utils.Logger;

/**
 * Created by vietbq on 1/6/17.
 */

public abstract class BaseSocketProcessor<T extends Object> implements Runnable {

    private static final int STATE_SUCCESS = 1;
    private Handler handler;
    private PacketItem packetItem;
    private T result;
    protected String TAG = getClass().getSimpleName();

    void setHandler(Handler handler) {
        this.handler = handler;
    }

    /**
     * @param packetItem
     */
    void setPacketItem(PacketItem packetItem) {
        this.packetItem = packetItem;
    }

    @Override
    public void run() {
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
        try {
            Logger.e(TAG, "Received : " + packetItem.getData());
            this.result = doInBackgroundData(packetItem);
            Message completeMessage =
                    handler.obtainMessage(STATE_SUCCESS, BaseSocketProcessor.this);
            completeMessage.sendToTarget();
        } catch (JSONException e) {
            e.printStackTrace();
            Logger.e(TAG, "Parse error JSON action " + packetItem.getAction());
        }
    }

    /**
     * Callback when processed data
     * This method run on UIThread
     *
     * @param data
     */
    protected abstract void didProcess(T data);

    /**
     * Process Data on Background Thread to prevent bottle-neck
     *
     * @param packetItem
     */
    protected abstract T doInBackgroundData(PacketItem packetItem) throws JSONException;

    public T getResult() {
        return result;
    }

    protected final void postEvent(final T result) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                EventBus.getDefault().post(result);
            }
        }, 200);
    }
}
