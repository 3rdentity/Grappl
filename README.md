![Grappl Logo](https://dl.dropboxusercontent.com/u/34769058/grappl/glogo3.png)

Website: http://grappl.io/

Donations: http://grappl.io/donate

Grappl is a tool for hosting servers behind closed ports.
Other people don't have to download anything to connect to Grappl'd servers, unlike other programs such as hamachi, they
act just like normal listening-IP servers.

Grappl works by transferring data through relay servers on grappl.io.

The project is still in an alpha state. This repository contains all the newest client code, and some of the server and web code.

This project uses the library [Gson](https://github.com/google/gson). Remember to set it up on your IDE.
We'll start using Maven, soon.

## API

Using Grappl's client API is very simple and straightforward. Just create a GrapplBuilder, and use
the supplied methods to configure Grappl's state. Use build() to get the Grappl object,
then use the Grappl object's connect() method to connect to your relay server of choice. This
may be one of the official ones (n.grappl.io, e.grappl.io, p.grappl.io) or a custom one.

## License

Code in the com.daexsys.grappl.client package is released under GPL v3.

All other code (server/web) is All Rights Reserved. It will eventually be removed in a code cleanup.

## Development

Grappl is primarily developed by Matt Hebert.

His twitter is here: https://twitter.com/Cactose