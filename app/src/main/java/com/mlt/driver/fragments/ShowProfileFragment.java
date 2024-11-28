package com.mlt.driver.fragments;

import androidx.fragment.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import com.mlt.driver.R;
import com.mlt.driver.helper.SharedPreferencesManager;
import android.widget.Button;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import de.hdodenhof.circleimageview.CircleImageView;

public class ShowProfileFragment extends Fragment {

    private static final int GALLERY_REQUEST_CODE = 1;
    private static final int CAMERA_REQUEST_CODE = 2;
    private static final int STORAGE_PERMISSION_REQUEST_CODE = 3;
    private CircleImageView profileImage;
    private static final int PICK_IMAGE_REQUEST = 1;
    private SharedPreferencesManager sharedPreferencesManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);

        // Initialize Views
        profileImage = view.findViewById(R.id.profileImage);
        Button btnUploadImg = view.findViewById(R.id.btnUploadimg);
        TextView txtUserName = view.findViewById(R.id.txtUserName);
        TextView txtEmail = view.findViewById(R.id.txtEmail);
        TextView txtPhone = view.findViewById(R.id.txtPhone);
        TextView txtAddress = view.findViewById(R.id.txtAddress);

        // Initialize sharedPreferencesManager
        sharedPreferencesManager = new SharedPreferencesManager(getContext());

        // Set data from SharedPreferences
        txtUserName.setText(sharedPreferencesManager.getUserName());
        txtEmail.setText(sharedPreferencesManager.getEmail());
        txtPhone.setText(sharedPreferencesManager.getPhone());
        txtAddress.setText(sharedPreferencesManager.getAddress());

        // Load the profile image if available in SharedPreferences
        String profileImagePath = sharedPreferencesManager.getProfileImageUrl();
        if (!profileImagePath.isEmpty()) {
            File imgFile = new  File(profileImagePath);
            if(imgFile.exists()) {
                Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                profileImage.setImageBitmap(bitmap);
            }
        }
        // Set ClickListener for the card (Card2 as per your code)
        LinearLayout card2 = view.findViewById(R.id.card2);
        card2.setOnClickListener(v -> {
            // Show Toast message when card2 is clicked
            Toast.makeText(getContext(), "Contact Register to Change Details", Toast.LENGTH_SHORT).show();
        });
        // Handle Image Upload (Camera/Gallery)
        view.findViewById(R.id.btnUploadimg).setOnClickListener(v -> showImagePickerDialog());

        return view;
    }
    // Show Image Picker Dialog (Camera or Gallery)
    private void showImagePickerDialog() {
        String[] options = {"Camera", "Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setItems(options, (dialog, which) -> {
            if (which == 0) {
                // Open Camera
                checkCameraPermission();
            } else {
                // Open Gallery
                checkGalleryPermission();
            }
        });
        builder.show();
    }
    // Handle Image Picker Results (Camera or Gallery)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == getActivity().RESULT_OK) {
            Uri imageUri = data.getData();
            if (requestCode == CAMERA_REQUEST_CODE) {
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                profileImage.setImageBitmap(bitmap);
                saveImageToInternalStorage(bitmap);
            } else if (requestCode == GALLERY_REQUEST_CODE && data != null) {
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
                    profileImage.setImageBitmap(bitmap);
                    saveImageToInternalStorage(bitmap);
                    // You can save the image URL in SharedPreferences if needed
                    // sharedPreferencesManager.saveProfileImageUrl(imageUri.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
    // Check Camera Permission
    public void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.CAMERA)
                != android.content.pm.PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted, request permission
            requestPermissions(new String[]{android.Manifest.permission.CAMERA}, 1); // 1 is the request code
        } else {
            // Permission already granted, proceed with camera intent
            openCamera();
        }
    }

    // Check Gallery Permission (READ_EXTERNAL_STORAGE for Android versions below Android 11)
    public void checkGalleryPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != android.content.pm.PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted, request permission
            requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 2); // 2 is the request code
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (android.os.Environment.isExternalStorageManager()) {
                // Permission is granted, open gallery
                openGallery();
            } else {
                // Request permission to manage all files
                Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                startActivityForResult(intent, STORAGE_PERMISSION_REQUEST_CODE);
            }
        }
    }

    // Handle the result of permission request
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with camera intent
                openCamera();
            } else {
                // Permission denied, show a message to the user
                Toast.makeText(requireContext(), "Camera permission is required", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Open Camera
    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE); // CAMERA_REQUEST_CODE is the request code
    }

    // Open Gallery
    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE); // GALLERY_REQUEST_CODE is the request code
    }
    // Method to save image to internal storage
    private void saveImageToInternalStorage(Bitmap bitmap) {
        try {
            // Get the path where you want to save the image
            File directory = getContext().getFilesDir();  // Internal storage directory
            File file = new File(directory, "profile_image.jpg");  // Image file name

            // Create a FileOutputStream to write the bitmap to file
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);  // Compress and save the image
            fileOutputStream.close();

            // Save the file path in SharedPreferences
            sharedPreferencesManager.saveProfileImageUrl(file.getAbsolutePath());

            // You can now use the saved file as needed
            Toast.makeText(getContext(), "Image saved successfully", Toast.LENGTH_SHORT).show();

            // Optionally, update SharedPreferences to store the file path or URI
            // sharedPreferencesManager.saveProfileImageUrl(file.getAbsolutePath());

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Failed to save image", Toast.LENGTH_SHORT).show();
        }
    }
    public ShowProfileFragment() {
        // Required empty public constructor
    }
}

