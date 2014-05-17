package com.peterlavalle.eccentric.model

case class Table(name: String, links: Set[Link], columns: Set[Column], rows: Set[Row]) extends TNamed {

	def linkNameSet = links.map(_.name)

	def columnKeySet = columns.map(_.name)

	def row(uuid: String) =
		rows.filter(_.uuid == uuid).toList match {
			case List(row) =>
				row
		}

	def ++(column: Column) =
		(columns.filter(_.name == column.name) ++ links.filter(_.name == column.name)).toList match {
			case Nil =>
				Table(name, links, columns + column, rows)
		}

	def ++(link: Link) =
		(columns.filter(_.name == link.name) ++ links.filter(_.name == link.name)).toList match {
			case Nil =>
				Table(name, links + link, columns, rows)
		}

	def update(uuid: String, cells: Map[String, String]) =
		Table(name, links, columns, rows.filter(_.uuid != uuid) + Row(uuid, cells))
}
