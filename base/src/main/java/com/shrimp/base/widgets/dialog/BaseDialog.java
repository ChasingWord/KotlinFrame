package com.shrimp.base.widgets.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


public abstract class BaseDialog extends DialogFragment {
    protected View mContentView;
    private DismissListener dismissListener;

    public BaseDialog() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            setStyle(DialogFragment.STYLE_NO_TITLE, 0);
        }

        getExtraParams();
    }

    @Override
    public void onDestroy() {
        mContentView = null;
        super.onDestroy();
    }

    public void setDismissListener(DismissListener dismissListener) {
        this.dismissListener = dismissListener;
    }

    @Override
    public void onActivityCreated(Bundle arg0) {
        super.onActivityCreated(arg0);

        Dialog dialog = getDialog();
        if (dialog != null) {
            Window window = dialog.getWindow();
            window.setBackgroundDrawableResource(android.R.color.transparent);
            WindowManager.LayoutParams params = window.getAttributes();

            params.gravity = Gravity.CENTER;
            params.dimAmount = 0.35f;//设置透明度
            window.setAttributes(params);
            dialog.setCanceledOnTouchOutside(false);
        }
        initComponents(getContentView());
    }

    @SuppressLint("CommitTransaction")
    @Override
    public void show(FragmentManager manager, String tag) {
        Fragment fragment = manager.findFragmentByTag(tag);
        if (fragment != null) {
            manager.beginTransaction().remove(fragment).commitAllowingStateLoss();
        }
        show(manager.beginTransaction(), tag);
    }

    @Override
    public int show(FragmentTransaction transaction, String tag) {
        transaction.add(this, tag);
        return transaction.commitAllowingStateLoss();
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        if (dismissListener != null) {
            dismissListener.dismiss();
        }
    }

    final protected Bundle buildArguments() {
        Bundle b = getArguments();
        if (b == null) {
            b = new Bundle();
        }
        return b;
    }

    final protected View getContentView() {
        return mContentView;
    }

    protected abstract void getExtraParams();

    protected abstract void initComponents(View view);

    public interface DismissListener {
        void dismiss();
    }
}
