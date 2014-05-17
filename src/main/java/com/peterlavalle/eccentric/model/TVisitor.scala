package com.peterlavalle.eccentric.model

trait TVisitor extends ((Table, Row, Map[String, String], Map[Table, Set[Row]]) => Unit) {
	def apply(table: Table, row: Row, attributes: Map[String, String], remotes: Map[Table, Set[Row]])
}
