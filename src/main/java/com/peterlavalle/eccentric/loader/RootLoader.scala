package com.peterlavalle.eccentric.loader

import com.peterlavalle.eccentric.model.Root
import org.antlr.v4.runtime.{CommonTokenStream, ANTLRInputStream}
import org.antlr.v4.runtime.tree.ParseTreeWalker

object RootLoader extends (ANTLRInputStream => Root) with ((Loader, ANTLRInputStream) => Root)  {
	def apply(text: String): Root = apply(new Loader, new ANTLRInputStream(text))

	override def apply(stream: ANTLRInputStream): Root = apply(new Loader, stream)

	override def apply(loader: Loader, stream: ANTLRInputStream): Root = {
		ParseTreeWalker.DEFAULT.walk(
			loader,
			new LineParser(new CommonTokenStream(new LineLexer(stream))).tree_state())

		loader.root
	}
}
