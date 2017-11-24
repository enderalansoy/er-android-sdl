package com.easyroute.ui.dialog;

import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.Button;

import com.easyroute.R;
import com.easyroute.content.Preference;
/**
 * Created by imenekse on 11/03/17.
 */

public class PrivacyPolicyDialog extends BaseDialog implements OnClickListener {

    public interface OnPrivacyPolicyAcceptListener {

        void onPrivacyPolicyAccept();
    }

    private WebView webView;
    private Button btnPositive;
    private Button btnNegative;
    private OnPrivacyPolicyAcceptListener mOnPrivacyPolicyAcceptListener;

    @Override
    public int getLayoutId() {
        return R.layout.dialog_privacy_policy;
    }

    @Override
    public void initViews() {
        webView = (WebView) findViewById(R.id.webView);
        btnPositive = (Button) findViewById(R.id.btnPositive);
        btnNegative = (Button) findViewById(R.id.btnNegative);
    }

    @Override
    public void bindEvents() {
        btnPositive.setOnClickListener(this);
        btnNegative.setOnClickListener(this);
    }

    @Override
    public void setProperties() {
        setCancelable(false);
        webView.loadUrl("http://alberun.com/easyroute/easyroute_privacy_policy.html");
    }

    @Override
    public void onClick(View v) {
        if (btnPositive == v) {
            Preference.getInstance(context).setPrivacyPolicyAccepted(true);
            mOnPrivacyPolicyAcceptListener.onPrivacyPolicyAccept();
            dismiss();
        } else if (btnNegative == v) {
            dismiss();
            getActivity().finish();
        }
    }

    public void setOnPrivacyPolicyAcceptListener(OnPrivacyPolicyAcceptListener onPrivacyPolicyAcceptListener) {
        mOnPrivacyPolicyAcceptListener = onPrivacyPolicyAcceptListener;
    }
}
