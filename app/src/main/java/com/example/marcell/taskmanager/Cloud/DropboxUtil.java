package com.example.marcell.taskmanager.Cloud;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.NetworkIOException;
import com.dropbox.core.RetryException;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.CommitInfo;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.UploadBuilder;
import com.dropbox.core.v2.files.UploadSessionCursor;
import com.dropbox.core.v2.files.UploadSessionFinishErrorException;
import com.dropbox.core.v2.files.UploadSessionLookupErrorException;
import com.dropbox.core.v2.files.WriteMode;
import com.dropbox.core.v2.users.FullAccount;
import com.example.marcell.taskmanager.Data.PauseEvent;
import com.example.marcell.taskmanager.Data.UserPreferences;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;


public class DropboxUtil {
    private static final String TAG = DropboxUtil.class.getSimpleName();
    private static final String CLIENT_IDENTIFIER = "taskManager";

    private DbxClientV2 dbxClient;
    private DbxRequestConfig dbxConfig;


    public DropboxUtil(Context context) {
        String accessToken = UserPreferences.getUserToken(context);

        dbxConfig = new DbxRequestConfig(CLIENT_IDENTIFIER);
        dbxClient = new DbxClientV2(dbxConfig, accessToken);
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


    public interface OnAsyncTaskEventListener<T> {
        void onStart();

        void onProgress(int percentage);

        void onPause();

        void onSuccess(T object);

        void onFailed(Exception e);


    }

    public static long getFileSize(String fileUri) {
        File f = new File(fileUri);
        long size = f.length();
        return size;
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


            if (localFile.exists())  {
                String remoteFolderPath = params[1];

                // Note - this is not ensuring the name is a valid dropbox file name
                String remoteFileName = localFile.getName();
                try {
                    InputStream inputStream = new FileInputStream(localFile);
                    UploadBuilder uploadBuilder = dbxClient.files().uploadBuilder(remoteFolderPath + "/" + remoteFileName)
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

    private static void sleepQuietly(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ex) {
            // just exit
            Log.e(TAG, "Error uploading to Dropbox: interrupted during backoff.");
        }
    }

    public class UploadChunkedFile extends AsyncTask<String, Long, FileMetadata> {

        public static final long CHUNKED_UPLOAD_CHUNK_SIZE = 8L << 20; // 8MiB
        public static final int CHUNKED_UPLOAD_MAX_ATTEMPTS = 5;

        private final OnAsyncTaskEventListener<FileMetadata> callback;
        private int id;
        private int delay;
        private long size;

        private Handler handler;
        private boolean runUpload = true;
        DbxException thrown = null;


        public UploadChunkedFile(final int id, long fileSize, int delay, String filepath, OnAsyncTaskEventListener<FileMetadata> callback) {
            this.callback = callback;
            this.delay = delay;
            this.size = fileSize;
            this.id = id;


            if (delay < 0) {
                this.delay = 0;
            }

            if (size < CHUNKED_UPLOAD_CHUNK_SIZE) {
                Log.i(TAG, "File is smaller than chunk size. Should be using regular upload.");
            }

            EventBus.getDefault().register(this);
        }

        @Subscribe(threadMode = ThreadMode.BACKGROUND)
        public void onUpdateTaskEvent(PauseEvent pauseEvent) {
            if (pauseEvent.ID == id) {
                Log.d(TAG, "eventbus message! :" + pauseEvent.run);
                runUpload = pauseEvent.run;
            }
        }

        private void pauseUpload() {
            callback.onPause();
            while (!runUpload) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        protected FileMetadata doInBackground(String... params) {
            long uploaded = 0L;
            String sessionId = null;

            if (delay != 0) {
                try {
                    Thread.sleep(delay * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            publishProgress(0L, 0L);


            String localUriPath = params[0];
            String remoteFolder = params[1];
            Uri localUri = Uri.parse(localUriPath);
            File localFile = new File(localUri.getPath());
            Log.d(TAG, "File uri: " + localUri);
            String remoteFilePath = remoteFolder + "/" + localFile.getName();


            for (int i = 0; i < CHUNKED_UPLOAD_MAX_ATTEMPTS; ++i) {
                if (i > 0) {
                    System.out.printf("Retrying chunked upload (%d / %d attempts)\n", i + 1, CHUNKED_UPLOAD_MAX_ATTEMPTS);
                }

                try {
                    InputStream in = new FileInputStream(localFile);
                    // if this is a retry, make sure seek to the correct offset
                    in.skip(uploaded);

                    // (1) Start
                    if (sessionId == null) {
                        sessionId = dbxClient.files().uploadSessionStart()
                                .uploadAndFinish(in, CHUNKED_UPLOAD_CHUNK_SIZE)
                                .getSessionId();
                        uploaded += CHUNKED_UPLOAD_CHUNK_SIZE;
                        publishProgress(uploaded, size);
                    }

                    UploadSessionCursor cursor = new UploadSessionCursor(sessionId, uploaded);

                    // (2) Append
                    while ((size - uploaded) > CHUNKED_UPLOAD_CHUNK_SIZE) {
                        if (!runUpload) {
                            pauseUpload();
                            publishProgress(uploaded, size);
                        }
                        dbxClient.files().uploadSessionAppendV2(cursor)
                                .uploadAndFinish(in, CHUNKED_UPLOAD_CHUNK_SIZE);
                        uploaded += CHUNKED_UPLOAD_CHUNK_SIZE;
                        publishProgress(uploaded, size);
                        cursor = new UploadSessionCursor(sessionId, uploaded);
                    }

                    // (3) Finish
                    long remaining = size - uploaded;
                    CommitInfo commitInfo = CommitInfo.newBuilder(remoteFilePath)
                            .withMode(WriteMode.ADD)
                            .withClientModified(new Date(localFile.lastModified()))
                            .build();
                    FileMetadata metadata = dbxClient.files().uploadSessionFinish(cursor, commitInfo)
                            .uploadAndFinish(in, remaining);

                    Log.d(TAG, metadata.toStringMultiline());
                    return metadata;
                } catch (RetryException ex) {
                    thrown = ex;
                    // RetryExceptions are never automatically retried by the client for uploads. Must
                    // catch this exception even if DbxRequestConfig.getMaxRetries() > 0.
                    sleepQuietly(ex.getBackoffMillis());
                    continue;
                } catch (NetworkIOException ex) {
                    thrown = ex;
                    Log.e(TAG, "Network exception");
                    // network issue with Dropbox (maybe a timeout?) try again
                    continue;
                } catch (UploadSessionLookupErrorException ex) {
                    if (ex.errorValue.isIncorrectOffset()) {
                        thrown = ex;
                        // server offset into the stream doesn't match our offset (uploaded). Seek to
                        // the expected offset according to the server and try again.
                        uploaded = ex.errorValue
                                .getIncorrectOffsetValue()
                                .getCorrectOffset();
                        continue;
                    } else {
                        // Some other error occurred, give up.
                        Log.e(TAG, "Error uploading to Dropbox: " + ex.getMessage());
//                        System.exit(1);
                        return null;
                    }
                } catch (UploadSessionFinishErrorException ex) {
                    if (ex.errorValue.isLookupFailed() && ex.errorValue.getLookupFailedValue().isIncorrectOffset()) {
                        thrown = ex;
                        // server offset into the stream doesn't match our offset (uploaded). Seek to
                        // the expected offset according to the server and try again.
                        uploaded = ex.errorValue
                                .getLookupFailedValue()
                                .getIncorrectOffsetValue()
                                .getCorrectOffset();
                        continue;
                    } else {
                        // some other error occurred, give up.
                        Log.e(TAG, "Error uploading to Dropbox: " + ex.getMessage());
//                        System.exit(1);
                        return null;
                    }
                } catch (DbxException ex) {
                    Log.e(TAG, "Error uploading to Dropbox: " + ex.getMessage());
                    return null;
                } catch (IOException ex) {
                    Log.e(TAG, "Error reading from file \"" + localFile + "\": " + ex.getMessage());
                    return null;
                }
            }

            // if we made it here, then we must have run out of attempts
            Log.e(TAG, "Maxed out upload attempts to Dropbox. Most recent error: " + thrown.getMessage());
//            System.exit(1);


            return null;
        }

        @Override
        protected void onProgressUpdate(Long... values) {
            super.onProgressUpdate(values);
            long uploaded = values[0];
            long size = values[1];

            int completePercentage = (int) (uploaded * 100.0 / size + 0.5);

            if (uploaded == 0) {
                callback.onStart();
            }
            callback.onProgress(completePercentage);
        }

        @Override
        protected void onPostExecute(FileMetadata result) {
            super.onPostExecute(result);
            if (thrown != null) {
                callback.onFailed(thrown);
            } else if (result == null) {
                callback.onFailed(null);
            } else {
                callback.onSuccess(result);
            }
            EventBus.getDefault().unregister(this);
        }
    }


    public class GetUserNameTask extends AsyncTask<Void, Void, String> {
        public Exception exception;
        private OnAsyncTaskEventListener<String> callback;


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
                account = dbxClient.users().getCurrentAccount();

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
}
