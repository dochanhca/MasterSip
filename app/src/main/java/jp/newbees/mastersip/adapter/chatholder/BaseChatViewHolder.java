package jp.newbees.mastersip.adapter.chatholder;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import jp.newbees.mastersip.R;
import jp.newbees.mastersip.model.BaseChatItem;

/**
 * Created by thangit14 on 1/25/17.
 */

public abstract class BaseChatViewHolder<T extends BaseChatItem> extends RecyclerView.ViewHolder {
    private Context context;

    public BaseChatViewHolder(View root, Context context) {
        super(root);
        this.context = context;
        initView(root);
    }

    protected abstract void initView(View root);

    public abstract void bindView(T baseChatItem);

    public Context getContext() {
        return context;
    }

    protected void loadGiftImage(String imageUrl, final ImageView imageView) {
        Glide.with(getContext()).load(imageUrl)
                .asBitmap()
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        int w = resource.getWidth();
                        int h = resource.getHeight();
                        setImageGiftSize(w, h);
                        imageView.setImageBitmap(resource);
                    }

                    private void setImageGiftSize(int w, int h) {
                        double radius = getContext().getResources().getDimensionPixelOffset(R.dimen.size_50dp);
                        double hypo = Math.sqrt(Math.pow(w, 2) + Math.pow(h, 2));
                        double p = radius / hypo;
                        w = (int) (w * p);
                        h = (int) (h * p);

                        ViewGroup.LayoutParams layoutParams = imageView.getLayoutParams();
                        layoutParams.height = h;
                        layoutParams.width = w;
                        imageView.setLayoutParams(layoutParams);
                    }
                });
    }

    protected String getCallType(int chatType) {
        String result = "";
        switch (chatType) {
            case BaseChatItem.ChatType.CHAT_VOICE_CALL:
                result = getContext().getString(R.string.voice_call);
                break;
            case BaseChatItem.ChatType.CHAT_VIDEO_CALL:
                result = getContext().getString(R.string.video_call);
                break;
            case BaseChatItem.ChatType.CHAT_VIDEO_CHAT_CALL:
                result = getContext().getString(R.string.video_chat_call);
                break;
            default:
                break;
        }
        return result;
    }
}