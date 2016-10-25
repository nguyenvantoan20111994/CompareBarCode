package com.example.toanpc.barcodecompare.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.toanpc.barcodecompare.R;
import com.example.toanpc.barcodecompare.model.ItemCheck;

import java.util.List;

/**
 * Created by toanpc on 09/10/2016.
 */
public class RecycleViewAdapter extends RecyclerView.Adapter<RecycleViewAdapter.ViewHolderBarCode> {
    private List<ItemCheck> mItemCheckList;
    @Override
    public ViewHolderBarCode onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolderBarCode(LayoutInflater.from(parent.getContext()).
                inflate(R.layout.item_recyclerview, parent, false));
    }

    public RecycleViewAdapter(List<ItemCheck> mItemCheckList) {
        this.mItemCheckList = mItemCheckList;
    }

    @Override
    public void onBindViewHolder(ViewHolderBarCode holder, int position) {
        ItemCheck itemCheck=mItemCheckList.get(position);
        holder.mTxtBarCode1.setText(itemCheck.getmBarcode1());
        holder.mTxtBarCode2.setText(itemCheck.getmBarcode2());
        holder.mTxtTime.setText(itemCheck.getmTimeCheck().toString());
        if(itemCheck.getmStatus()){
            holder.mTxtStatus.setText("Pass");
        }else{
            holder.mTxtStatus.setText("Fail");
        }

    }

    @Override
    public int getItemCount() {
        return mItemCheckList!=null ? mItemCheckList.size():0;
    }

    public class ViewHolderBarCode extends RecyclerView.ViewHolder{
        protected TextView mTxtBarCode1,mTxtBarCode2,mTxtTime,mTxtStatus;

        public ViewHolderBarCode(View itemView) {
            super(itemView);
            mTxtBarCode1=(TextView) itemView.findViewById(R.id.text_barcode1);
            mTxtBarCode2=(TextView) itemView.findViewById(R.id.text_barcode2);
            mTxtTime=(TextView) itemView.findViewById(R.id.text_time);
            mTxtStatus=(TextView) itemView.findViewById(R.id.text_status);
        }
    }
}
