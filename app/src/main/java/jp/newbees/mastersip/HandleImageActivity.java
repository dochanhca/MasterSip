package jp.newbees.mastersip;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import java.util.ArrayList;
import java.util.List;

import jp.newbees.mastersip.model.SelectionItem;
import jp.newbees.mastersip.model.UserItem;
import jp.newbees.mastersip.presenter.DownloadAndReportPresenter;
import jp.newbees.mastersip.ui.BaseActivity;
import jp.newbees.mastersip.ui.dialog.SelectionDialog;
import jp.newbees.mastersip.ui.dialog.TextDialog;
import jp.newbees.mastersip.ui.payment.PaymentActivity;
import jp.newbees.mastersip.ui.payment.PaymentFragment;
import jp.newbees.mastersip.utils.ConfigManager;
import jp.newbees.mastersip.utils.FileUtils;

/**
 * Created by ducpv on 5/26/17.
 */

public abstract class HandleImageActivity extends BaseActivity implements
        DownloadAndReportPresenter.DownloadImageView, SelectionDialog.OnSelectionDialogClick,
        TextDialog.OnTextDialogPositiveClick {

    private static final int REQUEST_DOWNLOAD_IMAGE = 31;
    private static final int REQUEST_BUY_POINT = 15;

    protected DownloadAndReportPresenter downloadAndReportPresenter;

    private List<SelectionItem> reportReasons;

    protected abstract int getImageId();

    protected abstract int getReportImageType();

    protected abstract int getDownloadImageType();

    protected abstract int getMinPoint();

    protected abstract String getImagePath();

    protected abstract String getUserId();

    @Override
    protected void initViews(Bundle savedInstanceState) {
        downloadAndReportPresenter = new DownloadAndReportPresenter(this, this);
    }

    @Override
    public void didRequestDownloadImage() {
        handleDownloadImage(getImagePath());
    }

    @Override
    public void didRequestDownloadImageError(int errorCode, String errorMessage) {
        disMissLoading();
        showToastExceptionVolleyError(this, errorCode, errorMessage);
    }

    @Override
    public void didGetListReportReason(List<SelectionItem> reportReasons) {
        this.reportReasons = reportReasons;
        disMissLoading();
        SelectionDialog.openSelectionDialogFromActivity(getSupportFragmentManager(),
                (ArrayList<SelectionItem>) reportReasons
                , getString(R.string.report_user), getString(R.string.send), reportReasons.get(0));
    }

    @Override
    public void didGetListReportReasonError(int errorCode, String errorMessage) {
        disMissLoading();
        showToastExceptionVolleyError(this, errorCode, errorMessage);
    }

    @Override
    public void didReportUser() {
        disMissLoading();
        showMessageDialog(getString(R.string.reported_user),
                getString(R.string.mess_report_user_sucess), "", false);
    }

    @Override
    public void didReportUserError(int errorCode, String errorMessage) {
        disMissLoading();
        showToastExceptionVolleyError(this, errorCode, errorMessage);
    }

    @Override
    public void onItemSelected(int position) {
        showLoading();
        downloadAndReportPresenter.reportUser(getUserId(), reportReasons.get(position).getId(),
                getReportImageType(), getImagePath());
    }

    @Override
    public void onTextDialogOkClick(int requestCode) {
        if (requestCode == REQUEST_DOWNLOAD_IMAGE) {
            requestDownloadImage();
        } else if (requestCode == REQUEST_BUY_POINT) {
            PaymentActivity.startActivityForResult(this, REQUEST_BUY_POINT);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_BUY_POINT && resultCode == RESULT_OK) {
            showDialogBuyPointSuccess(data);
        }
    }

    private void showDialogBuyPointSuccess(Intent data) {
        StringBuilder message = new StringBuilder();
        message.append(getString(R.string.settlement_is_completed))
                .append("\n")
                .append(data.getStringExtra(PaymentFragment.POINT))
                .append(getString(R.string.pt))
                .append(getString(R.string.have_been_granted));
        showMessageDialog(message.toString());
    }

    private void requestDownloadImage() {
        UserItem currentUser = ConfigManager.getInstance().getCurrentUser();
        int minPoint = ConfigManager.getInstance().getMinPointDownImageChat();
        if (minPoint > currentUser.getCoin() && currentUser.isMale()) {
            showDialogMissingPoint();
        } else {
            showLoading();
            downloadAndReportPresenter.requestDownloadImage(getImageId(), getDownloadImageType());
        }
    }

    protected void showConfirmDownloadImageDialog() {
        UserItem currentUser = ConfigManager.getInstance().getCurrentUser();

        String title = getString(R.string.save_image);
        String content = currentUser.isMale()
                ? String.format(getString(R.string.mess_confirm_down_image_for_male), getMinPoint())
                : getString(R.string.mess_confirm_down_image_for_female);

        TextDialog textDialog = new TextDialog.Builder()
                .setTitle(title)
                .setRequestCode(REQUEST_DOWNLOAD_IMAGE)
                .build(content);
        textDialog.show(getSupportFragmentManager(), TextDialog.class.getSimpleName());
    }

    protected void showDialogMissingPoint() {
        // Missing point when download image

        String title = getString(R.string.mess_missing_point);
        String content = String.format(getString(R.string.mess_request_buy_point_when_down_image),
                getMinPoint());
        String positiveTitle = getString(R.string.add_point);
        TextDialog textDialog = new TextDialog.Builder()
                .setRequestCode(REQUEST_BUY_POINT)
                .setPositiveTitle(positiveTitle)
                .setTitle(title)
                .build(content);
        textDialog.show(getSupportFragmentManager(), TextDialog.class.getSimpleName());
    }

    private void handleDownloadImage(final String imageUrl) {
        final Handler handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                FileUtils.downloadImageFromUrl(HandleImageActivity.this, imageUrl);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        disMissLoading();
                        // Show dialog download image success
                        showMessageDialog(getString(R.string.mess_down_image_success));
                    }
                });
            }
        }).start();
    }
}
