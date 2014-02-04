import grails.plugin.restrenderers.ObjectRenderer
import grails.plugin.restrenderers.RendererRegistrar
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.springframework.context.ApplicationContext

class GrailsRestRenderersGrailsPlugin {
  def version = "0.5-RC1"
  def grailsVersion = "2.3 > *"
  def pluginExcludes = ["grails-app/views/error.gsp"]

  def title = "REST Renderers Plugin"
  def description = '''\
This plugin allows for a more-streamlined approach to developing RESTful renderers in Grails 2.3+. The plugin
uses the underlying Rendering framework in Grails 2.3 to provide developers with an opportunity to maximize
reusability of business logic when developing renderers. This plugin also provides test fixtures for unit testing
renderers with a mock RenderingContext.
'''
  def documentation = "http://github.com/danveloper/grails-rest-renderers"
  def license = "APACHE"
  def developers = [[name: "Daniel Woods", email: "danielpwoods@gmail.com"]]
  def issueManagement = [system: "GITHUB", url: "http://github.com/danveloper/grails-rest-renderers/issues"]
  def scm = [url: "http://github.com/danveloper/grails-rest-renderers"]

  def doWithSpring = {
    rendererRegistrar(RendererRegistrar)
  }

  def onChange = { event ->
    reload event
  }

  def onConfigChange = { event ->
    reload event
  }

  private static void reload(event) {
    ApplicationContext ctx = event.ctx
    RendererRegistrar registrar = ctx.getBean(RendererRegistrar)
    ObjectRenderer[] renderers = (ctx.getBeansOfType(ObjectRenderer)?.values() ?: [])
    registrar.reloadConfig((GrailsApplication) event.application, renderers)
    registrar.afterPropertiesSet()
  }
}
