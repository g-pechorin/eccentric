package com.peterlavalle.eccentric

import junit.framework.Assert._
import junit.framework.TestCase
import com.peterlavalle.eccentric.model.{Row, Table, TVisitor, Root}
import com.peterlavalle.eccentric.writer.RootWriter
import com.peterlavalle.eccentric.loader.RootLoader
import org.antlr.v4.runtime.ANTLRInputStream
import org.easymock.EasyMock

class BasicTest extends TestCase {
	def testCreationAndStorage() {
		val data = {
			var data = Root()

			data = data("people").create
			data = data("people").require("name")
			data = data("people")("age") = "0"

			data = data("job").create
			data = data("job")("title") = "worker"

			data = data("job").link("people")

			data
		}


		val text = RootWriter(data)
		assertEquals(text, RootWriter(data))

		val loaded: Root = RootLoader(new ANTLRInputStream(text))

		assertEquals(data, loaded)
		assertEquals(text, RootWriter(loaded))
	}

	def testVisit() {
		val peter = "abcb7a5e-e1c0-40c6-abfe-a5e22a642ba6"
		val niki = "4f60dc27-5c68-4410-a452-40a4747dd7cd"
		val peter_job_0 = "81c8aee0-1544-4790-96eb-f29647c7a655"
		val peter_job_1 = "0b418fbe-8bab-49ce-8af5-ff908ee3aaeb"
		val niki_job = "46706d61-373e-4cf0-8bd6-52e4cecafea5"

		val text = {
			var data = Root()

			data = data("people").create
			data = data("people").require("name")
			data = data("people")("age") = "0"
			data = data("people")("dog") = "none"

			data = data("job").create
			data = data("job")("title") = "worker"

			data = data("job").link("people")

			data = data("people")(peter) = Map("name" -> "Peter", "age" -> "31")
			data = data("people")(niki) = Map("name" -> "Niki", "dog" -> "Dozer")
			data = data("job")(peter_job_0) = Map("people" -> peter, "title" -> "Engineer")
			data = data("job")(peter_job_1) = Map("people" -> peter, "title" -> "Painter")
			data = data("job")(niki_job) = Map("people" -> niki) // I don't actually know what she's up too


			val text = RootWriter(data)

			// if these are equal than encoding is deterministic
			assertEquals(text, RootWriter(data))

			// if these are also equal than decoding is also deterministic
			assertEquals(RootLoader(text), RootLoader(RootWriter(data)))

			// finally; if these are equal than coding is correct
			val actual: Root = RootLoader(text)
			assertEquals(data, actual)

			text
		}

		// setup expectations ...
		val mock = EasyMock.createMock(classOf[(String, String, Map[String, String]) => Unit])
		EasyMock.expect(mock("people", peter, Map("name" -> "Peter", "age" -> "31", "dog" -> "none"))).andReturn(Unit)
		EasyMock.expect(mock("people", niki, Map("name" -> "Niki", "dog" -> "Dozer", "age" -> "0"))).andReturn(Unit)
		EasyMock.expect(mock("job", peter_job_0, Map("title" -> "Engineer", "people/name" -> "Peter", "people/age" -> "31", "people/dog" -> "none"))).andReturn(Unit)
		EasyMock.expect(mock("job", peter_job_1, Map("title" -> "Painter", "people/name" -> "Peter", "people/age" -> "31", "people/dog" -> "none"))).andReturn(Unit)
		EasyMock.expect(mock("job", niki_job, Map("title" -> "worker", "people/name" -> "Niki", "people/dog" -> "Dozer", "people/age" -> "0"))).andReturn(Unit)

		EasyMock.replay(mock)

		// run the visitor
		RootLoader(text)("people") ? new TVisitor {
			override def apply(table: Table, row: Row, fields: Map[String, String], remotes: Map[Table, Set[Row]]) {
				mock(table.name, row.uuid, fields)
			}
		}
		RootLoader(text)("job") ? new TVisitor {
			override def apply(table: Table, row: Row, fields: Map[String, String], remotes: Map[Table, Set[Row]]) {
				mock(table.name, row.uuid, fields)
			}
		}
		EasyMock.verify(mock)
	}
}
