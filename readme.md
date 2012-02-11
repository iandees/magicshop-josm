Installation
============

1. Download [the most recent version](https://github.com/iandees/magicshop-josm/downloads) and place the JAR in your JOSM plugins directory. (usually `~/.josm/plugins`)

2. Start JOSM and enable the plugin via the plugin preferences panel.


Compilation
============

1. Run `mvn package` in the root directory of the project.

2. Copy the resulting jar file from the `target/` directory to your JOSM plugins directory (usually `~/.josm/plugins`).

3. Start up JOSM and enable the plugin via the plugin preferences panel.

Usage
=====

1. Switch to Bing aerial layer.

2. Find a small section of road by zooming/panning. There is some arbitrary upper limit on size of the query to the Bing API.

3. Click the small ring icon on the toolbar to enable the Magicshop mode.

4. Click once in the middle of the road to place the first point.

5. Click once in the middle of the road (in a difference location) to place the second point. At this point JOSM will (silently) make the API call. When the data is returned it will show up in the most recently-used data layer as a new way.

Links
=====

* http://magicshop.cloudapp.net/
