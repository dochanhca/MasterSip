package jp.newbees.mastersip.ui.gift;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.newbees.mastersip.R;
import jp.newbees.mastersip.adapter.AdapterGiftsList;
import jp.newbees.mastersip.customviews.HiraginoTextView;
import jp.newbees.mastersip.model.GiftChatItem;
import jp.newbees.mastersip.model.GiftItem;
import jp.newbees.mastersip.model.UserItem;
import jp.newbees.mastersip.presenter.gift.GiftListPresenter;
import jp.newbees.mastersip.ui.BaseActivity;
import jp.newbees.mastersip.ui.BaseFragment;
import jp.newbees.mastersip.ui.chatting.ChatActivity;
import jp.newbees.mastersip.ui.dialog.TextDialog;
import jp.newbees.mastersip.ui.payment.PaymentActivity;
import jp.newbees.mastersip.ui.payment.PaymentFragment;
import jp.newbees.mastersip.utils.ConfigManager;

/**
 * Created by vietbq on 12/6/16.
 */

public class ListGiftFragment extends BaseFragment implements GiftListPresenter.GiftListView,
        AdapterGiftsList.OnGiftItemSelectListener,
        TextDialog.OnTextDialogPositiveClick {

    public static final String USER_ITEM = "USER_ITEM";
    private static final String OPEN_FROM = "OPEN FROM";
    private static final int REQUEST_SEND_GIFT = 1;
    private static final int REQUEST_BUY_POINT = 2;
    private static final int REQUEST_NOTIFY_SEND_GIFT_SUCESS = 3;
    public static final int OPEN_FROM_PROFILE_DETAILS = 1;
    public static final int OPEN_FROM_CHAT = 2;


    @BindView(R.id.rcv_gifts_list)
    RecyclerView rcvGiftsList;
    @BindView(R.id.txt_gifts_description)
    HiraginoTextView txtGiftsDescription;
    private AdapterGiftsList adapterGiftsList;
    private GiftListPresenter giftListPresenter;
    private UserItem userItem;
    private GiftItem currentGiftSelected;
    private int openFrom;

    public static final BaseFragment newInstance(UserItem userItem, int openFrom, boolean showFragmentActionBar) {
        BaseFragment fragment = new ListGiftFragment();
        Bundle argument = new Bundle();
        argument.putParcelable(ListGiftFragment.USER_ITEM, userItem);
        argument.putInt(ListGiftFragment.OPEN_FROM, openFrom);
        argument.putBoolean(SHOW_FRAGMENT_ACTION_BAR, showFragmentActionBar);
        fragment.setArguments(argument);
        return fragment;
    }

    @Override
    protected int layoutId() {
        return R.layout.fragment_gift;
    }

    @Override
    protected void init(View mRoot, Bundle savedInstanceState) {
        ButterKnife.bind(this, mRoot);

        String titleGiftsList = getString(R.string.gifts_list);
        if (getArguments().getBoolean(SHOW_FRAGMENT_ACTION_BAR)) {
            setFragmentTitle(titleGiftsList);
        } else {
            hideFragmentActionBar();
            ((BaseActivity) getActivity()).changeHeaderText(titleGiftsList);
        }

        userItem = getArguments().getParcelable(ListGiftFragment.USER_ITEM);
        openFrom = getArguments().getInt(ListGiftFragment.OPEN_FROM);
        txtGiftsDescription.setText(getDescription());
        adapterGiftsList = new AdapterGiftsList(getActivity().getApplicationContext(), new ArrayList<GiftItem>());
        rcvGiftsList.setAdapter(adapterGiftsList);
        adapterGiftsList.setGiftItemSelectListener(this);
        giftListPresenter = new GiftListPresenter(getContext(), this);
        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(getContext(), R.dimen._5dp);
        rcvGiftsList.addItemDecoration(itemDecoration);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setRetainInstance(true);
        giftListPresenter.loadGiftList();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_BUY_POINT && resultCode == Activity.RESULT_OK) {
            showDialogBuyPointSuccess(data);
        }
    }

    @Override
    public void didLoadGiftsList(List<GiftItem> giftItems) {
        adapterGiftsList.addAll(giftItems);
        adapterGiftsList.notifyDataSetChanged();
    }

    @Override
    public void didLoadGiftsListFailure(int errorCode, String errorMessage) {
        showToastExceptionVolleyError(errorCode, errorMessage);
    }

    @Override
    public void didSendGiftSuccess(GiftChatItem giftChatItem) {
        if (openFrom == OPEN_FROM_PROFILE_DETAILS) {
            showDialogSendGiftSuccess();
        } else if (openFrom == OPEN_FROM_CHAT) {
            openChatScreen();
        }
    }

    @Override
    public void didSendGiftFailure(int errorCode, String errorMessage) {
        showToastExceptionVolleyError(errorCode, errorMessage);
    }

    @Override
    public void didSendGiftErrorCauseNotEnoughPoint() {
        UserItem currentUser = ConfigManager.getInstance().getCurrentUser();
        if (currentUser.getGender() == UserItem.FEMALE) {
            showDialogNotEnoughPointForFemale(currentUser);
        } else {
            showDialogNotEnoughPointForMale(currentUser);
        }
    }

    @Override
    public void onGiftItemSelect(GiftItem giftItem) {
        this.currentGiftSelected = giftItem;
        String title = getString(R.string.title_send_gift);
        String content = String.format(getString(R.string.confirm_send_gift), userItem.getUsername(), giftItem.getName());
        TextDialog textDialog = new TextDialog.Builder()
                .setTitle(title)
                .build(this, content, REQUEST_SEND_GIFT);
        textDialog.show(getFragmentManager(), TextDialog.class.getSimpleName());
    }

    @Override
    public void onTextDialogOkClick(int requestCode) {
        if (requestCode == REQUEST_SEND_GIFT) {
            giftListPresenter.sendGiftToUser(userItem, currentGiftSelected);
        } else if (requestCode == REQUEST_BUY_POINT) {
            PaymentActivity.startActivityForResult(this, REQUEST_BUY_POINT);
        } else if (requestCode == REQUEST_NOTIFY_SEND_GIFT_SUCESS) {
            openChatScreen();
        }
    }

    @Override
    protected void onImageBackPressed() {
        if (openFrom == OPEN_FROM_CHAT) {
            getActivity().finish();
        }
    }

    private void showDialogNotEnoughPointForMale(UserItem currentUser) {
        String title = getString(R.string.mess_missing_point);
        String giftPrice = String.valueOf(currentGiftSelected.getPrice()) + "pt";
        String currentPoint = currentUser.getCoin() + "pt";
        String content = String.format(getString(R.string.content_not_enough_point_male), giftPrice, currentPoint);
        String positiveButton = getString(R.string.buy_point);
        TextDialog textDialog = new TextDialog.Builder()
                .setTitle(title)
                .setPositiveTitle(positiveButton)
                .build(this, content, REQUEST_BUY_POINT);
        textDialog.show(getFragmentManager(), TextDialog.class.getSimpleName());
    }

    private void showDialogNotEnoughPointForFemale(UserItem currentUser) {
        String giftPrice = String.valueOf(currentGiftSelected.getPrice()) + "pt";
        String currentPoint = currentUser.getCoin() + "pt";
        String title = getString(R.string.mess_missing_point);
        String content = String.format(getString(R.string.content_not_enough_point_female), giftPrice, currentPoint);
        showMessageDialog(title, content, "", false);
    }

    private void showDialogSendGiftSuccess() {
        String title = getString(R.string.title_send_gift_success);
        String content = String.format(getString(R.string.content_send_gift_success), userItem.getUsername(), currentGiftSelected.getName());
        TextDialog textDialog = new TextDialog.Builder()
                .setTitle(title)
                .hideNegativeButton(true)
                .build(this, content, REQUEST_NOTIFY_SEND_GIFT_SUCESS);
        textDialog.show(getFragmentManager(), TextDialog.class.getSimpleName());
    }

    private String getDescription() {
        UserItem userItem = ConfigManager.getInstance().getCurrentUser();
        int titleId = userItem.getGender() == UserItem.MALE ? R.string.gifts_description_male : R.string.gifts_description_female;
        return getString(titleId);
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

    private void openChatScreen() {
        ChatActivity.startChatActivity(getContext(), userItem);
    }
}
