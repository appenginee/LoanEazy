package com.loan.loaneazy.retrofit;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okio.BufferedSink;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Url;

public class FileUploader {

    public FileUploaderCallback fileUploaderCallback;
    private File[] files;
    public int uploadIndex = -1;
    private String uploadURL = "";
    private long totalFileLength = 0;
    private long totalFileUploaded = 0;
    private String filekey="";
    private UploadInterface uploadInterface;
    private String auth_token = "";
    private String[] responses;
    private String mFileName,mUserId,mFileType;


    private interface UploadInterface {

        @Multipart
        @POST
        Call<JsonObject> uploadFile(@Url String url, @Part MultipartBody.Part file, @Header("Authorization") String authorization);

        @Multipart
        @POST
        Call<JsonObject> uploadFile(@Url String url, @Part MultipartBody.Part file, @Part("fileName") RequestBody fileName,
                                    @Part("userid") RequestBody userid, @Part("fileType") RequestBody fileType);
    }

    public interface FileUploaderCallback{
        void onError(String message);
        void onFinish(String[] responses);
        void onProgressUpdate(int currentpercent, int totalpercent, int filenumber);
    }

    public class PRRequestBody extends RequestBody {
        private File mFile;

        private static final int DEFAULT_BUFFER_SIZE = 2048;

        public PRRequestBody(final File file) {
            mFile = file;

        }

        @Override
        public MediaType contentType() {
            // i want to upload only images
            return MediaType.parse("image/*");
        }

        @Override
        public long contentLength() throws IOException {
            return mFile.length();
        }

        @Override
        public void writeTo(BufferedSink sink) throws IOException {
            long fileLength = mFile.length();
            byte[] buffer = new byte[10200577];
            FileInputStream in = new FileInputStream(mFile);
            long uploaded = 1;

            try {
                int read;
                Handler handler = new Handler(Looper.getMainLooper());
                while ((read = in.read(buffer)) != -1) {

                    // update progress on UI thread
                    handler.post(new ProgressUpdater(uploaded, fileLength));
                    uploaded += read;
                    sink.write(buffer, 0, read);
                }
            } finally {
                in.close();
            }
        }
    }

    public FileUploader(){
        uploadInterface = ApiClient.getClient().create(UploadInterface.class);
    }

    public void uploadFiles(String url, String filekey, File[] files, String fileName, String userid, String fileType, FileUploaderCallback fileUploaderCallback){
        uploadFiles(url,filekey,files,fileName,userid,fileType,fileUploaderCallback,"");
    }

    public void uploadFiles(String url, String filekey, File[] files, String fileName, String userid, String fileType, FileUploaderCallback fileUploaderCallback, String auth_token){
        this.fileUploaderCallback = fileUploaderCallback;
        this.files = files;
        this.uploadIndex = -1;
        this.uploadURL = url;
        this.filekey = filekey;
        this.auth_token = auth_token;
        this.mFileName=fileName;
        this.mUserId=userid;
        this.mFileType=fileType;
        totalFileUploaded = 0;
        totalFileLength = 0;
        uploadIndex = -1;
        responses = new String[files.length];
        for(int i=0; i<files.length; i++){
            totalFileLength = totalFileLength + files[i].length();
        }
        uploadNext();
    }

    private void uploadNext(){
        if(files.length>0){
            if(uploadIndex!= -1)
                totalFileUploaded = totalFileUploaded + files[uploadIndex].length();
            uploadIndex++;
            if(uploadIndex < files.length){
                uploadSingleFile(uploadIndex);
            }else{
                fileUploaderCallback.onFinish(responses);
            }
        }else{
            fileUploaderCallback.onFinish(responses);
        }
    }
    @NonNull
    private RequestBody createPartFromString(String descriptionString) {
        //return RequestBody.create(MediaType.parse(FileUtils.MIME_TYPE_TEXT), descriptionString);
        return RequestBody.create(MediaType.parse("text/*"), descriptionString);
    }
    private void uploadSingleFile(final int index){
        PRRequestBody fileBody = new PRRequestBody(files[index]);
        MultipartBody.Part filePart = MultipartBody.Part.createFormData(filekey, files[index].getName(), fileBody);

        Call<JsonObject> call;
        if(auth_token.isEmpty()){
            call  = uploadInterface.uploadFile(uploadURL, filePart,createPartFromString(mFileName),createPartFromString(mUserId),createPartFromString(mFileType));
        }else{
            call  = uploadInterface.uploadFile(uploadURL, filePart, auth_token);
        }

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {
                Log.e("TAG", "onResponse: "+response.body() );
                if (response.isSuccessful()) {

                    JsonElement jsonElement = response.body();
                    responses[index] = jsonElement.toString();
                }else{
                    responses[index] = "";
                }
                uploadNext();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {


                Log.e("TAG", "onFailure: "+t.getMessage() );
                fileUploaderCallback.onError(t.getMessage());
            }
        });
    }

    private class ProgressUpdater implements Runnable {
        private long mUploaded;
        private long mTotal;
        public ProgressUpdater(long uploaded, long total) {
            mUploaded = uploaded;
            mTotal = total;
        }

        @Override
        public void run() {
            int current_percent = (int)(100 * mUploaded / mTotal);
            int total_percent = (int)(100 * (totalFileUploaded+mUploaded) / totalFileLength);
            fileUploaderCallback.onProgressUpdate(current_percent, total_percent,uploadIndex+1 );
        }
    }
}
