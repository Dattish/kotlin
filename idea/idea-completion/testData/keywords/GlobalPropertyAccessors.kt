fun foo() {
    bar()
}

fun bar() {
    foo()
}

var a : Int
  <caret>

// EXIST:  abstract
// EXIST: by
// EXIST:  class
// EXIST:  enum class
// EXIST:  final
// EXIST:  fun
// EXIST: { itemText: "get", tailText: null }
// EXIST: { itemText: "get", tailText: "() = ..." }
// EXIST: { itemText: "get", tailText: "() {...}" }
// EXIST:  internal
// EXIST:  object
// EXIST:  open
// EXIST:  private
// EXIST:  public
// EXIST: { itemText: "set", tailText: null }
// EXIST: { itemText: "set", tailText: "(value) = ..." }
// EXIST: { itemText: "set", tailText: "(value) {...}" }
// EXIST:  interface
// EXIST:  val
// EXIST:  var
// EXIST:  operator
// EXIST:  infix
// EXIST:  sealed class
// EXIST:  data class
// EXIST:  inline
// EXIST:  tailrec
// EXIST:  external
// EXIST:  annotation class
// EXIST:  const val
// EXIST:  suspend
// EXIST:  typealias
// EXIST:  expect
// EXIST:  actual
// EXIST:  lateinit var
// NOTHING_ELSE
