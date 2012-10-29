grails.project.work.dir = 'target'
grails.project.class.dir = 'target/classes'
grails.project.test.class.dir = 'target/test-classes'
grails.project.test.reports.dir = 'target/test-reports'
grails.project.dependency.resolution = {

	inherits "global"
	log "warn"

	repositories {
		grailsPlugins()
		grailsHome()
		grailsCentral()
		mavenLocal()
		mavenCentral()
		mavenRepo 'http://repo.grails.org/grails/libs-releases'
	}

	int grailsMajorVersion = grailsVersion.find(/^\d+/).toInteger()

	dependencies {
		compile 'joda-time:joda-time:2.1'

		test('org.hamcrest:hamcrest-all:1.1') {
			export = false
		}
		test('org.jodd:jodd-lagarto:3.4.0') {
			export = false
		}

		if (grailsMajorVersion < 2) {
			def datastoreVersion = '1.0.0.RC1'
			compile("org.grails:grails-datastore-gorm-plugin-support:$datastoreVersion",
					"org.grails:grails-datastore-gorm:$datastoreVersion",
					"org.grails:grails-datastore-core:$datastoreVersion",
					"org.grails:grails-datastore-simple:$datastoreVersion") {
				transitive = false
			}
			test("org.grails:grails-datastore-gorm-test:$datastoreVersion") {
				transitive = false
			}
		}
	}

	plugins {
		build(':release:2.0.4') {
			export = false
		}
		test(':spock:0.6') {
			export = false
		}
	}
}
