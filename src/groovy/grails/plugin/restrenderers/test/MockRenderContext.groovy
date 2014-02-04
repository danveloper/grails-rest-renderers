package grails.plugin.restrenderers.test

import grails.rest.render.AbstractRenderContext
import org.codehaus.groovy.grails.web.mime.MimeType
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus

/**
 * User: danielwoods
 * Date: 1/31/14
 */
class MockRenderContext extends AbstractRenderContext {
  Writer writer = new StringWriter()

  private Locale locale = Locale.US
  private HttpMethod method = HttpMethod.GET
  private HttpStatus status = HttpStatus.OK
  private String contentType = MimeType.ALL.name

  private String viewName
  private Map model

  private String action
  private String controller


  @Override
  String getResourcePath() {
    return null
  }

  @Override
  MimeType getAcceptMimeType() {
    return null
  }

  @Override
  Locale getLocale() {
    this.locale
  }

  void setLocale(Locale locale) {
    this.locale = locale
  }

  String getText() {
    this.writer.toString()
  }

  @Override
  HttpMethod getHttpMethod() {
    this.method
  }

  @Override
  void setStatus(HttpStatus status) {
    this.status = status
  }

  @Override
  void setContentType(String contentType) {
    this.contentType = contentType
  }

  String getContentType() {
    this.contentType
  }

  @Override
  void setViewName(String viewName) {
    this.viewName = viewName
  }

  @Override
  String getViewName() {
    this.viewName
  }

  @Override
  void setModel(Map model) {
    this.model = model
  }

  void setActionName(String action) {
    this.action = action
  }

  @Override
  String getActionName() {
    this.action
  }

  void setControllerName(String controller) {
    this.controller = controller
  }

  @Override
  String getControllerName() {
    this.controller
  }

  void reset() {
    this.writer = new StringWriter()
  }
}
