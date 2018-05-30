package com.example.marcell.taskmanager.UI;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.marcell.taskmanager.Cloud.DropboxUtil;
import com.example.marcell.taskmanager.Data.ErrorMessages;
import com.example.marcell.taskmanager.Data.TaskDescriptor;
import com.example.marcell.taskmanager.Data.UserPreferences;
import com.example.marcell.taskmanager.DataBase.TaskDBHandler;
import com.example.marcell.taskmanager.R;
import com.example.marcell.taskmanager.UI.EditTaskRecyclerView.KeywordRVAdapter;

import java.util.ArrayList;
import java.util.List;

public class EditTaskActivity extends AppCompatActivity {
    private static final String TAG = EditTaskActivity.class.getSimpleName();
    public final static int REQUEST_CODE = 1;
    private final int PICK_FILE_REQUEST = 1;

    TaskDescriptor editTask;
    private EditText nameEditText;
    private EditText descriptionEditText;
    private EditText filepathEditText;
    private Button filePathButton;

    private RecyclerView keywordRecyclerView;
    private KeywordRVAdapter keywordRVAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_task);

        //Initialize Layout items
        nameEditText = (EditText) findViewById(R.id.ed_addnewtask_name);
        descriptionEditText = (EditText) findViewById(R.id.ed_addnewtask_description);
        filepathEditText = (EditText) findViewById(R.id.ed_addnewtask_file);
        filePathButton = (Button) findViewById(R.id.bt_addedittask_browse);
        keywordRecyclerView = (RecyclerView) findViewById(R.id.rv_keyword);

        //Setup back button in task bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        filePathButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFileBrowser();
            }
        });



        LinearLayoutManager layoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL, false);
        keywordRecyclerView.setLayoutManager(layoutManager);
        keywordRecyclerView.setHasFixedSize(true);
        keywordRVAdapter = new KeywordRVAdapter();
        keywordRecyclerView.setAdapter(keywordRVAdapter);

        updateEditTask(getIntent());
        nameEditText.setText(editTask.getName());
        descriptionEditText.setText(editTask.getDescription());
        filepathEditText.setText(editTask.getFilePath());
        keywordRVAdapter.setKeywords(editTask.getKeywordList());

    }

    private void updateEditTask(Intent startIntent){
        if (startIntent.hasExtra(TaskDescriptor.class.getSimpleName())) {
            getSupportActionBar().setTitle(R.string.edit_task_toolbar_edit_title);

            int editTaskID = startIntent.getIntExtra(TaskDescriptor.class.getSimpleName(), 0);

            TaskDBHandler dbHandler = TaskDBHandler.getInstance(this);
            editTask = dbHandler.getTask(editTaskID);
        } else {
            getSupportActionBar().setTitle(R.string.edit_task_toolbar_add_title);
            editTask = new TaskDescriptor();
        }
    }


    private void showFileBrowser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("file/*");
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, PICK_FILE_REQUEST);
        } else {
            Toast.makeText(this, "No file browser available on device", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_FILE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            String filePath = DropboxUtil.getPath(this, data.getData());
            filepathEditText.setText(filePath);
        }

    }


    //Handle Back button pressed event
    @Override
    public boolean onSupportNavigateUp() {
        Intent intent = getIntent();
        setResult(RESULT_CANCELED, intent);
        finish();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.addedittask_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Save pressed
        int id = item.getItemId();
        boolean saveSuccessful = false;

        Log.d(TAG, "Save pressed");

        //On Save button pressed return newly entered task description to Process Activity
        try {

            if (id == R.id.addedittask_save_action) {
                if (saveSuccessful = readInputTask()) {

                    TaskDBHandler dbHandler = TaskDBHandler.getInstance(this);
                    dbHandler.addTask(editTask);
                }

                return true;
            }
        } finally {
            if (id == R.id.addedittask_save_action && saveSuccessful) {

                Intent intent = getIntent();
                intent.putExtra("TaskID", editTask.getId());
                setResult(RESULT_OK, intent);

                Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();
                finish();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        TaskDBHandler dbHandler = TaskDBHandler.getInstance(this);
        dbHandler.close();
    }

    private boolean readInputTask() {
        String name = nameEditText.getText().toString();
        String filepath = filepathEditText.getText().toString();
        String description = descriptionEditText.getText().toString();
        List<String> keywordList = keywordRVAdapter.getKeywords();

        if (name.equals("")) {
            showErrorMessage(ErrorMessages.INVALID_NAME);
            return false;
        }

        if (filepath == null) {
            showErrorMessage(ErrorMessages.INVALID_FILE_PATH);
            return false;
        } else if (filepath.equals("")) {
            showErrorMessage(ErrorMessages.INVALID_FILE_PATH);
            return false;
        }

        editTask.setName(name);
        editTask.setDescription(description);
        editTask.setFilePath(filepath);
        editTask.setKeywordList((TaskDescriptor.SerializedList<String>) keywordList);

        return true;
    }

    private void showErrorMessage(int msgType) {
        AlertDialog.Builder dlgAlert = new AlertDialog.Builder(this);

        String errorMessage = ErrorMessages.getErrorMessage(this, msgType);
        dlgAlert.setMessage(errorMessage);
        dlgAlert.setTitle("Error");
        dlgAlert.setPositiveButton("OK", null);
        dlgAlert.setCancelable(true);
        dlgAlert.create().show();

        dlgAlert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });
    }

}