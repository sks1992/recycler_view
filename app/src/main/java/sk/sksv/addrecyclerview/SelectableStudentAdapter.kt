package sk.sksv.addrecyclerview

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SelectableStudentAdapter(
    private val students: List<Student>,
    private val onItemClicked: (Student) -> Unit
) : RecyclerView.Adapter<SelectableStudentAdapter.ViewHolder>() {

    private var selectedPosition = -1

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val rootLayout: LinearLayout = itemView.findViewById(R.id.rootLayout)
        val tvStudentId: TextView = itemView.findViewById(R.id.tvStudentId)
        val tvClass: TextView = itemView.findViewById(R.id.tvClass)
        val tvSubject: TextView = itemView.findViewById(R.id.tvSubject)
        val tvTeacher: TextView = itemView.findViewById(R.id.tvTeacher)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_student_row, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val student = students[position]
        holder.tvStudentId.text = student.id
        holder.tvClass.text = student.studentClass
        holder.tvSubject.text = student.subject
        holder.tvTeacher.text = student.teacher

        if (selectedPosition == position) {
            holder.rootLayout.setBackgroundColor(Color.parseColor("#E8F5E9")) // Light Green
        } else {
            holder.rootLayout.setBackgroundColor(Color.TRANSPARENT)
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
