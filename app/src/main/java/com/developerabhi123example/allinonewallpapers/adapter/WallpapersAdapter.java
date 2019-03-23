package com.developerabhi123example.allinonewallpapers.adapter;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.developerabhi123example.allinonewallpapers.R;
import com.developerabhi123example.allinonewallpapers.models.Wallpaper;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class WallpapersAdapter extends RecyclerView.Adapter<WallpapersAdapter.WallpaperViewHolder> {

    private Context mctx;
    private List<Wallpaper> wallpaperList;

    public WallpapersAdapter(Context mctx, List<Wallpaper> wallpaperList) {
        this.mctx = mctx;
        this.wallpaperList = wallpaperList;

    }

    @NonNull
    @Override
    public WallpaperViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mctx).inflate(R.layout.recyclerview_wallpapers, parent, false);
        return new WallpaperViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WallpaperViewHolder holder, int position) {

        Wallpaper w = wallpaperList.get(position);
        holder.textView.setText(w.title);
        Glide.with(mctx)
                .load(w.url)
                .into(holder.imageView);
        if(w.isFavourite){
            holder.checkBoxFav.setChecked(true);
        }
    }

    @Override
    public int getItemCount() {
        return wallpaperList.size();
    }

    class WallpaperViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

        TextView textView;
        ImageView imageView;
        CheckBox checkBoxFav;
        ImageView buttonShare, buttonDownload;

        public WallpaperViewHolder(View itemView) {
            super(itemView);

            textView = itemView.findViewById(R.id.text_view_title);
            imageView = itemView.findViewById(R.id.image_view);
            checkBoxFav = itemView.findViewById(R.id.checkbox_favourites);
            buttonShare = itemView.findViewById(R.id.button_share);
            buttonDownload = itemView.findViewById(R.id.button_download);

            checkBoxFav.setOnCheckedChangeListener(this);
            buttonShare.setOnClickListener(this);
            buttonDownload.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.button_share:

                    shareWallpaper(wallpaperList.get(getAdapterPosition()));

                    break;
                case R.id.button_download:

                    downloadWallpaper(wallpaperList.get(getAdapterPosition()));

                    break;

            }

        }

        private void shareWallpaper(Wallpaper w) {
            ((Activity) mctx).findViewById(R.id.progressbar).setVisibility(View.VISIBLE);

            Glide.with(mctx)
                    .asBitmap()
                    .load(w.url)
                    .into(new SimpleTarget<Bitmap>() {
                              @Override
                              public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                                  ((Activity) mctx).findViewById(R.id.progressbar).setVisibility(View.GONE);

                                  Intent intent = new Intent(Intent.ACTION_SEND);
                                  intent.setType("image/*");
                                  intent.putExtra(Intent.EXTRA_STREAM, getLocalBitmapUri(resource));

                                  mctx.startActivity(Intent.createChooser(intent, "All In One Wallpapers"));
                              }
                          }
                    );
        }

        private Uri getLocalBitmapUri(Bitmap bmp) {
            Uri bmpUri = null;
            try {

                StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                StrictMode.setVmPolicy(builder.build());


                File file = new File(mctx.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),
                        "All_In_One_Wallpapers_" + System.currentTimeMillis() + ".png");
                FileOutputStream out = new FileOutputStream(file);
                bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
                out.close();
                bmpUri = Uri.fromFile(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return bmpUri;

        }

        private void downloadWallpaper(final Wallpaper wallpaper){

            ((Activity) mctx).findViewById(R.id.progressbar).setVisibility(View.VISIBLE);

            Glide.with(mctx)
                    .asBitmap()
                    .load(wallpaper.url)
                    .into(new SimpleTarget<Bitmap>() {
                              @Override
                              public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                                  ((Activity) mctx).findViewById(R.id.progressbar).setVisibility(View.GONE);

                                  Intent intent = new Intent(Intent.ACTION_VIEW);

                                  Uri uri = saveWallpaperAndGetUri(resource,wallpaper.id);

                                  if(uri != null){
                                      intent.setDataAndType(uri,"image/*");
                                      mctx.startActivity(Intent.createChooser(intent, "All In One Wallpapers"));
                                      Toast.makeText(mctx.getApplicationContext(),"Wallpaper Downloaded ! ",Toast.LENGTH_SHORT).show();
                                  }

                              }
                          }
                    );

        }

        private  Uri saveWallpaperAndGetUri(Bitmap bitmap , String id){

            if (ContextCompat.checkSelfPermission(mctx,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED){

                if (ActivityCompat
                        .shouldShowRequestPermissionRationale((Activity) mctx,Manifest.permission.WRITE_EXTERNAL_STORAGE))
                {
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);

                    Uri uri = Uri.fromParts("package",mctx.getPackageName(),null);
                    intent.setData(uri);
                    mctx.startActivity(intent);

                }

                else {

                    ActivityCompat.requestPermissions((Activity) mctx,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},100);
                }

                return null;

                }

                File folder = new File(Environment.getExternalStorageDirectory().toString() + "/All In One Wallpapers");
                folder.mkdirs();

            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());


            File file = new File(folder,id + ".jpg");
            try{
                FileOutputStream out = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG,100,out);
                out.flush();
                out.close();

                return Uri.fromFile(file);

            }
            catch (FileNotFoundException e)
            {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;

        }





        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            if (FirebaseAuth.getInstance().getCurrentUser() == null) {

                Toast.makeText(mctx, "Please LogIn First..", Toast.LENGTH_LONG).show();
                buttonView.setChecked(false);
                return;

            }


            int position = getAdapterPosition();
            Wallpaper w = wallpaperList.get(position);

            DatabaseReference dbFav = FirebaseDatabase.getInstance().getReference("users")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child("favourites")
                    .child(w.category);

            if (isChecked) {
                dbFav.child(w.id).setValue(w);
            } else {
                dbFav.child(w.id).setValue(null);
            }

        }
    }

}

