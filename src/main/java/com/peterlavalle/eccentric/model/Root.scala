package com.peterlavalle.eccentric.model

case class Root(tables: Set[Table] = Set()) {

	def table(name: String) =
		tables.filter(_.name == name).toList match {
			case List(r) => r
		}

	def apply(name: String) = new TTransaction {
		override def ?(visitor: ((Table, Row, Map[String, String], Map[Table, Set[Row]]) => Unit)) {
			tables
				.filter(_.name.matches(name))
				.foreach(table => table.rows.foreach(row => {

				def allCells(table: Table, row: Row): Map[String, String] = {
					val rowCells: Map[String, String] = row.cells.filter(table.columnKeySet contains _._1)
					val defaultCells: Map[String, String] = table.columns.filterNot(rowCells.keySet contains _.name).map(column => column.name -> column.default).toMap
					val linkedCells: Map[String, String] =
						table.links.map(link => {
							val linkTable: Table = Root.this.table(link.name)
							val linkRow = linkTable.row(row.cells(link.name))

							allCells(linkTable, linkRow).toList.map {
								case (key, value) =>
									(link.name + "/" + key) -> value
							}.toList
						}).flatten.toMap

					rowCells ++ defaultCells ++ linkedCells
				}

				visitor(table, row,
					allCells(table, row),
					// now, find remote rows that point at us
					tables
						.filter(_.linkNameSet contains name)
						.map(remoteTable => remoteTable -> remoteTable.rows.filter(remoteRow => remoteRow.cells(name) == row.uuid))
						.toMap)
			}))
		}

		override def update(uuid: String, cells: Map[String, String]) =
			try {
				(tables.filter(_.name == name).toList, tables.filter(_.name != name)) match {
					case (Nil, _) =>
						println(name)
						tables.map(t => t.name -> (t.name == name) -> t).foreach(println)
						throw new IllegalArgumentException("No such table `" + name + "`")
					case (List(table: Table), others: Set[Table]) =>
						val replacement = table(uuid) = cells
						Root(others + replacement)
				}
			} catch {
				case e: MatchError =>
					println("???")
					throw e
			}

		override def create: Root =
			tables.filter(table => table.name == name).toList match {
				case Nil =>
					Root(tables + Table(name, Set(), Set(), Set()))
			}

		override def get: Table = tables.filter(table => table.name != name).head

		override def link(remote: String): Root =
			(tables.filter(_.name == name).toList, tables.filterNot(_.name == name)) match {
				case (List(table: Table), others: Set[Table]) =>
					val replacement = table ++ Link(remote)
					Root(others + replacement)
			}


		override def require(column: String): Root =
			(tables.filter(_.name == name).toList, tables.filter(_.name != name)) match {
				case (List(table: Table), others: Set[Table]) =>
					val replacement = table ++ Column(column, null)
					Root(others + replacement)
			}

		override def update(column: String, default: String): Root =
			(tables.filter(_.name == name).toList, tables.filter(_.name != name)) match {
				case (List(table: Table), others: Set[Table]) =>
					val replacement = table ++ Column(column, default)
					Root(others + replacement)
			}
	}
}

