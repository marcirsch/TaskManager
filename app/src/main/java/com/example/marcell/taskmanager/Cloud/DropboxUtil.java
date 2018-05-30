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


public class DropboxUtil {
    private static final String TAG = DropboxUtil.class.getSimpleName();


    private static final String CLIENT_IDENTIFIER = "taskManager";

    DbxClientV2 mDbxClient;
    private DbxRequestConfig mDbxConfig;


    public interface OnAsyncTaskEventListener<T> {
        void onStart();
        void onSuccess(T object);
        void onFailed(Exception e);
        void onProgress(int percentage);
    }

    public DropboxUtil(final String accessToken) {
        mDbxConfig = new DbxRequestConfig(CLIENT_IDENTIFIER);
        mDbxClient = new DbxClientV2(mDbxConfig, accessToken);
    }


    public class UploadFile extends AsyncTask<String, Integer, FileMetadata> {

        private final OnAsyncTaskEventListener<FileMetadata> callback;
        private Exception mException;
        private int delay;


        public UploadFile(int delay, OnAsyncTaskEventListener<FileMetadata> callback) {
            this.callback = callback;
            this.delay = delay;

            if (delay < 0) {
                this.delay = 0;
            }
        }


        @Override
        protected FileMetadata doInBackground(String... params) {

            if (delay != 0) {
                try {
                    Thread.sleep(delay * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            publishProgress(0);

            String localUriPath = params[0];
            Uri localUri = Uri.parse(localUriPath);
            File localFile = new File(localUri.getPath());
            Log.d(TAG, "File uri: " + localUri);


            if (localFile.exists()) {
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
            if (values[0] == 0) {
                callback.onStart();
            }
            callback.onProgress(values[0]);
        }

        @Override
        protected void onPostExecute(FileMetadata result) {
            super.onPostExecute(result);
            if (mException != null) {
                callback.onFailed(mException);
            } else if (result == null) {
                callback.onFailed(null);
            } else {
                callback.onSuccess(result);
            }
        }
    }


    public class UploadChunkedFile extends AsyncTask<String, Integer, FileMetadata> {

        private final OnAsyncTaskEventListener<FileMetadata> callback;
        private Exception mException;
        private int delay;



        public UploadChunkedFile(int delay, String filepath, OnAsyncTaskEventListener<FileMetadata> callback) {
            this.callback = callback;
            this.delay = delay;

            if (delay < 0) {
                this.delay = 0;
            }
        }

        @Override
        protected FileMetadata doInBackground(String... params) {

            if (delay != 0) {
                try {
                    Thread.sleep(delay * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            publishProgress(0);

            String localUriPath = params[0];
            Uri localUri = Uri.parse(localUriPath);
            File localFile = new File(localUri.getPath());
            Log.d(TAG, "File uri: " + localUri);


            if (localFile.exists()) {
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
            if (values[0] == 0) {
                callback.onStart();
            }
            callback.onProgress(values[0]);
        }

        @Override
        protected void onPostExecute(FileMetadata result) {
            super.onPostExecute(result);
            if (mException != null) {
                callback.onFailed(mException);
            } else if (result == null) {
                callback.onFailed(null);
            } else {
                callback.onSuccess(result);
            }
        }
    }


    public class GetUserNameTask extends AsyncTask<Void, Void, String> {
        private OnAsyncTaskEventListener<String> callback;

        public Exception exception;


        public GetUserNameTask(OnAsyncTaskEventListener<String> callback) {
            this.callback = callback;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            callback.onStart();
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
                    callback.onSuccess(s);
                } else {
                    callback.onFailed(exception);
                }
            }
        }
    }


    public static String getPath(Context context, Uri uri) {
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = {"_data"};
            Cursor cursor;

            try {
                cursor = context.getContentResolver().query(uri, projection, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow("_data");
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }
}
