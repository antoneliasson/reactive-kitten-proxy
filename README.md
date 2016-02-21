# A proxy for all the reactive kitten banners you need.

This is the successor of [kittenproxy](https://github.com/antoneliasson/kittenproxy), the immensely popular cat image provider conceived by yours truly. Reactive Kitten Proxy is a complete rewrite in Scala using Play Framework and reactive programming techniques.

Killing Floor 2 Dedicated Server allows the administrator to specify a URL to a PNG image to use as the banner on the welcome screen. Naturally, a zombie shooter game should have a cute cat image as its banner, kindly provided by [The Cat API](http://thecatapi.com/).

However, The Cat API responds with a HTTP redirect to the actual image file, and the Killing Floor 2 client doesn't follow redirects, so it can't be used straight out of the box. This is where the *Reactive Kitten Proxy* comes in. It acts as a stupid proxy that requests an image from The Cat API and serves it with a HTTP 200 header that KF2 can understand.

Moreover, it would be *more funneh* if every player logging into the server for the same game (roughly simultaneously) received the same cat image, so that its cuteness can be discussed in the lobby before entering the battleground to shoot zombies. For this reason, *Reactive Kitten Proxy* caches the cat image for ten minutes before replacing it with a new one.

There is also a simple web application that presents all the images that have been used for the last (configurable) 24 hours along with their source URL. Useful if a visitor finds an image particularly funny and would like to find out where it came from. It is accessed through the root URL path, while the dynamic cat images are served through `/kitten.png`.

## Installation

Until binaries are provided, you will need the full development environment and build your own. Install [Typesafe Activator](https://www.typesafe.com/activator/download). Pull down this repository and cd into it. Start `activator`. To run the application in development mode type `run`. To build Debian packages type `debian:packageBin`. The packages have been tested in Debian 8 (Jessie).

By default the application uses an in-memory database (since the database is just a cache anyway). Its contents are cleared on every shutdown so every time the application is started, the database needs to be initialized with a table. This is done automatically in production mode. In development mode you simply open the application in a web browser and click *"Apply this script now!"* when the message *"Database 'default' needs evolution!"* is shown.

Example:

```sh
[5.0.7]anton@balder:~/git/reactive-kitten-proxy> activator
[info] Loading global plugins from /home/anton/.sbt/0.13/plugins
[info] Updating {file:/home/anton/.sbt/0.13/plugins/}global-plugins...
[info] Resolving org.fusesource.jansi#jansi;1.4 ...
[info] Done updating.
[info] Loading project definition from /home/anton/git/reactive-kitten-proxy/project
[info] Updating {file:/home/anton/git/reactive-kitten-proxy/project/}reactive-kitten-proxy-build...
[info] Resolving org.fusesource.jansi#jansi;1.4 ...
[info] Done updating.
[info] Set current project to reactive-kitten-proxy (in build file:/home/anton/git/reactive-kitten-proxy/)
[reactive-kitten-proxy] $ run

--- (Running the application, auto-reloading is enabled) ---

[info] p.c.s.NettyServer - Listening for HTTP on /0:0:0:0:0:0:0:0:9000

(Server started, use Ctrl+D to stop and go back to the console...)

[info] - application - Creating Pool for datasource 'default'
[info] - play.api.db.DefaultDBApi - Database [default] connected at jdbc:h2:mem:play
[info] - play.api.Play - Application started (Dev)
[info] - application - Shutting down connection pool.

[success] Total time: 12 s, completed Feb 21, 2016 10:54:38 AM
[reactive-kitten-proxy] $ debian:packageBin
[info] Wrote /home/anton/git/reactive-kitten-proxy/target/scala-2.11/reactive-kitten-proxy_2.11-1.0.1.pom
[info] Building debian package with native implementation
[info] dpkg-deb: building package `reactive-kitten-proxy' in `../reactive-kitten-proxy_1.0.1_all.deb'.
[success] Total time: 13 s, completed Feb 21, 2016 10:55:39 AM
```

The Debian package installs a systemd service descriptor, allowing the application to be easily controlled using `systemctl {status,start,stop,enable,disable} reactive-kitten-proxy.service`.

## Screenshot

See https://www.antoneliasson.se/journal/reactive-kitten-proxy/

## Demo

[http://kittenproxy.antoneliasson.se/kitten.png](http://kittenproxy.antoneliasson.se/kitten.png) (updates every ten minutes).
