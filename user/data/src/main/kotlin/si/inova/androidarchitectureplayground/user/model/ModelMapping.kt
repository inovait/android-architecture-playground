package si.inova.androidarchitectureplayground.user.model

import si.inova.androidarchitectureplayground.user.network.model.LightUserDto
import si.inova.androidarchitectureplayground.user.network.model.UserDto
import si.inova.androidarchitectureplayground.user.sqldelight.generated.DbUser

fun LightUserDto.toDb(lastUpdate: Long): DbUser {
   return DbUser(
      id = id.toLong(),
      first_name = firstName,
      last_name = lastName,
      maiden_name = null,
      age = null,
      gender = null,
      email = null,
      phone = null,
      hair_color = null,
      hair_type = null,
      full_data = false,
      last_update = lastUpdate
   )
}

fun LightUserDto.toUser(): User {
   return User(
      id = id,
      firstName = firstName,
      lastName = lastName
   )
}

fun UserDto.toDb(lastUpdate: Long): DbUser {
   return DbUser(
      id = id.toLong(),
      first_name = firstName,
      last_name = lastName,
      maiden_name = maidenName,
      age = age.toLong(),
      gender = gender,
      email = email,
      phone = phone,
      hair_color = hair.color,
      hair_type = hair.type,
      full_data = true,
      last_update = lastUpdate
   )
}

fun DbUser.toUser(): User {
   val hair = if (hair_color != null && hair_type != null) {
      User.Hair(color = hair_color, type = hair_type)
   } else {
      null
   }

   return User(
      id = id.toInt(),
      firstName = first_name,
      lastName = last_name,
      maidenName = maiden_name,
      age = age?.toInt(),
      gender = gender,
      email = email,
      phone = phone,
      hair = hair
   )
}

fun User.toDb(fullData: Boolean, lastUpdate: Long): DbUser {
   return DbUser(
      id = id.toLong(),
      first_name = firstName,
      last_name = lastName,
      maiden_name = maidenName,
      age = age?.toLong(),
      gender = gender,
      email = email,
      phone = phone,
      hair_color = hair?.color,
      hair_type = hair?.type,
      full_data = fullData,
      last_update = lastUpdate
   )
}
