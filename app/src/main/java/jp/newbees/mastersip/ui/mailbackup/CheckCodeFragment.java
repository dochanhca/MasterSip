package jp.newbees.mastersip.ui.mailbackup;

import android.os.Bundle;
import android.view.View;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.newbees.mastersip.R;
import jp.newbees.mastersip.customviews.HiraginoEditText;
import jp.newbees.mastersip.presenter.mailbackup.CheckCodePresenter;
import jp.newbees.mastersip.ui.BaseFragment;
import jp.newbees.mastersip.ui.dialog.TextDialog;
import jp.newbees.mastersip.utils.Constant;

/**
 * Created by thangit14 on 2/14/17.
 */
public class CheckCodeFragment extends BaseFragment implements CheckCodePresenter.CheckCodeListener, TextDialog.OnTextDialogClick {

    @BindView(R.id.edt_code)
    HiraginoEditText edtCode;

    private static final int SUCCESS_DIALOG = 0;

    private static final String CALL_FROM = "CALL_FROM";
    private CheckCodePresenter checkCodePresenter;
    private CallFrom callFrom;

    @Override
    protected int layoutId() {
        return R.layout.fragment_check_code;
    }

    @Override
    protected void init(View mRoot, Bundle savedInstanceState) {
        setFragmentTitle(getResources().getString(R.string.title_check_code_fragment));
        ButterKnife.bind(this, mRoot);
        callFrom = (CallFrom) getArguments().get(CALL_FROM);
        checkCodePresenter = new CheckCodePresenter(getContext(), this);
    }

    @Override
    public void onCheckCodeSuccessful() {
        disMissLoading();
        showDialogSuccessful();
    }

    @Override
    public void onCheckCodeError(int errorCode, String errorMessage) {
        disMissLoading();
        if (errorCode == Constant.Error.RESET_CODE_IS_NOT_MATCH) {
            showMessageDialog(getResources().getString(R.string.content_different_code));
        } else {
            showToastExceptionVolleyError(errorCode, errorMessage);
        }
    }

    @OnClick(R.id.btn_send)
    public void onClick() {
        showLoading();
        checkCodePresenter.checkCode(edtCode.getText().toString());
    }

    @Override
    public void onTextDialogOkClick(int requestCode) {
        if (requestCode == SUCCESS_DIALOG) {
            if (callFrom == CallFrom.NEW_BACKUP_EMAIL) {

            } else {
                getActivity().onBackPressed();
            }
        }
    }

    private void showDialogSuccessful() {
        String title;
        String content;
        switch (callFrom) {
            case NEW_BACKUP_EMAIL:
                title = getResources().getString(R.string.title_code_success_register_new_mail_backup);
                content = getResources().getString(R.string.content_code_success_register_new_mail_backup);
                break;
            case CHANGE_BACKUP_EMAIL:
                title = getResources().getString(R.string.title_code_success_register_change_mail_backup);
                content = getResources().getString(R.string.content_code_success_register_change_mail_backup);
                break;
            default:
                title = "";
                content = "";
                break;
        }
        TextDialog.openTextDialog(this, SUCCESS_DIALOG, getFragmentManager(), content, title, true);
    }

    public static CheckCodeFragment newInstance(CallFrom callFrom) {
        Bundle args = new Bundle();
        args.putSerializable(CALL_FROM, callFrom);
        CheckCodeFragment fragment = new CheckCodeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public enum CallFrom {
        NEW_BACKUP_EMAIL,CHANGE_BACKUP_EMAIL
    }
}
