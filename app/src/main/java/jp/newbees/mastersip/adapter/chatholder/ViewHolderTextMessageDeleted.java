package jp.newbees.mastersip.adapter.chatholder;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import jp.newbees.mastersip.R;
import jp.newbees.mastersip.model.BaseChatItem;

/**
 * Created by vynv on 5/16/17.
 */

public class ViewHolderTextMessageDeleted extends BaseChatViewHolder {
    private TextView txtContent;
    private TextView txtTime;
    private TextView txtState;
    public ViewHolderTextMessageDeleted(View root, Context context) {
        super(root, context);
    }

    @Override
    protected void initView(View root) {
        txtContent = (TextView) root.findViewById(R.id.txt_content);
        txtTime = (TextView) root.findViewById(R.id.txt_time);
        txtState = (TextView) root.findViewById(R.id.txt_state);
    }

    @Override
    public void bindView(BaseChatItem baseChatItem) {
        txtTime.setText(baseChatItem.getShortDate());
        txtContent.setText(getContext().getString(R.string.message_sender_not_good));
        txtState.setVisibility(
                baseChatItem.getMessageState() == BaseChatItem.MessageState.STT_READ ?
                        View.VISIBLE : View.GONE);
    }
}
