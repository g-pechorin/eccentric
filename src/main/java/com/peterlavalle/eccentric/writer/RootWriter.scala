package com.peterlavalle.eccentric.writer

import com.peterlavalle.eccentric.model.{Column, Row, Root}

object RootWriter extends (Root => String) {

	def nullable(text: String) = if (text != null) "\"" + text + "\"" else "null"

	override def apply(root: Root) = {

		def column(data: Column) = "\"" + data.name + "\" = " + nullable(data.default)

		def cell(data: (String, String)) = " \"" + data._1 + "\" -> " + nullable(data._2)

		def row(data: Row) = data.cells.toList.map(cell).foldLeft(" # \"" + data.uuid + "\" {")(_ + "\n\t" + _) + "\n}"

		List[String](
			// write tables
			root.tables.map(table => "table \"" + table.name + "\"\n").foldLeft("")(_ + _),

			// write links
			root.tables.map(table => table.links.map(link => "link \"" + table.name + "\" => \"" + link.name + "\"\n").foldLeft("")(_ + _)).foldLeft("")(_ + _ + "\n"),

			// write columns
			root.tables.map(table => table.columns.map(column).foldLeft("")(_ + "column \"" + table.name + "\" : " + _ + "\n")).foldLeft("")(_ + _ + "\n"),

			// write rows
			root.tables.map(table => table.rows.map(row).foldLeft("")(_ + "row \"" + table.name + "\" " + _ + "\n\n")).foldLeft("")(_ + _)
		).foldLeft("hash \"" + Integer.toHexString(root.hashCode()) + "\"\n\n")(_ + _)
	}
}
