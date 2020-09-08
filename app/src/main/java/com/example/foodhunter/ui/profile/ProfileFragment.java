package com.example.foodhunter.ui.profile;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.Glide;
import com.example.foodhunter.R;
import com.example.foodhunter.keys.StaticData;
import com.example.foodhunter.models.User;
import com.example.foodhunter.sessions.LocalSessionStore;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mikhaellopez.circularimageview.CircularImageView;
import static android.app.Activity.RESULT_OK;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class ProfileFragment extends Fragment {
    ProgressDialog progressDialog;
    CircularImageView iv;
    TextInputEditText edName,edEmail,edContact, edAddress;
    FloatingActionButton fab;
    private TextView txtCamera,txtGallery;
    ImageButton btnSave;
    Button uploadAddressBtn;
    private ProfileViewModel profileViewModel;
    private FirebaseFirestore db;
    private StorageReference mStorageRef;
    Uri path;
    Uri downloadUri, uri;
    BottomSheetDialog bottomSheetDialog;
    String currentUserId;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        profileViewModel =
                ViewModelProviders.of(this).get(ProfileViewModel.class);
        View root = inflater.inflate(R.layout.fragment_profile, container, false);
        iv = root.findViewById(R.id.profileImage);
        btnSave = root.findViewById(R.id.uploadBtn);
        edName = root.findViewById(R.id.edName);
        edEmail = root.findViewById(R.id.edEmail);
        edContact = root.findViewById(R.id.edContact);
        edAddress = root.findViewById(R.id.edAddress);
        uploadAddressBtn = root.findViewById(R.id.uploadAddress);
        fab = root.findViewById(R.id.floatingActionButton);
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Loading...");
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        db = FirebaseFirestore.getInstance();
        currentUserId = new LocalSessionStore(getContext()).getData(StaticData.USER_ID);
        showData(currentUserId);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnSave.setVisibility(View.VISIBLE);
                bottomSheetDialog = new BottomSheetDialog(getContext());
                View modal = getLayoutInflater().inflate(R.layout.modal_layout,null);
                bottomSheetDialog.setContentView(modal);
                bottomSheetDialog.show();
                txtCamera = bottomSheetDialog.findViewById(R.id.txtOpenCamera);
                txtGallery = bottomSheetDialog.findViewById(R.id.txtChooseGallery);
                callM();
//                ModalBottomScreen modal = new ModalBottomScreen();
//                modal.show(getFragmentManager().beginTransaction(),"Choose one option");
            }
        });
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mStorageRef = FirebaseStorage.getInstance().getReference();
                uploadData();
            }
        });
        uploadAddressBtn.setOnClickListener(new View.OnClickListener() {
            Dialog addressDialog = new Dialog(getContext());
            @Override
            public void onClick(View view) {
                addressDialog.setContentView(R.layout.address_dialog);
                Button btnSaveAddress = addressDialog.findViewById(R.id.saveAddressBtn);
                Button btnDismissAddressModal = addressDialog.findViewById(R.id.cancelAddressModal);
                final EditText editedAddress = addressDialog.findViewById(R.id.editedAddress);
                editedAddress.setText(String.valueOf(edAddress.getText()));
                addressDialog.show();
                btnDismissAddressModal.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        addressDialog.dismiss();
                    }
                });
                btnSaveAddress.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        updateAddressInDb(String.valueOf(editedAddress.getText()));
                        addressDialog.dismiss();
                    }
                });
            }
        });
        return root;
    }

    private void updateAddressInDb(String address) {
        final ProgressDialog pdAddress = new ProgressDialog(getContext());
        pdAddress.setTitle("Please wait...");
        pdAddress.setMessage("Loading...");
        pdAddress.setCancelable(false);
        pdAddress.show();
        db.collection("users").document(currentUserId).update(StaticData.USER_ADDRESS,address).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                pdAddress.dismiss();
                if(task.isSuccessful()) {
                    Toast.makeText(getContext(), "Address updated successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Please try again", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pdAddress.dismiss();
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void uploadData() {
        if(path != null) {
            final ProgressDialog pd = new ProgressDialog(getContext());
            pd.setTitle("Uploading...");
            pd.setCancelable(false);
            pd.show();
            final StorageReference ref = mStorageRef.child("userDp/" + UUID.randomUUID().toString());
            ref.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(getContext(), "Image uploaded", Toast.LENGTH_LONG).show();
                    taskSnapshot.getStorage().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if(task.isSuccessful()) {
                                downloadUri = task.getResult();
                                if(downloadUri.toString() == null) {
                                    Toast.makeText(getContext(), "Please select an image", Toast.LENGTH_SHORT).show();
                                } else {
                                    updateDB();
                                }
                                pd.dismiss();
                            }
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    pd.dismiss();
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                    pd.setMessage("Uploaded " + (int)progress + "%");
                    Toast.makeText(getContext(), "Uploaded " + (int) progress + "%", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void updateDB() {
        Log.d("ProfileFragment","COMING IN UPDATE DB");
        db.collection("users").document(currentUserId).update(StaticData.USER_DP,downloadUri.toString())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            Toast.makeText(getContext(), "Uploaded successfully", Toast.LENGTH_SHORT).show();
                            btnSave.setVisibility(View.INVISIBLE);
                        }else {
                            Toast.makeText(getContext(), "Please try again", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void callM() {
        txtCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent camIn = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                File mediaStorageDir = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "Camera");
                if(!mediaStorageDir.exists()) {
                    if(mediaStorageDir.mkdir())
                        Log.d("ProfileFragment", "Created "+ mediaStorageDir);
                }

                String random = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
                File cameraPic = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + random + ".jpg");

                path = FileProvider.getUriForFile(getContext(), "com.example.foodhunter.provider", cameraPic);
                camIn.putExtra(MediaStore.EXTRA_OUTPUT,path);


                startActivityForResult(camIn,2020);
                bottomSheetDialog.dismiss();
            }
        });
        txtGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("*/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Image"),200);
                bottomSheetDialog.dismiss();
            }
        });
    }

    private void showData(final String userId) {
        db.collection("users").document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    Map<String,Object> map = documentSnapshot.getData();
                    User user = new User(
                      String.valueOf(map.get(StaticData.USER_NAME)),
                      String.valueOf(map.get(StaticData.USER_EMAIL)),
                      String.valueOf(map.get(StaticData.USER_CONTACT)),
                      String.valueOf(map.get(StaticData.USER_DP)),
                      (boolean)map.get(StaticData.IS_ADMIN),
                       String.valueOf(map.get(StaticData.USER_ADDRESS))
                    );
                    edName.setText(user.getUserName());
                    edContact.setText(user.getUserContact());
                    edEmail.setText(user.getUserEmail());
                    edAddress.setText(user.getUserAddress());
                    //Log.d("ProfileFragment", String.valueOf(map));
                    if(!user.getUserDp().isEmpty())
                    {
                        Glide.with(getContext()).load(user.getUserDp()).into(iv);
                    }
                }else {
                    Toast.makeText(getContext(), "Something went wrong!", Toast.LENGTH_LONG).show();
                }
                progressDialog.dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
            }
        });
    }



//    @Override
//    public void onButtonClicked(int text) {
//        if (text == R.string.choose_to_select_from_gallery) {
//            Intent intent = new Intent();
//            intent.setType("*/*"); //specifying the type of file to access
//            intent.setAction(Intent.ACTION_GET_CONTENT); //specifying the action to perform
//            startActivityForResult(Intent.createChooser(intent,"Select image"),200);
//        }else {
//            Intent camIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//            startActivityForResult(camIntent,2020);
//        }
//    }
    /**
     * accessing the selected file
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 2020 && resultCode == RESULT_OK) {
//            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
//            iv.setImageBitmap(bitmap);
            //path = data.getData();

            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(),path);
            } catch (IOException e) {
                e.printStackTrace();
            }
            iv.setImageBitmap(bitmap);
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,bytes);
            String path1 = MediaStore.Images.Media.insertImage(getContext().getContentResolver(),bitmap,null,null);
            uri = Uri.parse(path1);
//            Log.d("ProfileFragment", path.toString());
        }else{
            if(data != null && data.getData() != null) {
                path = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(),path);
                    iv.setImageBitmap(bitmap);
                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG,100,bytes);
                    String path1 = MediaStore.Images.Media.insertImage(getContext().getContentResolver(),bitmap,null,null);
                    uri = Uri.parse(path1);
                }catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}