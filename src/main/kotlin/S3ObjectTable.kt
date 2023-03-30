import org.jetbrains.exposed.dao.id.IntIdTable

object S3ObjectTable : IntIdTable() {

    val s3Key = varchar("s3_key", 1024)

    val extension = varchar("extension", 10)

    val fileSize = long("file_size")

    val createTime = long("create_time")

    val fileType = varchar("file_type", 10).nullable()

    val hasDeal = bool("has_deal").default(false)
}