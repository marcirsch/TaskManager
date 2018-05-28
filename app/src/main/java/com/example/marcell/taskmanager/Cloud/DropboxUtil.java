package com.example.marcell.taskmanager.Cloud;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.UploadBuilder;
import com.dropbox.core.v2.files.WriteMode;
import com.dropbox.core.v2.users.FullAccount;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;

//TODO in service
public class DropboxUtil {
    private static final String TAG = DropboxUtil.class.getSimpleName();


    private static final String CLIENT_IDENTIFIER = "taskManager";

    DbxClientV2 mDbxClient;
    private DbxRequestConfig mDbxConfig;


    public interface OnAsyncTaskEventListener<T> {
        void OnSuccess(T object);
        void OnFailure(Exception e);
        void OnProgress(int percentage);
    }

    public DropboxUtil(final String accessToken) {
        mDbxConfig = new DbxRequestConfig(CLIENT_IDENTIFIER);
        mDbxClient = new DbxClientV2(mDbxConfig, accessToken);
    }

    public class UploadFileTask extends AsyncTask<String, Integer, FileMetadata> {

        private final Context mContext;
        private final OnAsyncTaskEventListener<FileMetadata> mCallback;
        private Exception mException;


        public UploadFileTask(Context context, OnAsyncTaskEventListener<FileMetadata> callback) {
            mContext = context;
            mCallback = callback;
        }


        @Override
        protected FileMetadata doInBackground(String... params) {
            String localUriPath = params[0];
            Uri localUri = Uri.parse(localUriPath);
            File localFile = new File(localUri.getPath());
            Log.d(TAG, "File uri: " + localUri);


            if(localFile.exists()) {
                String remoteFolderPath = params[1];

                // Note - this is not ensuring the name is a valid dropbox file name
                String remoteFileName = localFile.getName();
                try {
                    InputStream inputStream = new FileInputStream(localFile);
                    UploadBuilder uploadBuilder = mDbxClient.files().uploadBuilder(remoteFolderPath + "/" + remoteFileName)
                            .withMode(WriteMode.OVERWRITE);


                    FileMetadata fileMetadata = uploadBuilder.uploadAndFinish(new ProgressUploadStream(inputStream, localFile.length(), new ProgressUploadStream.OnProgressListener() {
                        @Override
                        public void onProgress(int percentage) {
                            publishProgress(percentage);
                        }
                    }));


                    return fileMetadata;
                } catch (DbxException | IOException e) {
                    mException = e;
                }
            }


            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            mCallback.OnProgress(values[0]);
        }

        @Override
        protected void onPostExecute(FileMetadata result) {
            super.onPostExecute(result);
            if (mException != null) {
                mCallback.OnFailure(mException);
            } else if (result == null) {
                mCallback.OnFailure(null);
            } else {
                mCallback.OnSuccess(result);
            }
        }
    }


    public class GetUserNameTask extends AsyncTask<Void, Void, String> {
        private OnAsyncTaskEventListener<String> callback;
        private final Context context;
        public Exception exception;


        public GetUserNameTask(Context context, OnAsyncTaskEventListener<String> callback) {
            this.context = context;
            this.callback = callback;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... voids) {
            FullAccount account;
            try {
                account = mDbxClient.users().getCurrentAccount();

            } catch (Exception e) {
                exception = e;
                return null;
            }

            return account.getName().getDisplayName();
        }

        @Override
        protected void onPostExecute(String s) {
            if (callback != null) {
                if (exception == null) {
                    callback.OnSuccess(s);
                } else {
                    callback.OnFailure(exception);
                }
            }
        }
    }


    public static String getPath(Context context, Uri uri) throws URISyntaxException {
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = { "_data" };
            Cursor cursor = null;

            try {
                cursor = context.getContentResolver().query(uri, projection, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow("_data");
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            } catch (Exception e) {
                // Eat it
                e.printStackTrace();
            }
        }
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }
}
