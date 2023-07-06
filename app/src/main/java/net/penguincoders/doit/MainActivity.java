package net.penguincoders.doit;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import net.penguincoders.doit.Adapters.ToDoAdapter;
import net.penguincoders.doit.Model.ToDoModel;
import net.penguincoders.doit.Utils.JsonHelper;

import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements DialogCloseListener {
    private RecyclerView tasksRecyclerView;
    private FloatingActionButton fab;
    private List<ToDoModel> taskList;
    private ToDoAdapter tasksAdapter;
    private JsonHelper jsonHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Objects.requireNonNull(getSupportActionBar()).hide();

        tasksRecyclerView = findViewById(R.id.tasksRecyclerView);
        tasksRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        jsonHelper = new JsonHelper(MainActivity.this);

        taskList = jsonHelper.loadTasksFromJson();


        tasksAdapter = new ToDoAdapter(MainActivity.this, taskList);
        tasksRecyclerView.setAdapter(tasksAdapter);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new RecyclerItemTouchHelper(tasksAdapter, jsonHelper));
        itemTouchHelper.attachToRecyclerView(tasksRecyclerView);

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(v -> {
            AddNewTask fragment = AddNewTask.newInstance();
            fragment.setDialogCloseListener(() -> {
                // Dialog kapatıldığında yapılacak işlemler
                // Örneğin, RecyclerView'ı güncelleyebilirsiniz
                taskList = jsonHelper.loadTasksFromJson();
                tasksAdapter.setTasks(taskList);
                tasksAdapter.notifyDataSetChanged();
                MainActivity.this.recreate();
            });
            fragment.show(getSupportFragmentManager(), AddNewTask.TAG);
        });
    }

    @Override
    public void handleDialogClose(DialogInterface dialog) {
        taskList = jsonHelper.loadTasksFromJson();
        tasksAdapter.setTasks(taskList);
        tasksAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        jsonHelper.close();
    }
}
