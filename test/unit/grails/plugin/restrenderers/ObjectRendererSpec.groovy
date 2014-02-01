package grails.plugin.restrenderers

import grails.plugin.restrenderers.test.MockRenderContext
import grails.rest.render.RenderContext
import grails.test.mixin.TestMixin
import grails.test.mixin.web.ControllerUnitTestMixin
import groovy.json.JsonSlurper
import spock.lang.Shared
import spock.lang.Specification

/**
 * User: danielwoods
 * Date: 1/31/14
 */
@TestMixin([ControllerUnitTestMixin])
class ObjectRendererSpec extends Specification {

  @Shared MockRenderContext context = new MockRenderContext()

  void "test DefaultObjectRenderer gives back good responses"() {
    setup:
      def renderer = new DefaultObjectRenderer(TestDomainClass)
      def obj = new TestDomainClass()
      obj.foo = "bar"

    when:
      renderer.json obj, context

    then:
      context.text.contains "\"foo\": \"bar\""

    when:
      context.reset()
      renderer.json([obj], context)

    then:
      def json = new JsonSlurper().parseText(context.text)
      json instanceof Collection

    when:
      context.reset()
      renderer.xml obj, context

    then:
      context.text.contains "<foo>bar</foo>"

    cleanup:
      context.reset()
  }

  void "test custom renderer"() {
    setup:
      def renderer = new MySpecialObjectRenderer()
      def obj = new TestDomainClass()

    when:
      renderer.json obj, context

    then:
      context.text == "lol, no"
  }

  static class MySpecialObjectRenderer extends AbstractObjectRenderer<TestDomainClass> {

    @Override
    Class<TestDomainClass> getTargetType() {
      TestDomainClass
    }

    def json(TestDomainClass obj, RenderContext ctx) {
      ctx.writer.write "lol, no"
    }
  }

  static class TestDomainClass {
    String foo
  }
}
