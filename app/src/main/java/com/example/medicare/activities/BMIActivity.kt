package com.example.medicare.activities


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast

import com.example.medicare.R
import com.example.medicare.databinding.ActivityBmiactivityBinding
import java.math.BigDecimal
import java.math.RoundingMode

class BMIActivity : AppCompatActivity() {
    companion object{
        private const val Metric_Units_View = " Metric_Units_View"
        private const val Us_Units_View = " Us_Units_View"

    }
    private var currentvisibleview:String = Metric_Units_View


    private var binding: ActivityBmiactivityBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBmiactivityBinding.inflate(layoutInflater)
        setSupportActionBar(binding?.toolbarBmiActivity)
        setContentView(binding?.root)
        if(supportActionBar!=null){
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.title = "CALCULATOR BMI"

        }
        binding?.toolbarBmiActivity?.setNavigationOnClickListener{
            onBackPressed()
        }
        makevisiblemetricunitview()

        binding?.rgUnits?.setOnCheckedChangeListener{_,checkedID:Int ->
            if(checkedID == R.id.rbMetricUnits){
                makevisiblemetricunitview()
            }
            else{
                makevisibleUSunitview()
            }
        }

        binding?.btncalculator?.setOnClickListener {
            calculateUnits()
        }
    }
    private fun makevisiblemetricunitview(){
        currentvisibleview = Metric_Units_View
        binding?.tilmetricheight?.visibility = View.VISIBLE
        binding?.tilmetricweight?.visibility = View.VISIBLE

        binding?.tilUsMetricUnitWeight?.visibility = View.GONE
        binding?.tilmetricunitheightFeet?.visibility=View.GONE
        binding?.tilmetricunitheightInch?.visibility=View.GONE

        binding?.etMetricheight?.text!!.clear()
        binding?.etMetricheight?.text!!.clear()

        binding?.llDisplayBMIResult?.visibility = View.INVISIBLE
    }
    private fun makevisibleUSunitview(){
        currentvisibleview = Us_Units_View
        binding?.tilmetricheight?.visibility = View.INVISIBLE
        binding?.tilmetricweight?.visibility = View.INVISIBLE

        binding?.tilUsMetricUnitWeight?.visibility = View.VISIBLE
        binding?.tilmetricunitheightFeet?.visibility=View.VISIBLE
        binding?.tilmetricunitheightInch?.visibility=View.VISIBLE

        binding?.etUSmetricheightfeet?.text!!.clear()
        binding?.etUSmetricheightinch?.text!!.clear()

        binding?.etUsMetricUnitWeight?.text!!.clear()

        binding?.llDisplayBMIResult?.visibility = View.INVISIBLE
    }


    private fun displayBMIResult(bmi:Float){
        val bmiLabel : String
        val bmiDescription: String
        if(bmi.compareTo(15f)<=0){
            bmiLabel = "Very Severely UnderWeight"
            bmiDescription = "Oops! You really Need to take better care of yourself! Eat more!"
        }else if(bmi.compareTo(15f)>0 && bmi.compareTo(16f)<=0){
            bmiLabel = "Severely UnderWeight"
            bmiDescription = "Oops! You really Need to take better care of yourself! Eat more!"
        }
        else if(bmi.compareTo(16f)>0 && bmi.compareTo(18.5f)<=0){
            bmiLabel = "UnderWeight"
            bmiDescription = "Oops! You really Need to take better care of yourself! Eat more!"
        }
        else if(bmi.compareTo(18.5f)>0 && bmi.compareTo(25f)<=0){
            bmiLabel = "Normal"
            bmiDescription = "Congratulations! You are in a good shape!"
        }
        else if(bmi.compareTo(25f)>0 && bmi.compareTo(30f)<=0){
            bmiLabel = "OverWeight"
            bmiDescription = "Oops! You really Need to take care of yourself! Workout more!"
        }
        else if(bmi.compareTo(30f)>0 && bmi.compareTo(35f)<=0){
            bmiLabel = "Obese Class | (Moderately Obese)"
            bmiDescription = "Oops! You really Need to take care of yourself! Workout more!"
        }
        else if(bmi.compareTo(35f)>0 && bmi.compareTo(40f)<=0){
            bmiLabel = "Obese Class | (Severely Obese)"
            bmiDescription = "OMG! You are in Dangerous Condition! Act Now!"
        }else {
            bmiLabel = "Obese Class | (Very Severely Obese)"
            bmiDescription = "OMG! You are in Dangerous Condition! Act Now!"
        }
        val bmivalue = BigDecimal(bmi.toDouble()).setScale(2,RoundingMode.HALF_EVEN).toString()
        binding?.llDisplayBMIResult?.visibility = View.VISIBLE
        binding?.tvBMIValue?.text = bmivalue
        binding?.tvBMIType?.text = bmiLabel
        binding?.tvMBIDescription?.text = bmiDescription

    }
    private fun validatemetricunit():Boolean{
        var isvalid = true
        if(binding?.etMetricWeight?.text.toString().isEmpty()){
            isvalid = false
        }else if(binding?.etMetricheight?.text.toString().isEmpty()){
            isvalid = false
        }
        return isvalid
    }
    private fun calculateUnits(){
        if(currentvisibleview== Metric_Units_View){
            if(validatemetricunit()){
                val heightvalue: Float = binding?.etMetricheight?.text.toString().toFloat()/100

                val weightvalue: Float = binding?.etMetricWeight?.text.toString().toFloat()

                val bmi = weightvalue/(heightvalue*heightvalue)

                displayBMIResult(bmi)

            }else{
                Toast.makeText(this@BMIActivity,"Please enter Vaild Values", Toast.LENGTH_SHORT).show()
            }
        }else{
            if(validateUsunit()){
                val usUnitHeightvalueFeet:String = binding?.etUSmetricheightfeet?.text.toString()
                val usUnitHeightvalueInch: String = binding?.etUSmetricheightinch?.text.toString()
                val usUnitWeightvalue: Float= binding?.etUsMetricUnitWeight?.text.toString().toFloat()

                val heightvalue = (usUnitHeightvalueFeet.toFloat()*12) + usUnitHeightvalueInch.toFloat()

                val bmi = 703*(usUnitWeightvalue / (heightvalue*heightvalue))
                displayBMIResult(bmi)
            }
            else{
                Toast.makeText(this@BMIActivity,"Please enter Vaild Values", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun validateUsunit():Boolean{
        var isvalid = true
        if(binding?.etUSmetricheightfeet?.text.toString().isEmpty()){
            isvalid = false
        }else if(binding?.etUSmetricheightinch?.text.toString().isEmpty()){
            isvalid = false
        }
        else if(binding?.etUsMetricUnitWeight?.text.toString().isEmpty()){
            isvalid = false
        }
        return isvalid
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

}