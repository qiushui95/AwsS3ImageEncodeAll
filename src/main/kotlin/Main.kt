import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request

fun main(args: Array<String>): Unit = runBlocking {

    Database.connect(
        "jdbc:mysql://13.229.134.152:3306/S3Image",
        driver = "com.mysql.cj.jdbc.Driver",
        user = "root",
        password = "12356789"
    )

    val execType = args[0]

    val s3Client = S3Client.builder().build()

    transaction {
        SchemaUtils.create(S3ObjectTable)

        addLogger(StdOutSqlLogger)

        when (execType) {
            "list" -> listS3(s3Client)
            else -> println("不认识的execType($execType)")
        }

    }
}

private fun listS3(s3Client: S3Client) {
    val request = ListObjectsV2Request.builder()
        .bucket("res-southeast")
        .build()

    val response = s3Client.listObjectsV2(request)

    var count = 0

    while (response.isTruncated) {
        for (s3Object in response.contents()) {
            count++
            val s3Key = s3Object.key()

            val extension = s3Key.substring(s3Key.lastIndexOf('.'))

            S3ObjectTable.insert {
                it[S3ObjectTable.s3Key] = s3Key
                it[S3ObjectTable.extension] = extension
            }
        }

        println("已处理${count}个")
    }
}