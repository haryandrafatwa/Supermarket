package com.example.supermarket.Profile;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.supermarket.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

public class DetailProfileDragment extends Fragment{
    private static final int GALLERY = 22 ;
    private static final String APP_TAG = "Supermarket";
    private static final int CAMERAA = 33;
    private Toolbar toolbar;
    private TextView done_name,cancel_name,done_phone,cancel_phone,done_email,cancel_email,change,delete;
    private EditText et_name, et_phone, et_email;
    private ImageButton ib_name,ib_phone,ib_email;
    private Button btnSave;
    private CircleImageView circleImageView,circleImageView2;
    private ProgressBar progressBar;
    private LinearLayout tools_name,tools_phone,tools_email;
    private RelativeLayout layout_profile;
    private BottomNavigationView bottomNavigationView;
    private String name, phone, email,imageURL,defaultURL;
    private Uri filePath;
    private File finalFile;
    private String photoFileName = "ic_user.png";
    private int changed = 0;

    private DatabaseReference userRefs;
    private StorageReference imageRefs;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if(changed > 0){
                    alertOnBackPressed();
                }else{
                    getActivity().getSupportFragmentManager().popBackStack();
                }
            }
        };
        getActivity().getOnBackPressedDispatcher().addCallback(this,callback);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile_detail, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        initialize();
    }

    //method utk menginisiasi seluruh objek yang ada pada halaman ini
    private void initialize(){
        toolbar = getActivity().findViewById(R.id.toolbar_profile);
        setToolbar();

        defaultURL = "https://firebasestorage.googleapis.com/v0/b/supermarket-telu.appspot.com/o/DisplayPictures%2Fdummy%2Fic_user.png?alt=media&token=47cc2045-f15e-4cd5-a3bf-0080d97545a6";
        layout_profile = getActivity().findViewById(R.id.layout_profile);
        layout_profile.setForeground(getActivity().getDrawable(R.drawable.fg_solid_white));

        bottomNavigationView = getActivity().findViewById(R.id.bottomNavBar);
        bottomNavigationView.setVisibility(View.GONE);
        circleImageView = getActivity().findViewById(R.id.iv_profile_picture);
        circleImageView2 = getActivity().findViewById(R.id.iv_profile_picture_foreground);
        progressBar = getActivity().findViewById(R.id.pb_profile);
        btnSave = getActivity().findViewById(R.id.btn_simpan);
        change = getActivity().findViewById(R.id.change);
        delete = getActivity().findViewById(R.id.delete);

        et_name = getActivity().findViewById(R.id.et_nama_profile);
        ib_name = getActivity().findViewById(R.id.ib_edit_name_profile);
        tools_name = getActivity().findViewById(R.id.layout_tool_name);
        cancel_name = getActivity().findViewById(R.id.cancel_name);
        done_name = getActivity().findViewById(R.id.done_name);

        et_phone = getActivity().findViewById(R.id.et_phone_profile);
        ib_phone = getActivity().findViewById(R.id.ib_edit_phone_profile);
        tools_phone = getActivity().findViewById(R.id.layout_tool_phone);
        cancel_phone = getActivity().findViewById(R.id.cancel_phone);
        done_phone = getActivity().findViewById(R.id.done_phone);

        et_email = getActivity().findViewById(R.id.et_email_profile);
        ib_email = getActivity().findViewById(R.id.ib_edit_email_profile);
        tools_email = getActivity().findViewById(R.id.layout_tool_email);
        cancel_email = getActivity().findViewById(R.id.cancel_email);
        done_email = getActivity().findViewById(R.id.done_email);

        ib_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tools_name.setVisibility(View.VISIBLE);
                et_name.setEnabled(true);
                ib_name.setVisibility(View.GONE);
            }
        });

        cancel_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ib_name.setVisibility(View.VISIBLE);
                et_name.setEnabled(false);
                tools_name.setVisibility(View.GONE);
                et_name.setText(name);
            }
        });

        done_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ib_name.setVisibility(View.VISIBLE);
                et_name.setEnabled(false);
                tools_name.setVisibility(View.GONE);
                name = et_name.getText().toString();
                changed+=1;
                btnSave.setVisibility(View.VISIBLE);
            }
        });

        ib_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tools_phone.setVisibility(View.VISIBLE);
                et_phone.setEnabled(true);
                ib_phone.setVisibility(View.GONE);
            }
        });

        cancel_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ib_phone.setVisibility(View.VISIBLE);
                et_phone.setEnabled(false);
                tools_phone.setVisibility(View.GONE);
                et_phone.setText(phone);
            }
        });

        done_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ib_phone.setVisibility(View.VISIBLE);
                et_phone.setEnabled(false);
                tools_phone.setVisibility(View.GONE);
                phone = et_phone.getText().toString();
                btnSave.setVisibility(View.VISIBLE);
                changed+=1;
            }
        });

        ib_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tools_email.setVisibility(View.VISIBLE);
                et_email.setEnabled(true);
                ib_email.setVisibility(View.GONE);
            }
        });

        cancel_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ib_email.setVisibility(View.VISIBLE);
                et_email.setEnabled(false);
                tools_email.setVisibility(View.GONE);
                et_email.setText(email);
            }
        });

        done_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ib_email.setVisibility(View.VISIBLE);
                et_email.setEnabled(false);
                tools_email.setVisibility(View.GONE);
                email = et_email.getText().toString();
                btnSave.setVisibility(View.VISIBLE);
                changed+=1;
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageURL = defaultURL;
                btnSave.setVisibility(View.VISIBLE);
                circleImageView2.setVisibility(View.GONE);
                userRefs.child("displayPicture").setValue(imageURL);
                changed+=1;
            }
        });

        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPictureDialog();
                btnSave.setVisibility(View.VISIBLE);
                changed+=1;
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                updateData();
                changed=0;
            }
        });

        userRefs = FirebaseDatabase.getInstance().getReference().child("User").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        imageRefs = FirebaseStorage.getInstance().getReference().child("DisplayPictures").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("ic_user.png");
        //proses mengambil data user dari firebase database
        userRefs.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                name = dataSnapshot.child("name").getValue().toString();
                phone = dataSnapshot.child("phonenumber").getValue().toString();
                email = dataSnapshot.child("email").getValue().toString();
                imageURL = dataSnapshot.child("displayPicture").getValue().toString();

                Picasso.get().load(imageURL).fit().into(circleImageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        layout_profile.setForeground(null);
                        progressBar.setVisibility(View.GONE);

                        et_name.setText(name);
                        et_phone.setText(phone);
                        et_email.setText(email);
                    }

                    @Override
                    public void onError(Exception e) {
                        
                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    //method utk menampilkan pesan dialog ketika ingin mengubah foto profile
    private void showPictureDialog(){
        final Dialog dialog1 = new Dialog(getActivity(),R.style.CustomAlertDialog);
        dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog1.getWindow().setBackgroundDrawable(getActivity().getDrawable(R.drawable.bg_btn_solid_white));
        dialog1.setContentView(R.layout.dialog_take_photo);
        dialog1.setCancelable(true);

        RelativeLayout layoutCamera = dialog1.findViewById(R.id.layout_camera);
        RelativeLayout layoutGallery = dialog1.findViewById(R.id.layout_gallery);

        layoutCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
                dialog1.cancel();
            }
        });

        layoutGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choosePhotoFromGallery();
                dialog1.cancel();
            }
        });

        dialog1.show();
    }

    //proses pengupdatean data dari firebase database
    private void updateData() {

        if (filePath != null){
            HashMap userMap = new HashMap();
            userMap.put("email",email);
            userMap.put("phonenumber",phone);
            userMap.put("name",name);
            userRefs.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    uploadImage();
                }
            });
        }else{
            HashMap userMap = new HashMap();
            userMap.put("email",email);
            userMap.put("phonenumber",phone);
            userMap.put("name",name);
            userRefs.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    initializeDialogUpdated();
                }
            });
        }

    }

    //proses upload image baru ke firebase storage dan juga mengambil link download dan disimpan ke firebase database
    private void uploadImage(){
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle("Uploading...");
        progressDialog.show();
        progressDialog.setCancelable(false);
        imageRefs.putFile(filePath)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        progressDialog.dismiss();
                        imageRefs.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                userRefs.child("displayPicture").setValue(uri.toString());
                                initializeDialogUpdated();
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(getActivity(), "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                .getTotalByteCount());
                        progressDialog.setMessage("Uploaded "+(int)progress+"%");
                    }
                });
    }

    //method utk menampilkan pesan dialog utk validasi akan melakukan update
    private void initializeDialogUpdated(){
        android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(getActivity(),R.style.CustomAlertDialog);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_updated,null);
        dialogView.setBackgroundColor(Color.TRANSPARENT);
        dialog.setView(dialogView);
        dialog.setCancelable(false);
        dialog.setTitle(null);

        dialog.setPositiveButton("OKAY", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        dialog.show();
    }

    //method utk menampilkan alert ketika menekan tombol back jika sudah ada perubahan
    public void alertOnBackPressed(){
        AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(getActivity());

        // Setting Dialog Title
        alertDialog2.setTitle("Confirmation");

        alertDialog2.setCancelable(false);

        // Setting Dialog Message
        alertDialog2.setMessage("Are you sure want to back?");

        // Setting Positive "Yes" Btn
        alertDialog2.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Write your code here to execute after dialog
                ProfileFragment profileFragment = new ProfileFragment();
                setFragment(profileFragment);
            }
        });

        alertDialog2.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog2.show();

    }

    private void setFragment(Fragment fragment) // fungsi buat pindah - pindah fragment
    {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        for(int i = 0; i < fm.getBackStackEntryCount(); ++i) {
            fm.popBackStack();
        }
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frameFragment,fragment);
        fragmentTransaction.commit();
    }

    //method utk mengecek apakah device telah memberikan permission utk aplikasi menggunakan camera atau tidak, jika sudah ada, maka akan dikirimkan ke halaman camera
    private void dispatchTakePictureIntent() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, 1);
        } else {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            finalFile = getPhotoFileUri(photoFileName);
            filePath = FileProvider.getUriForFile(getActivity(), "com.example.supermarket.fileprovider", finalFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, filePath);
            if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                startActivityForResult(intent, CAMERAA);
            }
        }

    }

    //method utk mengolah hasil pilihan foto, dan ditampilkan ke circle image view yang ada pada halaman
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY && resultCode == RESULT_OK) {

            try {
                filePath = data.getData();
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), filePath);
                circleImageView2.setImageBitmap(bitmap);
                circleImageView2.setVisibility(View.VISIBLE);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        if (requestCode == CAMERAA && resultCode == RESULT_OK) {

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), filePath);
                circleImageView2.setImageBitmap(bitmap);
                circleImageView2.setVisibility(View.VISIBLE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //method utk melakukan konversi dari abstract path menjadi absolute path
    public File getPhotoFileUri(String fileName) {
        File mediaStorageDir = new File(getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES), APP_TAG);

        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d(APP_TAG, "failed to create directory");
        }

        File file = new File(mediaStorageDir.getPath() + File.separator + fileName);

        return file;
    }

    //method utk menampilkan galeri foto user
    private void choosePhotoFromGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), GALLERY);
    }

    //method utk mensetting toolbar agar tidak ada title dan juga agar ada tombol back
    private void setToolbar() {
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("");
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
    }
}
