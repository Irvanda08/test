import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.test.data.UserRepository
import com.example.test.data.pref.UserModel
import com.example.test.data.response.UpdatePasswordResponse
import com.example.test.data.retrofit.ApiConfig
import com.example.test.data.retrofit.PasswordChangeRequest
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditPasswordViewModel(private val repository: UserRepository) : ViewModel() {
    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }
    fun changePassword(token: String, userId: String, oldPassword: String, newPassword: String, confirmPassword: String, onSuccess: (String) -> Unit, onError: (String) -> Unit) {
        val apiService = ApiConfig.getApiService()
        val request = PasswordChangeRequest(oldPassword, newPassword, confirmPassword)

        viewModelScope.launch {
            val call = apiService.changePassword("Bearer $token", userId, request)
            call.enqueue(object : Callback<UpdatePasswordResponse> {
                override fun onResponse(call: Call<UpdatePasswordResponse>, response: Response<UpdatePasswordResponse>) {
                    if (response.isSuccessful) {
                        response.body()?.let {
                            it.message?.let { it1 -> onSuccess(it1) }
                        }
                    } else {
                        onError("Failed to update password: ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<UpdatePasswordResponse>, t: Throwable) {
                    onError("Failed to update password: ${t.message}")
                }
            })
        }
    }
}
