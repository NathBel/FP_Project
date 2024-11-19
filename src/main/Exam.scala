package fr.umontpellier.ig5

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions.col
import org.neo4j.driver.{AuthTokens, GraphDatabase, Result}

object Exam {

  def main(args: Array[String]): Unit = {
    /*val logFile = "data/cve-2023.json" // Should be some file on your system

    val spark = SparkSession.builder
      .appName("CVE Filter")
      .master("local[*]")
      .getOrCreate()

    spark.sparkContext.setLogLevel("ERROR")

    val logData = spark.read.textFile(logFile).cache()

    // Sélectionnez et renommez les colonnes d'intérêt
    val filteredData = logData.select(
      col("cve.CVE_data_meta.ID").as("ID"),
      col("cve.description.description_data")(0)("value").as("Description"),
      col("impact.baseMetricV3.cvssV3.baseScore").as("baseScore"),
      col("impact.baseMetricV3.cvssV3.baseSeverity").as("baseSeverity"),
      col("impact.baseMetricV3.exploitabilityScore").as("exploitabilityScore"),
      col("impact.baseMetricV3.impactScore").as("impactScore")
    )

    filteredData.show(false)

    spark.stop()*/

    val uri = "bolt://localhost:7687" // Remplacez par l'URI de votre instance Neo4j
    val user = "neo4j" // Remplacez par votre nom d'utilisateur
    val password = "password" // Remplacez par votre mot de passe

    val driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password))
    val session = driver.session()

    // Requête Cypher pour récupérer et filtrer les données CVE
    val query =
      """
      MATCH (cve:CVE)
      RETURN
        cve.id AS ID,
        cve.description AS Description,
        cve.baseScore AS baseScore,
        cve.baseSeverity AS baseSeverity,
        cve.exploitabilityScore AS exploitabilityScore,
        cve.impactScore AS impactScore
      """

    // Exécution de la requête et traitement des résultats
    val result: Result = session.run(query)

    // Affichage des résultats
    while (result.hasNext) {
      val record = result.next()
      println(s"ID: ${record.get("ID").asString()}")
      println(s"Description: ${record.get("Description").asString()}")
      println(s"Base Score: ${record.get("baseScore").asDouble()}")
      println(s"Base Severity: ${record.get("baseSeverity").asString()}")
      println(s"Exploitability Score: ${record.get("exploitabilityScore").asDouble()}")
      println(s"Impact Score: ${record.get("impactScore").asDouble()}")
      println("----")
    }

    // Fermeture de la session et du driver
    session.close()
    driver.close()

  }

}
