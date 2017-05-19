package jp.newbees.mastersip.footerdialog;

import java.util.LinkedList;

import jp.newbees.mastersip.R;
import jp.newbees.mastersip.event.FooterDialogEvent;
import jp.newbees.mastersip.model.UserItem;
import jp.newbees.mastersip.ui.CallActivity;
import jp.newbees.mastersip.utils.ConfigManager;
import jp.newbees.mastersip.utils.Constant;
import jp.newbees.mastersip.utils.Logger;

/**
 * Created by thangit14 on 5/17/17.
 */

public class FooterManager {
    public static final int SHOW_TIME = 3000;
    public static final int ANIM_TIME = 400;
    private LinkedList<FooterDialogEvent> deque;
    private static FooterManager mInstance;
    private CallActivity callActivity;
    private UserItem userInChatActivity;
    private boolean isExecutorRunning = false;

    public static FooterManager getInstance(CallActivity callActivity) {
        if (mInstance == null) {
            mInstance = new FooterManager(callActivity);
        }
        mInstance.callActivity = callActivity;
        return mInstance;
    }

    public static void changeActivity(CallActivity callActivity) {
        if (mInstance != null) {
            mInstance.callActivity = callActivity;
        }
    }

    /**
     * prevent use constructor to create instance
     *
     * @param callActivity
     */
    private FooterManager(CallActivity callActivity) {
        deque = new LinkedList<>();
        this.callActivity = callActivity;
    }

    public final void add(FooterDialogEvent footerDialogEvent) {
        synchronized (deque) {
            deque.add(footerDialogEvent);
            Logger.e("FooterManager", "after add, size =  " + deque.size());
            if (!isExecutorRunning) {
                Logger.e("FooterManager", "----- start Thread ------");
                startThreadToExecute();
            }
        }
    }

    private void startThreadToExecute() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                isExecutorRunning = true;
                android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
                runExecutor(poll());
                isExecutorRunning = false;
            }
        }).start();
    }

    private void runExecutor(FooterDialogEvent footerDialogEvent) {
        if (footerDialogEvent == null) {
            Logger.e("FooterManager", "----- end Thread ------");
            return;
        }
        if (checkCondition(footerDialogEvent)) {
            showFooterDialog(footerDialogEvent);
        }
        runExecutor(poll());
    }

    /**
     * @param footerDialogEvent
     * @return true if need to show footer dialog
     */
    private boolean checkCondition(FooterDialogEvent footerDialogEvent) {
        if (isInChatActivity()
                && (footerDialogEvent.getType() == Constant.FOOTER_DIALOG_TYPE.CHAT_TEXT
                    || footerDialogEvent.getType() == Constant.FOOTER_DIALOG_TYPE.SEND_GIFT)
                && userInChatActivity.getUserId().equalsIgnoreCase(footerDialogEvent.getCompetitor().getUserId())) {
            return false;
        }
        return true;
    }

    private void showFooterDialog(FooterDialogEvent footerDialogEvent) {
        try {
            Logger.e("FooterManager", "show Footer Dialog");

            genMessageFooterDialog(footerDialogEvent);
            performShowFooterDialog(footerDialogEvent);
            // wait for footer dialog hide to continue show other footer dialog
            Thread.sleep(SHOW_TIME + ANIM_TIME + ANIM_TIME);
            Logger.e("FooterManager", "end show, size of queue " + deque.size());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private final FooterDialogEvent poll() {
        synchronized (deque) {
            return deque.poll();
        }
    }

    private final boolean isEmpty() {
        synchronized (deque) {
            return deque.isEmpty();
        }
    }

    private void genMessageFooterDialog(FooterDialogEvent footerDialogEvent) {
        String message;
        int iconResourceId;

        switch (footerDialogEvent.getType()) {
            case Constant.FOOTER_DIALOG_TYPE.SEND_GIFT:
                iconResourceId = R.drawable.footer_dialog_gift;
                message = getMessageForGift(footerDialogEvent);
                break;
            case Constant.FOOTER_DIALOG_TYPE.FOOT_PRINT:
                iconResourceId = R.drawable.footer_dialog_foot_print;
                message = getMessageForFootPrint(footerDialogEvent);
                break;
            case Constant.FOOTER_DIALOG_TYPE.FOLLOW:
                iconResourceId = R.drawable.footer_dialog_follow;
                message = getMessageWithUserName(footerDialogEvent, R.string.footer_dialog_follow_message_for_male);
                break;
            case Constant.FOOTER_DIALOG_TYPE.CHAT_TEXT:
                iconResourceId = R.drawable.footer_dialog_message;
                message = getMessageWithUserName(footerDialogEvent, R.string.footer_dialog_follow_message_for_male);
                break;
            default:
                iconResourceId = R.drawable.footer_dialog_gift;
                message = "not yet handle this type";
                break;
        }
        footerDialogEvent.setMessage(message);
        footerDialogEvent.setIconResourceId(iconResourceId);
    }

    private String getMessageForFootPrint(FooterDialogEvent footerDialogEvent) {
        String message;
        if (ConfigManager.getInstance().getCurrentUser().isMale()) {
            message = getMessageWithUserName(footerDialogEvent, R.string.footer_dialog_foot_print_message_for_male);
        } else {
            message = getMessageWithUserName(footerDialogEvent, R.string.footer_dialog_foot_print_message_for_female);
        }
        return message;
    }

    private String getMessageForGift(FooterDialogEvent footerDialogEvent) {
        String message;
        if (ConfigManager.getInstance().getCurrentUser().isMale()) {
            message = getMessageWithUserName(footerDialogEvent, R.string.footer_dialog_gift_message_for_male);
        } else {
            message = String.format(callActivity.getString(R.string.footer_dialog_gift_message_for_female),
                    footerDialogEvent.getCompetitor().getUsername(),
                    footerDialogEvent.getGiftPoint());
        }
        return message;
    }

    private String getMessageWithUserName(FooterDialogEvent footerDialogEvent, int stringId) {
        return String.format(callActivity.getString(stringId),
                footerDialogEvent.getCompetitor().getUsername());
    }

    private void performShowFooterDialog(final FooterDialogEvent footerDialogEvent) {
        callActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                callActivity.showFooterDialog(footerDialogEvent);
            }
        });
    }

    private boolean isInChatActivity() {
        return userInChatActivity != null;
    }

    public void startChatActivity(UserItem competitor) {
        synchronized (deque) {
            this.userInChatActivity = competitor;
            removeFooterDialogs(competitor);
        }
    }

    /**
     * remove all competitor's footer dialog in chatting screen
     * @param competitor
     */
    private void removeFooterDialogs(UserItem competitor) {
        for (int i = 0; i < deque.size(); i++) {
            FooterDialogEvent event = deque.get(i);
            if (competitor.getUserId().equalsIgnoreCase(event.getCompetitor().getUserId())
                    && (event.getType() == Constant.FOOTER_DIALOG_TYPE.SEND_GIFT
                        || event.getType() == Constant.FOOTER_DIALOG_TYPE.CHAT_TEXT)) {
                deque.remove(event);
            }
        }
    }

    public void stopChatActivity() {
        this.userInChatActivity = null;
    }
}
