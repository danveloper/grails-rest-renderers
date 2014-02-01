import app.TestDomain
import grails.plugin.restrenderers.DefaultObjectRenderer

// Place your Spring DSL code here
beans = {
  testDomainRenderer(DefaultObjectRenderer, TestDomain)
}
