
package com.shkiper.taskmanager

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.shkiper.taskmanager.adapters.TaskAdapter
import com.shkiper.taskmanager.adapters.TaskItemTouchHelperCallback
import com.shkiper.taskmanager.fragments.TaskSheetDialog
import com.shkiper.taskmanager.models.Task
import com.shkiper.taskmanager.repositories.TaskRepository
import com.shkiper.taskmanager.utils.MyViewModelFactory
import com.shkiper.taskmanager.utils.Utils
import com.shkiper.taskmanager.viewmodels.MainViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.add_task_dialog.*


class MainActivity : AppCompatActivity() {

    private lateinit var taskAdapter: TaskAdapter
    private lateinit var viewModel: MainViewModel
    private var sizeOfDoneTasks: Int = 0
    private var sizeOfTasks: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViewModel()
        initViews()

    }

    private fun initViews(){
        taskAdapter = TaskAdapter()
        val divider = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        val touchCallback = TaskItemTouchHelperCallback(taskAdapter){
            val task = it
            viewModel.addToDone(task)
            val snackBar: Snackbar = Snackbar.make(rv_task_list, "${it.title} is done", Snackbar.LENGTH_LONG)
            snackBar.setAction("Undo"){
                viewModel.restoreFromDone(task)
            }
            snackBar.show()
        }

        val touchHelper = ItemTouchHelper(touchCallback)
        touchHelper.attachToRecyclerView(rv_task_list)

        with(rv_task_list){
            adapter = taskAdapter
            layoutManager = LinearLayoutManager(this@MainActivity)
            addItemDecoration(divider)
        }

        btn_clear.setOnClickListener{
            viewModel.deleteAll()
        }

        fab.setOnClickListener {
            openDialog()
        }
    }

    private fun openDialog(){
        val fm: FragmentManager = supportFragmentManager
        val taskDialog = TaskSheetDialog()
        taskDialog.show(fm, "Open Dialog")
    }

    private fun bindDoingCounter(quantity: Int){
        tv_doing.text = quantity.toString()
        sizeOfTasks = quantity
        pie_progress.setProgress(Utils.percentOfDone(sizeOfDoneTasks, quantity), true)
    }

    private fun bindDoneCounter(quantity: Int){
        tv_done.text = quantity.toString()
        sizeOfDoneTasks = quantity
        pie_progress.setProgress(Utils.percentOfDone(quantity, sizeOfTasks), true)
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(this, MyViewModelFactory(TaskRepository(this))).get(MainViewModel::class.java)
        viewModel.getTaskData().observe(this, Observer { tasks ->
            taskAdapter.updateData(tasks.filter { !it.isComplete })
            bindDoingCounter(tasks.size)
            bindDoneCounter(tasks.filter { it.isComplete }.size)
        })
    }

    fun addTask(task: Task){
        viewModel.insertTask(task)
    }

}
