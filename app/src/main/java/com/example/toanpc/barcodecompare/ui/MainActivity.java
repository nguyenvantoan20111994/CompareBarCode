package com.example.toanpc.barcodecompare.ui;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.icu.util.Calendar;
import android.os.Build;
import android.provider.SyncStateContract;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.toanpc.barcodecompare.R;
import com.example.toanpc.barcodecompare.controller.RealmCotroller;
import com.example.toanpc.barcodecompare.model.ItemCheck;
import com.google.zxing.client.android.integration.IntentIntegrator;
import com.google.zxing.client.android.integration.IntentResult;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private FrameLayout mFrameCheckBarcode,mFrameListCheck;
    private EditText mEditBarCode1, mEditBarCode2;
    private ImageButton mButtonBarCode1, mButtonBarCode2;
    private static final int REQUESTCODE1=112;
    private static final int REQUESTCODE2=113;
    private static final int MY_PERMISSIONS_REQUEST=115;
    ItemCheck mItemCheck=new ItemCheck();
    private View mView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        if (!checkPermission()){
           requestPermissions();
        }
    }
    private void initView(){
        mButtonBarCode1=(ImageButton) findViewById(R.id.button_camera1) ;
        mButtonBarCode2=(ImageButton) findViewById(R.id.button_camera2) ;
        mFrameCheckBarcode=(FrameLayout) findViewById(R.id.frame_button_check_bar);
        mFrameListCheck=(FrameLayout) findViewById(R.id.frame_button_listcheck);
        mEditBarCode1=(EditText) findViewById(R.id.edit_text_barcode1);
        mView=(View) findViewById(android.R.id.content);
        mEditBarCode2=(EditText) findViewById(R.id.edit_text_barcode2);
        mFrameCheckBarcode.setOnClickListener(this);
        mFrameListCheck.setOnClickListener(this);
        mButtonBarCode1.setOnClickListener(this);
        mButtonBarCode2.setOnClickListener(this);
    }
    public boolean checkPermission(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED){
            return false;
        }else{
            return true;
        }
    }
    public void requestPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                Snackbar.make(mView,getString(R.string.permissions),
                        Snackbar.LENGTH_INDEFINITE).setAction(getString(R.string.retry), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST);
                    }
                }).show();
            } else {
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA} , MY_PERMISSIONS_REQUEST);
            }
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.frame_button_check_bar:
                checkBarCode();
                break;
            case R.id.frame_button_listcheck:
                List<ItemCheck> itemChecks=new ArrayList<>();
                itemChecks=RealmCotroller.getAll();
                if(itemChecks.size()==0){
                    Toast.makeText(getBaseContext(),"scan does not find in the database",Toast.LENGTH_LONG).show();
                }else{
                    startActivity(new Intent(this,ListCheckActivity.class));
                }
                break;
            case R.id.button_camera1:
                Camera(REQUESTCODE1);
                break;
            case R.id.button_camera2:
                Camera(REQUESTCODE2);
                break;
        }
    }

    public void Camera(int requestcode){
        if (checkPermission()){
            IntentIntegrator.initiateScan(MainActivity.this,requestcode);
        }else {
            requestPermissions();
        }

    }
    public void checkBarCode(){
        String barcode1=mEditBarCode1.getText().toString().toLowerCase().trim();
        String barcode2=mEditBarCode2.getText().toString().toLowerCase().trim();
        mItemCheck.setmBarcode1(barcode1);
        mItemCheck.setmBarcode2(barcode2);
        Date currentDate = new Date(System.currentTimeMillis());
        mItemCheck.setmTimeCheck(currentDate);

        if(!barcode1.equals("")&& !barcode2.equals("")){
            if(barcode1.equals(barcode2)){
                showFragmentLogin(true);
                mEditBarCode1.setText("");
                mEditBarCode2.setText("");
                mItemCheck.setmStatus(true);
            }else{
                showFragmentLogin(false);
                mEditBarCode1.setText("");
                mEditBarCode2.setText("");
                mItemCheck.setmStatus(false);
            }
            RealmCotroller.saveObject(mItemCheck);
        }else{
            Toast.makeText(getBaseContext(),"Scan your text barcode 1 and barcode 2",Toast
                .LENGTH_LONG)
                .show();
        }

    }
    public void showFragmentLogin(boolean check) {
        ShowDialogFragment forgetFragment = ShowDialogFragment.newInstace(check);
        forgetFragment.show(getSupportFragmentManager(), "Show");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            } else {
               requestPermissions();
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.N)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result!=null){
            switch (requestCode){
                case REQUESTCODE1:
                    mEditBarCode1.setText(result.getContents());;
                    break;
                case REQUESTCODE2:
                    mEditBarCode2.setText(result.getContents());
                    break;

            }
        }else {
            Toast.makeText(getBaseContext(),R.string.toast_result, Toast.LENGTH_SHORT).show();
        }


    }
}
