package com.example.viewpagerdemo;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.wajahatkarim3.easyflipview.EasyFlipView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class CardFragment extends Fragment {

    protected View fragView;
    protected Context fragContext;

    private RecyclerView recyclerView;
    private RecyclerAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    ImageView selectImage;
    ImageView outputImage;
    Button captureBtn;
    Button shareBtn;
    Button storageBtn;

    public static TextView nameCard;
    public static TextView phoneCard;
    public static TextView addressCard;

    EasyFlipView easyFlipView;
    ImageView userImage;
    RelativeLayout layoutToCapture;

    private int[] images = {
            R.drawable.flower,
            R.drawable.milkyway,
            R.drawable.toystory,
            R.drawable.tree
    };

    private OnFragmentInteractionListener mListener;

    public CardFragment() {
        // Required empty public constructor
    }

    public static CardFragment newInstance() {
        CardFragment fragment = new CardFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        } else {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_card, container, false);
        this.fragView = view;
        this.fragContext = getContext();


        layoutManager = new GridLayoutManager(fragContext, 1, GridLayoutManager.HORIZONTAL, false);

        recyclerView = (RecyclerView) fragView.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new RecyclerAdapter(images);
        recyclerView.setAdapter(adapter);

        selectImage = (ImageView) fragView.findViewById(R.id.cardImage);
        nameCard = fragView.findViewById(R.id.nameCard);
        phoneCard = fragView.findViewById(R.id.phoneCard);
        addressCard = fragView.findViewById(R.id.addressCard);
        userImage = fragView.findViewById(R.id.userImage);
        layoutToCapture = (RelativeLayout) fragView.findViewById(R.id.relativeLayout);

        adapter.setOnItemClickListener(new RecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                selectImage.setImageResource(images[position]);
                selectImage.setVisibility(View.VISIBLE);
            }
        });

        setCardText();
        setUserImage();

        easyFlipView = fragView.findViewById(R.id.easyFilpView);
        easyFlipView.flipTheView();

        easyFlipView.setOnFlipListener(new EasyFlipView.OnFlipAnimationListener() {
            @Override
            public void onViewFlipCompleted(EasyFlipView flipView, EasyFlipView.FlipState newCurrentSide)
            {
                if(flipView.isBackSide()) {
                    nameCard.setVisibility(View.VISIBLE);
                    phoneCard.setVisibility(View.VISIBLE);
                    addressCard.setVisibility(View.VISIBLE);
                } else {
                    nameCard.setVisibility(View.GONE);
                    phoneCard.setVisibility(View.GONE);
                    addressCard.setVisibility(View.GONE);
                }
            }
        });

        captureBtn = (Button) fragView.findViewById(R.id.capture);
        captureBtn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                takeScreenshot();
            }
        });

        storageBtn = (Button) fragView.findViewById(R.id.storage);
        storageBtn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent launchIntent = getActivity().getPackageManager().getLaunchIntentForPackage("com.sec.android.app.myfiles");
                if (launchIntent != null) {
                    startActivity(launchIntent); //null pointer check in case package name was not found
                } else {
                    Toast.makeText(fragContext, "File manager unavailable", Toast.LENGTH_SHORT).show();
                }
//                Intent intent = new Intent();
//                intent.setAction(Intent.ACTION_GET_CONTENT);
//                intent.setType("image/*");
//                startActivity(intent);

            }
        });

        return view;
    }

    private void takeScreenshot() {
        Date now = new Date();
        android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);

        try {
            // image naming and path  to include sd card  appending name you choose for file
           final String mPath = Environment.getExternalStorageDirectory().toString() + "/DCIM/Camera/" + now + ".jpg";


            layoutToCapture.setDrawingCacheEnabled(true);
            final Bitmap bitmap = Bitmap.createBitmap(layoutToCapture.getDrawingCache());
            layoutToCapture.setDrawingCacheEnabled(false);

            final File imageFile = new File(mPath);

            if(ActivityCompat.checkSelfPermission(fragContext,Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                FileOutputStream outputStream = new FileOutputStream(imageFile);
                int quality = 100;
                bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
                outputStream.flush();
                outputStream.close();

                Toast.makeText(fragContext, "Captured @ " +  mPath, Toast.LENGTH_SHORT).show();
            } else {
                Dexter.withActivity(getActivity())
                        .withPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .withListener(new MultiplePermissionsListener() {
                            @Override
                            public void onPermissionsChecked(MultiplePermissionsReport report) {
                                if (report.areAllPermissionsGranted()) {
                                    FileOutputStream outputStream = null;
                                    try {
                                        outputStream = new FileOutputStream(imageFile);
                                        int quality = 100;
                                        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
                                        outputStream.flush();
                                        outputStream.close();

                                        Toast.makeText(fragContext, "Captured @ " +  mPath, Toast.LENGTH_SHORT).show();

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                } else if (report.isAnyPermissionPermanentlyDenied()) {
                                    Toast.makeText(fragContext, "PERMISSION DENIED", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                                token.continuePermissionRequest();
                            }
                        }).check();
            }
        } catch (Throwable e) {
            // Several error may come out with file handling or DOM
            e.printStackTrace();
        }
    }

    private void openScreenshot(File imageFile) {

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getPath(), options);

        // outputImage.setImageBitmap(bitmap);
    }

    private void setCardText() {
        if(MainActivity.contactName != null) {
            nameCard.setText(MainActivity.contactName);
        }
        if(MainActivity.contactPhone != null) {
            phoneCard.setText(MainActivity.contactPhone);
        }
        if(MainActivity.contactAddress != null) {
            addressCard.setText(MainActivity.contactAddress);
        }
    }

    private  void setUserImage() {
        if(MainActivity.selUri != null) {
            Glide.with(this).load(MainActivity.selUri).into(userImage);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        setCardText();
        setUserImage();
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
