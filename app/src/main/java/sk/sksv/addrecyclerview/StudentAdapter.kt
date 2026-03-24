package sk.sksv.addrecyclerview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import sk.sksv.addrecyclerview.databinding.ItemStudentBinding

class StudentAdapter(private val students: List<Student>) : RecyclerView.Adapter<StudentAdapter.StudentViewHolder>() {

    class StudentViewHolder(val binding: ItemStudentBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentViewHolder {
        val binding = ItemStudentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StudentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StudentViewHolder, position: Int) {
        val student = students[position]
        holder.binding.tvStudentId.text = "ID: ${student.id}"
        holder.binding.tvClass.text = "Class: ${student.studentClass}"
        holder.binding.tvSubject.text = "Subject: ${student.subject}"
        holder.binding.tvTeacher.text = "Teacher: ${student.teacher}"
    }

    override fun getItemCount(): Int {
        return students.size
    }
}
