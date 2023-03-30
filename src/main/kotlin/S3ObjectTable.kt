import org.jetbrains.exposed.dao.id.IntIdTable

object S3ObjectTable : IntIdTable() {

    val s3Key = varchar("s3_key", 1024).uniqueIndex()

    val extension = varchar("extension", 10)

    val fileType = varchar("file_type", 10).nullable()

    val hasDeal = bool("has_deal").default(false)
}