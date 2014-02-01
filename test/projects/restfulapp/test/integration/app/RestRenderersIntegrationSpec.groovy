package app

import grails.plugin.restrenderers.RendererRegistrar
import grails.rest.render.RendererRegistry
import org.codehaus.groovy.grails.web.mime.MimeType
import spock.lang.Specification

/**
 * User: danielwoods
 * Date: 1/31/14
 */
class RestRenderersIntegrationSpec extends Specification {

  def grailsApplication

  void "registrar is properly registered within the spring context"() {
    given:
      def ctx = grailsApplication.mainContext

    expect:
      ctx.getBean(RendererRegistrar)
  }

  void "renderers are properly resolvable from the grails rendering framework"() {
    setup:
      def ctx = grailsApplication.mainContext
      RendererRegistry registry = ctx.getBean(RendererRegistry)

    when:
      def renderer = registry.findRenderer(MimeType.JSON, TestDomain)

    then:
      renderer
      renderer instanceof RendererRegistrar.ClosureRenderer
  }
}
