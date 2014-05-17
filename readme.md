Eccentric Data Tree
===================

This is a "hold my beer" attempt to create an embeddable relational immutable plain-text database.
I wanted it to replace JSON based spiderwebs.
It is built with plain-old Maven.
For now it's on a DropBox repository `https://dl.dropboxusercontent.com/u/15094498/maven/repository-SNAPSHOT/`
Someday ... maybe I'll bother with Central

		<dependency>
			<groupId>com.peterlavalle.eccentric</groupId>
			<artifactId>eccentric</artifactId>
		</dependency>

It should be;
 * ACID compliant - every interaction creates a new tree in memory
 * immutable - there's no way to alter the system's state
 * thread safe - see above
 * human decipherable - the text can be eyeballed to see what's what
 * merge-proof - the text files save a hash key that's checked when you load it
 * mostly tested (the remote join parameter isn't tested currently)

It should not (be required to) be;
 * idiot-proof (don't create cyclic links)
 * Eclipse friendly (It's a Maven project; not an Eclipse project configured through Maven dammit!)
 * cryptographically unique (the GUID thing; it works well enough)
 * tamper resistant (the hash key can be recalculated; I don't care though and neither should you)
 * easily human editable (srrsly! why're you edittin dumped data!?)
 * super-duper-fast (speed is a trap where you start cutting off features for the sake of benchmarks nobody will ever care about. fish climb trees)
 * ubiquitous (it will never replace XML for data interchange)
 * native / Android / iOS / whatever (it's meant to run at compile time - not to be distributed!)
 * SQL / UnQL / NoSQL usable (it'd for embedding remember? the *QL languages work for data exchange)

TODO
----

It doesn't do any error checking

... vs JSON
-----------

JSON is a widely understood format.
... but ... if you're storing trees of data in it you're making a leap.
If you're looking at the .json files; you're doing something very, very wrong.
The issue is that humans shouldn't be manipulating text structures by hand; that's what computers are for!
In the scenario I envisage; JSON gives you a way to structure your data on disk, but you still need something to manipulate that data.
This tool gives you a closed box with some buttons - you can open the box but you really shouldn't have to.
It enforces some bits of relationally logic for you; JSON does not.

... and I could have written this guy against JSON but it would have sucked!
