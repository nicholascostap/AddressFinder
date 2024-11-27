package com.example.addressfinder.controllers

import com.example.addressfinder.models.ResponseAddress
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface Service {
    @GET("{cep}/json")
    fun getAddress(@Path("cep") cep: String): Call<ResponseAddress>
}