import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.medicare.Firebase.FirestoreClass
import com.example.medicare.R
import com.example.medicare.activities.BookingActivity
import com.example.medicare.activities.DoctorDescriptionActivity
import com.example.medicare.activities.createTwilioApiService
import com.example.medicare.models.Appointment
import com.example.medicare.models.AppointmentUser
import com.example.medicare.models.Doctor
import com.example.medicare.models.User
import com.example.medicare.utils.Constants
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.w3c.dom.Text
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.TimeUnit

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
        val cancel_appointment : Button = view.findViewById(R.id.cancel_appointment)

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
                    val dateFormat = SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault())
                    val appointmentDateTime = dateFormat.parse("${model.date} ${model.time}")

// Calculate time difference
                    val currentTime = Calendar.getInstance().time
                    val timeDifferenceMillis = appointmentDateTime.time - currentTime.time
                    val timeDifferenceHours = TimeUnit.MILLISECONDS.toHours(timeDifferenceMillis)

                    if (timeDifferenceHours > 0) {
                        // The appointment is in the future, so show the cancel_appointment button
                        holder.cancel_appointment.visibility = View.VISIBLE

                        holder.cancel_appointment.setOnClickListener {

                            // Parse date and time
                            val dateFormat = SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault())
                            val appointmentDateTime = dateFormat.parse("${model.date} ${model.time}")

                            // Calculate time difference
                            val currentTime = Calendar.getInstance().time
                            val timeDifferenceMillis = appointmentDateTime.time - currentTime.time
                            val timeDifferenceHours = TimeUnit.MILLISECONDS.toHours(timeDifferenceMillis)

                            if (timeDifferenceHours < 2) {
                                // Appointment cannot be canceled
                                val snackbarMessage = "Cannot cancel appointment. It is less than 2 hours before the appointment time."
                                val snackbar = Snackbar.make(holder.itemView, snackbarMessage, Snackbar.LENGTH_SHORT)
                                snackbar.show()
                            } else {
                                val alertDialogBuilder = AlertDialog.Builder(context)
                                alertDialogBuilder.setMessage("Are you sure you want to cancel this appointment?")
                                alertDialogBuilder.setPositiveButton("Yes") { _, _ ->
                                    val bookingIdToDelete = model.id

                                    // Update the doctor's appointment list
                                    val appointmentList = doctorDetails.appointment.toMutableList()
                                    val appointmentToDelete =
                                        appointmentList.find { it.id == bookingIdToDelete }

                                    if (appointmentToDelete != null) {
                                        appointmentList.remove(appointmentToDelete)

                                        // Update the doctorDetails object in Firestore
                                        FirebaseFirestore.getInstance().collection(Constants.DOCTOR)
                                            .document(model.doctor_id)
                                            .update("appointment", appointmentList)
                                            .addOnSuccessListener {
                                                // Update the user's appointment list
                                                FirebaseFirestore.getInstance()
                                                    .collection(Constants.USERS)
                                                    .document(FirestoreClass().getCurrentUserID())
                                                    .get()
                                                    .addOnSuccessListener { documentSnapshot ->
                                                        val userDetails =
                                                            documentSnapshot.toObject(User::class.java)
                                                        val userAppointmentList =
                                                            userDetails?.userappointment?.toMutableList()
                                                        val userAppointmentToDelete =
                                                            userAppointmentList?.find { it.id == bookingIdToDelete }

                                                        if (userAppointmentToDelete != null) {
                                                            userAppointmentList.remove(
                                                                userAppointmentToDelete
                                                            )

                                                            // Update the user's appointment list in Firestore
                                                            FirebaseFirestore.getInstance()
                                                                .collection(Constants.USERS)
                                                                .document(FirestoreClass().getCurrentUserID())
                                                                .update(
                                                                    "userappointment",
                                                                    userAppointmentList
                                                                )
                                                                .addOnSuccessListener {
                                                                    // Both updates are successful
                                                                    Toast.makeText(
                                                                        context,
                                                                        "Appointment Cancelled",
                                                                        Toast.LENGTH_SHORT
                                                                    ).show()
                                                                    notifyDataSetChanged()
                                                                    if (context is BookingActivity) {
                                                                        context.updatelist()
                                                                    }
                                                                    val twilioApiService =
                                                                        createTwilioApiService()
                                                                    val toPhoneNumber =
                                                                        "+" + userDetails.mobile.toString()  // Replace with the recipient's phone number
                                                                    val fromPhoneNumber =
                                                                        "+12568418319" // Replace with your Twilio phone number
                                                                    val message =
                                                                        "Appointment with ${doctorDetails.name}, Booking Id: ${bookingIdToDelete} is Cancelled"

// You should run this in a background thread or coroutine to avoid blocking the UI thread.
// For simplicity, we'll use a coroutine here.
                                                                    GlobalScope.launch(Dispatchers.IO) {
                                                                        try {
                                                                            val response =
                                                                                twilioApiService.sendSMS(
                                                                                    DoctorDescriptionActivity.TwilioConstants.ACCOUNT_SID,
                                                                                    toPhoneNumber,
                                                                                    fromPhoneNumber,
                                                                                    message
                                                                                ).execute()
                                                                            if (response.isSuccessful) {

                                                                            } else {
                                                                                // SMS sending failed
                                                                                // You can handle the failure case here
                                                                            }
                                                                        } catch (e: IOException) {
                                                                            e.printStackTrace()
                                                                            // Handle the exception here
                                                                        }
                                                                    }
                                                                    val twilioApiServicedoctor =
                                                                        createTwilioApiService()
                                                                    val toPhoneNumberdoctor =
                                                                        "+" + doctorDetails.mobile.toString()  // Replace with the recipient's phone number
                                                                    val fromPhoneNumberdoctor =
                                                                        "+12568418319" // Replace with your Twilio phone number
                                                                    val messagedoctor =
                                                                        "Appointment with ${userDetails.name}, Booking Id: ${bookingIdToDelete} is Cancelled"

// You should run this in a background thread or coroutine to avoid blocking the UI thread.
// For simplicity, we'll use a coroutine here.
                                                                    GlobalScope.launch(Dispatchers.IO) {
                                                                        try {
                                                                            val response =
                                                                                twilioApiServicedoctor.sendSMS(
                                                                                    DoctorDescriptionActivity.TwilioConstants.ACCOUNT_SID,
                                                                                    toPhoneNumberdoctor,
                                                                                    fromPhoneNumberdoctor,
                                                                                    messagedoctor
                                                                                ).execute()
                                                                            if (response.isSuccessful) {

                                                                            } else {
                                                                                // SMS sending failed
                                                                                // You can handle the failure case here
                                                                            }
                                                                        } catch (e: IOException) {
                                                                            e.printStackTrace()
                                                                            // Handle the exception here
                                                                        }
                                                                    }
                                                                }
                                                                .addOnFailureListener { e ->
                                                                    // Handle user appointment update failure
                                                                }
                                                        } else {
                                                            // Handle case where user appointment not found
                                                        }
                                                    }
                                                    .addOnFailureListener { e ->
                                                        // Handle user appointment fetch failure
                                                    }
                                            }
                                            .addOnFailureListener { e ->
                                                // Handle doctor appointment update failure
                                            }
                                    }

                                }
                                alertDialogBuilder.setNegativeButton("No") { _, _ ->
                                    // Dismiss the dialog when "No" is clicked
                                }
                                val alertDialog = alertDialogBuilder.create()
                                alertDialog.show()
                            }


                            // Set click listener for the button

                            // Apply animation
                            setAnimation(holder.itemView)
                        }
                    } else {
                        // The appointment has already passed, so hide the cancel_appointment button
                        holder.cancel_appointment.visibility = View.GONE
                    }

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
