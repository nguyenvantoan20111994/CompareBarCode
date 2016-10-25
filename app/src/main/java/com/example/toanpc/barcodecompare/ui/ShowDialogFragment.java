package com.example.toanpc.barcodecompare.ui;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.support.v4.app.DialogFragment;
import com.example.toanpc.barcodecompare.R;

/**
 * Created by toanpc on 09/10/2016.
 */
public class ShowDialogFragment extends DialogFragment implements View.OnClickListener {
    Button mBtnOk,mBtnClose;
    public static final String BUNDLE_MESSAGE="message";
    View mView;
    TextView mTxtPassFail,mTxtTitle;

    public static ShowDialogFragment newInstace(boolean message) {
        ShowDialogFragment showDialogFragment=new ShowDialogFragment();
        Bundle bundle=new Bundle();
        bundle.putBoolean(BUNDLE_MESSAGE,message);
        showDialogFragment.setArguments(bundle);
        return showDialogFragment;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_dialog, container, false);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        findViewById();
        boolean myValue = this.getArguments().getBoolean(BUNDLE_MESSAGE);
        setData(myValue);
        return mView;
    }

    public void findViewById(){
        mBtnClose=(Button) mView.findViewById(R.id.close_button);
        mBtnOk=(Button) mView.findViewById(R.id.positive_button);
        mTxtPassFail=(TextView) mView.findViewById(R.id.message);
        mTxtTitle=(TextView) mView.findViewById(R.id.title);
        mBtnClose.setOnClickListener(this);
        mBtnOk.setOnClickListener(this);
    }
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void setData(boolean check){
        if(!check){
            mBtnOk.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.fail));
            mBtnClose.setBackground(ContextCompat.getDrawable(getContext(),R.drawable.bt_dialog_close_fail));
            mTxtTitle.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.fail));
            mBtnClose.setTextColor(ContextCompat.getColor(getContext(),R.color.fail));
            mTxtPassFail.setText("Fail");
        }else{
            mTxtPassFail.setText("Pass");
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.positive_button:
                dismiss();
                break;
            case R.id.close_button:
                dismiss();
                break;
        }
    }

}
