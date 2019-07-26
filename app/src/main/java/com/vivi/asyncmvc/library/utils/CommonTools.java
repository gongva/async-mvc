package com.vivi.asyncmvc.library.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.text.Layout;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.ViewTreeObserver;
import android.webkit.URLUtil;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.vivi.asyncmvc.base.ActivitySupport;
import com.vivi.asyncmvc.comm.AppContext;
import com.vivi.asyncmvc.comm.listener.PermissionRequestListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Desc:通用工具类
 * CreateTime: 2018/12/25
 */
public class CommonTools {

    private static String cameraFilePath; //相机拍照文件路径

    /**
     * SD卡是否可用
     *
     * @return
     */
    private static boolean isCanUseSD() {
        try {
            return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 以当前时间作为文件名的图片保存路径
     *
     * @return
     */
    public static String newHikPicPath() {
        return newHikPicPath(String.valueOf(System.currentTimeMillis()));
    }

    /**
     * 图片保存路径
     *
     * @param fileName
     * @return
     */
    public static String newHikPicPath(String fileName) {
        cameraFilePath = FileUtil.getDICMBasePath() + fileName + ".jpg";
        new File(cameraFilePath).getParentFile().mkdirs();
        return cameraFilePath;
    }

    /**
     * 获取相机拍摄的照片
     *
     * @return
     */
    public static String getCameraFilePath() {
        return cameraFilePath;
    }

    /**
     * 打开相机，默认RequestCode：AppContext.REQUEST_CODE_OPEN_CAMERA
     *
     * @param activity
     */
    public static void openCamera(final Activity activity) {
        openCamera(activity, AppContext.REQUEST_CODE_OPEN_CAMERA);
    }

    /**
     * 打开相机，校验权限
     *
     * @param activity
     * @param requestCode
     */
    public static void openCamera(final Activity activity, final int requestCode) {
        if (!isCanUseSD()) {
            UI.showToast("未找到存储卡，不能拍照");
            return;
        }
        ActivitySupport.requestRunPermission(new String[]{Manifest.permission.CAMERA}, new PermissionRequestListener() {
            @Override
            public void onGranted() {
                openCameraGo(activity, requestCode);
            }
        });
    }

    /**
     * 打开相机
     *
     * @param activity
     * @param requestCode
     */
    private static void openCameraGo(Activity activity, int requestCode) {
        newHikPicPath();
        File cameraFile = new File(cameraFilePath);
        cameraFile.getParentFile().mkdirs();

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, getUriFromFileCompat(activity, cameraFile));
        activity.startActivityForResult(intent, requestCode);
    }

    /**
     * 获取打开相机的URI
     *
     * @param context
     * @param file
     * @return
     */
    public static Uri getUriFromFileCompat(Context context, File file) {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
            return Uri.fromFile(file);
        } else {
            /**
             * 7.0 调用系统相机拍照不再允许使用Uri方式，应该替换为FileProvider
             * 并且这样可以解决MIUI系统上拍照返回size为0的情况
             */
            return FileProvider.getUriForFile(context, getFileProviderAuthorities(context), file);
        }
    }

    /**
     * 获取fileProvider
     *
     * @param context
     * @return
     */
    public static String getFileProviderAuthorities(Context context) {
        return context.getPackageName() + ".android7.fileprovider";
    }

    /**
     * 打开裁剪
     *
     * @param activity
     * @param filePath
     * @param aspectWidth
     * @param aspectHeight
     * @param outputWidth
     * @param outputHeight
     */
    public static void openCrop(Activity activity, String filePath, int aspectWidth, int aspectHeight, int outputWidth, int outputHeight) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        File file = new File(filePath);
        if (file != null && !file.exists()) {
            UI.showToast("图片错误");
            return;
        }
        intent.setDataAndType(getUriFromFileCompat(activity, file), "image/*");
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        }
        intent.putExtra("crop", "true");
        if (aspectWidth != 0 && aspectHeight != 0) {
            intent.putExtra("aspectX", aspectWidth);
            intent.putExtra("aspectY", aspectHeight);
        }
        intent.putExtra("outputX", outputWidth);
        intent.putExtra("outputY", outputHeight);
        intent.putExtra("return-data", false);
        intent.putExtra("noFaceDetection", true);
        Uri outUri = Uri.fromFile(newCropTmpFile(activity));
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outUri);
        if (Build.VERSION.SDK_INT >= 26) {
            activity.grantUriPermission(activity.getPackageName(), outUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        }
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.name());
        activity.startActivityForResult(intent, AppContext.REQUEST_CODE_IMAGE_CROP);
    }

    /**
     * 获取裁剪时的临时文件
     *
     * @param context
     * @return
     */
    public static File getCropTmpFile(Context context) {
        File file = context.getExternalCacheDir();
        if (file == null) {
            file = context.getCacheDir();
        }
        File tmpFile = new File(file.getAbsolutePath() + File.separator + "cropTmp.jpg");
        return tmpFile;
    }

    /**
     * 新建裁剪时的临时文件
     *
     * @param context
     * @return
     */
    private static File newCropTmpFile(Context context) {
        File tmpFile = getCropTmpFile(context);
        if (tmpFile.exists()) {
            tmpFile.delete();
        }
        try {
            tmpFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tmpFile;
    }

    /**
     * 复制文件
     *
     * @param oldFile
     * @param newFile
     */
    public static void copyFile(File oldFile, File newFile) {
        InputStream inStream = null;
        FileOutputStream fs = null;
        try {
            int byteread = 0;
            if (oldFile.exists()) {
                inStream = new FileInputStream(oldFile);
                fs = new FileOutputStream(newFile);
                byte[] buffer = new byte[2048];
                while ((byteread = inStream.read(buffer)) != -1) {
                    fs.write(buffer, 0, byteread);
                }
                fs.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 判断是否是我们服务器图片地址
     *
     * @param str
     * @return
     */
    public static boolean isServerImageUrl(String str) {
        if (str == null) {
            return false;
        }
        File file = new File(str);
        return !file.exists() && !URLUtil.isNetworkUrl(str);
    }

    /**
     * 判断是否是本地的图片地址
     *
     * @param str
     * @return
     */
    public static boolean isLocalImageUrl(String str) {
        if (str == null) {
            return false;
        }
        File file = new File(str);
        return file.exists();
    }

    public static StringBuilder insertSpaces(String numbers) {
        if (numbers == null) {
            return new StringBuilder();
        }
        StringBuilder sbCardNumForShow = new StringBuilder(numbers);
        if (!numbers.contains(" ")) {
            //没有空格分隔，添加
            int sum = numbers.length() / 4;
            for (int i = 1; i <= sum; i++) {
                int index = sum * i;
                if (index < numbers.length()) {
                    sbCardNumForShow.insert(index + i - 1, " ");
                }
            }
        }
        return sbCardNumForShow;
    }


    public static String decodeUrl(String url) {
        return URLDecoder.decode(url);
    }


    /**
     * 生成二维码
     *
     * @param logoW    logo宽高
     * @param QR_WIDTH 二维码宽高
     * @param text     生成二维码的链接
     * @param logoBmp  logo
     * @return
     */
    public static Bitmap createCode(float logoW, int QR_WIDTH, String text, Bitmap logoBmp) {
        try {
            //图像数据转换，使用了矩阵转换
            Hashtable<EncodeHintType, Object> hints = new Hashtable<>();
            hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
            hints.put(EncodeHintType.MARGIN, 1);
            BitMatrix bitMatrix = new QRCodeWriter().encode(text, BarcodeFormat.QR_CODE, QR_WIDTH, QR_WIDTH, hints);
            int[] pixels = new int[QR_WIDTH * QR_WIDTH];
            for (int y = 0; y < QR_WIDTH; y++) {
                //下面这里按照二维码的算法，逐个生成二维码的图片，//两个for循环是图片横列扫描的结果
                for (int x = 0; x < QR_WIDTH; x++) {
                    if (bitMatrix.get(x, y)) {
                        pixels[y * QR_WIDTH + x] = 0xff000000;//黑色
                    } else {
                        pixels[y * QR_WIDTH + x] = 0xffffffff;//白色
                    }
                }
            }
            //------------------添加图片部分------------------//
            Bitmap bitmap = Bitmap.createBitmap(QR_WIDTH, QR_WIDTH, Bitmap.Config.ARGB_8888);
            //设置像素点
            bitmap.setPixels(pixels, 0, QR_WIDTH, 0, 0, QR_WIDTH, QR_WIDTH);
            Canvas canvas = new Canvas(bitmap);
            //二维码
            canvas.drawBitmap(bitmap, 0, 0, null);
            //------------------添加logo部分------------------//
            //图片绘制在二维码中央，合成二维码图片
            if (logoBmp != null) {
                Matrix m = new Matrix();
                float sx = logoW / logoBmp.getWidth();
                float sy = logoW / logoBmp.getHeight();
                m.setScale(sx, sy);//设置缩放信息
                logoBmp = Bitmap.createBitmap(logoBmp, 0, 0, logoBmp.getWidth(), logoBmp.getHeight(), m, false);
                canvas.drawBitmap(logoBmp,
                        bitmap.getWidth() / 2 - logoBmp.getWidth() / 2,
                        bitmap.getHeight() / 2 - logoBmp.getHeight() / 2, null);
            }

            return bitmap;
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 从一段html代码中扣出文字
     *
     * @param htmlStr
     * @return
     */
    public static String delHTMLTag(String htmlStr) {
        // 定义HTML标签的正则表达式
        String regEx_script = "<script[^>]*?>[\\s\\S]*?<\\/script>"; //定义script的正则表达式
        String regEx_style = "<style[^>]*?>[\\s\\S]*?<\\/style>"; //定义style的正则表达式
        String regEx_html = "<[^>]+>";

        // 过滤script标签
        Pattern p_script = Pattern.compile(regEx_script, Pattern.CASE_INSENSITIVE);
        Matcher m_script = p_script.matcher(htmlStr);
        htmlStr = m_script.replaceAll("");

        // 过滤style标签
        Pattern p_style = Pattern.compile(regEx_style, Pattern.CASE_INSENSITIVE);
        Matcher m_style = p_style.matcher(htmlStr);
        htmlStr = m_style.replaceAll("");

        // 过滤html标签
        Pattern p_html = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);
        Matcher m_html = p_html.matcher(htmlStr);
        htmlStr = m_html.replaceAll("");
        htmlStr = htmlStr.replace("&nbsp", "");
        htmlStr = htmlStr.replace("\\n", "");
        htmlStr = htmlStr.replace("\\r", "");
        htmlStr = htmlStr.replace("\n", "");
        htmlStr = htmlStr.replace("\r", "");

        return htmlStr.trim(); //返回文本字符串
    }

    /**
     * 工具方法：生成带颜色的文字
     * 应用场景：搜索时关键字高亮或变色
     *
     * @param originalString 完整的字符串
     * @param colorKeys      需要变色的关键字
     * @param colors         关键字颜色
     * @return
     */
    public static CharSequence generateColorText(String originalString, String[] colorKeys, int[] colors) {
        if (originalString == null || colorKeys == null || colors == null || colorKeys.length == 0 || colors.length == 0 || (colorKeys.length != colors.length)) {
            return originalString;
        }
        StringBuilder sb = new StringBuilder(originalString);
        SpannableString spannableString = new SpannableString(sb);
        for (int i = 0; i < colorKeys.length; i++) {
            int fromIndex = -colorKeys[i].length();
            List<Integer> listIndex = new ArrayList<>();
            while ((fromIndex = originalString.indexOf(colorKeys[i], fromIndex + colorKeys[i].length())) > -1) {
                listIndex.add(fromIndex);
            }
            for (Integer index : listIndex) {
                ForegroundColorSpan colorSpan = new ForegroundColorSpan(colors[i]);
                spannableString.setSpan(colorSpan, index, index + colorKeys[i].length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        return spannableString;
    }

    /**
     * TextView的文字是否是否超过行数
     *
     * @param textView
     * @param maxLines
     * @param callBack
     */
    public static void isOverFlowed(final TextView textView, final int maxLines, final TextOverFlowedCallBack callBack) {
        if (callBack == null) return;
        ViewTreeObserver viewTreeObserver = textView.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Layout l = textView.getLayout();
                if (l != null) {
                    int lines = l.getLineCount();
                    if (lines == maxLines) {
                        callBack.overFlowed(l.getEllipsisCount(lines - 1) > 0);
                    }
                }
            }
        });
    }

    public interface MergeCallBack {
        void mergeComplete(Bitmap bmp);
    }

    public interface TextOverFlowedCallBack {
        void overFlowed(boolean isOverFlowed);
    }
}
