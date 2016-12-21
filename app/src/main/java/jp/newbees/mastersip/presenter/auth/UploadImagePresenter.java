package jp.newbees.mastersip.presenter.auth;

import android.content.Context;

import java.io.InputStream;

import jp.newbees.mastersip.model.ImageItem;
import jp.newbees.mastersip.model.UserItem;
import jp.newbees.mastersip.network.api.BaseUploadTask;
import jp.newbees.mastersip.network.api.UploadImageTask;
import jp.newbees.mastersip.presenter.BaseUploadPresenter;

/**
 * Created by ducpv on 12/21/16.
 */

public class UploadImagePresenter extends BaseUploadPresenter {

    @Override
    protected void didResponseTask(BaseUploadTask task) {
        if (task instanceof UploadImageTask) {
            ImageItem imageItem = ((UploadImageTask) task).getDataResponse();
            view.onUploadImageSuccess(imageItem);
        }
    }

    @Override
    protected void didErrorRequestTask(BaseUploadTask task, int errorCode, String errorMessage) {
        view.onUploadImageFailure(errorCode, errorMessage);
    }

    public interface View {

        void onUploadImageSuccess(ImageItem imageItem);

        void onUploadImageFailure(int errorCode, String errorMessage);
    }

    private final Context context;
    private final View view;

    public UploadImagePresenter(Context context, View view) {
        this.context = context;
        this.view = view;
    }

    public final void upLoadImage(String userId, int typeUpload, InputStream inputStream) {
        UploadImageTask uploadImageTask = new UploadImageTask(context, userId,
                typeUpload, inputStream);
        requestToServer(uploadImageTask);
    }
}
