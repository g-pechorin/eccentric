package com.peterlavalle.eccentric.model

trait TNamed {
	val name: String
	assert(name.matches("^[\\.\\w]+$"))
}
