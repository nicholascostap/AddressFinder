package com.example.addressfinder

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.addressfinder.controllers.Client
import com.example.addressfinder.models.Address
import com.example.addressfinder.models.ResponseAddress
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainLayout)) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        val fieldPostalCode = findViewById<EditText>(R.id.fieldPostalCode)
        val btnSearch = findViewById<Button>(R.id.btnSearch)

        btnSearch.setOnClickListener {
            val postalCode = formatPostalCode(fieldPostalCode.text.toString())

            if (validatePostalCode(postalCode)) {
                getAddress(postalCode)
            } else {
                Toast.makeText(this, "Invalid postal code", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun formatPostalCode(postalCode: String): String {
        return postalCode.replace(Regex("\\D"), "")
    }

    private fun validatePostalCode(postalCode: String): Boolean {
        if (postalCode.isEmpty()) {
            return false
        }

        if (postalCode.length < 8 || postalCode.length > 8) {
            return false
        }

        return true
    }

    private fun getAddress(postalCode: String) {

        Client.instance.getAddress(postalCode)
            .enqueue(
                object : Callback<ResponseAddress> {

                    override fun onResponse(
                        call: Call<ResponseAddress>,
                        response: Response<ResponseAddress>
                    ) {
                        if (response.isSuccessful) {
                            val result = response.body()

                            val address = Address(
                                result?.logradouro ?: "",
                                result?.bairro ?: "",
                                result?.localidade ?: "",
                                result?.cep ?: "",
                                result?.estado ?: ""
                            )

                            with(findViewById<TextView>(R.id.addressTextView)) {
                                text = """
                            Street: ${address.street}
                            District: ${address.district}
                            City: ${address.city}
                            Postal Code: ${address.postalCode}
                            State: ${address.state}
                        """.trimIndent()
                            }

                        } else {
                            with(findViewById<TextView>(R.id.addressTextView)) {
                                text = "Address not found"
                            }
                        }
                    }

                    override fun onFailure(call: Call<ResponseAddress>, t: Throwable) {
                        with(findViewById<TextView>(R.id.addressTextView)) {
                            text = "Error: ${t.message}"
                        }
                    }
                }
            )
    }
}