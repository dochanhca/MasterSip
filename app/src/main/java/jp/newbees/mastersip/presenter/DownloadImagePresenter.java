package jp.newbees.mastersip.presenter;

import android.content.Context;

import jp.newbees.mastersip.network.api.BaseTask;
import jp.newbees.mastersip.network.api.DownloadImageTask;

/**
 * Created by ducpv on 5/23/17.
 */

public class DownloadImagePresenter extends BasePresenter {

    private DownloadImageView downloadImageView;

    public interface DownloadImageView {
        void didDownloadImage();

        void didDownloadImageError(int errorCode, String errorMessage);
    }

    public DownloadImagePresenter(Context context, DownloadImageView downloadImageView) {
        super(context);
        this.downloadImageView = downloadImageView;
    }

    @Override
    protected void didResponseTask(BaseTask task) {
        if (task instanceof DownloadImageTask) {
            downloadImageView.didDownloadImage();
        }
    }

    @Override
    protected void didErrorRequestTask(BaseTask task, int errorCode, String errorMessage) {
        if (task instanceof DownloadImageTask) {
            downloadImageView.didDownloadImageError(errorCode, errorMessage);
        }
    }

    public void downloadImage(int imageId, int type) {
        DownloadImageTask downloadImageTask = new DownloadImageTask(getContext(), imageId, type);
        requestToServer(downloadImageTask);
    }
}
