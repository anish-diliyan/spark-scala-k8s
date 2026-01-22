import org.apache.spark.sql.SparkSession

object BasicSparkJob {

  def main(args: Array[String]): Unit = {

    val spark = SparkSession.builder()
      .appName("Spark Scala Demo")
      .master("local[*]")
      .getOrCreate()

    val data = Seq(("Anish", 28), ("Rahul", 32), ("Amit", 25))

    val df = spark.createDataFrame(data).toDF("name", "age")

    df.show()
    spark.stop()
  }
}

