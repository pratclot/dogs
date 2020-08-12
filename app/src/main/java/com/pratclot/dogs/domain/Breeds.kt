package com.pratclot.dogs.domain

data class Breeds(
    val message: Map<String, List<String>>,
    val status: String
) {
    fun toList(): List<Breed> {
        val res = this.message.flatMap {
            listOf(it.toBreed())
        }
        return res
    }

    fun getOne(breed: Breed): Breed {
        return Breed(breed.name, message[breed.name] ?: error(""))
    }
}

data class Breed(
    val name: String,
    val subBreeds: List<String>
) {
    fun hasSubBreeds(): Boolean {
        return subBreeds.isNotEmpty()
    }

    fun getSubBreads(): List<Breed> {
        return subBreeds.flatMap {
            listOf(Breed(it, emptyList()))
        }
    }
}

fun Map.Entry<String, List<String>>.toBreed(): Breed {
    return Breed(key, value)
}

data class BreedImages(
    val message: List<String>,
    val status: String
) {
    fun toList(): List<BreedImage> {
        return message.flatMap {
            listOf(BreedImage(it))
        }
    }
}

data class BreedImage(
    val imageUrl: String
) {
    val parsedUrl = imageUrl.split("/")
    val breed: String = when (parsedUrl.size) {
        6 -> parsedUrl[4]
        else -> parsedUrl[5]
    }
    val parentBreed: String? = when (parsedUrl.size) {
        6 -> null
        else -> parsedUrl[4]
    }
}