package jp.newbees.mastersip.adapter.chatholder;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import jp.newbees.mastersip.R;
import jp.newbees.mastersip.model.BaseChatItem;
import jp.newbees.mastersip.model.TextChatItem;

/**
 * Created by thangit14 on 1/25/17.
 */

public class ViewHolderTextMessage extends BaseChatViewHolder {
    private TextView txtContent;
    private TextView txtTime;
    private TextView txtState;

    public ViewHolderTextMessage(View root, Context context) {
        super(root, context);
    }

    @Override
    protected void initView(View root) {
        txtContent = (TextView) root.findViewById(R.id.txt_content);
        txtTime = (TextView) root.findViewById(R.id.txt_time);
        txtState = (TextView) root.findViewById(R.id.txt_state);
    }

    @Override
    public void bindView(BaseChatItem item) {
        TextChatItem textChatItem = (TextChatItem) item;
        txtTime.setText(textChatItem.getShortDate());
        txtContent.setText(textChatItem.getMessage());
        txtState.setVisibility(
                textChatItem.getMessageState() == BaseChatItem.MessageState.STT_READ ?
                        View.VISIBLE : View.GONE);
    }

}
