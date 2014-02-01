package grails.plugin.restrenderers

import grails.test.mixin.TestMixin
import grails.test.mixin.support.GrailsUnitTestMixin
import org.grails.plugins.web.rest.render.DefaultRendererRegistry
import spock.lang.Specification

/**
 * User: danielwoods
 * Date: 1/31/14
 */
@TestMixin(GrailsUnitTestMixin)
class RendererRegistrarSpec extends Specification {

  void setupSpec() {
    grailsApplication.config.grails.mime.types = ["json": ["application/json", "text/json"]]
  }

  void "test renderers are properly registered"() {
    setup:
      defineBeans {
        testDomainClassRenderer(DefaultObjectRenderer, ObjectRendererSpec.TestDomainClass)
        rendererRegistrar(RendererRegistrar)
        rendererRegistry(DefaultRendererRegistry)
      }
      def ctx = grailsApplication.mainContext
      // It would seem the bean is not initialized until we do this in unit test
      ctx.getBean(RendererRegistrar)

    when:
      def registry = ctx.getBean(DefaultRendererRegistry)
      def renderers = registry.getRegisteredObjects(ObjectRendererSpec.TestDomainClass)

    then:
      renderers
      renderers.size() == grailsApplication.config.grails.mime.types.json.size()
  }

  static class TestDomainClass {
    String foo
  }
}
