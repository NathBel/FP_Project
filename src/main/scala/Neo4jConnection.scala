import org.apache.spark.sql.{SaveMode, SparkSession}

object Neo4jConnection {
    def main(args: Array[String]): Unit = {
      // Replace with the actual connection URI and credentials
      val url = "neo4j://localhost:7687"
      val username = "neo4j"
      val password = "password"
      val dbname = "neo4j"

      val spark = SparkSession.builder
        .config("neo4j.url", url)
        .config("neo4j.authentication.basic.username", username)
        .config("neo4j.authentication.basic.password", password)
        .config("neo4j.database", dbname)
        .appName("Spark App")
        .master("local[*]")
        .getOrCreate()

      val data2023 = spark.read.json("nvdcve-1.1-2023.json")
      val data2024 = spark.read.json("nvdcve-1.1-2024.json")

      // Write to Neo4j
      data2023.write
        .format("org.neo4j.spark.DataSource")
        .mode(SaveMode.Overwrite)
        .save()

      data2024.write
        .format("org.neo4j.spark.DataSource")
        .mode(SaveMode.Overwrite)
        .save()

      // Read from Neo4j
      val ds = spark.read
        .format("org.neo4j.spark.DataSource")
        .load()

      ds.show()
    }
  }

