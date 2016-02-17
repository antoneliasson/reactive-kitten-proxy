name := """reactive-kitten-proxy"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala, DebianPlugin)

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  jdbc,
  cache,
  ws,
  specs2 % Test,
  "com.typesafe.play" %% "anorm" % "2.5.0",
  evolutions
)

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"

// Play provides two styles of routers, one expects its actions to be injected, the
// other, legacy style, accesses its actions statically.
routesGenerator := InjectedRoutesGenerator

maintainer in Linux := "Anton Eliasson <devel@antoneliasson.se>"

packageSummary in Linux := "A reactive proxy for all your kitten banner needs"

packageDescription :=
  """Reactive Kitten Proxy caches images from The Cat API. The intended use is
    | to provide a banner for the server welcome message in Killing Floor 2 (or
    | any other game server with a welcome screen). Images are cached for a few
    | (configurable) minutes before they are replaced by a new cat image.
    | There is also a simple web site that presents all the images that have
    | been used for the last (configurable) 24 hours along with its source URL.
    | Useful if a user finds an image particularly funny and would like to find
    | out where it came from.""".stripMargin

import com.typesafe.sbt.packager.archetypes.ServerLoader

serverLoading in Debian := ServerLoader.Systemd
