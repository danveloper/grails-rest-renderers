package grails.plugin.restrenderers

import grails.rest.render.RenderContext

/**
 * User: danielwoods
 * Date: 1/31/14
 */
class DefaultObjectRenderer<T> extends AbstractObjectRenderer<T> {
  final Class targetType

  DefaultObjectRenderer(Class<T> targetType) {
    this.targetType = targetType
  }

  def json(T obj, RenderContext ctx) {
    write getJsonConverter(obj).toString(), ctx
  }

  def json(Collection<T> obj, RenderContext ctx) {
    write getJsonConverter(obj).toString(), ctx
  }

  def xml(T obj, RenderContext ctx) {
    write getXmlConverter(obj).toString(), ctx
  }

  def xml(Collection<T> obj, RenderContext ctx) {
    write getXmlConverter(obj).toString(), ctx
  }
}
