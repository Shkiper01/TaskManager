package com.shkiper.taskmanager

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.shkiper.taskmanager.adapters.TaskAdapter
import com.shkiper.taskmanager.models.Task
import com.shkiper.taskmanager.viewmodels.MainViewModel
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    private lateinit var taskAdapter: TaskAdapter
    private lateinit var viewModel: MainViewModel

    private val task  = Task("1", "Cделать домашнюю работу")
    private val task2  = Task("2", "Сделать уборку")



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViewModel()
        initViews()
        viewModel.add(task)
        viewModel.add(task2)
    }


    private fun initViews(){
        taskAdapter = TaskAdapter()
        val divider = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        with(rv_task_list){
            adapter = taskAdapter
            layoutManager = LinearLayoutManager(this@MainActivity)
            addItemDecoration(divider)
        }


    }


    private fun initViewModel() {
        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        viewModel.getTaskData().observe(this, Observer { taskAdapter.updateData(it) })
    }
}