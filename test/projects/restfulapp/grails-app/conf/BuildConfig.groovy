grails.servlet.version = "3.0"
grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"
grails.project.work.dir = "target/work"
grails.project.target.level = 1.6
grails.project.source.level = 1.6
grails.project.dependency.resolver = "maven"
grails.project.dependency.resolution = {
  inherits("global") {
  }
  log "error"
  checksums true
  legacyResolve false
  repositories {
    inherits true
    grailsPlugins()
    mavenCentral()
  }
  dependencies {}
  plugins {
    build ":tomcat:7.0.50"
  }
}
grails.plugin.location.rr = "../../../../grails-rest-renderers"
