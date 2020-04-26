package com.example.supermarket.Home.Sell;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.icu.text.NumberFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.supermarket.Home.HomeFragment;
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

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;

public class SellFragment extends Fragment {

    private static final int GALLERY = 1;
    private static final int CAMERAA = 2;
    private static final String APP_TAG = "Supermarket";
    private String idProduct, uid, by;
    private String photoFileName = "ic_user.png";
    private Uri filePath;
    private File finalFile;

    private DatabaseReference produkRefs,userRefs;
    private StorageReference imageRefs;

    private Toolbar toolbar;
    private BottomNavigationView bottomNavigationView;
    private CardView cardView;
    private ImageView imageView;
    private ImageButton imageButton;
    private Button btnPost;
    private Spinner kategori, kondisi;
    private TextView hapus_image;
    private EditText et_kategori, et_nama, et_alamat, et_kondisi,et_stok, et_harga, et_deskripsi;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            //method penurunan dari fragment, jika menekan tombol back pada device, dilihat apakah inputan sudah ada yg terisi atau belum, jika sudah makan akan menampilkan pesan dialog
            public void handleOnBackPressed() {
                if(!TextUtils.isEmpty(et_alamat.getText().toString()) || !TextUtils.isEmpty(et_kategori.getText().toString()) || !TextUtils.isEmpty(et_nama.getText().toString()) ||
                        !TextUtils.isEmpty(et_kondisi.getText().toString()) || !TextUtils.isEmpty(et_harga.getText().toString()) || !TextUtils.isEmpty(et_deskripsi.getText().toString()) ||
                        !TextUtils.isEmpty(et_stok.getText().toString()) || filePath != null){
                    alertOnBackPressed();
                }else{
                    getActivity().getSupportFragmentManager().popBackStack();
                }
            }
        };
        getActivity().getOnBackPressedDispatcher().addCallback(this,callback);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sell, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        initialize();
    }

    //method utk menginisiasi semua objek yang ada pada halaman ini
    private void initialize(){
        toolbar = getActivity().findViewById(R.id.toolbar_sell);
        setToolbar();
        bottomNavigationView = getActivity().findViewById(R.id.bottomNavBar);
        bottomNavigationView.setVisibility(View.GONE);

        cardView = getActivity().findViewById(R.id.cv_image_sell);
        imageView = getActivity().findViewById(R.id.iv_image_sell);
        imageButton = getActivity().findViewById(R.id.btn_add_image_sell);
        btnPost = getActivity().findViewById(R.id.btn_finish_sell);
        kategori = getActivity().findViewById(R.id.spin_kategori);
        kondisi = getActivity().findViewById(R.id.spin_kondisi);
        hapus_image = getActivity().findViewById(R.id.hapus_foto);

        et_kategori = getActivity().findViewById(R.id.et_kategori_sell);
        et_nama = getActivity().findViewById(R.id.et_nama_sell);
        et_alamat = getActivity().findViewById(R.id.et_alamat_sell);
        et_kondisi = getActivity().findViewById(R.id.et_kondisi_sell);
        et_stok = getActivity().findViewById(R.id.et_stok_sell);
        et_harga = getActivity().findViewById(R.id.et_harga_sell);
        et_deskripsi = getActivity().findViewById(R.id.et_deskripsi_sell);

        et_harga.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            private String current = "";
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                //proses utk melakukan format edit text menjadi format USD
                if (!s.toString().equals(current)) {
                    et_harga.removeTextChangedListener(this);

                    Locale local = new Locale("en", "US");
                    String replaceable = String.format("[$,.]",
                            NumberFormat.getCurrencyInstance().getCurrency()
                                    .getSymbol(local));
                    String cleanString = s.toString().replaceAll(replaceable,
                            "");

                    double parsed;
                    try {
                        parsed = Double.parseDouble(cleanString);
                    } catch (NumberFormatException e) {
                        parsed = 0.00;
                    }

                    NumberFormat formatter = NumberFormat
                            .getCurrencyInstance(local);
                    formatter.setMaximumFractionDigits(0);
                    formatter.setParseIntegerOnly(true);
                    String formatted = formatter.format((parsed));

                    current = formatted;
                    et_harga.setText(formatted);
                    et_harga.setSelection(formatted.length());
                    et_harga.addTextChangedListener(this);
                }
            }
        });

        //konten utk dropdown kategori barang
        String[] entriesKategori = new String[]{
                "Kategori barang", "Electronic", "Furniture", "Vegetable", "Fruit", "Snack", "Beverage", "Appliance", "Sport"
        };

        //proses pengassignan konten dropdown kedalam dropdown adapter
        ArrayAdapter<String> spinnerAdapterKategori = new ArrayAdapter<String>(getActivity(), R.layout.spinner_item, entriesKategori);
        spinnerAdapterKategori.setDropDownViewResource(R.layout.spinner_item);
        kategori.setAdapter(spinnerAdapterKategori);
        kategori.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String tipe = (String) parent.getItemAtPosition(position);
                if(!tipe.equalsIgnoreCase("Kategori barang")){
                    et_kategori.setText(tipe);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        String[] entriesKondisi = new String[]{
                "Kondisi barang", "New", "Preloved"
        };

        ArrayAdapter<String> spinnerAdapterKondisi = new ArrayAdapter<String>(getActivity(), R.layout.spinner_item, entriesKondisi);
        spinnerAdapterKondisi.setDropDownViewResource(R.layout.spinner_item);
        kondisi.setAdapter(spinnerAdapterKondisi);
        kondisi.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String tipe = (String) parent.getItemAtPosition(position);
                if(!tipe.equalsIgnoreCase("Kondisi barang")){
                    et_kondisi.setText(tipe);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initializeDialogTakeImage();
            }
        });

        //proses ketika textview hapus image ditekan, maka textview hapus image akan hilang, foto akan hilang, dan tombol tambah foto akan muncul
        hapus_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hapus_image.setVisibility(View.GONE);
                imageButton.setVisibility(View.VISIBLE);
                imageView.setVisibility(View.GONE);
            }
        });

        produkRefs = FirebaseDatabase.getInstance().getReference().child("Product");
        userRefs = FirebaseDatabase.getInstance().getReference().child("User").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        //proses pengambilan data user berupa uid dan juga nama
        userRefs.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                uid = dataSnapshot.getKey();
                by = dataSnapshot.child("name").getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        imageRefs = FirebaseStorage.getInstance().getReference().child("Product").child("ProductImage").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //kondisi pengecekan apakah foto sudah terpilih atau belum
                if (filePath != null){
                    //kondisi pengecekan apakah semua inputan sudah diisi atau belum
                    if(TextUtils.isEmpty(et_alamat.getText().toString()) && TextUtils.isEmpty(et_kategori.getText().toString()) && TextUtils.isEmpty(et_nama.getText().toString()) &&
                            TextUtils.isEmpty(et_kondisi.getText().toString()) && TextUtils.isEmpty(et_harga.getText().toString()) && TextUtils.isEmpty(et_deskripsi.getText().toString()) &&
                            TextUtils.isEmpty(et_stok.getText().toString())){
                        Toast.makeText(getActivity(), "Please complete all information of product first", Toast.LENGTH_SHORT).show();
                    }else{
                        produkRefs.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                Locale local = new Locale("en", "US");
                                String replace = String.format("[$,]", NumberFormat.getCurrencyInstance().getCurrency().getSymbol(local));
                                String clean = et_harga.getText().toString().replaceAll(replace, "");

                                HashMap produkMap = new HashMap();
                                produkMap.put("nama_produk",et_nama.getText().toString());
                                produkMap.put("kategori",et_kategori.getText().toString());
                                produkMap.put("alamat",et_alamat.getText().toString());
                                produkMap.put("kondisi",et_kondisi.getText().toString());
                                produkMap.put("deskripsi",et_deskripsi.getText().toString());
                                produkMap.put("stok",Integer.valueOf(et_stok.getText().toString()));
                                produkMap.put("harga",Integer.valueOf(clean));
                                produkMap.put("rating",0);
                                produkMap.put("imageURL","-");
                                produkMap.put("report",0);
                                produkMap.put("by",by);
                                produkMap.put("uid",uid);
                                produkMap.put("numOfRating",0);
                                idProduct = String.valueOf(dataSnapshot.getChildrenCount());
                                //proses utk menambahkan produk kedalam firebase database
                                produkRefs.child(idProduct).updateChildren(produkMap).addOnCompleteListener(new OnCompleteListener() {
                                    @Override
                                    public void onComplete(@NonNull Task task) {
                                        uploadImage();
                                    }
                                });
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                }else{
                    Toast.makeText(getActivity(), "Please insert the image of product first", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //method utk menampilkan pesan dialog ketika button post ditekan dan muncul alert berupa produk berhasil di post
    private void initializeDialogPosted(){
        android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(getActivity(),R.style.CustomAlertDialog);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_posted,null);
        dialogView.setBackgroundColor(Color.TRANSPARENT);
        dialog.setView(dialogView);
        dialog.setCancelable(false);
        dialog.setTitle(null);

        dialog.setPositiveButton("OKAY", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                HomeFragment homeFragment = new HomeFragment();
                setFragment(homeFragment);
            }
        });

        dialog.show();
    }

    //method utk menampilkan pilihan ketika memilih foto, apakah ingin mengambil dari galeri atau dari camera langsung
    private void initializeDialogTakeImage(){
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

    //method utk proses upload image dan juga upload link download image
    private void uploadImage(){
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle("Uploading...");
        progressDialog.show();
        progressDialog.setCancelable(false);
        imageRefs.child(idProduct).child("productImage.png").putFile(filePath)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        progressDialog.dismiss();
                        imageRefs.child(idProduct).child("productImage.png").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                produkRefs.child(idProduct).child("imageURL").setValue(uri.toString());
                                initializeDialogPosted();
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

    //method utk menampilkan pesan dialog ketika tombol back ditekan
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
                HomeFragment homeFragment = new HomeFragment();
                setFragment(homeFragment);
            }
        });

        alertDialog2.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog2.show();

    }

    //method utk memeriksa apakah device telah memberikan privilege utk aplikasi dalam menggunakan camera atau tidak, jika sudah maka akan ditampilkan halaman camera
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

    //method penurunan dari intent, untuk menangkap hasil dari activitynya, dalam hal ini image
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //proses menampilkan image hasil pilihan user ke dalam halaman
        if (requestCode == GALLERY && resultCode == RESULT_OK) {

            try {
                filePath = data.getData();
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), filePath);
                Bitmap newbitMap = Bitmap.createScaledBitmap(bitmap, cardView.getWidth(), cardView.getHeight(), true);
                imageView.setImageBitmap(newbitMap);
                imageView.setVisibility(View.VISIBLE);
                hapus_image.setVisibility(View.VISIBLE);
                imageButton.setVisibility(View.GONE);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        if (requestCode == CAMERAA && resultCode == RESULT_OK) {

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), filePath);
                Bitmap newbitMap = Bitmap.createScaledBitmap(bitmap, cardView.getWidth(), cardView.getHeight(), true);
                imageView.setImageBitmap(newbitMap);
                imageView.setVisibility(View.VISIBLE);
                hapus_image.setVisibility(View.VISIBLE);
                imageButton.setVisibility(View.GONE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //method utk mengubah dari abstract path menjadi absolute path
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
