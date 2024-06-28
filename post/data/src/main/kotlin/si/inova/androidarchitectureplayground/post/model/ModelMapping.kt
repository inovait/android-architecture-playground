package si.inova.androidarchitectureplayground.post.model

import si.inova.androidarchitectureplayground.post.network.model.LightPostDto
import si.inova.androidarchitectureplayground.post.network.model.PostDto
import si.inova.androidarchitectureplayground.post.sqldelight.generated.DbPost

fun DbPost.toPost(): Post {
   return Post(
      id = id.toInt(),
      title = title,
      body = body,
      userId = user_id?.toInt(),
      tags = tags?.split(","),
      numReactions = num_reactions?.toInt(),
      image = image_url
   )
}

fun PostDto.toPost(): Post {
   return Post(id = id, title = title, body = body, userId = userId, tags = tags)
}

fun LightPostDto.toPost(): Post {
   return Post(id = id, title = title)
}

fun LightPostDto.toDb(lastUpdate: Long): DbPost {
   return DbPost(
      id = id.toLong(),
      title = title,
      body = null,
      user_id = null,
      tags = null,
      num_reactions = null,
      image_url = null,
      full_data = false,
      last_update = lastUpdate
   )
}

fun PostDto.toDb(lastUpdate: Long): DbPost {
   return DbPost(
      id = id.toLong(),
      title = title,
      body = body,
      user_id = userId.toLong(),
      tags = tags.joinToString(","),
      num_reactions = (reactions.likes + reactions.dislikes).toLong(),
      image_url = image,
      full_data = true,
      last_update = lastUpdate
   )
}

fun Post.toDb(fullData: Boolean, lastUpdate: Long): DbPost {
   return DbPost(
      id = id.toLong(),
      title = title,
      body = body,
      user_id = userId?.toLong(),
      tags = tags?.joinToString(","),
      num_reactions = numReactions?.toLong(),
      image_url = image,
      full_data = fullData,
      last_update = lastUpdate
   )
}
