package com.peterlavalle.eccentric.model

import com.peterlavalle.eccentric.bitz.PUID

/**
 * Not really a transaction since all operations are inherently ACID - they just produce different versions of the database
 */
trait TTransaction {
	def ?(visitor: ((Table, Row, Map[String, String], Map[Table, Set[Row]]) => Unit))

	def update(uuid: String, cells: Map[String, String]): Root

	def ++(cells: Map[String, String]): (Root, String) = {
		val uuid = PUID.apply

		update(uuid, cells) -> uuid
	}

	def link(table: String): Root

	def update(column: String, default: String): Root

	def create: Root

	def require(column: String): Root

	def get: Table
}
