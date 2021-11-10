package com.example.recognize;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


import com.example.recognize.utils.AuthService;
import com.example.recognize.utils.ContentUtil;
import com.example.recognize.utils.FileUtil;
import com.example.recognize.utils.TextRecognition;
import com.hjq.permissions.OnPermission;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;

import java.io.ByteArrayOutputStream;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "DEBUG";
    private static final int LOADING = 0x00;
    private static final int OK = 0x01;
    private static final int universal = 0x02;
    private static final int card = 0x03;
    private static final int certificate = 0x04;
    private static final int handwrite = 0x05;
    private static final int netImage = 0x06;
    private static final int CAMERA = 0x07;

    private View container;
    private ImageView icon;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Attempt to request network and storage permissions at boot time
        tryApplyPermission();

        container = findViewById(R.id.main);
        icon = findViewById(R.id.show);
    }

    public void tryApplyPermission() {
        XXPermissions
                .with(this)
                .permission(Permission.MANAGE_EXTERNAL_STORAGE,Permission.CAMERA)
                .request(new OnPermission() {
                    @Override
                    public void hasPermission(List<String> granted, boolean all) {
                        if (all) {
                        } else {
                            Toast.makeText(MainActivity.this, granted.toString() + "Permissions have been granted for the time being!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void noPermission(List<String> denied, boolean never) {
                        if (never) {
                            Toast.makeText(MainActivity.this, denied.toString() + "Permissions have been permanently disabled, please grant app storage permissions in order to ensure proper use of the app!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    public void chooseFile(View view) {
        findViewById(R.id.universal_word_recognition).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupWindow pw = new PopupWindow(v, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
                View pwView = LayoutInflater.from(v.getContext()).inflate(R.layout.pw, null,false);
                pw.setContentView(pwView);
                pw.setTouchable(true);

                View camera = pwView.findViewById(R.id.camera);
                View image = pwView.findViewById(R.id.images);

                pw.setBackgroundDrawable(new ColorDrawable(0x00000000));
                pw.showAsDropDown(v, 0, 0, Gravity.BOTTOM);


                camera.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(intent, CAMERA);
                    }
                });

                image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                        intent.setType("image/*");
                        startActivityForResult(intent, universal);
                    }
                });

            }
        });


//        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//        intent.setType("image/*");
//        switch (view.getId()) {
//            case R.id.universal_word_recognition:
//                startActivityForResult(intent, universal);
//                break;
//            case R.id.card_recognition:
//                startActivityForResult(intent, card);
//                break;
//            case R.id.certificates_recognition:
//                startActivityForResult(intent, certificate);
//                break;
//            case R.id.hand_write_recognition:
//                startActivityForResult(intent, handwrite);
//                break;
//            default:
//                break;
//        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 0:
                if (grantResults.length > 0) {
                    Toast.makeText(this, "Storage access has been granted!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Failed to get storage access!", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            QMUITipDialog loading = new QMUITipDialog
                    .Builder(this)
                    .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                    .setTipWord("text detecting...")
                    .create();
            final Bitmap[] bitmap = new Bitmap[1];

            Handler handler = new Handler(Looper.myLooper()) {
                @Override
                public void handleMessage(@NonNull Message msg) {
                    if (msg.what == LOADING) {
                        loading.show();
                    } else {
                        loading.dismiss();
                    }
                }
            };

            Intent intent = new Intent(MainActivity.this, ResultActivity.class);



            if (requestCode == CAMERA) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String token = AuthService.getAuth();
                        TextRecognition tr = new TextRecognition();
                        bitmap[0] = (Bitmap) data.getExtras().get("data");

                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        bitmap[0].compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        byte[] bytes = baos.toByteArray();
                        String result = tr.handwritingRecognition(token, bytes);
//                        intent.putExtra("type", "universal");
                        intent.putExtra("type", "handwrite");
                        intent.putExtra("result", result);
                        startActivity(intent);
                    }
                }).start();
                return;
            }


            if (requestCode == netImage) {
                new Thread(() -> {
                    String token = AuthService.getAuth();
                    TextRecognition tr = new TextRecognition();

                    handler.sendEmptyMessage(LOADING);
                    String result = tr.netImageRecognition(token, data.getStringExtra("url"));
                    handler.sendEmptyMessage(OK);

                    intent.putExtra("result", result);
                    intent.putExtra("type", "netImage");
                    startActivity(intent);

                }).start();

                return;
            }

            String filePath = ContentUtil.getPathFromUri(this, data.getData());
            Log.e(TAG, "onActivityResult: " + filePath);

            final byte[][] bytes = new byte[2][];
            try {
                bytes[0] = FileUtil.readFileByBytes(filePath);
            } catch (Exception e) {
                e.printStackTrace();
            }

            new Thread(() -> {
                String token = AuthService.getAuth();
                TextRecognition tr = new TextRecognition();
                handler.sendEmptyMessage(LOADING);
                String result = null;

                switch (requestCode) {
                    case universal:
                        result = tr.universalTextRecognition(token, bytes[0]);
                        intent.putExtra("type", "universal");
                        break;
                    case card:
                        result = tr.cardTextRecognition(token, bytes[0]);
                        intent.putExtra("type", "card");
                        break;
                    case certificate:
                        result = tr.idCardRecognition(token, bytes[0]);
                        intent.putExtra("type", "certificate");
                        break;
                    case handwrite:
                        result = tr.handwritingRecognition(token, bytes[0]);
                        intent.putExtra("type", "handwrite");
                        break;
                    default:
                        break;
                }
                handler.sendEmptyMessage(OK);
                intent.putExtra("result", result);
                startActivity(intent);
            }).start();

        }

    }


    public void inputNetUrl(View view) {
        QMUIDialog.EditTextDialogBuilder builder = new QMUIDialog.EditTextDialogBuilder(this);
        builder.setTitle("Please enter web image url")
                .addAction("cancel", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        dialog.dismiss();
                    }
                })
                .addAction("yes", (dialog, index) -> {
                    String url = builder.getEditText().getText().toString();
                    Intent intent = new Intent();
                    intent.putExtra("url", url);
                    onActivityResult(netImage, 0, intent);
                })
                .show();
    }


}
