# A proxy for all the reactive kitten banners you need.

This is the successor of [kittenproxy](https://github.com/antoneliasson/kittenproxy), the immensely popular cat image provider conceived by yours truly. Reactive Kitten Proxy is a complete rewrite in Scala using Play Framework and reactive programming techniques.

Killing Floor 2 Dedicated Server allows the administrator to specify a URL to a PNG image to use as the banner on the welcome screen. Naturally, a zombie shooter game should have a cute cat image as its banner, kindly provided by [The Cat API](http://thecatapi.com/).

However, The Cat API responds with a HTTP redirect to the actual image file, and the Killing Floor 2 client doesn't follow redirects, so it can't be used straight out of the box. This is where the *Reactive Kitten Proxy* comes in. It acts as a stupid proxy that requests an image from The Cat API and serves it with a HTTP 200 header that KF2 can understand.

Moreover, it would be *more funneh* if every player logging into the server for the same game (roughly simultaneously) received the same cat image, so that its cuteness can be discussed in the lobby before entering the battleground to shoot zombies. For this reason, *Reactive Kitten Proxy* caches the cat image for ten minutes before replacing it with a new one.

There is also a simple web application that presents all the images that have been used for the last (configurable) 24 hours along with their source URL. Useful if a visitor finds an image particularly funny and would like to find out where it came from. It is accessed through the root URL path, while the dynamic cat images are served through `/kitten.png`.

## Installation

TODO

## Screenshot

See https://www.antoneliasson.se/journal/reactive-kitten-proxy/

## Demo

[http://kittenproxy.antoneliasson.se/kitten.png](http://kittenproxy.antoneliasson.se/kitten.png) (updates every ten minutes).
