package com.example.viewpagerdemo;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
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
import java.util.Set;
import java.util.UUID;


public class CardFragment extends Fragment {

    public static final String START_SENDING_IMAGE = "START SENDING IMAGE";
    public static final String END_SENDING_IMAGE = "END SENDING IMAGE";

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


    private static final int READ_PERMISION_REQ_CODE = 151;
    private static final int WRITE_PERMISSION_REQ_CODE = 416;
    private static final int REQUEST_ENABLE_BT = 784;

    private BluetoothManager mBluetoothManager = null;

    private static final UUID MY_UUID_SECURE = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private BluetoothAdapter bluetoothAdapter; // 블루투스 어댑터
    private Set<BluetoothDevice> deviceSet; // 블루투스 디바이스 데이터 셋
    private BluetoothDevice bluetoothDevice; // 블루투스 디바이스
    private int pairedDeviceNum;

    String mConnectedDeviceName;
    String mConnectedDeviceAddress;

    static String receivedBitmapString = "";


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

        shareBtn = (Button) fragView.findViewById(R.id.share);
        shareBtn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareScreenshot();
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

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Toast.makeText(fragContext, "NO BLUETOOTH ADAPTER", Toast.LENGTH_SHORT).show();
        } else {
            if (!bluetoothAdapter.isEnabled()) {
                Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(intent, REQUEST_ENABLE_BT);
            } else if (mBluetoothManager == null) {
                setupChat();
            }
        }

        return view;
    }

    private void shareScreenshot() {
        selectBluetoothDevice();
    }

    private void setupChat() {
        // Initialize the BluetoothChatService to perform bluetooth connections
        mBluetoothManager = new BluetoothManager(fragContext, mHandler);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mBluetoothManager != null) {
            mBluetoothManager.stop();
        }
    }

    @SuppressLint("HandlerLeak")
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothManager.STATE_CONNECTED:
                            break;
                        case BluetoothManager.STATE_CONNECTING:
                            break;
                        case BluetoothManager.STATE_LISTEN:
                        case BluetoothManager.STATE_NONE:
                            break;
                    }
                    break;
                case Constants.MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    // construct a string from the buffer
                    String writeMessage = new String(writeBuf);

                    Log.i("***BT", writeMessage);
                    // textViewReceive.setText("ME: " + writeMessage);

                    break;
                case Constants.MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the bufferl
                    String readMessage = new String(readBuf, 0, msg.arg1);

                    Log.i("***BT", readMessage);

                    String[] words = readMessage.split(";");
                    nameCard.setText(words[0]);
                    phoneCard.setText(words[1]);
                    addressCard.setText(words[2]);

//                    if(readMessage.substring(readMessage.lastIndexOf("\n") + 1).equals(END_SENDING_IMAGE)) {
//                        receivedBitmapString.concat(readMessage.substring(0, readMessage.lastIndexOf("\n")));
//
//                        Intent i = new Intent(fragContext, ImageFromRemote.class);
//                        i.putExtra("imgStr",receivedBitmapString);
//                        startActivity(i);
//
//                        Log.i("***BT", receivedBitmapString);
//                        receivedBitmapString = "";
//
//                    } else {
//                        receivedBitmapString.concat(readMessage);
//                    }
                    break;

                case Constants.MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    mConnectedDeviceName = msg.getData().getString(Constants.DEVICE_NAME);
                    mConnectedDeviceAddress = msg.getData().getString(Constants.DEVICE_ADDRESS);

                    if (null != this) {
                        Toast.makeText(fragContext, "Connected to " + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                    }
                    break;
                case Constants.MESSAGE_TOAST:
                    break;
            }
        }
    };

    private void sendMessage(String message) {

        // Check that there's actually something to send
        if (message.length() > 0) {
            // Get the message bytes and tell the BluetoothChatService to write
            byte[] send = message.getBytes();
            mBluetoothManager.write(send);
        }
    }

    private String getDeviceProperty(BluetoothDevice device) {
        return device.getName() +": " + device.getAddress();
    }

    private String getDeviceProperty(String name, String addr) {
        return name +": " + addr;
    }

    public void selectBluetoothDevice() {

        // 이미 페어링 되어있는 블루투스 기기를 찾습니다.
        deviceSet = bluetoothAdapter.getBondedDevices();

        // 페어링 된 디바이스의 크기를 저장
        pairedDeviceNum = deviceSet.size();

        // 페어링 되어있는 장치가 없는 경우
        if (pairedDeviceNum == 0) {
            // 페어링을 하기위한 함수 호출
            AlertDialog.Builder builder = new AlertDialog.Builder(fragContext);

            builder.setTitle("페어링 되어있는 블루투스 디바이스가 없습니다");
            builder.setPositiveButton("알겠소", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // 그냥 종료
                }
            });

            // 뒤로가기 버튼 누를 때 창이 안닫히도록 설정
            // builder.setCancelable(false);

            // 다이얼로그 생성
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        } else {
            // 디바이스를 선택하기 위한 다이얼로그 생성
            AlertDialog.Builder builder = new AlertDialog.Builder(fragContext);
            builder.setTitle("Device List");

            // 페어링 된 각각의 디바이스의 이름과 주소를 저장
            final List<String> list = new ArrayList<>();

            // 모든 디바이스의 이름을 리스트에 추가
            for (BluetoothDevice tmpDevice : deviceSet) {
                list.add(getDeviceProperty(tmpDevice));
            }

            list.add("취소");

            final int listSize = list.size();

            // List를 CharSequence 배열로 변경
            final CharSequence[] charSequences = list.toArray(new CharSequence[listSize]);

            // 해당 아이템을 눌렀을 때 호출 되는 이벤트 리스너
            builder.setItems(charSequences, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // 취소시 리턴
                    if (which == (listSize-1)) {
                        return;
                    } else {
                        connectDevice(charSequences[which].toString());
                    }
                }
            });

            // 뒤로가기 버튼 누를 때 창이 안닫히도록 설정
            builder.setCancelable(false);

            // 다이얼로그 생성
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
    }

    public void connectDevice(String deviceProperty) {

        if((mConnectedDeviceAddress != null) && (getDeviceProperty(mConnectedDeviceName, mConnectedDeviceAddress).equals(deviceProperty))) {
            Log.i("***EE", "JUST SEND IT!");
            SendJSON();
            return;
        }

        // 페어링 된 디바이스들을 모두 탐색
        for (BluetoothDevice tempDevice : deviceSet) {
            // 사용자가 선택한 이름과 같은 디바이스로 설정하고 반복문 종료
            if (deviceProperty.equals(getDeviceProperty(tempDevice))) {
                bluetoothDevice = tempDevice;
                break;
            }
        }
        mBluetoothManager.connect(bluetoothDevice, true);
        SendJSON();
    }

    public void SendJSON() {
        if(bluetoothDevice != null) {
            mConnectedDeviceName = bluetoothDevice.getName();
            mConnectedDeviceAddress = bluetoothDevice.getAddress();
        }

        // 페어링을 하기위한 함수 호출
        AlertDialog.Builder builder = new AlertDialog.Builder(fragContext);

        builder.setTitle("명함 전송");
        builder.setMessage("Name: " + mConnectedDeviceName + "\nAddress: " + mConnectedDeviceAddress);
        builder.setPositiveButton("전송", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                sendMessage(MainActivity.contactName + ";" + MainActivity.contactPhone + ";" + MainActivity.contactAddress);
//                layoutToCapture.setDrawingCacheEnabled(true);
//                final Bitmap bitmap = Bitmap.createBitmap(layoutToCapture.getDrawingCache());
//                layoutToCapture.setDrawingCacheEnabled(false);
               // sendMessage(START_SENDING_IMAGE);
//                sendMessage(ObjectSerializer.BitMapToString(bitmap));
//                sendMessage(END_SENDING_IMAGE);
            }
        });


        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 그냥 종료
            }
        });

        // 뒤로가기 버튼 누를 때 창이 안닫히도록 설정
        // builder.setCancelable(false);

        // 다이얼로그 생성
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
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

        // Performing this check in onResume() covers the case in which BT was
        // not enabled during onStart(), so we were paused to enable it...
        // onResume() will be called when ACTION_REQUEST_ENABLE activity returns.
        if (mBluetoothManager != null) {
            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (mBluetoothManager.getState() == mBluetoothManager.STATE_NONE) {
                // Start the Bluetooth chat services
                mBluetoothManager.start();
            }
        }

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
