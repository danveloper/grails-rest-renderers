REST Renderers Plugin for Grails
---

The _REST Renderers Plugin_ for Grails provides some simplifications to the extremely powerful Restful Rendering framework that exists within Grails 2.3+. This framework allows developers to devise a correlation between domain entity, request media type, and a renderer to provide greater control over the model data that is given to a consumer of a Restful API. The structure natively provided by Grails, however, does not harbor an environment of reusability, and requires that new renderers be built for the extent of Mime Types with which an API will support. Additionally, the native Grails rendering framework requires different renderers be developed and registered for object instances versus a collection of their types. Given these shortcomings, the potential exists for higher levels of code complexity and duplication. This plugin serves as a mechanism to develop renderers in a simplified manner that supports code reusability and semantically simpler mechanisms to develop the renderer-to-media-type correlation. An additional goal of the plugin is to provide some text fixtures for renderers, so that developers may directly unit test their rendering code.

Installing
----

To use the plugin code within your application, simply add the following line to your `BuildConfig.groovy`'s `plugins` closure: `compile :grails-rest-renderers:0.5-RC1`. If you are a plugin developer and are interested in helping with the development of this plugin, please see the example application located within the `test/projects` directory for details on how to inline the plugin code.

Getting Started
---

The plugin provides a structure for semantically correlating a media type to a method on a renderer instance. It additionally allows for polymorphically defined methods to handle individual object instances and a collection of their types. To facilitate this, the plugin relies on Groovy's introspective features to determine is a given renderer is capable of rendering both an object and a collection of its types. Using this plugin, renderers must implement the `ObjectRenderer` interface, and must be registered within the Spring context. An `AbstractObjectRenderer` type is available with some common support methods (including retrieving converters) to assist in concrete implementation. For a more practical explanation, consider the following code, which provides a renderer for a contrived `MyDomainClass` type:

```groovy
class MyDomainClassRenderer extends AbstractObjectRenderer<MyDomainClass> {

    @Override
    Class<MyDomainClass> getTargetType() {
      MyDomainClass
    }

    def json(MyDomainClass obj, RenderContext ctx) {
      def model = getModel(obj)
      write getJsonConverter(model).toString(), ctx
    }

    def json(Collection<MyDomainClass> objs, RenderContext ctx) {
      def models = GParsPool.withPool {
        objs.collectParallel { MyDomainClass instance ->
          getModel instance
        }
      }
      write getJsonConverter(models).toString(), ctx
    }

    private static Map getModel(MyDomainClass instance) {
      [
        id: instance.name,
        first_prop: instance.association.someProperty,
        nested_struct: [
          prop1: instance.mySuperSweetProperty
          prop2: instance.owner.type.name()
        ]
      ]
    }
  }
}
```

Given simply the presence of the `json` methods that support an object's instance as well as a collection of those objects, the plugin will provide Grails with a bridge to use those methods when employing the Rendering framework. Nothing within your application will need to deviate from the standard Grails mechanism of rendering objects -- the plugin simply provides a more-sensible structure for defining renderer handlers. Given that, your controller code that already uses Grails' internal rendering framework needn't change to facilitate the simplifications provided by the plugin:

```groovy
class MyDomainClassController extends RestfulController<MyDomainClass> {
  static responseFormats = ['json']  

  MyDomainClassController() {
    super(MyDomainClass)
  }

  def show() {
    respond MyDomainClass.load(params.id)
  }
}
```

One thing you _must_ do, however is ensure that your renderers are registered within the Spring application context. Similar to the manner in which you must register native Grails renderers, you can define your renderers within your application's `resources.groovy` file, as such:

```groovy
beans = {
  myDomainClassRenderer(MyDomainClassRenderer)
}
```

And that's it! The plugin will detect all `ObjectRenderer` instances and will coordinate the bridge between the plugin's structure and the underlying Grails Rendering framework.

Verisoning & Supporting Media Types
----

The correlation of media-type-to-renderer-method is rooted in the definition of media types within your application's `Config.groovy`, under the `grails.mime.types` key. Providing support for any of the defined media types is as simple as defining an appropriately named method on your renderer instance. For example, consider the following `Config.groovy` entry:

```groovy
grails.mime.types = [ 
    all:           '*/*', 
    json:          ['application/json', 'text/json'],
    text:          'text/plain',
    xml:           ['text/xml', 'application/xml']
]
```

In the _Getting Started_ section, we demonstrated a renderer that provided handlers for the "json" media type. Additionally providing support for XML is as simple as defining the appropriate handlers on the renderer class, as such:

```groovy
class MyDomainClassRenderer extends AbstractObjectRenderer<MyDomainClass> {

    @Override
    Class<MyDomainClass> getTargetType() {
      MyDomainClass
    }

    def json(MyDomainClass obj, RenderContext ctx) {
      // ... see above.
    }

    def json(Collection<MyDomainClass> objs, RenderContext ctx) {
      // ... see above
    }

    def xml(MyDomainClass obj, RenderContext ctx) {
      def model = getModel(obj)
      write getXmlConverter(model).toString(), ctx
    }

    def xml(Collection<MyDomainClass> objs, RenderContext ctx) {
      // ... process the collection and render accordingly.
    }

    private static Map getModel(MyDomainClass instance) {
      // ... see above.
    }
  }
}
```

Given this capability, you can very easily provide versioning for your API, simply by adding a new media type for your `Config.groovy`, and defining the appropriate handlers on your renderer. Say, for example, that you wanted to define a new version of your API, so you would simply add the following key to your `grails.mime.types` configuration to represent the new media type: `appv2: 'application/vnd.app.org.company.mydomainclass+json;v=2.0'`. Then, on your renderer, provide an appropriate handler for `appv2`:

```groovy
class MyDomainClassRenderer extends AbstractObjectRenderer<MyDomainClass> {

    @Override
    Class<MyDomainClass> getTargetType() {
      MyDomainClass
    }

    def json(MyDomainClass obj, RenderContext ctx) {
      // ... see above.
    }

    def json(Collection<MyDomainClass> objs, RenderContext ctx) {
      // ... see above
    }

    def appv2(MyDomainClass obj, RenderContext ctx) {
      def model = getModelv2(obj)
      write getXmlConverter(model).toString(), ctx
    }

    def appv2(Collection<MyDomainClass> objs, RenderContext ctx) {
      // ... process the collection
    }

    private static Map getModel(MyDomainClass instance) {
      // ... see above.
    }

    private static Map getModelv2(MyDomainClass instance) {
      def model = getModel(instance)
      model.something = instance.someOtherThing
      model
    }
  }
}
```

This approach allows you to make use of the already-existing logic that the new version of your API may need, and makes for a greatly simplified renderer implementation.

Interoperability
---

The plugin is simply providing sensible decorations to the existing Rendering framework offered by Grails-proper. In that, it's not a one-or-the-other kind of thing; indeed, if for some reason you need to fall back to the Grails-proper manner of building renderers, then the plugin certainly won't stop you from doing that! If, for example, you wanted to use the plugin's structure for developing a renderer for an individual's object instance, but you had the need to build a CollectionRenderer based off of the native Grails structure for doing so, then you would simply _not_ define the handler method that supports `Collection` instances. For example:

```groovy
class MyDomainClassRenderer extends AbstractObjectRenderer<MyDomainClass> {

    @Override
    Class<MyDomainClass> getTargetType() {
      MyDomainClass
    }

    def json(MyDomainClass obj, RenderContext ctx) {
      // do the rendering
    }
  }
}
```

The above renderer will register an individual object renderer with Grails, but will leave you free to provide your own implementation of a `CollectionRenderer` (and vice-versa). That said, since the plugin _is_ using the Grails rendering framework under-the-covers, there can be only one renderer in the `RendererRegistry` for any type of object-to-media-type correlation. To say that more simply: if you have a Grails-proper custom `Renderer` instance that you've registered for `application/json` media types, then you cannot also have a renderer defined by the plugin's structure with a `json` method on it.

Testing
---

Grails doesn't provide any test fixtures for unit testing your renderer logic, so another goal of this plugin is to provide some support in that area. In that, the plugin offers a `MockRenderContext` class that can be used when crafting unit tests. To demonstrate this, consider the following Renderer:

```groovy
class MyDomainClassRenderer extends AbstractObjectRenderer<MyDomainClass> {

    @Override
    Class<MyDomainClass> getTargetType() {
      MyDomainClass
    }

    def json(MyDomainClass obj, RenderContext ctx) {
      if (!obj.active)
        write getJsonConverter([error: "not active"]).toString(), ctx
      else
        write getJsonConverter(obj).toString()
    }
  }
}
```

This example employs some business logic to determine what response will go back to the API's consumer, and as such is something that will need to be tested. The following unit test demonstrates how to utilize the `MockRendererContext` to go about that:

```groovy
@TestMixin([ControllerUnitTestMixin])
class MyDomainClassRendererSpec extends Specification {

  @Shared MockRenderContext ctx = new MockRenderContext()

  void "inactive objects render error text"() {
    setup:
      def renderer = new MyDomainClassRenderer()
      def obj = new MyDomainClass(active: false)

    when:
      renderer.json obj, ctx

    then:
      ctx.text.contains "\"error\": \"not active\""
  }
}
```

Basically, the `MockRenderContext` will allow you to inspect the textual representation of the renderer's response through its `getText()` method.

License
---

Still trying to work this out.

Contact
---

Dan Woods: g:[danielpwoods at gmail dot com](mailto:danielpwoods@gmail.com); t:[@danveloper](http://twitter.com/danveloper)

Date
---
31 JAN 14
