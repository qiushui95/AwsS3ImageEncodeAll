import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.SqlExpressionBuilder.isNotNull
import org.jetbrains.exposed.sql.Table

object S3ObjectTable : Table() {

    private val id: Column<EntityID<Int>> = integer("id").autoIncrement().entityId()

    override val primaryKey = PrimaryKey(id)

    val s3Key = varchar("s3_key", 1024).uniqueIndex()

    val extension = varchar("extension", 10)

    val fileType = varchar("file_type", 10).nullable()

    val hasDeal = bool("has_deal").default(false)
}