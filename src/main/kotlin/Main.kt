import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.AwsCredentials
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request

fun main(args: Array<String>): Unit = runBlocking {

    Database.connect(
        "jdbc:mysql://127.0.0.1:3306/S3Image",
        driver = "com.mysql.cj.jdbc.Driver",
        user = "root",
        password = "12356789"
    )

    val execType = args[0]

    println("当前execType($execType)")

    val s3Client = S3Client.builder()
//        .credentialsProvider(
//            StaticCredentialsProvider.create(
//                AwsBasicCredentials.create(
//                    "AKIAWTEKVF6PS6A2LSXV",
//                    "Lf254Nw7d5kxRjeQ5etDQKugfD1XL+0aBzHq4Kwn"
//                )
//            )
//        )
        .build()

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

            val lastDotIndex = s3Key.lastIndexOf('.')

            val extension = if (lastDotIndex == -1 || lastDotIndex + 1 >= s3Key.length) {
                ""
            } else {
                s3Key.substring(lastDotIndex + 1)
            }

            S3ObjectTable.insert {
                it[S3ObjectTable.s3Key] = s3Key
                it[S3ObjectTable.extension] = extension
            }
        }

        println("已处理${count}个")
    }
}