package grails.plugin.restrenderers

import grails.converters.JSON
import grails.converters.XML
import grails.rest.render.RenderContext
import org.codehaus.groovy.grails.commons.DomainClassArtefactHandler
import org.codehaus.groovy.grails.web.converters.IncludeExcludeConverter
import org.codehaus.groovy.grails.web.sitemesh.GrailsRoutablePrintWriter

/**
 * User: danielwoods
 * Date: 1/28/14
 */
abstract class AbstractObjectRenderer<T> implements ObjectRenderer<T> {
  List includes = []
  List excludes = []
  boolean prettyPrint = true

  static void write(String msg, RenderContext ctx) {
    def writer = ctx.writer
    if (ctx.writer instanceof GrailsRoutablePrintWriter) {
      // TODO: wtf? why is this necessary?
      writer = (GrailsRoutablePrintWriter)ctx.writer
      writer.blockFlush = false
    }
    writer.write msg
    writer.flush()
  }

  JSON getJsonConverter(object) {
    def converter = object as JSON
    converter.prettyPrint = prettyPrint
    imposeFilters converter, getTargets(object)
    converter
  }

  XML getXmlConverter(object) {
    def converter = object as XML
    imposeFilters converter, getTargets(object)
    converter
  }

  private static List<Class> getTargets(object) {
    def targets = [object.getClass()]
    if (Collection.isAssignableFrom(object.getClass()) && object.size()) {
      targets = object*.getClass().unique()
    }
    targets
  }

  private void imposeFilters(IncludeExcludeConverter converter, List<Class> targets) {
    for (Class target in targets) {
      if (DomainClassArtefactHandler.isDomainClass(target)) {
        if (excludes)
          converter.setExcludes target, excludes
        if (includes)
          converter.setIncludes target, includes
      } else {
        if (includes)
          converter.includes = includes
        if (excludes)
          converter.excludes = excludes
      }
    }
  }
}
