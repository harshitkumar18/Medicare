import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.medicare.R
import com.example.medicare.models.AppointmentUser
import com.example.medicare.models.Doctor
import com.example.medicare.utils.Constants
import com.google.firebase.firestore.FirebaseFirestore
import org.w3c.dom.Text

class BookingListAdapter(
    private val context: Context,
    private var list: ArrayList<AppointmentUser>
) : RecyclerView.Adapter<BookingListAdapter.ViewHolder>() {

    private var onClickListener: OnClickListener? = null

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivDoctorImage: ImageView = view.findViewById(R.id.item_bookeddoctor_image)
        val tvDoctorName: TextView = view.findViewById(R.id.item_tv_name_doctor_details)
        val tvBookingTimeDate: TextView = view.findViewById(R.id.booking_time_and_date)
        val tvbookingid : TextView = view.findViewById(R.id.booking_id)
        val bookingStatus : TextView = view.findViewById(R.id.booking_status)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.booking_item_card, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = list[position]

        // Fetch doctor details based on the appointment's doctor_id
        FirebaseFirestore.getInstance().collection(Constants.DOCTOR)
            .document(model.doctor_id)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                val doctorDetails = documentSnapshot.toObject(Doctor::class.java)

                if (doctorDetails != null) {
                    // Populate the views with doctor details
                    Glide.with(context)
                        .load(doctorDetails.image)
                        .centerCrop()
                        .placeholder(R.drawable.ic_board_place_holder)
                        .into(holder.ivDoctorImage)

                    holder.tvDoctorName.text = doctorDetails.name
                    holder.tvbookingid.text = "Booking id: ${model.id}"
                    holder.tvBookingTimeDate.text = "Time: ${model.time}, Date: ${model.date}"
                    holder.bookingStatus.text = "Status: ${model.status}"

                    // Set click listener for the button

                    // Apply animation
                    setAnimation(holder.itemView)
                }
            }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    interface OnClickListener {
        fun onClick(position: Int, model: AppointmentUser)
    }

    private fun setAnimation(view: View) {
        val anim = AlphaAnimation(0.0f, 1.0f)
        anim.duration = 1000
        view.startAnimation(anim)
    }
}
