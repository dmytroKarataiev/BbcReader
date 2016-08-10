# RssReader
![Animation of the app]()

Rss feed reader for a BBC network news feed. Works fast, utilises MVP architecture and several modern libraries to perform REST work, image downloading and caching.

# Requirements
* [Specifications](materials/Android Developer Interview Assignment.pdf).
* To be simple enough to make it under 8 hours - this caused me to make several sacrifices:
  * Realm instead of SQLite, therefore inability to use a ContentProvider.
  * Without a ContentProvider - no SyncAdapter, which would be the best way to perform battery-efficient regular updates.
* API to get data in XML format: [BBC](http://feeds.bbci.co.uk/news/rss.xml)
* Tablet Design.

# Features
* Used MVP Architecture for the app.
* Realm Database for caching and storing the data.
* Accessibility features: RTL, content descriptions, empty TextView when there are no articles.
* [Google Coding Style](https://source.android.com/source/code-style.html).
* ShareActionProvider to send links to friends.
* Material Design and Shared Transitions, reveal animations.
* Search of articles.
* Scroll to the top by clicking the ActionBar.
* Palette to have a nice-looking design.
* For API 21+ - auto-updates in background with notifications (JobScheduler).

# Libraries
* [Realm](https://realm.io) - fast and efficient database, which dramatically increases a speed of development.
* [Butterknife](http://jakewharton.github.io/butterknife/) - boilerplate code cutter, improves speed of development, makes code easier to read.
* [Retrofit](http://square.github.io/retrofit/) - basically the standard in the Android development for all REST-related works.
* [RxJava](https://github.com/ReactiveX/RxAndroid), [Retrolambda](https://github.com/evant/gradle-retrolambda) - most modern way to manage streams of data and asynctasks and a plugin to make us of nice Java 8 lambda syntax.
* [Picasso](http://square.github.io/picasso/) - one of the best image downloading and caching libraries for Android, increases speed of development.

# Used materials
* BBC Icon - [www.iconarchive.com](http://www.iconarchive.com/show/circle-icons-by-martz90/bbc-news-icon.html).
* BBC Logo - [wikimedia.org](https://upload.wikimedia.org/wikipedia/commons/e/eb/BBC.svg).

License
-------

	The MIT License (MIT)

	Copyright (c) 2016 Dmytro Karataiev

	Permission is hereby granted, free of charge, to any person obtaining a copy
	of this software and associated documentation files (the "Software"), to deal
	in the Software without restriction, including without limitation the rights
	to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
	copies of the Software, and to permit persons to whom the Software is
	furnished to do so, subject to the following conditions:

	The above copyright notice and this permission notice shall be included in all
	copies or substantial portions of the Software.

	THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
	IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
	FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
	AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
	LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
	OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
	SOFTWARE.