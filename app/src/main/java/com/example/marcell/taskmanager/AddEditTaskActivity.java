package com.example.marcell.taskmanager;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.marcell.taskmanager.Cloud.DropboxUtil;
import com.example.marcell.taskmanager.Data.TaskDescriptor;

import java.net.URISyntaxException;

public class AddEditTaskActivity extends AppCompatActivity {
    private static final String TAG = AddEditTaskActivity.class.getSimpleName();
    private final int INVALID_NAME = -2;
    private final int INVALID_FILE_PATH = -3;
    private final int INVALID_KEYWORD = -4;

    private final int PICK_FILE_REQUEST = 1;

    private EditText nameEditText;
    private EditText descriptionEditText;
    private EditText filepathEditText;
    private Button filePathButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_task);
        getSupportActionBar().setTitle(R.string.addedittask_toolbar_title);

        //Setup back button in task bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //Initialize Layout items
        nameEditText = (EditText) findViewById(R.id.ed_addnewtask_name);
        descriptionEditText = (EditText) findViewById(R.id.ed_addnewtask_description);
        filepathEditText = (EditText) findViewById(R.id.ed_addnewtask_file);
        filePathButton = (Button) findViewById(R.id.bt_addedittask_browse);

        filePathButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFileBrowser();
            }
        });

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

        if(requestCode == PICK_FILE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
//            FileProvider.getUriForFile(this,)
            String filePath = null;
            try {
                filePath = DropboxUtil.getPath(this,data.getData());
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }

//            String filePath = data.getData().toString();
            filepathEditText.setText(filePath);
        }

    }



    //Handle Back button pressed event
    @Override
    public boolean onSupportNavigateUp() {
        Intent returnIntern = new Intent();
        setResult(Activity.RESULT_CANCELED, returnIntern);
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
        TaskDescriptor taskDescriptor = null;

        Log.d(TAG, "Save pressed");

        //On Save button pressed return newly entered task description to Process Activity
        try {

            if (id == R.id.addedittask_save_action) {
                taskDescriptor = getInputTask();

                return true;
            }
        } finally {
            if (id == R.id.addedittask_save_action && (taskDescriptor != null)) {
                Intent returnIntent = new Intent();
                Bundle bundle = new Bundle();
                setResult(Activity.RESULT_OK, returnIntent);
                bundle.putSerializable("TaskDescriptor", taskDescriptor);

                returnIntent.putExtras(bundle);

                Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();
                finish();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private TaskDescriptor getInputTask() {
        TaskDescriptor taskDescriptor = new TaskDescriptor();
        String name = nameEditText.getText().toString();
        String filepath = filepathEditText.getText().toString();
        String description = descriptionEditText.getText().toString();

        if (name.equals("")) {
            showErrorMessage(INVALID_NAME);
            return null;
        }

        if(filepath == null){
            showErrorMessage(INVALID_FILE_PATH);
            return null;
        } else if(filepath.equals("")){
            showErrorMessage(INVALID_FILE_PATH);
            return null;
        }

        taskDescriptor.setName(name);
        taskDescriptor.setDescription(description);
        taskDescriptor.setFilePath(filepath);

        return taskDescriptor;
    }

    private void showErrorMessage(int msgType) {
        AlertDialog.Builder dlgAlert = new AlertDialog.Builder(this);
        String errorMessage;

        switch (msgType) {
            case INVALID_NAME:
                errorMessage = getString(R.string.addedittask_invalid_name);
                break;
            case INVALID_FILE_PATH:
                errorMessage = getString(R.string.addedittask_invalid_filepath);
                break;
            case INVALID_KEYWORD:
                errorMessage = getString(R.string.addedittask_invalid_keywords);
                break;
            default:
                errorMessage = "Unknown error";
                break;
        }

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
