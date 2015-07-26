![Grappl Logo](https://dl.dropboxusercontent.com/u/34769058/grappl/glogo3.png)

Website: http://grappl.io/

Donations: http://grappl.io/donate

Grappl is a tool for hosting servers behind closed ports.
People connecting don't have to download anything to connect to Grappl'd servers, unlike other programs such as hamachi.

It works by transferring data through relay servers on grappl.io.

The project is still in an alpha state. This repository contains all the newest client code.

## API

Using Grappl's client API is very simple and straightforward.

Just create a GrapplBuilder, use the supplied methods to configure Grappl's state, then use build() to create the Grappl object. Like so:

    Grappl grappl = new GrapplBuilder().atLocalPort(25565).build();

After that, use the grappl object's connect method to open the server.

    grappl.connect(relayServerIP);

'relayserverIP' is a string representing the relay server of your choice. This may be one of the official ones (n.grappl.io, e.grappl.io, p.grappl.io) or a custom one.

    grappl.getPublicAddress();

...will give you the address that your server is now live on the internet behind! Congrats, you've got Grappl working through its API. It's that simple.

## License

The Grappl client code is released under LGPL v2.1.

## Development

Grappl is primarily developed by Matt Hebert.

His twitter is here: https://twitter.com/Cactose