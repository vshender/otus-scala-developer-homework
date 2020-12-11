name := "otus-scala-developer-homework"

version := "0.1"

scalaVersion := "2.12.7"


libraryDependencies ++= Seq(
  "ru.yandex.qatools.embed" % "postgresql-embedded"  % "2.9" % Test,
  "org.postgresql"          % "postgresql"           % "42.2.2" % Test,

    "org.scalatest"           %% "scalatest"            % "3.2.3" % Test,
    "org.scalikejdbc" %% "scalikejdbc"                  % "3.5.0" % Test,
    "ch.qos.logback"  %  "logback-classic"              % "1.2.3" % Test,
    "org.scalikejdbc" %% "scalikejdbc-test"             % "3.5.0"   % Test
)
