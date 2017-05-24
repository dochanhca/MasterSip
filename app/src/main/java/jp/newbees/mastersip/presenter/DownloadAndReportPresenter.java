package jp.newbees.mastersip.presenter;

import android.content.Context;

import java.util.List;

import jp.newbees.mastersip.model.SelectionItem;
import jp.newbees.mastersip.network.api.BaseTask;
import jp.newbees.mastersip.network.api.DownloadImageTask;
import jp.newbees.mastersip.network.api.GetListReportReasonTask;
import jp.newbees.mastersip.network.api.ReportTask;

/**
 * Created by ducpv on 5/23/17.
 */

public class DownloadAndReportPresenter extends BasePresenter {

    private DownloadImageView downloadImageView;

    public interface DownloadImageView {
        void didDownloadImage();

        void didDownloadImageError(int errorCode, String errorMessage);

        void didGetListReportReason(List<SelectionItem> reportReasons);

        void didGetListReportReasonError(int errorCode, String errorMessage);

        void didReportUser();

        void didReportUserError(int errorCode, String errorMessage);
    }

    public DownloadAndReportPresenter(Context context, DownloadImageView downloadImageView) {
        super(context);
        this.downloadImageView = downloadImageView;
    }

    @Override
    protected void didResponseTask(BaseTask task) {
        if (task instanceof DownloadImageTask) {
            downloadImageView.didDownloadImage();
        } else if (task instanceof GetListReportReasonTask) {
            downloadImageView.didGetListReportReason(((GetListReportReasonTask) task).getDataResponse());
        } else if (task instanceof ReportTask) {
            downloadImageView.didReportUser();
        }
    }

    @Override
    protected void didErrorRequestTask(BaseTask task, int errorCode, String errorMessage) {
        if (task instanceof DownloadImageTask) {
            downloadImageView.didDownloadImageError(errorCode, errorMessage);
        } else if (task instanceof GetListReportReasonTask) {
            downloadImageView.didGetListReportReasonError(errorCode, errorMessage);
        } else if (task instanceof ReportTask) {
            downloadImageView.didReportUserError(errorCode, errorMessage);
        }
    }

    public void downloadImage(int imageId, int type) {
        DownloadImageTask downloadImageTask = new DownloadImageTask(getContext(), imageId, type);
        requestToServer(downloadImageTask);
    }

    public void getListReportReason(int type) {
        GetListReportReasonTask getListReportReasonTask = new GetListReportReasonTask(getContext(),
                type);
        requestToServer(getListReportReasonTask);
    }

    public void reportUser(String userId, int reportDetailId, int type, String imagePath) {
        ReportTask reportTask = new ReportTask(getContext(), userId, type,
                reportDetailId, imagePath);
        requestToServer(reportTask);
    }
}
