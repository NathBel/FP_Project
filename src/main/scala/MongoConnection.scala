import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.types._
import org.mongodb.scala._
import scala.concurrent.ExecutionContext.Implicits.global

object Main extends App {

  // Initialize Spark
  val spark = SparkSession.builder()
    .appName("ScalaProject")
    .master("local[*]")
    .getOrCreate()
  import spark.implicits._

  // 1. Define schema for JSON data
  val schema = StructType(Seq(
    StructField("id", StringType, true),
    StructField("description", StringType, true),
    StructField("impactScore", DoubleType, true),
    StructField("year", IntegerType, true)
  ))

  // 2. Load JSON from NIST
  val jsonFile = "nvdcve-1.1-2023.json"
  val rawData = spark.read.schema(schema)
    .option("mode", "PERMISSIVE") // Handle corrupted records
    .json(jsonFile)

  // Show loaded data for verification
  rawData.show()

  // 3. Connect to MongoDB Atlas
  val mongoClient: MongoClient = MongoClient("mongodb+srv://bellusnathan:oLkTmo78dn4cXjJK@cluster0.pe4iy.mongodb.net/?retryWrites=true&w=majority")
  val database: MongoDatabase = mongoClient.getDatabase("Cluster0")
  val collection: MongoCollection[Document] = database.getCollection("myCollection")

  // 4. Write data to MongoDB Atlas
  rawData.collect().foreach { row =>
    val document = Document(
      "id" -> row.getAs[String]("id"),
      "description" -> row.getAs[String]("description"),
      "impactScore" -> row.getAs[Double]("impactScore"),
      "year" -> row.getAs[Int]("year")
    )
    // Insert the document into MongoDB
    collection.insertOne(document).toFuture().onComplete {
      case scala.util.Success(_) => println("Document inserted successfully")
      case scala.util.Failure(exception) => println(s"Failed to insert document: ${exception.getMessage}")
    }
  }

  // 5. Read data from MongoDB into Spark DataFrame
  val df = spark.read.format("mongodb")
    .option("uri", "mongodb+srv://bellusnathan:oLkTmo78dn4cXjJK@cluster0.pe4iy.mongodb.net/?retryWrites=true&w=majority/Cluster0.myCollection")
    .load()

  df.show()

  // Close resources
  mongoClient.close()
  spark.stop()
}
