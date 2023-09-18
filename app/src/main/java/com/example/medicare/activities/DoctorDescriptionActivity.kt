// DoctorDescriptionActivity.kt
package com.example.medicare.activities

import TimingAdapter
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.medicare.Firebase.FirestoreClass
import com.example.medicare.R
import com.example.medicare.adapter.TimingAdapterLatest
import com.example.medicare.models.Appointment
import com.example.medicare.models.AppointmentUser
import com.example.medicare.models.Doctor
import com.example.medicare.models.SlotAvailability
import com.example.medicare.models.Timing
import com.example.medicare.models.User
import com.example.medicare.utils.Constants
import com.google.firebase.firestore.FirebaseFirestore
import de.hdodenhof.circleimageview.CircleImageView
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.random.Random

class DoctorDescriptionActivity : BaseActivity() {
    var timeselected: String = ""
    var dayselected: String = ""
    var bookings: ArrayList<Appointment> = ArrayList()
    var appointment_by_user: Appointment = Appointment()
    var appointment_details_by_doctor: AppointmentUser = AppointmentUser()
    var timing_user: ArrayList<Timing> = ArrayList()
    var mposition: Int = 0
    var mpositiondate: Int = -1
    var userbookings: ArrayList<AppointmentUser> = ArrayList()
    var muserDetails: User = User()
    var mdoctorDetails: Doctor = Doctor()
    var datelistnew: ArrayList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_doctor_description)
        val selectedDoctor = intent.getParcelableExtra<Doctor>(Constants.DOCTOR_MODEL)
        Log.e("DoctorDescriptionActivity_Doctor", "Selected Doctor: ${selectedDoctor}")

        if (selectedDoctor != null) {
            setupActionBar(selectedDoctor.name)
        }
        if (selectedDoctor != null) {
            setupui(selectedDoctor)
        }

        val uniqueDates = ArrayList<String>()

        if (selectedDoctor != null) {
            val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val dayFormatter = SimpleDateFormat("dd EEE", Locale.getDefault())

            for (timing in selectedDoctor.timing) {

                    val dateStr = timing.time

                    try {
                        val date = dateFormatter.parse(dateStr)
                        if (date != null) {
                            val formattedDate = dayFormatter.format(date)
                            if (!uniqueDates.contains(formattedDate)) {
                                uniqueDates.add(formattedDate)
                            }
                        }
                    } catch (e: ParseException) {
                        // Handle parsing exceptions if any
                        e.printStackTrace()
                    }
                }

        }

        val datesList = getDatesList()
        dayuiset(selectedDoctor,uniqueDates)

        findViewById<Button>(R.id.btn_book_appointment).setOnClickListener {
            if (selectedDoctor != null) {
                bookappointment(selectedDoctor)
            }
        }
    }

    fun userDetails(user: User) {
        muserDetails = user
    }

    fun doctorDetails(doctor: Doctor) {
        mdoctorDetails = doctor
    }

    private fun dayuiset(selectedDoctor: Doctor?, datesList: List<String>) {
        val rv_day_list = findViewById<RecyclerView>(R.id.dayrecyclerview)
        val timingRecyclerView = findViewById<RecyclerView>(R.id.timingrecyclerview)
        val workingHoursTextView = findViewById<TextView>(R.id.working_time)

        val layoutManager = LinearLayoutManager(this@DoctorDescriptionActivity, LinearLayoutManager.HORIZONTAL, false)
        rv_day_list.layoutManager = layoutManager
        rv_day_list.setHasFixedSize(true)

        val adapter = TimingAdapter(datesList, this@DoctorDescriptionActivity)
        rv_day_list.adapter = adapter

        adapter.setOnClickListener(object : TimingAdapter.OnClickListener {
            override fun onClick(position: Int) {
                if (mpositiondate != position) {
                    // When a new date is selected, show the timing RecyclerView and "Working Hours" TextView
                    dayselected = datesList[position]
                    mpositiondate = position
                    timingRecyclerView.visibility = View.VISIBLE
                    workingHoursTextView.visibility = View.VISIBLE

                    // Update the timing RecyclerView with the new selected date position
                    var workingtime: ArrayList<SlotAvailability> = ArrayList()

                    if (selectedDoctor != null && position >= 0 && position < selectedDoctor.timing.size) {
                        val timing = selectedDoctor.timing[position]
                        workingtime.addAll(timing.dateSlotMap)

                        // Define a custom comparator to sort based on the time field

                        timinguiset(workingtime)
                    }
                } else {
                    // When the same date is selected again (deselected), hide the timing RecyclerView and "Working Hours" TextView
                    dayselected = ""
                    mpositiondate = -1
                    timingRecyclerView.visibility = View.GONE
                    workingHoursTextView.visibility = View.GONE
                }
            }
        })

        // Initially, hide the timing RecyclerView and "Working Hours" TextView
        timingRecyclerView.visibility = View.GONE
        workingHoursTextView.visibility = View.GONE
    }






    private fun setupui(selectedDoctor: Doctor) {
        val name = findViewById<TextView>(R.id.doctor_name_decription)
        name.text = selectedDoctor.name
        findViewById<TextView>(R.id.doctor_speciality_description).text =
            "Speciality: ${selectedDoctor.speciality}"
        findViewById<TextView>(R.id.doctordegree).text = "Degree: ${selectedDoctor.degree}"
        findViewById<TextView>(R.id.hospital_name_desciption).text = "Hospital: ${selectedDoctor.hospital}"
        findViewById<TextView>(R.id.address_doctor_description).text = "Address: ${selectedDoctor.address}"
        findViewById<TextView>(R.id.doctor_details_description).text = selectedDoctor.about
        val nav_user_image = findViewById<CircleImageView>(R.id.doctor_image_decription)
        Glide
            .with(this)
            .load(selectedDoctor.image)
            .centerCrop()
            .placeholder(R.drawable.ic_user_place_holder)
            .into(nav_user_image)
    }

    fun timinguiset(timinglist: ArrayList<SlotAvailability>) {
        val rv_timing_list = findViewById<RecyclerView>(R.id.timingrecyclerview)

        val layoutManager =
            LinearLayoutManager(this@DoctorDescriptionActivity, LinearLayoutManager.HORIZONTAL, false)
        rv_timing_list.layoutManager = layoutManager
        rv_timing_list.setHasFixedSize(true)

        // Pass the selected date position to TimingAdapterLatest
        val adapter = TimingAdapterLatest(mpositiondate, timinglist, this@DoctorDescriptionActivity)
        rv_timing_list.adapter = adapter

        adapter.setOnClickListener(object :
            TimingAdapterLatest.OnClickListener {
            override fun onClick(position: Int) {
                timeselected = timinglist[position].date
                mposition = position
            }
        })
    }

    private fun bookappointment(selectedDoctor: Doctor) {
        FirebaseFirestore.getInstance().collection(Constants.DOCTOR)
            .document(selectedDoctor.documentId)
            .get()
            .addOnSuccessListener { document ->
                mdoctorDetails = document.toObject(Doctor::class.java)!!

                // Continue with the booking logic inside this block
                if (timeselected.isNotEmpty() && dayselected.isNotEmpty()) {
                    // Check if the selected date and time are already booked
                    val isAlreadyBooked = mdoctorDetails.appointment.any { appointment ->
                        appointment.date == dayselected && appointment.time == timeselected
                    }

                    // Check if the user has already booked an appointment within the last 24 hours for the same date and time
                    val currentTimeMillis = System.currentTimeMillis()
                    val twentyFourHoursInMillis = 24 * 60 * 60 * 1000 // 24 hours in milliseconds
                    val isWithin24Hours = mdoctorDetails.appointment.any { appointment ->
                        appointment.date == dayselected &&
                                appointment.time == timeselected &&
                                appointment.user_id == FirestoreClass().getCurrentUserID() &&
                                (currentTimeMillis - appointment.timestamp) < twentyFourHoursInMillis
                    }

                    if (isAlreadyBooked) {
                        // Display a toast message indicating that the slot is already booked
                        Toast.makeText(
                            this,
                            "Slot already booked on $dayselected at $timeselected",
                            Toast.LENGTH_LONG
                        ).show()
                    } else if (isWithin24Hours) {
                        // Display a toast message indicating that the user has already booked an appointment within 24 hours
                        Toast.makeText(
                            this,
                            "You can't book another appointment within 24 hours for the same slot",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        val randomId = Random.nextInt(200, 1001) // Generates a random integer between 200 and 1000
                        appointment_by_user.id = randomId.toString()
                        appointment_by_user.user_id = FirestoreClass().getCurrentUserID()
                        appointment_by_user.date = dayselected
                        appointment_by_user.time = timeselected
                        appointment_by_user.status = "BOOKING CONFIRMED"
                        appointment_by_user.timestamp = currentTimeMillis // Store the current timestamp

                        // Parse the values as integers
                        if (mpositiondate >= 0 && mpositiondate < mdoctorDetails.timing.size) {
                            var remainingSlot = mdoctorDetails.timing[mpositiondate].dateSlotMap[mposition].remainingSlots.toInt()
                            var totalBookedSlot = mdoctorDetails.timing[mpositiondate].dateSlotMap[mposition].totalBookedSlots.toInt()

                            remainingSlot -= 1
                            totalBookedSlot += 1

                            // Update the values in the specific Timing object
                            mdoctorDetails.timing[mpositiondate].dateSlotMap[mposition].remainingSlots = remainingSlot.toString()
                            mdoctorDetails.timing[mpositiondate].dateSlotMap[mposition].totalBookedSlots = totalBookedSlot.toString()
                        }

                        timing_user = mdoctorDetails.timing

                        // Add the new appointment to the doctor's list
                        mdoctorDetails.appointment.add(appointment_by_user)

                        // Update the doctor's appointment list in Firestore
                        val doctorHashMap = HashMap<String, Any>()
                        doctorHashMap[Constants.DOCUMENT_ID] = selectedDoctor.documentId
                        doctorHashMap[Constants.APPOINTMENT] = mdoctorDetails.appointment
                        doctorHashMap[Constants.TIMING] = mdoctorDetails.timing

                        FirestoreClass().updatedoctordetails(
                            this@DoctorDescriptionActivity,
                            doctorHashMap,
                            selectedDoctor.documentId
                        )

                        // Fetch the user document from Firestore
                        FirebaseFirestore.getInstance().collection(Constants.USERS)
                            .document(FirestoreClass().getCurrentUserID())
                            .get()
                            .addOnSuccessListener { userDocument ->
                                muserDetails = userDocument.toObject(User::class.java)!!
                                val userHashMap = HashMap<String, Any>()
                                userbookings = muserDetails.userappointment

                                appointment_details_by_doctor.doctor_id = selectedDoctor.documentId
                                appointment_details_by_doctor.id = randomId.toString()
                                appointment_details_by_doctor.date = dayselected
                                appointment_details_by_doctor.time = timeselected
                                appointment_details_by_doctor.status = "Booking Done"
                                appointment_details_by_doctor.timestamp = currentTimeMillis

                                userbookings.add(appointment_details_by_doctor)
                                userHashMap[Constants.USERAPPOINTMENT] = userbookings

                                // Update the user's appointment list in Firestore
                                FirestoreClass().updateuserappointmentdetails(
                                    this@DoctorDescriptionActivity,
                                    userHashMap,
                                    FirestoreClass().getCurrentUserID()
                                )

                                startActivity(Intent(this@DoctorDescriptionActivity, MainActivity::class.java))
                                finish()
                            }
                    }
                } else {
                    Toast.makeText(this, "Please Select the date and time", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun setupActionBar(name: String) {
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar_Doctor_Description)
        setSupportActionBar(toolbar)
        toolbar.setNavigationIcon(R.drawable.ic_black_color_back_24dp)
        val title_toolbar = findViewById<TextView>(R.id.tv_title_DescriptionDoctor)
        title_toolbar.text = name
        setSupportActionBar(findViewById(R.id.toolbar_Doctor_Description))

        val actionbar = supportActionBar
        if (actionbar != null) {
            actionbar.setDisplayHomeAsUpEnabled(true)
            actionbar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
        }
        findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar_Doctor_Description).setNavigationOnClickListener {
            onBackPressed()
        }
    }

    fun getDatesList(): List<String> {
        val dateFormat = SimpleDateFormat("dd EEE", Locale.getDefault())
        val calendar = Calendar.getInstance()
        val datesList = mutableListOf<String>()

        for (i in 0 until 7) { // Change '5' to '7' for the next 7 days
            val formattedDate = dateFormat.format(calendar.time)
            datesList.add(formattedDate)
            calendar.add(Calendar.DAY_OF_MONTH, 1) // Increment the date by one day
        }

        return datesList
    }
}
