package com.manager.hamster.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.manager.hamster.R;
import com.manager.hamster.databinding.ActivityThemSanPhamBinding;
import com.manager.hamster.model.MessageModel;
import com.manager.hamster.model.sanPhamMoi;
import com.manager.hamster.retrofit.ApiHamster;
import com.manager.hamster.retrofit.retrofitClient;
import com.manager.hamster.utils.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ThemSanPhamActivity extends AppCompatActivity {

    Spinner spinner;
    int loai =0;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    ApiHamster apiHamster;
    ActivityThemSanPhamBinding binding;
    String mediaPath;
    FrameLayout frameLayout;
    sanPhamMoi sanphamMoiSua;
    boolean flag=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityThemSanPhamBinding.inflate(getLayoutInflater());
        apiHamster= retrofitClient.getInstance(utils.BASE_URL).create(ApiHamster.class);
        setContentView(binding.getRoot());
        initView();
        initData();
        initControl();
        Intent intentget =getIntent();
        sanphamMoiSua=(sanPhamMoi ) intentget.getSerializableExtra("sua");
        if(sanphamMoiSua == null){
            flag=false;
        }else {
            flag=true;
            binding.btnThemSPQuanLy.setText("Sửa sản phẩm");
            //show dât
            binding.txtQuanLyThemMoTaSanPham.setText(sanphamMoiSua.getMota());
            binding.txtQuanLyThemGiaSanPham.setText(sanphamMoiSua.getGiasp()+"");
            binding.txtQuanLyThemHinhAnhSanPham.setText(sanphamMoiSua.getHinhanh());
            binding.txtQuanLyThemTenSanPham.setText(sanphamMoiSua.getTensanpham());
            binding.spinnerThemSanPhamQuanLy.setSelection(sanphamMoiSua.getLoai());



        }


    }

    private void initControl() {
        frameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentMenu= new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intentMenu);
                finish();
            }
        });
    }

    private void initData() {
        List<String> stringList = new ArrayList<>();
        stringList.add("Vui lòng chọn danh mục");
        stringList.add("Hamster Robo");
        stringList.add("Hamster Bear");
        stringList.add("Hamster Winter White");
        ArrayAdapter <String> adapter  = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,stringList);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                loai=i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        binding.btnThemSPQuanLy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(flag == false){
                    themsanpham();
                }else {
                    suaSanPham();
                }

            }
        });

        binding.imgQuanLyCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImagePicker.with(ThemSanPhamActivity.this)
                        .crop()	    			//Crop image(Optional), Check Customization for more option
                        .compress(1024)			//Final image size will be less than 1 MB(Optional)
                        .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                        .start();
            }
        });

    }

    private void suaSanPham() {
        String string_tensp= binding.txtQuanLyThemTenSanPham.getText().toString().trim();
        String string_giasp= binding.txtQuanLyThemGiaSanPham.getText().toString().trim();
        String string_hinhanhsp= binding.txtQuanLyThemHinhAnhSanPham.getText().toString().trim();
        String string_motasp= binding.txtQuanLyThemMoTaSanPham.getText().toString().trim();

        if(TextUtils.isEmpty(string_tensp)||TextUtils.isEmpty(string_giasp)||TextUtils.isEmpty(string_hinhanhsp)||TextUtils.isEmpty(string_motasp)||loai==0){
            Toast.makeText(getApplicationContext(), "Mời nhập đủ thông tin", Toast.LENGTH_SHORT).show();
        }else {
            compositeDisposable.add(apiHamster.suasp(string_tensp,string_giasp,string_hinhanhsp,string_motasp,loai,sanphamMoiSua.getId())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            messageModel -> {
                                if(messageModel.isSuccess()){
                                    Toast.makeText(getApplicationContext(), messageModel.getMessage(), Toast.LENGTH_SHORT).show();


                                }else{
                                    Toast.makeText(getApplicationContext(), messageModel.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            },throwable -> {
                                Toast.makeText(getApplicationContext(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
                            }

                    ));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mediaPath=data.getDataString();
        uploadMultipleFiles();
        Log.d("log","OnActivityResult: " +mediaPath);
    }

    private void themsanpham() {
        String string_tensp= binding.txtQuanLyThemTenSanPham.getText().toString().trim();
        String string_giasp= binding.txtQuanLyThemGiaSanPham.getText().toString().trim();
        String string_hinhanhsp= binding.txtQuanLyThemHinhAnhSanPham.getText().toString().trim();
        String string_motasp= binding.txtQuanLyThemMoTaSanPham.getText().toString().trim();

        if(TextUtils.isEmpty(string_tensp)||TextUtils.isEmpty(string_giasp)||TextUtils.isEmpty(string_hinhanhsp)||TextUtils.isEmpty(string_motasp)||loai==0){
            Toast.makeText(getApplicationContext(), "Mời nhập đủ thông tin", Toast.LENGTH_SHORT).show();
        }else {
            compositeDisposable.add(apiHamster.themsp(string_tensp,string_giasp,string_hinhanhsp,string_motasp,(loai))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            messageModel -> {
                                if(messageModel.isSuccess()){
                                    Toast.makeText(getApplicationContext(), messageModel.getMessage(), Toast.LENGTH_SHORT).show();


                                }else{
                                    Toast.makeText(getApplicationContext(), messageModel.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            },throwable -> {
                                Toast.makeText(getApplicationContext(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
                            }

                    ));
        }
    }
    private String getPath(Uri uri){
        String result;
        Cursor cursor =getContentResolver().query(uri,null,null,null,null);
        if(cursor == null ){
            result = uri.getPath();
        }else{
            cursor.moveToFirst();
            int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result=cursor.getString(index);
            cursor.close();
        }
        return result;
    }

    // Uploading Image/Video
    private void uploadMultipleFiles() {
        Uri uri=Uri.parse(mediaPath);
        File file = new File(getPath(uri));
        RequestBody requestBody1 = RequestBody.create(MediaType.parse("*/*"), file);
        MultipartBody.Part fileToUpload1 = MultipartBody.Part.createFormData("file", file.getName(), requestBody1);
        Call<MessageModel> call =apiHamster.uploadFile(fileToUpload1);
        call.enqueue(new Callback< MessageModel >() {
            @Override
            public void onResponse(Call < MessageModel > call, Response< MessageModel > response) {
                MessageModel serverResponse = response.body();
                if (serverResponse != null) {
                    if (serverResponse.isSuccess()) {
                        binding.txtQuanLyThemHinhAnhSanPham.setText(serverResponse.getName());
                    } else {
                        Toast.makeText(getApplicationContext(), serverResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {

                    Log.v("Response", serverResponse.toString());
                }

            }
            @Override
            public void onFailure(Call < MessageModel > call, Throwable t) {
                Log.d("log",t.getMessage());
            }
        });
    }

    private void initView() {
        frameLayout= findViewById(R.id.frameThemSPMMenu);
        spinner=findViewById(R.id.spinnerThemSanPhamQuanLy);
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}