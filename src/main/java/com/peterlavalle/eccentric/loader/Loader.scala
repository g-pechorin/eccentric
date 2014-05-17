package com.peterlavalle.eccentric.loader

import scala.collection.JavaConversions._
import com.peterlavalle.eccentric.loader.LineParser._
import org.antlr.v4.runtime.tree.ParseTree
import com.peterlavalle.eccentric.model.Root


class Loader extends LineBaseListener {

	def asText(rule: ParseTree) =
		if (rule.getText == "null")
			null
		else {
			assert(rule.getText.matches("^\"([^\"]*)\"$"))
			rule.getText.replaceAll("^\"([^\"]*)\"$", "$1")
		}


	override def exitTree_state(node: Tree_stateContext) {
		val actual: String = Integer.toHexString(root.hashCode())
		val expected: String = asText(node.STRING())
				if (actual != expected)
					throw new IllegalArgumentException("The hash code didn't match")
	}

	var root = Root(Set())

	override def enterAdd__link(node: Add__linkContext) {
		root = root(asText(node.table_name())).link(asText(node.column_name()))
	}

	override def enterAdd__row(node: Add__rowContext) {
		root =
			root(asText(node.table_name()))(asText(node.uuid())) =
				(0 until node.column_name().length)
					.map(index => asText(node.column_name()(index)) -> asText(node.nullable(index)))
					.toMap
	}

	override def enterAdd__column(node: Add__columnContext) {
		root = root(asText(node.table_name()))(asText(node.column_name())) = asText(node.nullable())
	}

	override def enterAdd__table(node: Add__tableContext) {
		root = root(asText(node.table_name())).create
	}
}
