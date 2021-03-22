package tutorial.webapp

import japgolly.scalajs.react._
import japgolly.scalajs.react.extra.Ajax
import japgolly.scalajs.react.vdom.html_<^._
import org.scalajs.dom
import org.scalajs.dom.{console, document, html}

import scala.scalajs.js
import scala.scalajs.js.JSON
import scala.scalajs.js.annotation.JSName

object TutorialApp {

  @js.native
  trait UserAjaxResponse extends js.Object {
    val data: List[IndexedOrg]
  }

  @js.native
  trait IndexedOrg extends js.Object {
    val index: Int
    //    @JSName("first_name") val firstName: String
    val organisation: Organisation
  }

  @js.native
  trait Organisation extends js.Object {
    val name: String
    //    val ids: List[OrgID]
    //    @JSName("first_name") val firstName: String
    //    val organisation: Organisation
  }


  def login(username: String = "user", password: String = "demo"): AsyncCallback[Unit] =
    Ajax.post("http://127.0.0.1:8080/auth")
      .setRequestContentTypeJsonUtf8
      .and(_.withCredentials = true)
      .send(s"""{ "username": "$username", "password": "$password" }""")
      .validateStatusIsSuccessful(Callback.throwException) // Ensure (status >= 200 && status < 300) || status == 304
      .asAsyncCallback
      .void

  val mountNode = dom.document.getElementById("root")


  val Main = React.Suspense(
    fallback = <.div(^.color := "#33c", ^.fontSize := "150%", "AJAX in progress. Loading..."),
    asyncBody = loadAndRenderOrganisations.handleError(onError))

  def loadOrganisations(): AsyncCallback[List[IndexedOrg]] =
    Ajax.get("http://127.0.0.1:8080/organisation")
      .setRequestContentTypeJsonUtf8
      .and(_.withCredentials = true)
      .send
      .validateStatusIs(200)(Callback.throwException)
      .asAsyncCallback
      .map(xhr => {
        console.log(xhr.responseText)
        JSON.parse(xhr.responseText).asInstanceOf[List[IndexedOrg]]
      })

  //      .map(_.data)

  def loadAndRenderOrganisations: AsyncCallback[VdomElement] =
    for {
      _ <- login()
      orgs <- loadOrganisations()
    } yield renderOrganisations(orgs)

  def renderOrganisations(orgs: List[IndexedOrg]): VdomElement = {
    val th = <.th(^.border := "#666 solid 1px", ^.padding := "0.5em 0.8em")
    val td = <.td(^.border := "#666 solid 1px")
    <.table(
      <.thead(
        <.tr(
          th("Index"),
          th("Name"),
          <.tbody(
            orgs.toTagMod(org =>
              <.tr(
                td(^.padding := "0.5em", org.index),
                td(^.padding := "0.5em", org.organisation.name)))))))
  }

  def onError(error: Throwable): AsyncCallback[VdomElement] =
    AsyncCallback.delay {
      error.printStackTrace()
      <.div(^.color.red, ^.fontSize := "120%",
        <.div("An error occurred."),
        <.div(error.toString))
    }

  def entryPoint() = {
    //     https://japgolly.github.io/scalajs-react/#examples/hello
    //    val HelloMessage = ScalaComponent.builder[String]
    //      .render($ => <.div("Hello ", $.props))
    //      .build
    //    HelloMessage("world").renderIntoDOM(mountNode)

    Main.renderIntoDOM(mountNode)


    //    https://japgolly.github.io/scalajs-react/#examples/timer
    //    case class State(secondsElapsed: Long)
    //
    //    class Backend($: BackendScope[Unit, State]) {
    //      var interval: js.UndefOr[js.timers.SetIntervalHandle] =
    //        js.undefined
    //
    //      def tick =
    //        $.modState(s => State(s.secondsElapsed + 1))
    //
    //      def start = Callback {
    //        interval = js.timers.setInterval(1000)(tick.runNow())
    //      }
    //
    //      def clear = Callback {
    //        interval foreach js.timers.clearInterval
    //        interval = js.undefined
    //      }
    //
    //      def render(s: State) =
    //        <.div("Seconds elapsed: ", s.secondsElapsed)
    //    }
    //
    //    val Timer = ScalaComponent.builder[Unit]
    //      .initialState(State(0))
    //      .renderBackend[Backend]
    //      .componentDidMount(_.backend.start)
    //      .componentWillUnmount(_.backend.clear)
    //      .build
    //
    //    Timer().renderIntoDOM(mountNode)

    //    https://japgolly.github.io/scalajs-react/#examples/todo
    //    val TodoList = ScalaFnComponent[List[String]] { props =>
    //      def createItem(itemText: String) = <.li(itemText)
    //
    //      <.ul(props map createItem: _*)
    //    }
    //
    //    case class State(items: List[String], text: String)
    //
    //    class Backend($: BackendScope[Unit, State]) {
    //      def onChange(e: ReactEventFromInput) = {
    //        val newValue = e.target.value
    //        $.modState(_.copy(text = newValue))
    //      }
    //
    //      def handleSubmit(e: ReactEventFromInput) =
    //        e.preventDefaultCB >>
    //          $.modState(s => State(s.items :+ s.text, ""))
    //
    //      def render(state: State) =
    //        <.div(
    //          <.h3("TODO"),
    //          TodoList(state.items),
    //          <.form(^.onSubmit ==> handleSubmit,
    //            <.input(^.onChange ==> onChange, ^.value := state.text),
    //            <.button("Add #", state.items.length + 1)
    //          )
    //        )
    //    }
    //
    //    val TodoApp = ScalaComponent.builder[Unit]
    //      .initialState(State(Nil, ""))
    //      .renderBackend[Backend]
    //      .build
    //
    //    TodoApp().renderIntoDOM(mountNode)

    //    https://japgolly.github.io/scalajs-react/#examples/refs
    //    class Backend($: BackendScope[Unit, String]) {
    //
    //      val inputRef = Ref[html.Input]
    //
    //      def handleChange(e: ReactEventFromInput) =
    //        $.setState(e.target.value)
    //
    //      def clearAndFocusInput =
    //        $.setState("", inputRef.foreach(_.focus()))
    //
    //      def render(state: String) =
    //        <.div(
    //          <.div(
    //            ^.onClick --> clearAndFocusInput,
    //            "Click to Focus and Reset"),
    //          <.input(
    //            ^.value := state,
    //            ^.onChange ==> handleChange)
    //            .withRef(inputRef)
    //        )
    //    }
    //
    //    val App = ScalaComponent.builder[Unit]
    //      .initialState("")
    //      .renderBackend[Backend]
    //      .build
    //
    //    App().renderIntoDOM(mountNode)


    //    https://japgolly.github.io/scalajs-react/#examples/product-table
    //    case class Product(name: String, price: Double, category: String, stocked: Boolean)
    //
    //    case class State(filterText: String, inStockOnly: Boolean)
    //
    //    class Backend($: BackendScope[_, State]) {
    //      def onTextChange(e: ReactEventFromInput) =
    //        e.extract(_.target.value)(value =>
    //          $.modState(_.copy(filterText = value)))
    //
    //      def onCheckBox =
    //        $.modState(s => s.copy(inStockOnly = !s.inStockOnly))
    //    }
    //
    //    val ProductCategoryRow = ScalaComponent.builder[String]
    //      .render_P(category => <.tr(<.th(^.colSpan := 2, category)))
    //      .build
    //
    //    val ProductRow = ScalaComponent.builder[Product]
    //      .render_P(p =>
    //        <.tr(
    //          <.td(<.span(^.color.red.unless(p.stocked), p.name)),
    //          <.td(p.price))
    //      )
    //      .build
    //
    //    def productFilter(s: State)(p: Product): Boolean =
    //      p.name.contains(s.filterText) &&
    //        (!s.inStockOnly || p.stocked)
    //
    //    val ProductTable = ScalaComponent.builder[(List[Product], State)]
    //      .render_P { case (products, state) =>
    //        val rows = products.filter(productFilter(state))
    //          .groupBy(_.category).toList
    //          .flatMap { case (cat, ps) =>
    //            ProductCategoryRow.withKey(cat)(cat) :: ps.map(p => ProductRow.withKey(p.name)(p))
    //          }
    //        <.table(
    //          <.thead(
    //            <.tr(
    //              <.th("Name"),
    //              <.th("Price"))),
    //          <.tbody(
    //            rows.toVdomArray))
    //      }
    //      .build
    //
    //    val SearchBar = ScalaComponent.builder[(State, Backend)]
    //      .render_P { case (s, b) =>
    //        <.form(
    //          <.input.text(
    //            ^.placeholder := "Search Bar ...",
    //            ^.value := s.filterText,
    //            ^.onChange ==> b.onTextChange),
    //          <.p(
    //            <.input.checkbox(
    //              ^.onClick --> b.onCheckBox),
    //            "Only show products in stock"))
    //      }
    //      .build
    //
    //    val FilterableProductTable = ScalaComponent.builder[List[Product]]
    //      .initialState(State("", false))
    //      .backend(new Backend(_))
    //      .renderPS(($, p, s) =>
    //        <.div(
    //          SearchBar((s, $.backend)),
    //          ProductTable((p, s))
    //        )
    //      ).build
    //
    //    val PRODUCTS = List(
    //      Product("FootBall", 49.99, "Sporting Goods", true),
    //      Product("Baseball", 9.99, "Sporting Goods", true),
    //      Product("basketball", 29.99, "Sporting Goods", false),
    //      Product("ipod touch", 99.99, "Electronics", true),
    //      Product("iphone 5", 499.99, "Electronics", true),
    //      Product("Nexus 7", 199.99, "Electronics", true))
    //
    //    FilterableProductTable(PRODUCTS).renderIntoDOM(mountNode)
  }

  def main(args: Array[String]): Unit = {
    entryPoint()
  }

}
