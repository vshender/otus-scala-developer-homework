name := "otus-scala-developer-homework"

version := "0.1"

scalaVersion := "2.13.4"

libraryDependencies ++= Seq(
    "org.postgresql"              % "postgresql"                      % "42.2.2"  % Test,
    "org.scalatest"              %% "scalatest"                       % "3.2.3"   % Test,
    "org.scalikejdbc"            %% "scalikejdbc"                     % "3.5.0"   % Test,
    "org.scalikejdbc"            %% "scalikejdbc-test"                % "3.5.0"   % Test,
    "com.dimafeng"               %% "testcontainers-scala-postgresql" % "0.38.8"  % Test,
    "com.dimafeng"               %% "testcontainers-scala-scalatest"  % "0.38.8"  % Test,
    "com.typesafe.slick"         %% "slick"                           % "3.3.3",
    "org.flywaydb"                % "flyway-core"                     % "7.3.2",
    "org.scalacheck"             %% "scalacheck"                      % "1.14.3"  % Test,
    "org.scalatestplus"          %% "scalacheck-1-14"                 % "3.2.0.0" % Test,
    "com.github.alexarchambault" %% "scalacheck-shapeless_1.14"       % "1.2.3"   % Test
)
