package jp.newbees.mastersip.customviews;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import de.hdodenhof.circleimageview.CircleImageView;
import jp.newbees.mastersip.R;

/**
 * Created by thangit14 on 1/6/17.
 */

public class ChatHistory extends LinearLayout {
    private LayoutInflater inflater;

    public ChatHistory(Context context) {
        super(context);
        init();
    }

    public ChatHistory(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ChatHistory(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    private void init() {
        inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void addMyChat(String text) {
        View view = inflater.inflate(R.layout.my_chat_text_item,this,false);
        TextView textView = (TextView) view.findViewById(R.id.txt_content);
        textView.setText(text);
        this.addView(view);
    }

    public void addReplyChat(String text, String urlAvatar) {
        View view = inflater.inflate(R.layout.reply_chat_text_item,this,false);
        TextView textView = (TextView) view.findViewById(R.id.txt_content);
        textView.setText(text);
        CircleImageView imgAvatar = (CircleImageView) view.findViewById(R.id.img_reply_avatar);
        Glide.with(getContext()).load(urlAvatar).into(imgAvatar);
        this.addView(view);
    }
}
