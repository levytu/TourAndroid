package com.manager.hamster.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.manager.hamster.Interface.ItemClickListener;
import com.manager.hamster.R;
import com.manager.hamster.activity.ChiTietActivity;
import com.manager.hamster.model.sanPhamMoi;
import com.manager.hamster.utils.utils;

import java.text.DecimalFormat;
import java.util.List;

public class hamsterAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context context;
    List<sanPhamMoi> array;
    private static final int VIEW_TYPE_DATA =0 ;
    private static final int VIEW_TYPE_LOADING =1 ;

    public hamsterAdapter(Context context, List<sanPhamMoi> array) {
        this.context = context;
        this.array = array;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType==VIEW_TYPE_DATA){
            View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_hamster,parent,false);
            return new MyViewHolder(view);
        }else{
                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_loading,parent,false);
            return new LoadingViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof  MyViewHolder){
            MyViewHolder myViewHolder = (MyViewHolder) holder;
            sanPhamMoi sanPhamMoi= array.get(position);
            myViewHolder.txttensp.setText(sanPhamMoi.getTensanpham());
            DecimalFormat decimalFormat =new DecimalFormat("###,###,###");
            myViewHolder.txtgiasp.setText("Giá: "+decimalFormat.format(Double.parseDouble(sanPhamMoi.getGiasp()))+"Đ");
            myViewHolder.txtmota.setText(sanPhamMoi.getMota());
            if(sanPhamMoi.getHinhanh().contains("http")){
                Glide.with(context).load(sanPhamMoi.getHinhanh()).into(((MyViewHolder) holder).hinhanh);
            }else {
                String hinh = utils.BASE_URL+"images/"+sanPhamMoi.getHinhanh();
                Glide.with(context).load(hinh).into(((MyViewHolder) holder).hinhanh);
            }
            myViewHolder.setItemClickListener(new ItemClickListener() {
                @Override
                public void onClick(View view, int pos, boolean isLongClick) {
                    if(!isLongClick){
                        Intent intent= new Intent(context, ChiTietActivity.class);
                        intent.putExtra("chitiet",sanPhamMoi);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    }
                }
            });
        }else{
            LoadingViewHolder   loadingViewHolder=(LoadingViewHolder) holder;
            loadingViewHolder.progressBar.setIndeterminate(true);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return  array.get(position) == null ? VIEW_TYPE_LOADING:VIEW_TYPE_DATA;
    }

    @Override
    public int getItemCount() {
        return array.size();
    }
    public class LoadingViewHolder extends RecyclerView.ViewHolder{
        ProgressBar progressBar;
        public LoadingViewHolder(@NonNull View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.progressbar);
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView txttensp,  txtgiasp,txtmota;
        ImageView hinhanh;
        private ItemClickListener itemClickListener;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txttensp= itemView.findViewById(R.id.itemHamster_Ten);
            txtgiasp= itemView.findViewById(R.id.itemHamster_Gia);
            txtmota= itemView.findViewById(R.id.itemHamster_MoTa);
            hinhanh= itemView.findViewById(R.id.itemHamster_image);
            itemView.setOnClickListener(this);
        }

        public void setItemClickListener(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }

        @Override
        public void onClick(View view) {
            itemClickListener.onClick(view,getAdapterPosition(),false);
        }
    }
}
