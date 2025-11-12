package barlovinto.ricky.myslambook

import android.net.Uri
import java.io.Serializable

data class UserProfile(
    val defineLove: String,
    val defineFriendship: String,
    val describeMe: String,
    val adviceForMe: String,
    val rating: Float,
    val photoUri: String? // Uri as String para maipasa sa Intent
) : Serializable
