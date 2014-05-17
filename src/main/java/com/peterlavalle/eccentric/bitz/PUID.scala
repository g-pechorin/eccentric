package com.peterlavalle.eccentric.bitz

/**
 * Creates a probably unique ID
 */
object PUID {
	def apply = java.util.UUID.randomUUID().toString
}
