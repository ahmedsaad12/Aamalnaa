package com.creative.share.apps.aamalnaa.activities_fragments.activity_my_ads;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.creative.share.apps.aamalnaa.R;
import com.creative.share.apps.aamalnaa.activities_fragments.activity_adsdetails.AdsDetialsActivity;
import com.creative.share.apps.aamalnaa.adapters.Ads_Adapter;
import com.creative.share.apps.aamalnaa.databinding.ActivityMyAdsBinding;
import com.creative.share.apps.aamalnaa.interfaces.Listeners;
import com.creative.share.apps.aamalnaa.language.Language;
import com.creative.share.apps.aamalnaa.models.Adversiment_Model;
import com.creative.share.apps.aamalnaa.models.UserModel;
import com.creative.share.apps.aamalnaa.preferences.Preferences;
import com.creative.share.apps.aamalnaa.remote.Api;
import com.creative.share.apps.aamalnaa.share.Common;
import com.creative.share.apps.aamalnaa.tags.Tags;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.paperdb.Paper;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyAdsActivity extends AppCompatActivity implements Listeners.BackListener {
    private ActivityMyAdsBinding binding;
    private String lang;
    private Ads_Adapter ads_adapter;
    private List<Adversiment_Model.Data> advesriment_data_list;
    private LinearLayoutManager manager;
    private boolean isLoading = false;
    private int current_page2 = 1;
    private Preferences preferences;
    private UserModel userModel;
    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(Language.updateResources(newBase, Paper.book().read("lang", "ar")));

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_my_ads);
        initView();
        if(userModel!=null){
        getAds();}

    }

    private void initView() {
        advesriment_data_list=new ArrayList<>();
        preferences= Preferences.getInstance();
        userModel=preferences.getUserData(this);
        Paper.init(this);
        lang = Paper.book().read("lang", Locale.getDefault().getLanguage());
        binding.setLang(lang);
        binding.setBackListener(this);
        manager = new LinearLayoutManager(this);

        binding.progBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(this, R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
        binding.recView.setLayoutManager(manager);
        ads_adapter = new Ads_Adapter(advesriment_data_list,this);
        binding.recView.setItemViewCacheSize(25);
        binding.recView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        binding.recView.setDrawingCacheEnabled(true);
        binding.progBar.setVisibility(View.GONE);
        binding.llNoStore.setVisibility(View.GONE);
        binding.swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getAds();
            }
        });
//        binding.recView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//                if(dy>0){
//                    int totalItems = ads_adapter.getItemCount();
//                    int lastVisiblePos = manager.findLastCompletelyVisibleItemPosition();
//                    if (totalItems > 5 && (totalItems - lastVisiblePos) == 1 && !isLoading) {
//                        isLoading = true;
//                        advesriment_data_list.add(null);
//                        ads_adapter.notifyItemInserted(advesriment_data_list.size() - 1);
//                        int page= current_page2 +1;
//                        loadMore(page);
//
//
//
//
//                    }
//                }
//            }
//        });
        binding.recView.setAdapter(ads_adapter);

    }
    public void getAds() {
        //   Common.CloseKeyBoard(homeActivity, edt_name);
        advesriment_data_list.clear();
        ads_adapter.notifyDataSetChanged();
        binding.progBar.setVisibility(View.VISIBLE);

        // rec_sent.setVisibility(View.GONE);
        try {


            Api.getService( Tags.base_url)
                    .getMyAds(userModel.getUser().getId()+"")
                    .enqueue(new Callback<Adversiment_Model>() {
                        @Override
                        public void onResponse(Call<Adversiment_Model> call, Response<Adversiment_Model> response) {
                            binding.swipeRefresh.setRefreshing(false);

                            binding.progBar.setVisibility(View.GONE);
                            if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                                advesriment_data_list.clear();
                                advesriment_data_list.addAll(response.body().getData());
                                if (response.body().getData().size() > 0) {
                                    // rec_sent.setVisibility(View.VISIBLE);
                                    //  Log.e("data",response.body().getData().get(0).getAr_title());

                                    binding.llNoStore.setVisibility(View.GONE);
                                    ads_adapter.notifyDataSetChanged();
                                    //   total_page = response.body().getMeta().getLast_page();

                                } else {
                                    ads_adapter.notifyDataSetChanged();

                                    binding.llNoStore.setVisibility(View.VISIBLE);

                                }
                            } else {
                                ads_adapter.notifyDataSetChanged();

                                binding.llNoStore.setVisibility(View.VISIBLE);

                                //Toast.makeText(activity, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                                try {
                                    Log.e("Error_code", response.code() + "_" + response.errorBody().string());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<Adversiment_Model> call, Throwable t) {
                            try {
                                binding.swipeRefresh.setRefreshing(false);

                                binding.progBar.setVisibility(View.GONE);
                                binding.llNoStore.setVisibility(View.VISIBLE);


                                Toast.makeText(MyAdsActivity.this, getString(R.string.something), Toast.LENGTH_SHORT).show();
                                Log.e("error", t.getMessage());
                            } catch (Exception e) {
                            }
                        }
                    });
        }catch (Exception e){
            binding.progBar.setVisibility(View.GONE);
            binding.llNoStore.setVisibility(View.VISIBLE);

        }
    }

    private void loadMore(int page) {
        try {


            Api.getService(Tags.base_url)
                    .getMyAds( userModel.getUser().getId()+"")
                    .enqueue(new Callback<Adversiment_Model>() {
                        @Override
                        public void onResponse(Call<Adversiment_Model> call, Response<Adversiment_Model> response) {
                            advesriment_data_list.remove(advesriment_data_list.size() - 1);
                            ads_adapter.notifyItemRemoved(advesriment_data_list.size() - 1);
                            isLoading = false;
                            if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {

                                advesriment_data_list.addAll(response.body().getData());
                                // categories.addAll(response.body().getCategories());
                                current_page2 = response.body().getCurrent_page();
                                ads_adapter.notifyDataSetChanged();

                            } else {
                                //Toast.makeText(activity, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                                try {
                                    Log.e("Error_code", response.code() + "_" + response.errorBody().string());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<Adversiment_Model> call, Throwable t) {
                            try {
                                advesriment_data_list.remove(advesriment_data_list.size() - 1);
                                ads_adapter.notifyItemRemoved(advesriment_data_list.size() - 1);
                                isLoading = false;
                                // Toast.makeText(activity, getString(R.string.something), Toast.LENGTH_SHORT).show();
                                Log.e("error", t.getMessage());
                            } catch (Exception e) {
                            }
                        }
                    });}
        catch (Exception e){
            advesriment_data_list.remove(advesriment_data_list.size() - 1);
            ads_adapter.notifyItemRemoved(advesriment_data_list.size() - 1);
            isLoading = false;
        }
    }
    public void showdetials(int id) {
        Intent intent=new Intent(MyAdsActivity.this, AdsDetialsActivity.class);
        intent.putExtra("search",id);
        startActivity(intent);
    }
    @Override
    public void back() {
        finish();
    }
    public void DeleteMYAd(int id, int layoutPosition) {
        try {


            ProgressDialog dialog = Common.createProgressDialog(this,getString(R.string.wait));
            dialog.setCancelable(false);
            dialog.show();
            Api.getService(Tags.base_url).DeleteMyAd(id,userModel.getUser().getId()).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    dialog.dismiss();
                    if(response.isSuccessful()){
                        advesriment_data_list.remove(layoutPosition);
                        ads_adapter.notifyItemRemoved(layoutPosition);
                    }
                    else {
                        if (response.code() == 422) {
                            Toast.makeText(MyAdsActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                            //  Log.e("error",response.code()+"_"+response.errorBody()+response.message()+password+phone+phone_code);
                            try {

                                Log.e("error",response.code()+"_"+response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        else if (response.code() == 500) {
                            try {

                                Log.e("error",response.code()+"_"+response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                           // Toast.makeText(MyAdsActivity.this, "Server Error", Toast.LENGTH_SHORT).show();

                        }
                        else {
                            try {

                                Log.e("error",response.code()+"_"+response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            Toast.makeText(MyAdsActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    try {
                        dialog.dismiss();
                        if (t.getMessage()!=null)
                        {
                            Log.e("error",t.getMessage());
                            if (t.getMessage().toLowerCase().contains("failed to connect")||t.getMessage().toLowerCase().contains("unable to resolve host"))
                            {
                                Toast.makeText(MyAdsActivity.this,R.string.something, Toast.LENGTH_SHORT).show();
                            }else
                            {
                                Toast.makeText(MyAdsActivity.this,t.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }

                    }catch (Exception e){}
                }
            });}
        catch (Exception e){

        }
    }

}
