package grails.plugin.restrenderers

/**
 * User: danielwoods
 * Date: 1/31/14
 */
public interface ObjectRenderer<T> {
  Class<T> getTargetType()
}