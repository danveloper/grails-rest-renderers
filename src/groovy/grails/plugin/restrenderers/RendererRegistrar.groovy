package grails.plugin.restrenderers

import grails.rest.render.ContainerRenderer
import grails.rest.render.RenderContext
import grails.rest.render.Renderer
import grails.rest.render.RendererRegistry
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.codehaus.groovy.grails.web.mime.MimeType
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Autowired

/**
 * User: danielwoods
 * Date: 1/31/14
 */
class RendererRegistrar implements InitializingBean {
  @Autowired
  RendererRegistry registry

  private List<ObjectRenderer> renderers
  private Map<String, String> applicationMimeTypes

  @Autowired
  RendererRegistrar(GrailsApplication grailsApplication, ObjectRenderer ... renderers) {
    reloadConfig grailsApplication, renderers
  }

  @Override
  void afterPropertiesSet() throws Exception {
    for (ObjectRenderer renderer in renderers) {
      final Class target = renderer.targetType
      applicationMimeTypes.each { String key, mimes ->
        [mimes].flatten().each { String stringMimeType ->
          def mimeType = new MimeType(stringMimeType)
          final Closure callable = renderer.&"$key"
          if (renderer.respondsTo(key, target, RenderContext)) {
            registry.addRenderer new ClosureRenderer(callable, mimeType, target)
          }
          if (renderer.respondsTo(key, Collection, RenderContext)) {
            registry.addContainerRenderer target, new ClosureContainerRenderer(callable, mimeType, target)
          }
        }
      }
    }
  }

  void reloadConfig(GrailsApplication grailsApplication, ObjectRenderer ... renderers) {
    log.info "Reloading REST Renderers Configuration..."
    this.applicationMimeTypes = grailsApplication.config.grails.mime.types as Map
    this.renderers = renderers
  }

  static class ClosureRenderer implements Renderer {
    final Closure callable
    final MimeType mimeType
    final Class target

    ClosureRenderer(Closure callable, MimeType mimeType, Class target) {
      this.callable = callable
      this.mimeType = mimeType
      this.target   = target
    }

    Class getTargetType() {
      target
    }

    void render(Object object, RenderContext context) {
      context.contentType = mimeType.name
      callable.call object, context
    }

    MimeType[] getMimeTypes() {
      [mimeType] as MimeType[]
    }
  }

  static class ClosureContainerRenderer extends ClosureRenderer implements ContainerRenderer {
    ClosureContainerRenderer(Closure callable, MimeType mimeType, Class target) {
      super(callable, mimeType, target)
    }

    Class getComponentType() {
      target
    }

    Class getTargetType() {
      Collection
    }
  }
}
