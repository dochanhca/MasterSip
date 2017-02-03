package jp.newbees.mastersip.adapter.chatholder;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import jp.newbees.mastersip.R;
import jp.newbees.mastersip.model.BaseChatItem;

/**
 * Created by thangit14 on 1/25/17.
 */

public class ViewHolderHeader extends BaseChatViewHolder {
    private TextView txtContent;

    public ViewHolderHeader(View root, Context context) {
        super(root, context);
    }


    @Override
    protected void initView(View root) {
        txtContent = (TextView) root.findViewById(R.id.txt_content);
    }

    @Override
    public void bindView(BaseChatItem baseChatItem) {
        txtContent.setText(baseChatItem.getDisplayDate());
    }
}