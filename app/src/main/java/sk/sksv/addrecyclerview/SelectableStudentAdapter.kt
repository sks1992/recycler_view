package sk.sksv.addrecyclerview

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import sk.sksv.addrecyclerview.databinding.ItemStudentRowBinding

class SelectableStudentAdapter(
    private val students: List<Student>,
    private val onItemClicked: (Student) -> Unit
) : RecyclerView.Adapter<SelectableStudentAdapter.ViewHolder>() {

    private var selectedPosition = -1

    class ViewHolder(val binding: ItemStudentRowBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemStudentRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val student = students[position]
        holder.binding.tvStudentId.text = student.id
        holder.binding.tvClass.text = student.studentClass
        holder.binding.tvSubject.text = student.subject
        holder.binding.tvTeacher.text = student.teacher

        if (selectedPosition == position) {
            holder.binding.rootLayout.setBackgroundColor(Color.parseColor("#E8F5E9")) // Light Green
        } else {
            holder.binding.rootLayout.setBackgroundColor(Color.TRANSPARENT)
        }

        holder.itemView.setOnClickListener {
            val previousPosition = selectedPosition
            selectedPosition = holder.bindingAdapterPosition
            notifyItemChanged(previousPosition)
            notifyItemChanged(selectedPosition)
            onItemClicked(student)
        }
    }

    override fun getItemCount(): Int = students.size
}
