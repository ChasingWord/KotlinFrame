package com.shrimp.base.widgets.dialog;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.shrimp.base.R;

import androidx.fragment.app.FragmentTransaction;

/**
 * 在每次进行dismiss的时候进行onDestroy()并设置为null
 * 因为在dismiss的时候，对应的DialogFragment已经移除了，会经过onDestroyView
 * 对应的Fragment经过销毁，所以对应持有的引用需要设置为null，否则会引起内存泄露
 */
public class ProgressDialog extends BaseDialog {

    private String mMessage;
    private boolean isShowing;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.shrimp_view_loading, container, false);
        setDismissListener(() -> isShowing = false);
        return mContentView;
    }

    public boolean isShowing() {
        return isShowing;
    }

    @Override
    protected void getExtraParams() {

        Bundle bundle = getArguments();
        if (bundle == null)
            return;
        mMessage = bundle.getString("message");
    }

    public void setMessage(String text) {
        Bundle bundle = buildArguments();
        bundle.putString("message", text);
        setArguments(bundle);
    }

    @Override
    protected void initComponents(View view) {
        TextView tvMessage = view.findViewById(R.id.define_porgress_tvMessage);
        if (!TextUtils.isEmpty(mMessage)) {
            tvMessage.setText(mMessage);
        }
    }

    @Override
    public void onActivityCreated(Bundle arg0) {
        super.onActivityCreated(arg0);
        WindowManager.LayoutParams params = this.getDialog().getWindow().getAttributes();
        params.gravity = Gravity.CENTER;
//        params.gravity = Gravity.TOP;
//        params.y = GenericTools.dip2px(getActivity(), 50);
        params.dimAmount = 0.5f;
        this.getDialog().getWindow().setAttributes(params);
    }

    @Override
    public int show(FragmentTransaction transaction, String tag) {
        if (isShowing) return 0;
        try {
            isShowing = true;
            return super.show(transaction, tag);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public void dismiss() {
        if (!isShowing) return;
        try {
            super.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
